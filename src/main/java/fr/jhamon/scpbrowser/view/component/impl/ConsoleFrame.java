package fr.jhamon.scpbrowser.view.component.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jcraft.jcterm.Connection;
import com.jcraft.jcterm.JCTermSwing;

import fr.jhamon.scpbrowser.view.component.Console;


/**
 * Copyright (c) 2002-2015 Atsuhiko Yamanaka, JCraft,Inc.
 */
public class ConsoleFrame extends JFrame implements Console {

  private static final long serialVersionUID = 7185602866783654332L;

  private JCTermSwing terminal;
  private Connection connection;

  /**
   *
   */
  public ConsoleFrame() {
    super();
    this.setResizable(true);
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        ConsoleFrame.this.close();
      }
    });

    this.initComponents();
    this.build();

    this.setVisible(true);
  }

  private void initComponents() {
    this.terminal = new JCTermSwing();
    this.terminal.setAntiAliasing(false);
  }

  private void build() {
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(this.terminal, BorderLayout.CENTER);
    this.pack();
    this.terminal.setVisible(true);

    ComponentAdapter resizeListener = new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        Component c = e.getComponent();
        Container cp = ((JFrame) c).getContentPane();
        int cw = c.getWidth();
        int ch = c.getHeight();
        int cwm = c.getWidth() - cp.getWidth();
        int chm = c.getHeight() - cp.getHeight();
        cw -= cwm;
        ch -= chm;
        ConsoleFrame.this.terminal.setSize(cw, ch);
      }
    };
    this.addComponentListener(resizeListener);
  }

  @Override
  public void requestFocus() {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        ConsoleFrame.this.setVisible(true);
        ((JFrame) ConsoleFrame.this).requestFocus();
      }
    });
  }

  @Override
  public void setTitle(final String title) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        ((JFrame) ConsoleFrame.this).setTitle(title);
      }
    });
  }

  @Override
  public void start(Connection connection) {
    this.connection = connection;
    this.terminal.start(connection);
  }

  @Override
  public void close() {
    this.connection.close();

    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        ConsoleFrame.this.setVisible(false);
        ConsoleFrame.this.dispose();
      }
    });
  }

}
