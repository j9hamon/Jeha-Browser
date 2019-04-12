package fr.jhamon.scpbrowser.view.component.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;

import fr.jhamon.scpbrowser.model.ContentModel;
import fr.jhamon.scpbrowser.utils.IconUtils;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;
import fr.jhamon.scpbrowser.view.component.ContentViewer;
import fr.jhamon.scpbrowser.view.component.SessionView;
import fr.jhamon.scpbrowser.view.component.event.handler.ContentEventHandler;
import fr.jhamon.scpbrowser.view.component.impl.table.FolderContentTable;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class SessionPanel extends JPanel implements SessionView {

  private static final long serialVersionUID = -4333834882963691874L;

  private JTextField pathField;
  private JButton pathButton;
  private JButton homeButton;
  private JButton newFolderButton;
  private ContentViewer contentTable;

  private ContentEventHandler eventHandler;

  private JFileChooser fileChooser;

  public SessionPanel() {
    super();
    this.initComponents();
    this.build();
  }

  private void initComponents() {
    this.pathField = new JTextField();
    this.pathButton = new JButton(
        IconUtils.createImageIcon("/right.png", 16, 16));
    this.pathButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        SessionPanel.this.eventHandler
        .onRequestPath(SessionPanel.this.pathField.getText());
      }
    });
    this.homeButton = new JButton(
        IconUtils.createImageIcon("/home.png", 16, 16));
    this.homeButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        SessionPanel.this.eventHandler.onRequestPath("");
      }
    });
    this.newFolderButton = new JButton(
        IconUtils.createImageIcon("/newFolder.png", 16, 16));
    this.newFolderButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            JTextField fileNameField = new JTextField();
            int dialogCode = JOptionPane.showConfirmDialog(SessionPanel.this,
                fileNameField,
                PropertiesUtils
                .getViewProperty("scpbrowser.dialog.newFolder.title"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (dialogCode == JOptionPane.OK_OPTION
                && !StringUtils.isBlank(fileNameField.getText())) {
              SessionPanel.this.eventHandler
              .onMakeDirEvent(fileNameField.getText());
            }
          }
        });
      }
    });

    this.contentTable = new FolderContentTable();

    this.fileChooser = new JFileChooser() {

      private static final long serialVersionUID = 6310848709236878971L;

      @Override
      protected JDialog createDialog(Component parent)
          throws HeadlessException {
        JDialog dialog = super.createDialog(parent);
        dialog.setModalityType(ModalityType.APPLICATION_MODAL);
        return dialog;
      }
    };
    this.fileChooser.setMultiSelectionEnabled(false);
    this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    this.fileChooser.setDialogTitle(
        PropertiesUtils.getViewProperty("scpbrowser.dialog.filechooser.title"));

  }

  private void build() {
    this.setLayout(new BorderLayout());

    JPanel pathPanel = new JPanel(new BorderLayout());
    pathPanel.add(this.pathField, BorderLayout.CENTER);
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(this.pathButton);
    buttonPanel.add(this.homeButton);
    buttonPanel.add(this.newFolderButton);
    pathPanel.add(buttonPanel, BorderLayout.EAST);
    this.add(pathPanel, BorderLayout.PAGE_START);

    JScrollPane contentScrollPane = new JScrollPane((JTable) this.contentTable,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    ((JTable) this.contentTable).setFillsViewportHeight(true);
    ;
    this.add(contentScrollPane, BorderLayout.CENTER);
  }

  public void clearContentTable() {
    this.contentTable.clear();
  }

  public void setContentTable(List<ContentModel> content) {
    this.contentTable.setContent(content);
  }

  @Override
  public ContentViewer getContentTable() {
    return this.contentTable;
  }

  @Override
  public void setPath(final String path) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        SessionPanel.this.pathField.setText(path);
      }
    });
  }

  @Override
  public void close() {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        SessionPanel.this.setVisible(false);
      }
    });
  }

  @Override
  public void openUploadFileChooser() {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        int returnVal = fileChooser.showDialog(null, PropertiesUtils
            .getViewProperty("scpbrowser.dialog.filechooser.button"));
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          new Thread(new Runnable() {
            @Override
            public void run() {
              eventHandler.onUploadEvent(
                  fileChooser.getSelectedFile().getAbsolutePath());
            }
          }).start();
        }
      }
    });
  }

  @Override
  public void setEventHandler(ContentEventHandler eventHandler) {
    this.eventHandler = eventHandler;
    this.contentTable.setEventHandler(eventHandler);
  }

}
