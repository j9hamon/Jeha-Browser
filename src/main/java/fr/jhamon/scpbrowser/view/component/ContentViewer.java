package fr.jhamon.scpbrowser.view.component;

import java.util.List;

import fr.jhamon.scpbrowser.model.ContentModel;
import fr.jhamon.scpbrowser.view.component.event.handler.ContentEventHandler;

/**
 * Component used to visualize the remote server's content
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public interface ContentViewer {

  /**
   * Clear the view
   */
  void clear();

  /**
   * Set the content
   *
   * @param content list of content to set
   */
  void setContent(List<ContentModel> content);

  /**
   * @return the content selected by user
   */
  ContentModel getSelectectedContent();

  /**
   * Set the view event handler
   *
   * @param sessionPresenter
   */
  void setEventHandler(ContentEventHandler sessionPresenter);

}
