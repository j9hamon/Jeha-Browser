package fr.jhamon.scpbrowser.model;

import java.util.Date;

import fr.jhamon.scpbrowser.utils.TransferUtils;

/**
 * Specifically describe a file element
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class FileModel extends ContentModel {

  private long size;

  private FileType type;

  /**
   * @param name             content name
   * @param path             content path
   * @param lastModifiedDate last date of modification
   * @param size             content size
   * @param type             content type (extension)
   */
  public FileModel(String name, String path, Date lastModifiedDate, long size,
      FileType type) {
    super(name, path, lastModifiedDate);
    this.size = size;
    this.type = type;
  }

  /**
   * @return the size as a readable string with unit
   */
  public String getFormatedSize() {
    return TransferUtils.getFormatedSize(this.size);
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
   * @return the type
   */
  public FileType getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(FileType type) {
    this.type = type;
  }
}
