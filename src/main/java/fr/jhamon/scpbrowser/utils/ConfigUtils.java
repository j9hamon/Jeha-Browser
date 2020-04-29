package fr.jhamon.scpbrowser.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import fr.jhamon.scpbrowser.model.SessionConfigModel;

/**
 * Configuration utilities
 *
 * @author J.Hamon Copyright 2019 J.Hamon
 *
 */
public class ConfigUtils {

  private static final INIConfiguration CONFIG_FILE;

  static {
    INIConfiguration conf;
    try {
      conf = new Configurations().ini("config.ini");
      // create default conf file;
    } catch (ConfigurationException e) {
      conf = null;
      LoggerUtils.fatal("Configuration fatal error", e);
      System.exit(1);
    }
    CONFIG_FILE = conf;
    if (conf != null) {
      saveConfig();
    }
  }

  public static boolean isMotiveRequired() {
    return CONFIG_FILE.getBoolean("motiveRequired", true);
  }

  /**
   * @param property property key to find
   * @return the property value
   */
  public static String getConfigProperty(String property) {
    return (String) CONFIG_FILE.getProperty(property);
  }

  /**
   * @param property property key to find
   * @return the property value
   */
  public static String getConfigProperty(String property, String defaultVal) {
    String val = getConfigProperty(property);
    if (val == null) {
      val = defaultVal;
    }
    return val;
  }

  /**
   * @return the list of session configurations stored in the configuration file
   */
  public static List<SessionConfigModel> getSessionConfigs() {
    List<SessionConfigModel> sessionList = new ArrayList<SessionConfigModel>();
    List<Object> strSessionList = CONFIG_FILE.getList("sessions.session");
    for (Object strSession : strSessionList) {
      String[] parts = ((String) strSession).split(",");
      if (parts.length == 4) {
        sessionList.add(
            new SessionConfigModel(parts[0], parts[1], parts[2], parts[3]));
      } else if (parts.length == 8) {
        try {
          sessionList.add(new SessionConfigModel(parts[0], parts[1], parts[2],
              parts[3], Integer.valueOf(parts[4]), Integer.valueOf(parts[5]),
              Integer.valueOf(parts[6]), Integer.valueOf(parts[7])));
        } catch (NumberFormatException e) {
          System.out.println("invalid: " + strSession);
        }
      } else {
        System.out.println("invalid: " + strSession);
      }
    }
    Collections.sort(sessionList);
    return sessionList;
  }

  /**
   * @return the list of session configurations stored in the configuration file
   */
  public static void setSessionConfigs(List<SessionConfigModel> list) {
    CONFIG_FILE.clearProperty("sessions.session");
    List<String> stringSessions = new ArrayList<String>();
    for (SessionConfigModel session : list) {
      stringSessions.add(session.toString());
    }
    CONFIG_FILE.addProperty("sessions.session", stringSessions);
    saveConfig();
  }

  /**
   * Add a new session configuration in the configuration file
   *
   * @param model session configuration to save
   */
  public static void addSessionConfig(SessionConfigModel model) {
    CONFIG_FILE.addProperty("sessions.session", model.toString());
    saveConfig();
  }

  /**
   * Save the actual configuration to a file
   */
  public static void saveConfig() {
    try {
      CONFIG_FILE.write(new FileWriter("config.ini"));
    } catch (ConfigurationException e) {
      LoggerUtils.error("Configuration save error", e);
    } catch (IOException e) {
      LoggerUtils.error("Configuration save error", e);
    }
  }

  /**
   * @return the download directory as a windows drive path
   */
  public static String getDownloadDirectory() {
    String directory = getConfigProperty("downloadDir");
    directory = directory.replace("${user.home}",
        System.getProperty("user.home"));
    directory = directory.replace("\\", "/");
    if (!directory.endsWith("/") && !directory.endsWith("\\")) {
      directory += File.separator;
    }
    return directory;
  }

  /**
   * @return the download directory as a cygwin drive path
   */
  public static String getUnixDownloadDirectory() {
    String directory = getDownloadDirectory();
    Matcher driveMatcher = Constantes.WINDOWS_DRIVE_PATTERN.matcher(directory);
    if (driveMatcher.find()) {
      directory = directory.replace(driveMatcher.group(1), String.format(
          Constantes.UNIX_DRIVE_TEMPLATE, driveMatcher.group(2).toLowerCase()));
    }
    return directory;
  }
}
