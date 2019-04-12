package fr.jhamon.scpbrowser.model;

import java.util.Date;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class TransferModel {

  public enum Type {
    DOWNLOAD, UPLOAD
  };

  private Type type;

  private String fileName;

  private String localDir;

  private String remoteDir;

  private long size;

  private Date startDate;

  private Date endDate;

  /**
   * @return the localDir
   */
  public String getLocalDir() {
    return localDir;
  }

  /**
   * @param localDir the localDir to set
   */
  public void setLocalDir(String localDir) {
    this.localDir = localDir;
  }

  /**
   * @return the remoteDir
   */
  public String getRemoteDir() {
    return remoteDir;
  }

  /**
   * @param remoteDir the remoteDir to set
   */
  public void setRemoteDir(String remoteDir) {
    this.remoteDir = remoteDir;
  }

  /**
   * @return the size
   */
  public long getSize() {
    return size;
  }

  /**
   * @param size the size to set
   */
  public void setSize(long size) {
    this.size = size;
  }

  /**
   * @return the startDate
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * @param startDate the startDate to set
   */
  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  /**
   * @return the endDate
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * @param endDate the endDate to set
   */
  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  /**
   * @return the type
   */
  public Type getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(Type type) {
    this.type = type;
  }

  /**
   * @return the fileName
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * @param fileName the fileName to set
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

}
