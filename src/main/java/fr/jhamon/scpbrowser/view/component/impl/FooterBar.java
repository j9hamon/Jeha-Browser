package fr.jhamon.scpbrowser.view.component.impl;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fr.jhamon.scpbrowser.Context;
import fr.jhamon.scpbrowser.utils.Constantes;
import fr.jhamon.scpbrowser.utils.IconUtils;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;

/**
 * @author J.Hamon Copyright 2019 J.Hamon
 *
 */
public class FooterBar extends JPanel {

  private static final long serialVersionUID = -4181067387388345640L;

  private JLabel userLabel;

  private JLabel passphraseLabel;

  private JLabel versionLabel;

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

    this.versionLabel = new JLabel(Constantes.VERSION,
        IconUtils.createImageIcon("/about.png", 16, 16), JLabel.RIGHT);
    this.versionLabel.setBorder(BorderFactory.createEtchedBorder());
    this.versionLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    this.versionLabel.addMouseListener(new MouseListener() {

      @Override
      public void mouseReleased(MouseEvent e) {
      }

      @Override
      public void mousePressed(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        try {
          Desktop.getDesktop().browse(
              new URL("https://github.com/j9hamon/Jeha-Browser/releases")
                  .toURI());
        } catch (IOException | UnsupportedOperationException | SecurityException
            | URISyntaxException ex) {
          JOptionPane.showMessageDialog(null,
              PropertiesUtils.getViewProperty("scpbrowser.dialog.about.message",
                  Constantes.VERSION),
              PropertiesUtils.getViewProperty("scpbrowser.dialog.about.title"),
              JOptionPane.INFORMATION_MESSAGE);
        }
      }
    });
  }

  private void build() {
    this.setLayout(new BorderLayout());
    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
    leftPanel.add(Box.createRigidArea(new Dimension(2, 0)));
    leftPanel.add(this.userLabel);
    leftPanel.add(Box.createRigidArea(new Dimension(5, 0)));
    leftPanel.add(this.passphraseLabel);
    leftPanel.add(Box.createRigidArea(new Dimension(5, 0)));
    leftPanel.add(this.versionLabel);

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
