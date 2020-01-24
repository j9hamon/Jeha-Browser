package fr.jhamon.scpbrowser.view.component.event.handler;

import java.io.File;

import fr.jhamon.scpbrowser.model.ContentModel;
import fr.jhamon.scpbrowser.model.FileModel;

/**
 * Handler interface to manage the user action related to content
 *
 * @author J.Hamon Copyright 2019 J.Hamon
 *
 */
public interface ContentEventHandler {

  /**
   * Handle upload request
   *
   * @param absolutePath file path to upload
   */
  void onUploadEvent(String absolutePath, String motive);

  /**
   * Handle download request
   *
   * @param fileToDownload  file to download
   * @param destinationFile destination
   */
  void onDownloadEvent(FileModel fileToDownload, File destinationFile);

  /**
   * Handle content selection navigation event
   *
   * @param content content to move to
   */
  public void onOpenSelectedContent(ContentModel content);

  /**
   * Handle content selection download event
   *
   * @param content content to download
   */
  public void onDownloadSelectedContent(ContentModel content, boolean sameName);

  /**
   * Handle content selection deletion event
   *
   * @param content content to remove
   */
  public void onDeleteSelectedContent(ContentModel content, String motive);

  /**
   * Handle content rename event
   *
   * @param content    content to move
   * @param newContent content with the new name
   */
  public void onMoveSelectedContent(ContentModel content,
      ContentModel newContent, String motive);

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
  void onMakeDirEvent(String text, String motive);

}
