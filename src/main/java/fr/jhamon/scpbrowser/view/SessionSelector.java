package fr.jhamon.scpbrowser.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.StringUtils;

import fr.jhamon.scpbrowser.model.SessionConfigModel;
import fr.jhamon.scpbrowser.utils.Constantes;
import fr.jhamon.scpbrowser.utils.IconUtils;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;
import fr.jhamon.scpbrowser.utils.SpringUtilities;
import fr.jhamon.scpbrowser.view.component.event.handler.SessionSelectionEventHandler;
import fr.jhamon.scpbrowser.view.component.impl.SessionConfigBoxRenderer;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class SessionSelector extends JDialog {

  private static final long serialVersionUID = 5735994813021468883L;

  private JComboBox<SessionConfigModel> savedSessionsBox;
  private JButton savedSessionConnectButton;

  private SessionSelectionEventHandler eventHandler;

  private JLabel nameLabel;
  private JTextField nameField;
  private JLabel serverLabel;
  private JTextField serverField;
  private JLabel usernameLabel;
  private JTextField usernameField;
  private JLabel passwordLabel;
  private JTextField passwordField;
  private JLabel timeoutConnectLabel;
  private JTextField timeoutConnectField;
  private JLabel timeoutCommandLabel;
  private JTextField timeoutCommandField;
  private JLabel timeoutDownloadLabel;
  private JTextField timeoutDownloadField;
  private JLabel timeoutUploadLabel;
  private JTextField timeoutUploadField;
  private JButton saveSessionButton;
  private JButton newSessionConnectButton;

  public SessionSelector() {
    super();
    this.setModalityType(ModalityType.APPLICATION_MODAL);
    this.setTitle(
        PropertiesUtils.getViewProperty("scpbrowser.dialog.session.title"));
    this.initComponents();
    this.build();
    this.pack();
    this.setVisible(true);
  }

  private void initComponents() {
    this.savedSessionsBox = new JComboBox<SessionConfigModel>();
    this.savedSessionsBox.setEditable(false);
    this.savedSessionsBox.setRenderer(new SessionConfigBoxRenderer());
    this.savedSessionConnectButton = new JButton(
        IconUtils.createImageIcon("/right.png"));
    this.savedSessionConnectButton.setToolTipText(PropertiesUtils
        .getViewProperty("scpbrowser.dialog.session.tooltip.connect"));
    this.savedSessionConnectButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        new Thread(new Runnable() {

          @Override
          public void run() {
            eventHandler.onStartSavedSession();
          }
        }).start();
      }
    });

    this.nameLabel = new JLabel(PropertiesUtils
        .getViewProperty("scpbrowser.dialog.session.new.session.name"));
    this.nameField = new JTextField(30);
    this.nameField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        checkInsuffisantData();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        checkInsuffisantData();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        checkInsuffisantData();
      }
    });
    this.serverLabel = new JLabel(PropertiesUtils
        .getViewProperty("scpbrowser.dialog.session.new.session.server"));
    this.serverField = new JTextField(30);
    this.serverField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        checkInsuffisantData();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        checkInsuffisantData();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        checkInsuffisantData();
      }
    });
    this.usernameLabel = new JLabel(PropertiesUtils
        .getViewProperty("scpbrowser.dialog.session.new.session.username"));
    this.usernameField = new JTextField(30);
    this.usernameField.getDocument()
    .addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        checkInsuffisantData();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        checkInsuffisantData();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        checkInsuffisantData();
      }
    });
    this.passwordLabel = new JLabel(PropertiesUtils
        .getViewProperty("scpbrowser.dialog.session.new.session.password"));
    this.passwordField = new JTextField(30);

    this.timeoutConnectLabel = new JLabel(PropertiesUtils.getViewProperty(
        "scpbrowser.dialog.session.new.session.timeout.connect"));
    this.timeoutConnectField = new JFormattedTextField(
        NumberFormat.getNumberInstance());
    this.timeoutConnectField
    .setText(String.valueOf(Constantes.TIMEOUT_CONNECT));
    this.timeoutCommandLabel = new JLabel(PropertiesUtils.getViewProperty(
        "scpbrowser.dialog.session.new.session.timeout.command"));
    this.timeoutCommandField = new JFormattedTextField(
        NumberFormat.getNumberInstance());
    this.timeoutCommandField
    .setText(String.valueOf(Constantes.TIMEOUT_COMMAND));
    this.timeoutDownloadLabel = new JLabel(PropertiesUtils.getViewProperty(
        "scpbrowser.dialog.session.new.session.timeout.download"));
    this.timeoutDownloadField = new JFormattedTextField(
        NumberFormat.getNumberInstance());
    this.timeoutDownloadField
    .setText(String.valueOf(Constantes.TIMEOUT_DOWNLOAD));
    this.timeoutUploadLabel = new JLabel(PropertiesUtils.getViewProperty(
        "scpbrowser.dialog.session.new.session.timeout.upload"));
    this.timeoutUploadField = new JFormattedTextField(
        NumberFormat.getNumberInstance());
    this.timeoutUploadField.setText(String.valueOf(Constantes.TIMEOUT_UPLOAD));

    this.saveSessionButton = new JButton(
        IconUtils.createImageIcon("/save.png"));
    this.saveSessionButton.setEnabled(false);
    this.saveSessionButton.setToolTipText(PropertiesUtils
        .getViewProperty("scpbrowser.dialog.session.tooltip.save"));
    this.saveSessionButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (!StringUtils.isBlank(nameField.getText())
            && !StringUtils.isBlank(usernameField.getText())
            && !StringUtils.isBlank(serverField.getText())) {
          SessionSelector.this.saveSessionButton.setEnabled(false);
          new Thread(new Runnable() {

            @Override
            public void run() {
              eventHandler.onSaveSessionEvent();
            }
          }).start();
        }
      }
    });
    this.newSessionConnectButton = new JButton(
        IconUtils.createImageIcon("/right.png"));
    this.newSessionConnectButton.setEnabled(false);
    this.newSessionConnectButton.setToolTipText(PropertiesUtils
        .getViewProperty("scpbrowser.dialog.session.tooltip.connect"));
    this.newSessionConnectButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (!StringUtils.isBlank(nameField.getText())
            && !StringUtils.isBlank(usernameField.getText())
            && !StringUtils.isBlank(serverField.getText())) {
          new Thread(new Runnable() {

            @Override
            public void run() {
              eventHandler.onStartNewSession();
            }
          }).start();
        }
      }
    });

    this.savedSessionsBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SessionConfigModel savedConfigModel = (SessionConfigModel) SessionSelector.this.savedSessionsBox
            .getSelectedItem();
        SessionSelector.this.nameField.setText(savedConfigModel.getName());
        SessionSelector.this.serverField.setText(savedConfigModel.getServer());
        SessionSelector.this.usernameField
        .setText(savedConfigModel.getUsername());
        SessionSelector.this.passwordField
        .setText(savedConfigModel.getPassword());
        SessionSelector.this.timeoutCommandField.setText(String.valueOf(savedConfigModel.getCommandTimeout()));
        SessionSelector.this.timeoutConnectField.setText(String.valueOf(savedConfigModel.getConnectTimeout()));
        SessionSelector.this.timeoutDownloadField.setText(String.valueOf(savedConfigModel.getDownloadTimeout()));
        SessionSelector.this.timeoutUploadLabel.setText(String.valueOf(savedConfigModel.getUploadTimeout()));
      }
    });
  }

  protected void checkInsuffisantData() {
    if (StringUtils.isBlank(this.nameField.getText())
        || StringUtils.isBlank(usernameField.getText())
        || StringUtils.isBlank(serverField.getText())) {
      this.saveSessionButton.setEnabled(false);
      this.newSessionConnectButton.setEnabled(false);
    } else {
      this.saveSessionButton.setEnabled(true);
      this.newSessionConnectButton.setEnabled(true);
    }
  }

  private void build() {
    JPanel savedSessionPanel = new JPanel(new BorderLayout());
    savedSessionPanel.setBorder(BorderFactory.createTitledBorder(PropertiesUtils
        .getViewProperty("scpbrowser.dialog.session.saved.session.title")));
    savedSessionPanel.add(this.savedSessionsBox, BorderLayout.CENTER);
    savedSessionPanel.add(this.savedSessionConnectButton, BorderLayout.EAST);

    JPanel newSessionPanel = new JPanel();
    newSessionPanel.setLayout(new BoxLayout(newSessionPanel, BoxLayout.Y_AXIS));
    newSessionPanel.setBorder(BorderFactory.createTitledBorder(PropertiesUtils
        .getViewProperty("scpbrowser.dialog.session.new.session.title")));

    JPanel formPanel = new JPanel(new SpringLayout());
    formPanel.add(this.nameLabel);
    formPanel.add(this.nameField);
    formPanel.add(this.serverLabel);
    formPanel.add(this.serverField);
    formPanel.add(this.usernameLabel);
    formPanel.add(this.usernameField);
    formPanel.add(this.passwordLabel);
    formPanel.add(this.passwordField);
    SpringUtilities.makeCompactGrid(formPanel, 4, 2, 6, 6, 6, 6);

    JPanel timeoutPanel = new JPanel(new SpringLayout());
    timeoutPanel.add(this.timeoutConnectLabel);
    timeoutPanel.add(this.timeoutConnectField);
    timeoutPanel.add(this.timeoutCommandLabel);
    timeoutPanel.add(this.timeoutCommandField);
    timeoutPanel.add(this.timeoutDownloadLabel);
    timeoutPanel.add(this.timeoutDownloadField);
    timeoutPanel.add(this.timeoutUploadLabel);
    timeoutPanel.add(this.timeoutUploadField);
    SpringUtilities.makeCompactGrid(timeoutPanel, 4, 2, 6, 6, 6, 6);

    newSessionPanel.add(formPanel);
    newSessionPanel.add(new JSeparator(JSeparator.HORIZONTAL));
    newSessionPanel.add(timeoutPanel);
    JPanel formControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    formControlPanel.add(this.saveSessionButton);
    formControlPanel.add(this.newSessionConnectButton);
    newSessionPanel.add(formControlPanel);

    this.setLayout(new BorderLayout());
    this.add(savedSessionPanel, BorderLayout.NORTH);
    this.add(newSessionPanel, BorderLayout.CENTER);
  }

  public JComboBox<SessionConfigModel> getSavedSessionsBox() {
    return this.savedSessionsBox;
  }

  public JButton getSavedSessionConnectButton() {
    return this.savedSessionConnectButton;
  }

  public JTextField getNameField() {
    return this.nameField;
  }

  public JTextField getServerField() {
    return this.serverField;
  }

  public JTextField getUsernameField() {
    return this.usernameField;
  }

  public JTextField getPasswordField() {
    return this.passwordField;
  }

  public JButton getSaveSessionButton() {
    return this.saveSessionButton;
  }

  public JButton getNewSessionConnectButton() {
    return this.newSessionConnectButton;
  }

  public void setSavedSessionsList(final List<SessionConfigModel> configs) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        SessionSelector.this.savedSessionsBox
        .setModel(new DefaultComboBoxModel<SessionConfigModel>(
            configs.toArray(new SessionConfigModel[configs.size()])));
        SessionSelector.this.savedSessionsBox.setEnabled(configs.size() > 0);
        SessionSelector.this.savedSessionConnectButton
        .setEnabled(configs.size() > 0);
      }
    });

  }

  public void close() {
    this.setVisible(false);
    this.dispose();
  }

  public void setEventHandler(SessionSelectionEventHandler eventHandler) {
    this.eventHandler = eventHandler;
  }

  @Override
  public void setVisible(final boolean visible) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        SessionSelector.super.setVisible(visible);
      }
    });
  }

  public void setSelectedSession(final SessionConfigModel model) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        SessionSelector.this.savedSessionsBox.setSelectedItem(model);
      }

    });
  }


  /**
   * @return the timeoutConnectField
   */
  public JTextField getTimeoutConnectField() {
    return timeoutConnectField;
  }


  /**
   * @param timeoutConnectField the timeoutConnectField to set
   */
  public void setTimeoutConnectField(JTextField timeoutConnectField) {
    this.timeoutConnectField = timeoutConnectField;
  }


  /**
   * @return the timeoutCommandField
   */
  public JTextField getTimeoutCommandField() {
    return timeoutCommandField;
  }


  /**
   * @param timeoutCommandField the timeoutCommandField to set
   */
  public void setTimeoutCommandField(JTextField timeoutCommandField) {
    this.timeoutCommandField = timeoutCommandField;
  }


  /**
   * @return the timeoutDownloadField
   */
  public JTextField getTimeoutDownloadField() {
    return timeoutDownloadField;
  }


  /**
   * @param timeoutDownloadField the timeoutDownloadField to set
   */
  public void setTimeoutDownloadField(JTextField timeoutDownloadField) {
    this.timeoutDownloadField = timeoutDownloadField;
  }


  /**
   * @return the timeoutUploadField
   */
  public JTextField getTimeoutUploadField() {
    return timeoutUploadField;
  }


  /**
   * @param timeoutUploadField the timeoutUploadField to set
   */
  public void setTimeoutUploadField(JTextField timeoutUploadField) {
    this.timeoutUploadField = timeoutUploadField;
  }
}
