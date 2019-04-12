package fr.jhamon.scpbrowser.view.component.event.handler;

import fr.jhamon.scpbrowser.model.SessionConfigModel;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public interface ManagementUserEventHandler {

  public void onCloseSessionEvent();

  public void onNewSessionEvent();

  public void onRefreshSessionEvent();

  public void onUploadEvent();

  public void onChangeSessionEvent();

  public void onExitEvent();

  public void onStartSessionEvent(SessionConfigModel model);

  void onOpenConsoleEvent();
}
