package fr.jhamon.scpbrowser;

import fr.jhamon.scpbrowser.utils.ConfigUtils;

/**
 * Application context
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class Context {

  private char[] passphrase = new char[0];

  private String user = ConfigUtils.getConfigProperty("racf");

  public void setPassphrase(char[] sshPassphrase) {
    this.passphrase = sshPassphrase;
  }

  public char[] getPassphrase() {
    return this.passphrase;
  }

  public String getUser() {
    return user;
  }

}
