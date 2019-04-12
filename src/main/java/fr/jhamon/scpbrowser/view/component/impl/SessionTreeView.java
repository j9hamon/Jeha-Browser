package fr.jhamon.scpbrowser.view.component.impl;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fr.jhamon.scpbrowser.model.SessionConfigModel;
import fr.jhamon.scpbrowser.view.component.event.handler.ManagementUserEventHandler;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class SessionTreeView extends JPanel {

  private static final long serialVersionUID = -5068379132513584639L;

  private JTree sessionTree;
  private ManagementUserEventHandler eventHandler;

  public SessionTreeView() {
    super();
    this.initComponents();
    this.build();
  }

  private void initComponents() {
    this.sessionTree = new JTree();
    this.sessionTree.setCellRenderer(new SessionTreeRenderer());
    this.sessionTree.setRootVisible(false);
    this.sessionTree.setShowsRootHandles(true);
    this.sessionTree.getSelectionModel()
    .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

    MouseListener ml = new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (e.getClickCount() == 2) {
          int selRow = SessionTreeView.this.sessionTree
              .getRowForLocation(e.getX(), e.getY());
          TreePath selPath = SessionTreeView.this.sessionTree
              .getPathForLocation(e.getX(), e.getY());
          if (selRow != -1) {
            final Object nodeObject = ((DefaultMutableTreeNode) selPath
                .getLastPathComponent()).getUserObject();
            if (nodeObject != null
                && nodeObject instanceof SessionConfigModel) {
              new Thread(new Runnable() {

                @Override
                public void run() {
                  eventHandler
                  .onStartSessionEvent((SessionConfigModel) nodeObject);
                }
              }).start();
            }
          }
        }
      }
    };
    this.sessionTree.addMouseListener(ml);
  }

  private void build() {
    this.setLayout(new BorderLayout());
    JScrollPane scrollPane = new JScrollPane(this.sessionTree,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    this.add(scrollPane, BorderLayout.CENTER);
  }

  public void refreshTree(List<SessionConfigModel> sessionConfigs) {
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Sessions");
    for (SessionConfigModel model : sessionConfigs) {
      String[] modelPathName = model.getName().split("/");
      DefaultMutableTreeNode parentNode = rootNode;
      for (int id = 0; id < modelPathName.length; id++) {
        if (id == modelPathName.length - 1) {
          // prevents duplicates
          if (findChildNode(parentNode, modelPathName[id]) == null) {
            parentNode.add(new DefaultMutableTreeNode(model));
          }
        } else {
          TreeNode subNode = findChildNode(parentNode, modelPathName[id]);
          if (subNode != null) {
            parentNode = (DefaultMutableTreeNode) subNode;
          } else {
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(modelPathName[id]);
            parentNode.add(newNode);
            parentNode = newNode;
          }
        }
      }
    }
    TreeModel treeModel = new DefaultTreeModel(rootNode);
    this.sessionTree.setModel(treeModel);
  }

  private TreeNode findChildNode(DefaultMutableTreeNode node,
      Object objectToFind) {
    if (node != null) {
      Enumeration<TreeNode> nodeIter = node.children();
      while (nodeIter.hasMoreElements()) {
        TreeNode subNode = nodeIter.nextElement();
        if (((DefaultMutableTreeNode) subNode).getUserObject()
            .equals(objectToFind)) {
          return subNode;
        }
      }
    }
    return null;
  }

  public void setEventHandler(ManagementUserEventHandler handler) {
    this.eventHandler = handler;
  }
}
