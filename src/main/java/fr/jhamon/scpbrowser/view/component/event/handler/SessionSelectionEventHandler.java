package fr.jhamon.scpbrowser.view.component.event.handler;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public interface SessionSelectionEventHandler {

  void onStartNewSession();

  void onStartSavedSession();

  void onSaveSessionEvent();

}
