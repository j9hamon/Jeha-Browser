package fr.jhamon.scpbrowser.utils;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility to fetch the view properties
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class PropertiesUtils {

  private static final ResourceBundle VIEW_PROPERTIES_BUNDLE = ResourceBundle
      .getBundle("view");
  //  private static final ResourceBundle CONFIG_PROPERTIES_BUNDLE = ResourceBundle.getBundle("config");

  /**
   * @param property property key
   * @return property value
   */
  public static String getViewProperty(String property) {
    try {
      return VIEW_PROPERTIES_BUNDLE.getString(property);
    } catch (MissingResourceException e) {
      return null;
    }
  }

  /**
   * @param key    property key
   * @param params list of parameters
   * @return formatted property value
   */
  public static String getViewProperty(String key, Object... params) {
    try {
      return MessageFormat.format(getViewProperty(key), params);
    } catch (MissingResourceException e) {
      return null;
    }
  }

}
