package fr.jhamon.scpbrowser.utils;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

/**
 * Utility to manipulate {@link ImageIcon}
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class IconUtils {

  /**
   * Load the icon image
   *
   * @param path path to image
   * @return an ImageIcon
   */
  public static ImageIcon createImageIcon(String path) {
    URL imgURL = new IconUtils().getClass().getResource(path);
    if (imgURL != null) {
      return new ImageIcon(imgURL);
    } else {
      System.err.println("Couldn't find file: " + path);
      return null;
    }
  }

  /**
   * Load and resize the icon image
   *
   * @param path   path to image
   * @param width
   * @param height
   * @return a resized ImageIcon
   */
  public static ImageIcon createImageIcon(String path, int width, int height) {
    ImageIcon icon = createImageIcon(path);
    if (icon != null) {
      Image image = icon.getImage();
      Image newimg = image.getScaledInstance(width, height,
          java.awt.Image.SCALE_SMOOTH);
      icon = new ImageIcon(newimg);
    }
    return icon;
  }
}
