package fr.jhamon.scpbrowser.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Logging utility
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class LoggerUtils {

  private static final Logger LOGGER = LogManager.getLogger(LoggerUtils.class);

  /**
   * {@link Logger#fatal(String)}
   */
  public static void fatal(String message) {
    LOGGER.fatal(message);
  }

  /**
   * {@link Logger#fatal(String, Exception)}
   */
  public static void fatal(String message, Exception e) {
    LOGGER.fatal(message, e);
  }

  /**
   * {@link Logger#error(String, Exception)}
   */
  public static void error(String message, Exception e) {
    LOGGER.error(message, e);
  }

  /**
   * {@link Logger#error(String)}
   */
  public static void error(String message) {
    LOGGER.error(message);
  }

  /**
   * {@link Logger#debug(String)}
   */
  public static void debug(String message) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(message);
    }
  }
}
