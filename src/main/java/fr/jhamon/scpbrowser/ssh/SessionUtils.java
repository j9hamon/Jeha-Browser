package fr.jhamon.scpbrowser.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import fr.jhamon.scpbrowser.model.SessionConfigModel;
import fr.jhamon.scpbrowser.model.exception.TimeoutException;
import fr.jhamon.scpbrowser.utils.ConfigUtils;
import fr.jhamon.scpbrowser.utils.LoggerUtils;
import fr.jhamon.scpbrowser.utils.TransferUtils;

/**
 * Utility class for sessions
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class SessionUtils {

  public static final String SHELL_LIST_CMD = "ls -lagpoQB --time-style=full-iso\n";
  public static final String SHELL_PATH_CMD = "pwd\n";

  /**
   * Check the ssh passphrase validity by connecting to localhost
   *
   * @param passphrase
   * @return true if the passphrase is valid
   */
  public static boolean testPassphrase(char[] passphrase) {
    try {
      JSch jsch = new JSch();
      String user = ConfigUtils.getConfigProperty("racf");
      jsch.addIdentity(ConfigUtils.getConfigProperty("sshKey"),
          new String(passphrase));
      Session session = jsch.getSession(user, "localhost", 22);
      Properties config = new Properties();
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);
      session.connect();
      boolean result = session.isConnected();
      session.disconnect();
      return result;
    } catch (JSchException e) {
      LoggerUtils.error("Test passphrase", e);
      return false;
    }
  }

  /**
   * Build a SCP command to download a file from a server
   *
   * @param sessionConfModel server configuration
   * @param filePath         path to download
   * @param destFilePath     destination path
   * @return the shell command
   */
  public static String buildScpDownloadCommand(
      SessionConfigModel sessionConfModel, String filePath, String destFilePath) {
    StringBuilder builder = new StringBuilder("scp#")
        .append(sessionConfModel.getUsername()).append("@")
        .append(sessionConfModel.getServer()).append(":").append(filePath)
        .append(" ").append(ConfigUtils.getConfigProperty("racf")).append(":")
        .append(TransferUtils.getCygwinPath(destFilePath));
    return builder.toString();
  }

  /**
   * Build a SCP command to upload a file from a server
   *
   * @param sessionConfModel server configuration
   * @param filePath         file to upload
   * @param destDir          destination directory on the remote server1
   * @return the shell command
   */
  public static String buildScpUploadCommand(
      SessionConfigModel sessionConfModel, String filePath, String destDir) {
    StringBuilder builder = new StringBuilder("scp#")
        .append(ConfigUtils.getConfigProperty("racf")).append(":")
        .append(filePath).append(" ").append(sessionConfModel.getUsername())
        .append("@").append(sessionConfModel.getServer()).append(":")
        .append(destDir);
    return builder.toString();
  }

  /**
   * Build a SSH connection command
   *
   * @param sessionConfModel server configuration
   * @return the shell command
   */
  public static String buildSshCommand(SessionConfigModel sessionConfModel) {
    StringBuilder builder = new StringBuilder("ssh#")
        .append(sessionConfModel.getUsername()).append("@")
        .append(sessionConfModel.getServer());

    return builder.toString();
  }

  /**
   * Read on a stream until matching pattern is found
   *
   * @param stream          stream to read on
   * @param endOfCmdPattern pattern to match
   * @param timeout         maximum reading time allowed
   * @return the read string
   * @throws InterruptedException
   * @throws IOException
   * @throws TimeoutException     thrown if time limit is exceeded
   * @throws ConnectionClosedException
   */
  public static String readUntil(InputStream stream, String endOfCmdPattern,
      int timeout) throws InterruptedException, IOException, TimeoutException {
    StringBuilder cmdOutput = new StringBuilder();
    byte[] pattern = endOfCmdPattern.getBytes(StandardCharsets.UTF_8);
    int sizeOfPattern = pattern.length;
    int pos = 0;
    long maxTimeMillis = System.currentTimeMillis() + timeout;
    boolean timedOut = false;
    while (pos < sizeOfPattern && !timedOut) {
      if (stream.available() > 0) {
        byte readByte = (byte) stream.read();
        if (readByte == pattern[pos]) {
          pos++;
        } else {
          pos = 0;
        }
        cmdOutput.append((char) readByte);
      } else {
        Thread.sleep(20);
      }
      timedOut = System.currentTimeMillis() > maxTimeMillis;
      if (timedOut) {
        throw new TimeoutException();
      }
    }

    return cmdOutput.toString()
        .replaceAll("\\x1B\\[(\\d{1,2}(;\\d{1,2})?m)|\\x1B\\[K", "");
  }

  /**
   * Read on a stream until matching pattern is found
   *
   * @param stream          stream to read on
   * @param endOfCmdPattern pattern to match
   * @param timeout         maximum reading time allowed
   * @return true if the pattern is found
   * @throws InterruptedException
   * @throws IOException
   * @throws TimeoutException     thrown if time limit is exceeded
   * @throws ConnectionClosedException
   */
  public static boolean waitFor(InputStream stream, String endOfCmdPattern,
      int timeout) throws InterruptedException, IOException, TimeoutException {
    byte[] pattern = endOfCmdPattern.getBytes();
    int sizeOfPattern = pattern.length;
    int pos = 0;
    long maxTimeMillis = System.currentTimeMillis() + timeout;
    boolean timedOut = false;
    while (pos < sizeOfPattern && !timedOut) {
      if (stream.available() > 0) {
        byte readByte = (byte) stream.read();
        if (readByte == pattern[pos]) {
          pos++;
        } else {
          pos = 0;
        }
      } else {
        Thread.sleep(50);
      }
      timedOut = System.currentTimeMillis() > maxTimeMillis;
      if (timedOut) {
        throw new TimeoutException();
      }
    }
    return pos == sizeOfPattern;
  }

}
