package fr.jhamon.scpbrowser.view.component.event.handler;

import fr.jhamon.scpbrowser.model.ContentModel;

/**
 * Handler interface to manage the user action related to content
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public interface ContentEventHandler {

  /**
   * Handle upload request
   *
   * @param absolutePath file path to upload
   */
  void onUploadEvent(String absolutePath);

  /**
   * Handle content selection event
   *
   * @param content content to download or to move to
   */
  public void onSelectedContent(ContentModel content);

  /**
   * Handle user paht change request
   *
   * @param path path to go to
   */
  void onRequestPath(String path);

  /**
   * Handler user folder creation request
   *
   * @param text folder to create
   */
  void onMakeDirEvent(String text);

}
