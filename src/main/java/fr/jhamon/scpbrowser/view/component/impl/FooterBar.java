package fr.jhamon.scpbrowser.view.component.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.jhamon.scpbrowser.Context;
import fr.jhamon.scpbrowser.utils.IconUtils;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class FooterBar extends JPanel {

  private static final long serialVersionUID = -4181067387388345640L;

  private JLabel userLabel;

  private JLabel passphraseLabel;

  private ImageIcon activeIcon;
  private ImageIcon inactiveIcon;

  private TransferCounter downloadCounter;
  private TransferCounter uploadCounter;

  public FooterBar() {
    super();
    this.initComponents();
    this.build();
  }

  private void initComponents() {
    this.userLabel = new JLabel(
        PropertiesUtils.getViewProperty("scpbrowser.footerbar.label.user", ""));
    this.userLabel.setBorder(BorderFactory.createEtchedBorder());

    this.activeIcon = IconUtils.createImageIcon("/accept.png");
    this.inactiveIcon = IconUtils.createImageIcon("/alert.png");

    this.passphraseLabel = new JLabel(PropertiesUtils
        .getViewProperty("scpbrowser.footerbar.label.passphrase"));
    this.passphraseLabel.setHorizontalTextPosition(JLabel.LEFT);
    this.passphraseLabel.setIcon(this.inactiveIcon);
    this.passphraseLabel.setBorder(BorderFactory.createEtchedBorder());

    this.downloadCounter = new TransferCounter(false);
    this.uploadCounter = new TransferCounter(true);
  }

  private void build() {
    this.setLayout(new BorderLayout());
    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
    leftPanel.add(Box.createRigidArea(new Dimension(2, 0)));
    leftPanel.add(this.userLabel);
    leftPanel.add(Box.createRigidArea(new Dimension(5, 0)));
    leftPanel.add(this.passphraseLabel);

    this.add(leftPanel, BorderLayout.WEST);

    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
    rightPanel.add(Box.createRigidArea(new Dimension(2, 0)));
    rightPanel.add(this.downloadCounter);
    rightPanel.add(Box.createRigidArea(new Dimension(5, 0)));
    rightPanel.add(this.uploadCounter);
    rightPanel.add(Box.createRigidArea(new Dimension(2, 0)));

    this.add(rightPanel, BorderLayout.EAST);
  }

  public void togglePassphrase(boolean active) {
    if (active) {
      this.passphraseLabel.setIcon(this.activeIcon);
    } else {
      this.passphraseLabel.setIcon(this.inactiveIcon);
    }
  }

  public void start(Context context) {
    this.userLabel.setText(PropertiesUtils
        .getViewProperty("scpbrowser.footerbar.label.user", context.getUser()));
  }

  public void addFailedDownload() {
    this.downloadCounter.addFailure();
  }

  public void addRunningDownload() {
    this.downloadCounter.addRunning();
  }

  public void removeRunningDownload() {
    this.downloadCounter.removeRunning();
  }

  public void addSucceededDownload() {
    this.downloadCounter.addSuccess();
  }

  public void addFailedUpload() {
    this.uploadCounter.addFailure();
  }

  public void addRunningUpload() {
    this.uploadCounter.addRunning();
  }

  public void removeRunningUpload() {
    this.uploadCounter.removeRunning();
  }

  public void addSucceededUpload() {
    this.uploadCounter.addSuccess();
  }
}