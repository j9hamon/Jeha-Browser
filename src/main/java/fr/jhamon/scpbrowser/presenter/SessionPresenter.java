
package fr.jhamon.scpbrowser.presenter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;

import com.jcraft.jcterm.Connection;
import com.jcraft.jcterm.Term;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;

import fr.jhamon.scpbrowser.Context;
import fr.jhamon.scpbrowser.eventbus.MainEventBus;
import fr.jhamon.scpbrowser.eventbus.event.DownloadEvent;
import fr.jhamon.scpbrowser.eventbus.event.TransferEvent;
import fr.jhamon.scpbrowser.eventbus.event.UploadEvent;
import fr.jhamon.scpbrowser.model.ContentModel;
import fr.jhamon.scpbrowser.model.FileModel;
import fr.jhamon.scpbrowser.model.FileType;
import fr.jhamon.scpbrowser.model.FolderModel;
import fr.jhamon.scpbrowser.model.SessionModel;
import fr.jhamon.scpbrowser.model.exception.SessionException;
import fr.jhamon.scpbrowser.model.exception.TimeoutException;
import fr.jhamon.scpbrowser.ssh.SessionManager;
import fr.jhamon.scpbrowser.ssh.SessionUtils;
import fr.jhamon.scpbrowser.utils.ConfigUtils;
import fr.jhamon.scpbrowser.utils.DialogUtils;
import fr.jhamon.scpbrowser.utils.LoggerUtils;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;
import fr.jhamon.scpbrowser.utils.TransferUtils;
import fr.jhamon.scpbrowser.view.component.Console;
import fr.jhamon.scpbrowser.view.component.SessionView;
import fr.jhamon.scpbrowser.view.component.event.handler.ContentEventHandler;
import fr.jhamon.scpbrowser.view.component.impl.ConsoleFrame;

/**
 * Presenter for a session view
 *
 * @author J.Hamon Copyright 2019 J.Hamon
 *
 */
public class SessionPresenter implements ContentEventHandler {

  private static final Pattern PWD_PATTERN = Pattern.compile("(/.*)",
      Pattern.MULTILINE);

  private static final Pattern LS_PATTERN = Pattern.compile(
      "(\\S{10,11} +\\d+ +\\d+ +\\d{4}-\\d{2}-\\d{2} +\\d{2}:\\d{2}:\\d{2}.\\d{9} +[+-]\\d{4} +\".*\"\\/?)",
      Pattern.MULTILINE);

  private static final SimpleDateFormat LS_DATE_FORMAT = new SimpleDateFormat(
      "yyyy-MM-dd HH:mm:ss");

  private SessionView view;

  private Console consoleView;

  private SessionModel sessionModel;

  private Context context;

  private SessionManager sessionManager;

  private String currentDir;

  Channel consoleChannel;

  Pair<OutputStream, InputStream> contentStreams;

  private Connection connection;

  private String endOfCmdPattern;

  /**
   * @param view           session view
   * @param sessionModel   session model
   * @param context        applicaiton context
   * @param sessionManager session manager
   * @throws SessionException
   */
  public SessionPresenter(SessionView view, SessionModel sessionModel,
      Context context, SessionManager sessionManager) throws SessionException {
    this.view = view;
    this.sessionModel = sessionModel;
    this.context = context;
    this.sessionManager = sessionManager;
    this.endOfCmdPattern = String.format("[%s@",
        this.sessionModel.getConfiguration().getUsername());
    this.contentStreams = this.sessionManager
        .openContentChannel(this.sessionModel);
    this.view.setEventHandler(this);

    this.getCurrentDirectory();
    this.refreshContent();
  }

  /**
   * Open the console view
   */
  public void openConsoleView() {
    LoggerUtils.debug("Console view requested for "
        + this.getSessionModel().getConfiguration());
    if (this.consoleView != null && this.consoleView.isDisplayable()) {
      LoggerUtils.debug("Console view is already opened for "
          + this.getSessionModel().getConfiguration());
      this.consoleView.requestFocus();
    } else {
      try {
        String motive = this.sessionModel.getMotive();
        if (ConfigUtils.isMotiveRequired()) {
          motive = DialogUtils.showMotiveInputDialog(motive);
          if (motive == null) {
            // annulation
            return;
          }
          this.sessionModel.setMotive(motive);
        }
        this.consoleChannel = this.sessionManager
            .openConsoleChannel(this.sessionModel, motive);
        final OutputStream out = this.consoleChannel.getOutputStream();
        final InputStream in = this.consoleChannel.getInputStream();
        connection = new Connection() {

          @Override
          public InputStream getInputStream() {
            return in;
          }

          @Override
          public OutputStream getOutputStream() {
            return out;
          }

          @Override
          public void requestResize(Term term) {
            if (SessionPresenter.this.consoleChannel instanceof ChannelShell) {
              int c = term.getColumnCount();
              int r = term.getRowCount();
              ((ChannelExec) SessionPresenter.this.consoleChannel).setPtySize(c,
                  r, c * term.getCharWidth(), r * term.getCharHeight());
            }
          }

          @Override
          public void close() {
            SessionPresenter.this.consoleChannel.disconnect();
          }
        };
        this.consoleView = new ConsoleFrame();
        this.consoleView
            .setTitle(this.sessionModel.getConfiguration().getUsername() + "@"
                + this.sessionModel.getConfiguration().getServer());
        this.consoleView.start(connection);

        LoggerUtils.debug("Console view opened for "
            + this.getSessionModel().getConfiguration());
      } catch (IOException e) {
        LoggerUtils.error("Failed to open console view for "
            + sessionModel.getConfiguration(), e);
        DialogUtils.showError(
            PropertiesUtils.getViewProperty(
                "scpbrowser.dialog.action.console.error.message"),
            PropertiesUtils.getViewProperty(
                "scpbrowser.dialog.action.console.error.title"));
      } catch (SessionException e) {
        LoggerUtils.error("Failed to open console view for "
            + sessionModel.getConfiguration(), e);
        DialogUtils.showError(
            PropertiesUtils.getViewProperty(
                "scpbrowser.dialog.action.console.error.message"),
            PropertiesUtils.getViewProperty(
                "scpbrowser.dialog.action.console.error.title"));
      }
    }
  }

  /**
   * close the session view
   */
  public void closeView() {
    LoggerUtils
        .debug("Closing console view for " + sessionModel.getConfiguration());
    try {
      contentStreams.getLeft().close();
      contentStreams.getRight().close();
    } catch (IOException e) {
      LoggerUtils.error("Failed to close console streams for "
          + sessionModel.getConfiguration(), e);
    }
    if (this.consoleView != null) {
      this.consoleView.close();
    }
    this.view.close();
    this.sessionManager.closeSession(this.sessionModel);
  }

  /**
   * get the current remote directory
   */
  public void getCurrentDirectory() {
    LoggerUtils
        .debug("Request current path " + sessionModel.getConfiguration());
    try {
      // clear streams
      if (this.contentStreams.getRight().available() > 0) {
        this.contentStreams.getRight()
            .skip(this.contentStreams.getRight().available());
      }
      // send pwd command
      this.contentStreams.getLeft()
          .write(SessionUtils.SHELL_PATH_CMD.getBytes());
      this.contentStreams.getLeft().flush();

      // get the command output
      String cmdOutput = SessionUtils.readUntil(this.contentStreams.getRight(),
          endOfCmdPattern,
          this.sessionModel.getConfiguration().getCommandTimeout());

      // parse the output
      Matcher matcher = PWD_PATTERN.matcher(cmdOutput);
      if (matcher.find()) {
        this.currentDir = matcher.group(1);
        this.view.setPath(currentDir);
      }
    } catch (IOException e) {
      DialogUtils.showError(
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.path.error.message"),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.path.error.title"));
      LoggerUtils.error(PropertiesUtils
          .getViewProperty("scpbrowser.dialog.action.path.error.message") + " "
          + sessionModel.getConfiguration(), e);
    } catch (InterruptedException e) {
      DialogUtils.showError(
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.path.error.message"),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.path.error.title"));
      LoggerUtils.error(PropertiesUtils
          .getViewProperty("scpbrowser.dialog.action.path.error.message") + " "
          + sessionModel.getConfiguration(), e);
    } catch (TimeoutException e) {
      DialogUtils.showError(
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.path.error.message"),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.path.error.title"));
      LoggerUtils.error(PropertiesUtils
          .getViewProperty("scpbrowser.dialog.action.path.error.message") + " "
          + sessionModel.getConfiguration(), e);
    }
  }

  /**
   * Refresh the current directory content
   */
  public void refreshContent() {
    LoggerUtils
        .debug("Refresh current directory " + sessionModel.getConfiguration());
    try {
      // clear streams
      if (this.contentStreams.getRight().available() > 0) {
        this.contentStreams.getRight()
            .skip(this.contentStreams.getRight().available());
      }
      // send ls command
      this.contentStreams.getLeft()
          .write(SessionUtils.SHELL_LIST_CMD.getBytes());
      this.contentStreams.getLeft().flush();

      // get command output
      String cmdOutput = SessionUtils.readUntil(this.contentStreams.getRight(),
          endOfCmdPattern,
          this.sessionModel.getConfiguration().getCommandTimeout());

      Matcher matcher = LS_PATTERN.matcher(cmdOutput);
      // parse the output
      List<ContentModel> contentList = new ArrayList<ContentModel>();
      while (matcher.find()) {
        for (int idx = 1; idx < matcher.groupCount() + 1; idx++) {
          ContentModel contentModel;
          String[] parts = matcher.group(idx).split(" +", 7);
          String[] nameLink = parts[6].split(" -> ");
          Date date;
          try {
            date = LS_DATE_FORMAT.parse(parts[3] + " " + parts[4]);
          } catch (ParseException e) {
            date = null;
          }
          if (parts[6].endsWith("/")) {
            // folder
            contentModel = new FolderModel(nameLink[0].replaceAll("\"|\\/", ""),
                this.currentDir, date);
            contentList.add(contentModel);
          } else {
            // file
            int startExtension = parts[6].lastIndexOf(".");
            FileType type;
            if (startExtension >= 0) {
              String strFileExtension = parts[6].substring(
                  parts[6].lastIndexOf(".") + 1, parts[6].lastIndexOf("\""));
              type = FileType.getFileType(strFileExtension);
            } else {
              type = FileType.getDefaultFileType();
            }
            contentModel = new FileModel(nameLink[0].replaceAll("\"", ""),
                this.currentDir, date, Long.valueOf(parts[2]), type);
            contentList.add(contentModel);
          }
        }
      }
      // refresh view
      this.getView().getContentTable().setContent(contentList);
    } catch (IOException e) {
      DialogUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.action.refresh.error.message"),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.refresh.error.title"));
      LoggerUtils.error(PropertiesUtils
          .getViewProperty("scpbrowser.dialog.action.refresh.error.message")
          + " " + sessionModel.getConfiguration(), e);
    } catch (InterruptedException e) {
      DialogUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.action.refresh.error.message"),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.refresh.error.title"));
      LoggerUtils.error(PropertiesUtils
          .getViewProperty("scpbrowser.dialog.action.refresh.error.message")
          + " " + sessionModel.getConfiguration(), e);
    } catch (TimeoutException e) {
      DialogUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.action.refresh.error.message"),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.refresh.error.title"));
      LoggerUtils.error(PropertiesUtils
          .getViewProperty("scpbrowser.dialog.action.refresh.error.message")
          + " " + sessionModel.getConfiguration(), e);
    }
  }

  /**
   * upload a file to the remote active directory
   */
  public void uploadContent() {
    this.view.openUploadFileChooser();
  }

  /**
   * Move to the directory
   *
   * @param content
   */
  public void moveToDir(FolderModel content) {
    this.moveToDir(content.getPath() + "/" + content.getName());
  }

  /**
   * Move to the path
   *
   * @param path
   */
  public void moveToDir(String path) {
    LoggerUtils.debug(String.format("Moving to %s for %s", path,
        this.sessionModel.getConfiguration()));
    try {
      // send cd command
      this.contentStreams.getLeft().write(("cd \"" + path + "\"\n").getBytes());
      this.contentStreams.getLeft().flush();
      // read command output
      String cmdOutput = SessionUtils.readUntil(this.contentStreams.getRight(),
          endOfCmdPattern,
          this.sessionModel.getConfiguration().getCommandTimeout());
      boolean success = true;
      if (cmdOutput.contains("No such file or directory")) {
        success = false;
        DialogUtils.showError(
            PropertiesUtils.getViewProperty(
                "scpbrowser.dialog.action.move.error.message.notfound", path),
            PropertiesUtils
                .getViewProperty("scpbrowser.dialog.action.move.error.title"));
      } else if (cmdOutput.contains("Permission denied")) {
        success = false;
        DialogUtils.showError(
            PropertiesUtils.getViewProperty(
                "scpbrowser.dialog.action.move.error.message.permission", path),
            PropertiesUtils
                .getViewProperty("scpbrowser.dialog.action.move.error.title"));
      }
      this.getCurrentDirectory();
      if (success) {
        this.refreshContent();
      }
    } catch (IOException e) {
      DialogUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.action.move.error.message", path),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.move.error.title"));
      LoggerUtils.error(PropertiesUtils
          .getViewProperty("scpbrowser.dialog.action.move.error.message", path)
          + " " + sessionModel.getConfiguration());
    } catch (InterruptedException e) {
      DialogUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.action.move.error.message", path),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.move.error.title"));
      LoggerUtils.error(PropertiesUtils
          .getViewProperty("scpbrowser.dialog.action.move.error.message", path)
          + " " + sessionModel.getConfiguration());
    } catch (TimeoutException e) {
      DialogUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.action.move.error.message", path),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.move.error.title"));
      LoggerUtils.error(PropertiesUtils
          .getViewProperty("scpbrowser.dialog.action.move.error.message", path)
          + " " + sessionModel.getConfiguration());
    }
  }

  /**
   * Download a file from the remote server
   *
   * @param file file to download
   */
  public void downloadContent(FileModel file, File destination) {
    LoggerUtils.debug(String.format("Downloading %s for %s", file.getFullPath(),
        sessionModel.getConfiguration()));

    long downloadId = TransferUtils.getUniqueId();

    FileModel fileDest;
    if (destination != null) {
      fileDest = new FileModel(destination.getName(),
          destination.getParentFile().getAbsolutePath().replace('\\', '/'),
          null, 0, null);
    } else {
      fileDest = new FileModel(file.getName(),
          ConfigUtils.getDownloadDirectory(), null, 0, null);
    }
    try {
      MainEventBus.getInstance().post(new DownloadEvent(downloadId, file,
          fileDest, TransferEvent.Status.RUNNING));
      this.sessionManager.downloadFile(this.sessionModel, file.getFullPath(),
          destination);

      if (!(new File(fileDest.getFullPath()).exists())) {
        throw new SessionException("Erreur de telechargement.");
      }
      MainEventBus.getInstance().post(new DownloadEvent(downloadId, file,
          fileDest, TransferEvent.Status.SUCCESS));
    } catch (SessionException e) {
      MainEventBus.getInstance().post(new DownloadEvent(downloadId, file,
          fileDest, TransferEvent.Status.FAILURE));
      DialogUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.file.download.error.message", file.getName()),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.file.download.error.title"));
      LoggerUtils.error(PropertiesUtils.getViewProperty(
          "scpbrowser.dialog.file.download.error.message", file.getName()) + " "
          + sessionModel.getConfiguration());
    }
  }

  public SessionView getView() {
    return view;
  }

  public void setView(SessionView view) {
    this.view = view;
  }

  public SessionModel getSessionModel() {
    return sessionModel;
  }

  public void setSessionModel(SessionModel sessionModel) {
    this.sessionModel = sessionModel;
  }

  public Context getContext() {
    return context;
  }

  public void setContext(Context context) {
    this.context = context;
  }

  public SessionManager getSessionManager() {
    return sessionManager;
  }

  public void setSessionManager(SessionManager sessionManager) {
    this.sessionManager = sessionManager;
  }

  @Override
  public void onUploadEvent(String absolutePath) {
    LoggerUtils.debug(String.format("Uploading %s to %s for %s", absolutePath,
        this.currentDir, sessionModel.getConfiguration()));

    String motive = this.sessionModel.getMotive();
    if (ConfigUtils.isMotiveRequired()) {
      motive = DialogUtils.showMotiveInputDialog(motive);
      if (motive == null) {
        // annulation
        return;
      }
      this.sessionModel.setMotive(motive);
    }

    long uploadId = TransferUtils.getUniqueId();
    File file = new File(absolutePath);
    FileModel fileSrc = new FileModel(file.getName(),
        file.getParentFile().getAbsolutePath(), null, file.length(), null);

    FileModel fileDest = new FileModel(file.getName(), this.currentDir, null, 0,
        null);
    try {
      MainEventBus.getInstance().post(new UploadEvent(uploadId, fileSrc,
          fileDest, TransferEvent.Status.RUNNING));

      this.sessionManager.uploadFile(this.sessionModel, absolutePath,
          this.currentDir, motive);
      MainEventBus.getInstance().post(new UploadEvent(uploadId, fileSrc,
          fileDest, TransferEvent.Status.SUCCESS));
      this.refreshContent();

    } catch (SessionException e) {
      MainEventBus.getInstance().post(new UploadEvent(uploadId, fileSrc,
          fileDest, TransferEvent.Status.FAILURE));
      DialogUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.file.upload.error.message", absolutePath),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.file.upload.error.title"));
      LoggerUtils.error(String.format("Upload %s to %s failed for %s",
          absolutePath, this.currentDir, sessionModel.getConfiguration()));
    }
  }

  @Override
  public void onRequestPath(String path) {
    this.moveToDir(path);
  }

  @Override
  public void onMakeDirEvent(String text) {
    LoggerUtils.debug(String.format("Creating directory %s in %s for %s", text,
        this.currentDir, sessionModel.getConfiguration()));
    try {
      // clear streams
      if (this.contentStreams.getRight().available() > 0) {
        this.contentStreams.getRight()
            .skip(this.contentStreams.getRight().available());
      }
      // send mkdir command
      this.contentStreams.getLeft().write(("mkdir " + text + "\n").getBytes());
      this.contentStreams.getLeft().flush();
      // wait for command execution
      SessionUtils.waitFor(this.contentStreams.getRight(), endOfCmdPattern,
          this.sessionModel.getConfiguration().getCommandTimeout());

    } catch (IOException e) {
      DialogUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.action.mkdir.error.message", text),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.mkdir.error.title"));
      LoggerUtils.error(String.format("MakeDir %s in %s failed for %s", text,
          this.currentDir, sessionModel.getConfiguration()));
    } catch (InterruptedException e) {
      DialogUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.action.mkdir.error.message", text),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.mkdir.error.title"));
      LoggerUtils.error(String.format("MakeDir %s in %s failed for %s", text,
          this.currentDir, sessionModel.getConfiguration()));
    } catch (TimeoutException e) {
      DialogUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.action.mkdir.error.message", text),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.mkdir.error.title"));
      LoggerUtils.error(String.format("MakeDir %s in %s failed for %s", text,
          this.currentDir, sessionModel.getConfiguration()));
    }
  }

  @Override
  public void onDownloadSelectedContent(ContentModel content,
      boolean sameName) {
    if (content != null) {
      if (content instanceof FileModel) {
        if (sameName) {
          this.downloadContent((FileModel) content, null);
        } else {
          this.view.openDownloadFileChooser((FileModel) content);
        }
      } else {
        DialogUtils.showError(
            PropertiesUtils.getViewProperty(
                "scpbrowser.dialog.file.download.folder.error.message"),
            PropertiesUtils.getViewProperty(
                "scpbrowser.dialog.file.download.error.title"));
      }
    }
  }

  @Override
  public void onDeleteSelectedContent(ContentModel content) {
    LoggerUtils.debug(String.format("Remove content %s in %s for %s",
        content.getName(), this.currentDir, sessionModel.getConfiguration()));
    try {
      // clear streams
      if (this.contentStreams.getRight().available() > 0) {
        this.contentStreams.getRight()
            .skip(this.contentStreams.getRight().available());
      }
      // send mkdir command
      if (content instanceof FolderModel) {
        this.contentStreams.getLeft()
            .write(("rm -r " + ((FolderModel) content).getFullPath() + "\n")
                .getBytes());
      } else {
        this.contentStreams.getLeft().write(
            ("rm " + ((FileModel) content).getFullPath() + "\n").getBytes());
      }
      this.contentStreams.getLeft().flush();
      // wait for command execution
      String result = SessionUtils.readUntil(this.contentStreams.getRight(),
          endOfCmdPattern,
          this.sessionModel.getConfiguration().getCommandTimeout());
      if (result.contains("rm: cannot")) {
        DialogUtils.showError(
            PropertiesUtils.getViewProperty(
                "scpbrowser.dialog.file.remove.error.message",
                content.getFullPath()),
            PropertiesUtils
                .getViewProperty("scpbrowser.dialog.file.remove.error.title"));
      }

      refreshContent();
    } catch (IOException e) {
      DialogUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.action.rm.error.message", content.getName()),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.rm.error.title"));
      LoggerUtils.error(
          String.format("rm %s in %s failed for %s", content.getFullPath(),
              this.currentDir, sessionModel.getConfiguration()));
    } catch (InterruptedException e) {
      DialogUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.action.rm.error.message", content.getName()),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.rm.error.title"));
      LoggerUtils.error(
          String.format("rm %s in %s failed for %s", content.getFullPath(),
              this.currentDir, sessionModel.getConfiguration()));
    } catch (TimeoutException e) {
      DialogUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.action.rm.error.message", content.getName()),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.rm.error.title"));
      LoggerUtils.error(
          String.format("rm %s in %s failed for %s", content.getFullPath(),
              this.currentDir, sessionModel.getConfiguration()));
    }
  }

  @Override
  public void onMoveSelectedContent(ContentModel content,
      ContentModel newContent) {
    LoggerUtils.debug(String.format("Move content %s in %s to %s for %s",
        content.getName(), this.currentDir, newContent.getFullPath(),
        sessionModel.getConfiguration()));
    try {
      // clear streams
      if (this.contentStreams.getRight().available() > 0) {
        this.contentStreams.getRight()
            .skip(this.contentStreams.getRight().available());
      }
      // send mkdir command
      if (content instanceof FolderModel) {
        this.contentStreams.getLeft()
            .write(("mv -r" + ((FolderModel) content).getFullPath() + " "
                + newContent.getFullPath() + "\n").getBytes());
      } else {
        this.contentStreams.getLeft()
            .write(("mv " + ((FileModel) content).getFullPath() + " "
                + newContent.getFullPath() + "\n").getBytes());
      }
      this.contentStreams.getLeft().flush();
      // wait for command execution
      String result = SessionUtils.readUntil(this.contentStreams.getRight(),
          endOfCmdPattern,
          this.sessionModel.getConfiguration().getCommandTimeout());
      if (result.contains("mv: cannot")) {
        DialogUtils.showError(PropertiesUtils.getViewProperty(
            "scpbrowser.dialog.file.move.error.message", content.getFullPath()),
            PropertiesUtils
                .getViewProperty("scpbrowser.dialog.file.move.error.title"));
      }
      refreshContent();
    } catch (IOException e) {
      DialogUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.action.mv.error.message",
              content.getFullPath(), newContent.getFullPath()),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.mv.error.title"));
      LoggerUtils.error(
          String.format("mv %s to %s failed for %s", content.getFullPath(),
              newContent.getFullPath(), sessionModel.getConfiguration()));
    } catch (InterruptedException e) {
      DialogUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.action.mv.error.message",
              content.getFullPath(), newContent.getFullPath()),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.mv.error.title"));
      LoggerUtils.error(
          String.format("mv %s to %s failed for %s", content.getFullPath(),
              newContent.getFullPath(), sessionModel.getConfiguration()));
    } catch (TimeoutException e) {
      DialogUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.action.mv.error.message",
              content.getFullPath(), newContent.getFullPath()),
          PropertiesUtils
              .getViewProperty("scpbrowser.dialog.action.mv.error.title"));
      LoggerUtils.error(
          String.format("mv %s to %s failed for %s", content.getFullPath(),
              newContent.getFullPath(), sessionModel.getConfiguration()));
    }
  }

  @Override
  public void onOpenSelectedContent(ContentModel content) {
    if (content != null) {
      if (content instanceof FolderModel) {
        this.moveToDir((FolderModel) content);
      }
    }
  }

  @Override
  public void onDownloadEvent(FileModel fileToDownload, File destination) {
    if (fileToDownload != null && destination != null) {
      this.downloadContent(fileToDownload, destination);
    }
  }

}
