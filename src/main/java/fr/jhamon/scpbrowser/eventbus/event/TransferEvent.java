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

  private FileModel fileSrc;

  private FileModel fileDest;

  public TransferEvent(long id) {
    this.id = id;
  }

  public TransferEvent(long id, FileModel fileSrc,  FileModel fileDest, Status status) {
    this.id = id;
    this.fileSrc = fileSrc;
    this.fileDest = fileDest;
    this.status = status;
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
   * @return the fileSrc
   */
  public FileModel getFileSrc() {
    return fileSrc;
  }

  /**
   * @param file the fileSrc to set
   */
  public void setFileSrc(FileModel file) {
    this.fileSrc = file;
  }

  public long getId() {
    return id;
  }

  /**
   * @return the fileDest
   */
  public FileModel getFileDest() {
    return fileDest;
  }

  /**
   * @param fileDest the fileDest to set
   */
  public void setFileDest(FileModel fileDest) {
    this.fileDest = fileDest;
  }

}
