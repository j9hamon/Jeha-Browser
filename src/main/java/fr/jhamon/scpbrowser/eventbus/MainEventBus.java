package fr.jhamon.scpbrowser.eventbus;

import com.google.common.eventbus.EventBus;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class MainEventBus extends EventBus {

  private static MainEventBus instance = null;

  private MainEventBus() {
    super();
    this.register(new DeadEventListener());
  }

  public static MainEventBus getInstance() {
    if (instance == null) {
      instance = new MainEventBus();
    }
    return instance;
  }

}
