package fr.jhamon.scpbrowser.eventbus;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

import fr.jhamon.scpbrowser.utils.LoggerUtils;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class DeadEventListener {

  @Subscribe
  public void handleDeadEvent(DeadEvent deadEvent) {
    LoggerUtils.debug("Dead event: " + deadEvent);
  }
}
