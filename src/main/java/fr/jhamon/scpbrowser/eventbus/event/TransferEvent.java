package fr.jhamon.scpbrowser.eventbus.event;

import fr.jhamon.scpbrowser.model.FileModel;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class TransferEvent {

  public static enum Status {
    RUNNING, FAILURE, SUCCESS
  }

  private long id;

  private Status status;

  private FileModel file;

  private String destination;

  public TransferEvent(long id) {
    this.id = id;
  }

  public TransferEvent(long id, FileModel file, Status status,
      String destination) {
    this.id = id;
    this.file = file;
    this.status = status;
    this.destination = destination;
  }

  /**
   * @return the status
   */
  public Status getStatus() {
    return status;
  }

  /**
   * @param status the status to set
   */
  public void setStatus(Status status) {
    this.status = status;
  }

  /**
   * @return the file
   */
  public FileModel getFile() {
    return file;
  }

  /**
   * @param file the file to set
   */
  public void setFile(FileModel file) {
    this.file = file;
  }

  public String getDestination() {
    return destination;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public long getId() {
    return id;
  }

}
