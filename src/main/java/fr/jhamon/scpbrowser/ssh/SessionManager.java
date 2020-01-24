package fr.jhamon.scpbrowser.ssh;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import fr.jhamon.scpbrowser.model.SessionConfigModel;
import fr.jhamon.scpbrowser.model.SessionModel;
import fr.jhamon.scpbrowser.model.exception.SessionException;
import fr.jhamon.scpbrowser.model.exception.TimeoutException;
import fr.jhamon.scpbrowser.utils.ConfigUtils;
import fr.jhamon.scpbrowser.utils.Constantes;
import fr.jhamon.scpbrowser.utils.DialogUtils;
import fr.jhamon.scpbrowser.utils.LoggerUtils;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;
import fr.jhamon.scpbrowser.view.ssh.JschUserInfoUI;

/**
 * manager used to handle ssh sessions
 *
 * @author J.Hamon Copyright 2019 J.Hamon
 *
 */
public class SessionManager {

  private Multimap<Session, Channel> sessionConsoleChannelMap = ArrayListMultimap
      .create();
  private Multimap<Session, Channel> sessionContentChannelMap = ArrayListMultimap
      .create();

  public SessionManager() {
  }

  /**
   * Create a new session
   *
   * @param sessionConfig session configuration
   * @param passphrase    ssh passphrase
   * @return a new Session
   * @throws SessionException
   */
  public SessionModel openNewSession(SessionConfigModel sessionConfig,
      char[] passphrase) throws SessionException {
    JSch jsch = new JSch();
    try {
      jsch.addIdentity(ConfigUtils.getConfigProperty("sshKey"),
          new String(passphrase));
      jsch.setKnownHosts(Constantes.KNOWNHOSTS_FILE);
      Session session = jsch.getSession(ConfigUtils.getConfigProperty("racf"),
          ConfigUtils.getConfigProperty("serverRebondUrl"),
          Integer.valueOf(ConfigUtils.getConfigProperty("serverRebondPort")));
      session.setUserInfo(new JschUserInfoUI());
      session.connect();
      return new SessionModel(session, sessionConfig);
    } catch (JSchException e) {
      throw new SessionException(e);
    }
  }

  /**
   * Open a ssh console channel
   *
   * @param sessionModel
   * @return
   * @throws SessionException
   */
  public Channel openConsoleChannel(SessionModel sessionModel, String motive)
      throws SessionException {
    Session session = sessionModel.getSshSession();
    if (session == null) {
      throw new SessionException("No session created for config "
          + sessionModel.getConfiguration().toString());
    }
    if (!sessionModel.getSshSession().isConnected()) {
      throw new SessionException("Session is not connected");
    }
    try {
      // console channel
      ChannelExec channel = (ChannelExec) session.openChannel("exec");
      channel.setAgentForwarding(true);
      channel.setPty(true);
      channel.setPtyType("dumb");
      if (StringUtils.isNotBlank(motive)) {
        channel.setEnv(Constantes.MOTIVE_ENV_VAR, motive);
      }
      channel.setCommand(
          SessionUtils.buildSshCommand(sessionModel.getConfiguration()));

      channel.connect();

      synchronized (this.sessionConsoleChannelMap) {
        this.sessionConsoleChannelMap.put(session, channel);
      }

      return channel;
    } catch (JSchException /* | IOException */ e) {
      throw new SessionException(e);
    }
  }

  /**
   * Open a channel for content management
   *
   * @param sessionModel
   * @return
   * @throws SessionException
   */
  public Pair<OutputStream, InputStream> openContentChannel(
      SessionModel sessionModel) throws SessionException {
    Session session = sessionModel.getSshSession();
    if (session == null) {
      throw new SessionException("No session created for config "
          + sessionModel.getConfiguration().toString());
    }
    if (!sessionModel.getSshSession().isConnected()) {
      throw new SessionException("Session is not connected");
    }
    try {
      // content channel
      ChannelExec channel = (ChannelExec) session.openChannel("exec");
      channel.setAgentForwarding(true);
      channel.setPty(true);
      // channel.setPtyType("dumb");
      if (StringUtils.isNotBlank(sessionModel.getMotive())) {
        channel.setEnv(Constantes.MOTIVE_ENV_VAR, sessionModel.getMotive());
      }
      channel.setCommand(
          SessionUtils.buildSshCommand(sessionModel.getConfiguration()));

      Pair<OutputStream, InputStream> streams = Pair
          .of(channel.getOutputStream(), channel.getInputStream());
      channel.setErrStream(System.err);

      channel.connect();

      try {
        boolean pwdPrompt = SessionUtils.waitFor(streams.getRight(),
            "password:", sessionModel.getConfiguration().getConnectTimeout());
        // send app password
        if (pwdPrompt && !StringUtils
            .isBlank(sessionModel.getConfiguration().getPassword())) {
          // send app password
          streams.getLeft()
              .write(sessionModel.getConfiguration().getPassword().getBytes());
          streams.getLeft().write('\n');
          streams.getLeft().flush();
        }
      } catch (TimeoutException e) {
        if (!StringUtils
            .isBlank(sessionModel.getConfiguration().getPassword())) {
          throw new SessionException("Connection timeout");
        }
      } catch (InterruptedException e) {
        if (!StringUtils
            .isBlank(sessionModel.getConfiguration().getPassword())) {
          throw new SessionException("Connection timeout");
        }
      }

      SessionUtils.waitFor(streams.getRight(),
          String.format("[%s@", sessionModel.getConfiguration().getUsername()),
          sessionModel.getConfiguration().getConnectTimeout());

      synchronized (this.sessionContentChannelMap) {
        this.sessionContentChannelMap.put(session, channel);
      }

      return streams;
    } catch (JSchException e) {
      throw new SessionException(e);
    } catch (IOException e) {
      throw new SessionException(e);
    } catch (InterruptedException e) {
      throw new SessionException(e);
    } catch (TimeoutException e) {
      throw new SessionException(e);
    }
  }

  /**
   * Download a file for a remote server
   *
   * @param sessionModel session to connect
   * @param filePath     file to download
   * @return
   * @throws SessionException if download fails
   */
  public void downloadFile(SessionModel sessionModel, String filePath,
      File destination) throws SessionException {
    Session session = sessionModel.getSshSession();
    if (session == null) {
      throw new SessionException("No session created for config "
          + sessionModel.getConfiguration().toString());
    }
    if (!sessionModel.getSshSession().isConnected()) {
      throw new SessionException("Session is not connected");
    }

    try {
      if (destination != null && destination.getParentFile() != null
          && !destination.getParentFile().exists()) {
        destination.getParentFile().mkdirs();
      }
      ChannelExec channel = (ChannelExec) session.openChannel("exec");
      channel.setAgentForwarding(true);
      channel.setPty(true);

      String dlDestination = destination != null ? destination.getAbsolutePath()
          : ConfigUtils.getDownloadDirectory();
      channel.setCommand(SessionUtils.buildScpDownloadCommand(
          sessionModel.getConfiguration(), filePath, dlDestination));

      Pair<OutputStream, InputStream> streams = Pair
          .of(channel.getOutputStream(), channel.getInputStream());
      channel.setErrStream(System.err);

      channel.connect();

      boolean pwdPrompt;
      try {
        pwdPrompt = SessionUtils.waitFor(streams.getRight(), "password:",
            sessionModel.getConfiguration().getConnectTimeout());
        // send app password
        if (pwdPrompt && !StringUtils
            .isBlank(sessionModel.getConfiguration().getPassword())) {
          // send app password
          streams.getLeft()
              .write(sessionModel.getConfiguration().getPassword().getBytes());
          streams.getLeft().write('\n');
          streams.getLeft().flush();
        }

      } catch (TimeoutException e) {
        DialogUtils.showError(
            PropertiesUtils.getViewProperty(
                "scpbrowser.dialog.file.download.error.message", filePath),
            PropertiesUtils.getViewProperty(
                "scpbrowser.dialog.file.download.error.title"));
        LoggerUtils.error(PropertiesUtils.getViewProperty(
            "scpbrowser.dialog.file.download.error.message", filePath), e);
      }

      String cmdOutput;
      cmdOutput = SessionUtils.readUntil(streams.getRight(), " \r\n",
          sessionModel.getConfiguration().getDownloadTimeout());
      if (cmdOutput.contains("lost connection")) {
        throw new SessionException("Connection lost");
      }

      while (!channel.isClosed()) {
        Thread.sleep(50);
      }
      channel.disconnect();
      return;
    } catch (JSchException e) {
      throw new SessionException(e);
    } catch (IOException e) {
      throw new SessionException(e);
    } catch (InterruptedException e) {
      throw new SessionException(e);
    } catch (TimeoutException e) {
      throw new SessionException(e);
    }
  }

  /**
   * Upload a file to a remote session
   *
   * @param sessionModel session to connect
   * @param filePath     file to upload
   * @param destDir      destination path
   * @return
   * @throws SessionException if upload fails
   */
  public void uploadFile(SessionModel sessionModel, String filePath,
      String destDir, String motive) throws SessionException {
    Session session = sessionModel.getSshSession();
    if (session == null) {
      throw new SessionException("No session created for config "
          + sessionModel.getConfiguration().toString());
    }
    if (!sessionModel.getSshSession().isConnected()) {
      throw new SessionException("Session is not connected");
    }

    // format filePath
    filePath = filePath.replace("\\", "/");
    Matcher driveMatcher = Constantes.WINDOWS_DRIVE_PATTERN.matcher(filePath);
    if (driveMatcher.find()) {
      filePath = filePath.replace(driveMatcher.group(1), String.format(
          Constantes.UNIX_DRIVE_TEMPLATE, driveMatcher.group(2).toLowerCase()));
    }

    try {
      ChannelExec channel = (ChannelExec) session.openChannel("exec");
      channel.setAgentForwarding(true);
      channel.setPty(true);
      if (StringUtils.isNotBlank(motive)) {
        channel.setEnv(Constantes.MOTIVE_ENV_VAR, motive);
      }
      channel.setCommand(SessionUtils.buildScpUploadCommand(
          sessionModel.getConfiguration(), filePath, destDir));

      Pair<OutputStream, InputStream> streams = Pair
          .of(channel.getOutputStream(), channel.getInputStream());
      channel.setErrStream(System.err);

      channel.connect();

      boolean pwdPrompt;
      try {
        pwdPrompt = SessionUtils.waitFor(streams.getRight(), "password:",
            sessionModel.getConfiguration().getConnectTimeout());
        // send app password
        if (pwdPrompt && !StringUtils
            .isBlank(sessionModel.getConfiguration().getPassword())) {
          // send app password
          streams.getLeft()
              .write(sessionModel.getConfiguration().getPassword().getBytes());
          streams.getLeft().write('\n');
          streams.getLeft().flush();
        }
      } catch (TimeoutException e) {
        DialogUtils.showError(
            PropertiesUtils.getViewProperty(
                "scpbrowser.dialog.file.upload.error.message", filePath),
            PropertiesUtils
                .getViewProperty("scpbrowser.dialog.file.upload.error.title"));
        LoggerUtils.error(PropertiesUtils.getViewProperty(
            "scpbrowser.dialog.file.upload.error.message", filePath), e);
      }

      String cmdOutput;
      cmdOutput = SessionUtils.readUntil(streams.getRight(), " \r",
          sessionModel.getConfiguration().getUploadTimeout());
      if (cmdOutput.contains("No such file or directory")) {
        DialogUtils.showError(PropertiesUtils.getViewProperty(
            "scpbrowser.dialog.file.upload.error.message.notfound", filePath),
            PropertiesUtils
                .getViewProperty("scpbrowser.dialog.file.upload.error.title"));
        throw new SessionException("File not found");
      }
      if (cmdOutput.contains("lost connection")) {
        DialogUtils.showError(
            PropertiesUtils.getViewProperty(
                "scpbrowser.dialog.file.upload.error.message", filePath),
            PropertiesUtils
                .getViewProperty("scpbrowser.dialog.file.upload.error.title"));
        throw new SessionException("Connection lost");
      }

      while (!channel.isClosed()) {
        Thread.sleep(50);
      }

      channel.disconnect();
      return;
    } catch (JSchException e) {
      throw new SessionException(e);
    } catch (IOException e) {
      throw new SessionException(e);
    } catch (InterruptedException e) {
      throw new SessionException(e);
    } catch (TimeoutException e) {
      throw new SessionException(e);
    }
  }

  /**
   * Close all channels linked to a session model, then close the session
   *
   * @param sessionModel
   */
  public void closeSession(SessionModel sessionModel) {
    synchronized (this.sessionConsoleChannelMap) {
      for (Channel channel : this.sessionConsoleChannelMap
          .get(sessionModel.getSshSession())) {
        channel.disconnect();
      }
    }
    synchronized (this.sessionContentChannelMap) {
      for (Channel channel : this.sessionContentChannelMap
          .get(sessionModel.getSshSession())) {
        channel.disconnect();
      }
    }
    sessionModel.getSshSession().disconnect();
  }

  /**
   * Close all channels and sessions
   */
  public void closeAllSessions() {
    synchronized (this.sessionConsoleChannelMap) {
      synchronized (this.sessionContentChannelMap) {
        Set<Session> sessionSet = new HashSet<Session>();
        for (Session session : this.sessionConsoleChannelMap.keys()) {
          for (Channel channel : this.sessionConsoleChannelMap.get(session)) {
            channel.disconnect();
          }
          sessionSet.add(session);
        }
        for (Session session : this.sessionContentChannelMap.keys()) {
          for (Channel channel : this.sessionContentChannelMap.get(session)) {
            channel.disconnect();
          }
          sessionSet.add(session);
        }
        for (Session session : sessionSet) {
          session.disconnect();
        }
      }
    }
  }

}
