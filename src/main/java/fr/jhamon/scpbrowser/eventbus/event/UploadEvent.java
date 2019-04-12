package fr.jhamon.scpbrowser.eventbus.event;

import fr.jhamon.scpbrowser.model.FileModel;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class UploadEvent extends TransferEvent {

  public UploadEvent(long id) {
    super(id);
  }

  public UploadEvent(long id, FileModel file, Status status,
      String destination) {
    super(id, file, status, destination);
  }

}
