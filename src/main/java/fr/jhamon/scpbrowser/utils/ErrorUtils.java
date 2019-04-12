package fr.jhamon.scpbrowser.utils;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Utility to create error dialogs
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class ErrorUtils {

  /**
   * Open a new dialog. Close the application on close
   *
   * @param message dialog message
   * @param title   dialog title
   */
  public static void showFatalError(final String message, final String title) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        JOptionPane.showOptionDialog(null, message, title,
            JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null,
            new String[] { "OK" }, 0);
        System.exit(1);
      }
    });
  }

  /**
   * Open a new dialog
   *
   * @param message dialog message
   * @param title   dialog title
   */
  public static void showError(final String message, final String title) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        JOptionPane.showOptionDialog(null, message, title,
            JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null,
            new String[] { "OK" }, 0);
      }
    });
  }

}
