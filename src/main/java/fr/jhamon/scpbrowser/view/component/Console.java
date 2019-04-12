package fr.jhamon.scpbrowser.view.component;

import com.jcraft.jcterm.Connection;

/**
 * Component to interact on the remote server, emulating a shell console
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public interface Console {

  /**
   * request focus on component
   */
  void requestFocus();

  /**
   * Start the component
   *
   * @param connection connection to use
   */
  void start(Connection connection);

  /**
   * Close the view
   */
  void close();

  /**
   * Set the view title
   *
   * @param title title to set
   */
  void setTitle(String title);

  /**
   * @return true if the component can be displayed
   */
  boolean isDisplayable();

}
