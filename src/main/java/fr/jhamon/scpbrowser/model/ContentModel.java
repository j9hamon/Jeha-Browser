package fr.jhamon.scpbrowser.model;

import java.util.Date;

/**
 * Describe basic content on a server
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class ContentModel {

  protected String name;

  protected String path;

  protected Date lastModifiedDate;

  /**
   * @param name             content name
   * @param path             content path
   * @param lastModifiedDate last date of modification
   */
  public ContentModel(String name, String path, Date lastModifiedDate) {
    super();
    this.name = name;
    this.path = path;
    this.lastModifiedDate = lastModifiedDate;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the path
   */
  public String getPath() {
    return path;
  }

  /**
   * @param path the path to set
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * @return the lastModifiedDate
   */
  public Date getLastModifiedDate() {
    return lastModifiedDate;
  }

  /**
   * @param lastModifiedDate the lastModifiedDate to set
   */
  public void setLastModifiedDate(Date lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }



  /**
   * @return the content full path made of content path and name
   */
  public String getFullPath() {
    return new StringBuilder(this.path).append("/").append(this.name)
        .toString();
  }

}
