package fr.jhamon.scpbrowser.view.component.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import fr.jhamon.scpbrowser.Context;
import fr.jhamon.scpbrowser.utils.ConfigUtils;
import fr.jhamon.scpbrowser.utils.IconUtils;
import fr.jhamon.scpbrowser.view.component.MainView;
import fr.jhamon.scpbrowser.view.component.SessionView;
import fr.jhamon.scpbrowser.view.component.TransferView;
import fr.jhamon.scpbrowser.view.component.event.handler.ManagementUserEventHandler;

/**
 * Swing implementation of {@link MainView}
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class MainFrame extends JFrame implements MainView {

  private static final long serialVersionUID = 5539820266152873597L;

  private static final String DEFAULT_TITLE = "JeHa! SCP Browser ";

  public MenuBar menuBar;

  public ToolBar toolbar;

  public SessionTreeView sessionTree;

  public FooterBar footerbar;

  private JTabbedPane sessionTabsPane;

  private ManagementUserEventHandler eventHandler;

  private TransferView transferView;

  public MainFrame() {
    super();
    this.setSize(800, 600);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setTitle(DEFAULT_TITLE);

    ImageIcon icon = IconUtils.createImageIcon("/favicon.png");
    this.setIconImage(icon.getImage());
    this.setVisible(true);

    this.initComponents();
    this.build();

    this.addWindowListener(new WindowListener() {

      @Override
      public void windowOpened(WindowEvent e) {
      }

      @Override
      public void windowIconified(WindowEvent e) {
      }

      @Override
      public void windowDeiconified(WindowEvent e) {
      }

      @Override
      public void windowDeactivated(WindowEvent e) {
      }

      @Override
      public void windowClosing(WindowEvent e) {
        MainFrame.this.eventHandler.onExitEvent();
      }

      @Override
      public void windowClosed(WindowEvent e) {
      }

      @Override
      public void windowActivated(WindowEvent e) {
      }
    });
  }

  private void initComponents() {
    this.menuBar = new MenuBar();
    this.toolbar = new ToolBar();
    this.footerbar = new FooterBar();
    this.sessionTabsPane = new JTabbedPane(JTabbedPane.TOP,
        JTabbedPane.SCROLL_TAB_LAYOUT);
    this.sessionTree = new SessionTreeView();
    this.sessionTree.refreshTree(ConfigUtils.getSessionConfigs());

    this.transferView = new TransferPanel();
  }

  private void build() {
    this.getContentPane().setLayout(new BorderLayout());
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        this.sessionTabsPane, this.sessionTree);
    splitPane.setDividerLocation(0.50);
    splitPane.setResizeWeight(0.70);

    JSplitPane vSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPane,
        (JComponent) this.transferView);
    vSplitPane.setDividerLocation(0.80);
    vSplitPane.setResizeWeight(0.90);

    this.getContentPane().add(this.toolbar, BorderLayout.PAGE_START);
    this.getContentPane().add(vSplitPane, BorderLayout.CENTER);
    this.getContentPane().add(this.footerbar, BorderLayout.PAGE_END);

    //    this.pack();

  }

  @Override
  public ToolBar getToolbar() {
    return this.toolbar;
  }

  public void setToolbar(ToolBar toolbar) {
    this.toolbar = toolbar;
  }

  @Override
  public FooterBar getFooterbar() {
    return this.footerbar;
  }

  public void setFooterbar(FooterBar footerbar) {
    this.footerbar = footerbar;
  }

  @Override
  public SessionView createNewSessionView(String title, String tooltip) {
    SessionView newSessionView = new SessionPanel();
    this.sessionTabsPane.addTab(title, null, (SessionPanel) newSessionView, tooltip);
    this.toolbar.toggleSessionButtons(true);
    this.sessionTree.refreshTree(ConfigUtils.getSessionConfigs());
    return newSessionView;
  }

  @Override
  public void removeSessionView(SessionView view) {
    this.sessionTabsPane.remove((SessionPanel) view);
    this.toolbar.toggleSessionButtons(this.sessionTabsPane.getTabCount() > 0);
  }

  @Override
  public void showSessionView(SessionView view) {
    this.sessionTabsPane.setSelectedComponent((SessionPanel) view);
    this.toolbar.toggleSessionButtons(true);
  }

  @Override
  public void setEventHandler(ManagementUserEventHandler handler) {
    this.eventHandler = handler;
    this.toolbar.setEventHandler(handler);
    this.sessionTree.setEventHandler(handler);
  }

  @Override
  public void start(Context context) {
    this.footerbar.start(context);
  }

  @Override
  public SessionView getActiveView() {
    Component activePanel = this.sessionTabsPane.getSelectedComponent();
    if (activePanel instanceof SessionView) {
      return (SessionView) activePanel;
    }
    return null;
  }

  @Override
  public void close() {
    this.dispose();
  }

  @Override
  public TransferView getTransferView() {
    return this.transferView;
  }

}
