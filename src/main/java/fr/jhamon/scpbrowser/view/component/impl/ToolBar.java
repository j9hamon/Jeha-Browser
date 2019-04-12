package fr.jhamon.scpbrowser.view.component.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;

import fr.jhamon.scpbrowser.utils.IconUtils;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;
import fr.jhamon.scpbrowser.view.component.event.handler.ManagementUserEventHandler;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class ToolBar extends JToolBar {

  private static final long serialVersionUID = 4734599463856391893L;

  private JButton addButton;
  private JButton refreshButton;
  private JButton closeButton;
  private JButton uploadButton;
  private JButton consoleButton;

  private ManagementUserEventHandler eventHandler;

  public ToolBar() {
    super();
    this.setFloatable(true);
    this.setRollover(true);
    this.initComponents();
    this.build();
  }

  private void initComponents() {
    this.addButton = new JButton(
        IconUtils.createImageIcon("/plus.png", 16, 16));
    this.addButton.setToolTipText(PropertiesUtils
        .getViewProperty("scpbrowser.toolbar.tooltip.session.new"));
    this.addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        eventHandler.onNewSessionEvent();
      }
    });
    this.refreshButton = new JButton(
        IconUtils.createImageIcon("/refresh.png", 16, 16));
    this.refreshButton.setToolTipText(PropertiesUtils
        .getViewProperty("scpbrowser.toolbar.tooltip.session.refresh"));
    this.refreshButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        eventHandler.onRefreshSessionEvent();
      }
    });
    this.closeButton = new JButton(
        IconUtils.createImageIcon("/cross.png", 16, 16));
    this.closeButton.setToolTipText(PropertiesUtils
        .getViewProperty("scpbrowser.toolbar.tooltip.session.quit"));
    this.closeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        eventHandler.onCloseSessionEvent();
      }
    });
    this.uploadButton = new JButton(
        IconUtils.createImageIcon("/upload.png", 16, 16));
    this.uploadButton.setToolTipText(
        PropertiesUtils.getViewProperty("scpbrowser.toolbar.tooltip.upload"));
    this.uploadButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        eventHandler.onUploadEvent();
      }
    });
    this.consoleButton = new JButton(
        IconUtils.createImageIcon("/console.png", 16, 16));
    this.consoleButton.setToolTipText(PropertiesUtils
        .getViewProperty("scpbrowser.toolbar.tooltip.session.console"));
    this.consoleButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        eventHandler.onOpenConsoleEvent();
      }
    });

    this.toggleSessionButtons(false);
  }

  private void build() {
    this.add(this.addButton);
    this.add(this.refreshButton);
    this.add(this.uploadButton);
    this.add(this.consoleButton);
    this.add(this.closeButton);
  }

  public JButton getAddButton() {
    return this.addButton;
  }

  public JButton getRefreshButton() {
    return this.refreshButton;
  }

  public JButton getCloseButton() {
    return this.closeButton;
  }

  public JButton getUploadButton() {
    return this.uploadButton;
  }

  public JButton getConsoleButton() {
    return this.consoleButton;
  }

  public void setEventHandler(ManagementUserEventHandler handler) {
    this.eventHandler = handler;
  }

  public void toggleSessionButtons(boolean enabled) {
    this.refreshButton.setEnabled(enabled);
    this.closeButton.setEnabled(enabled);
    this.uploadButton.setEnabled(enabled);
    this.consoleButton.setEnabled(enabled);
  }

}
