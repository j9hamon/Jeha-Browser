package fr.jhamon.scpbrowser.view.component.impl;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import fr.jhamon.scpbrowser.utils.PropertiesUtils;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class MenuBar extends JMenuBar {

  private static final long serialVersionUID = 526733084962818486L;

  private JMenu sessionMenu;
  private JMenuItem newSessionButton;
  private JMenuItem quitCurrentSessionButton;
  private JMenuItem quitAllSessionButton;

  public MenuBar() {
    super();
    this.initComponents();
    this.build();
    this.setVisible(true);
  }

  private void initComponents() {
    this.sessionMenu = new JMenu(
        PropertiesUtils.getViewProperty("scpbrowser.menubar.menu.session"));
    this.newSessionButton = new JMenuItem(PropertiesUtils
        .getViewProperty("scpbrowser.menubar.button.session.new"));
    this.quitCurrentSessionButton = new JMenuItem(PropertiesUtils
        .getViewProperty("scpbrowser.menubar.button.session.quit.current"));
    this.quitAllSessionButton = new JMenuItem(PropertiesUtils
        .getViewProperty("scpbrowser.menubar.button.session.quit.all"));
  }

  private void build() {
    this.sessionMenu.add(this.newSessionButton);
    this.sessionMenu.addSeparator();
    this.sessionMenu.add(this.quitCurrentSessionButton);
    this.sessionMenu.add(this.quitAllSessionButton);

    this.add(this.sessionMenu);
    this.add(new JLabel());
  }
}
