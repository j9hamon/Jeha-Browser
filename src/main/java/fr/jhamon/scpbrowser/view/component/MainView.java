package fr.jhamon.scpbrowser.view.component;

import fr.jhamon.scpbrowser.Context;
import fr.jhamon.scpbrowser.view.component.event.handler.ManagementUserEventHandler;
import fr.jhamon.scpbrowser.view.component.impl.FooterBar;
import fr.jhamon.scpbrowser.view.component.impl.ToolBar;

/**
 * Application main view
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public interface MainView {

  /**
   * create a new session view
   *
   * @param title session view title
   * @return a session view
   */
  SessionView createNewSessionView(String title, String tooltip);

  /**
   * remove a session view
   *
   * @param view view to remove
   */
  void removeSessionView(SessionView view);

  /**
   * Set focus on a session view
   *
   * @param view view to set focus on
   */
  void showSessionView(SessionView view);

  /**
   * Set the main view event handler
   *
   * @param handler
   */
  void setEventHandler(ManagementUserEventHandler handler);

  /**
   * Start the view
   *
   * @param context application context
   */
  void start(Context context);

  /**
   * Get the active (focused) session view
   *
   * @return
   */
  SessionView getActiveView();

  /**
   * Close the main view
   */
  void close();

  /**
   * @return the application toolbar
   */
  ToolBar getToolbar();

  /**
   * @return the application footerbar
   */
  FooterBar getFooterbar();

  /**
   * @return the TransferView
   */
  TransferView getTransferView();

}
