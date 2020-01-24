package fr.jhamon.scpbrowser.model;

import com.jcraft.jsch.Session;

import fr.jhamon.scpbrowser.utils.Constantes;

/**
 * Object containing a session configuration and the associated {@link Session}
 *
 * @author J.Hamon Copyright 2019 J.Hamon
 *
 */
public class SessionModel {

  private Session sshSession;

  private SessionConfigModel configuration;

  private String motive = null;

  /**
   * Create a new instance with no {@link Session}
   *
   * @param configuration
   */
  public SessionModel(SessionConfigModel configuration) {
    super();
    this.sshSession = null;
    this.configuration = configuration;
    this.motive = Constantes.DEFAULT_MOTIVE;
  }

  /**
   * @param session
   * @param configuration
   */
  public SessionModel(Session session, SessionConfigModel configuration) {
    super();
    this.sshSession = session;
    this.configuration = configuration;
    this.motive = Constantes.DEFAULT_MOTIVE;
  }

  /**
   * @param session
   * @param configuration
   */
  public SessionModel(Session session, SessionConfigModel configuration,
      String motive) {
    super();
    this.sshSession = session;
    this.configuration = configuration;
    this.motive = motive;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((configuration == null) ? 0 : configuration.hashCode());
    result = prime * result
        + ((sshSession == null) ? 0 : sshSession.hashCode());
    return result;
  }

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
    SessionModel other = (SessionModel) obj;
    if (configuration == null) {
      if (other.configuration != null) {
        return false;
      }
    } else if (!configuration.equals(other.configuration)) {
      return false;
    }
    if (sshSession == null) {
      if (other.sshSession != null) {
        return false;
      }
    } else if (!sshSession.equals(other.sshSession)) {
      return false;
    }
    return true;
  }

  /**
   * @return the sshSession
   */
  public Session getSshSession() {
    return sshSession;
  }

  /**
   * @param sshSession the sshSession to set
   */
  public void setSshSession(Session sshSession) {
    this.sshSession = sshSession;
  }

  /**
   * @return the configuration
   */
  public SessionConfigModel getConfiguration() {
    return configuration;
  }

  /**
   * @param configuration the configuration to set
   */
  public void setConfiguration(SessionConfigModel configuration) {
    this.configuration = configuration;
  }

  public String getMotive() {
    return motive;
  }

  public void setMotive(String motive) {
    this.motive = motive;
  }

}
