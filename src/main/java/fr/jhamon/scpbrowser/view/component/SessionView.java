package fr.jhamon.scpbrowser.view.component;

import fr.jhamon.scpbrowser.view.component.event.handler.ContentEventHandler;

/**
 * Session view
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public interface SessionView {

  /**
   * @return the component used to visualise the remote server's content
   */
  ContentViewer getContentTable();

  /**
   * @param path current remote server directory
   */
  void setPath(String path);

  /**
   * Close the view
   */
  void close();

  /**
   * Open a view to pick the file to upload
   */
  void openUploadFileChooser();

  /**
   * Set the view event handler
   *
   * @param eventHandler
   */
  void setEventHandler(ContentEventHandler eventHandler);

}
