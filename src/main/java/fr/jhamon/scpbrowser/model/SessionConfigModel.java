
package fr.jhamon.scpbrowser.model;

import fr.jhamon.scpbrowser.utils.Constantes;

/**
 * Session configuration
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class SessionConfigModel implements Comparable<SessionConfigModel> {

  private String name;

  private String server;

  private String username;

  private String password;

  private int connectTimeout;

  private int commandTimeout;

  private int downloadTimeout;

  private int uploadTimeout;

  /**
   * Create a new configuration with default timeouts
   *
   * @param name     configuration name
   * @param server   server url
   * @param username username
   * @param password user password
   */
  public SessionConfigModel(String name, String server, String username,
      String password) {
    super();
    this.name = name;
    this.server = server;
    this.username = username;
    this.password = password;
  }

  /**
   * @param name            configuration name
   * @param server          server url
   * @param username        username
   * @param password        user password
   * @param connectTimeout  connection timeout (ms)
   * @param commandTimeout  command timeout (ms)
   * @param downloadTimeout download timeout (ms)
   * @param uploadTimeout   upload timeout (ms)
   */
  public SessionConfigModel(String name, String server, String username,
      String password, int connectTimeout, int commandTimeout,
      int downloadTimeout, int uploadTimeout) {
    super();
    this.name = name;
    this.server = server;
    this.username = username;
    this.password = password;
    this.connectTimeout = connectTimeout;
    this.commandTimeout = commandTimeout;
    this.downloadTimeout = downloadTimeout;
    this.uploadTimeout = uploadTimeout;
  }

  /**
   * @return the downloadTimeout, {@link Constantes.TIMEOUT_DOWNLOAD} if not set
   */
  public int getDownloadTimeout() {
    if (this.downloadTimeout > 0) {
      return this.downloadTimeout;
    } else {
      return Constantes.TIMEOUT_DOWNLOAD;
    }
  }

  /**
   * @param downloadTimeout the downloadTimeout to set
   */
  public void setDownloadTimeout(int downloadTimeout) {
    this.downloadTimeout = downloadTimeout;
  }

  /**
   * @return the connectTimeout, {@link Constantes.TIMEOUT_CONNECT} if not set
   */
  public int getConnectTimeout() {
    if (this.connectTimeout > 0) {
      return this.connectTimeout;
    } else {
      return Constantes.TIMEOUT_CONNECT;
    }
  }

  /**
   * @param connectTimeout the connectTimeout to set
   */
  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  /**
   * @return the commandTimeout, {@link Constantes.TIMEOUT_COMMAND} if not set
   */
  public int getCommandTimeout() {
    if (this.commandTimeout > 0) {
      return this.commandTimeout;
    } else {
      return Constantes.TIMEOUT_COMMAND;
    }
  }

  /**
   * @param commandTimeout the commandTimeout to set
   */
  public void setCommandTimeout(int commandTimeout) {
    this.commandTimeout = commandTimeout;
  }

  /**
   * @return the uploadTimeout, {@link Constantes.TIMEOUT_UPLOAD} if not set
   */
  public int getUploadTimeout() {
    if (this.uploadTimeout > 0) {
      return this.uploadTimeout;
    } else {
      return Constantes.TIMEOUT_UPLOAD;
    }
  }

  /**
   * @param uploadTimeout the uplaodTimeout to set
   */
  public void setUploadTimeout(int uploadTimeout) {
    this.uploadTimeout = uploadTimeout;
  }

  @Override
  public String toString() {
    return String.format("%s,%s,%s,%s,%d,%d,%d,%d", this.name, this.server,
        this.username, this.password, this.getConnectTimeout(),
        this.getCommandTimeout(), this.getDownloadTimeout(),
        this.getUploadTimeout());
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + commandTimeout;
    result = prime * result + connectTimeout;
    result = prime * result + downloadTimeout;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((password == null) ? 0 : password.hashCode());
    result = prime * result + ((server == null) ? 0 : server.hashCode());
    result = prime * result + uploadTimeout;
    result = prime * result + ((username == null) ? 0 : username.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SessionConfigModel other = (SessionConfigModel) obj;
    if (commandTimeout != other.commandTimeout) {
      return false;
    }
    if (connectTimeout != other.connectTimeout) {
      return false;
    }
    if (downloadTimeout != other.downloadTimeout) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (password == null) {
      if (other.password != null) {
        return false;
      }
    } else if (!password.equals(other.password)) {
      return false;
    }
    if (server == null) {
      if (other.server != null) {
        return false;
      }
    } else if (!server.equals(other.server)) {
      return false;
    }
    if (uploadTimeout != other.uploadTimeout) {
      return false;
    }
    if (username == null) {
      if (other.username != null) {
        return false;
      }
    } else if (!username.equals(other.username)) {
      return false;
    }
    return true;
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
   * @return the server
   */
  public String getServer() {
    return server;
  }

  /**
   * @param server the server to set
   */
  public void setServer(String server) {
    this.server = server;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }
  
  @Override
  public int compareTo(SessionConfigModel o) {
    return this.name==null ? -1 : this.name.compareTo(o.getName());
  }
}
