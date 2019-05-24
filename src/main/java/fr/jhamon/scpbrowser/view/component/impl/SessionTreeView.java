package fr.jhamon.scpbrowser.view.component.impl;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang3.StringUtils;

import fr.jhamon.scpbrowser.model.SessionConfigModel;
import fr.jhamon.scpbrowser.utils.ConfigUtils;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;
import fr.jhamon.scpbrowser.view.component.event.handler.ManagementUserEventHandler;

/**
 * @author J.Hamon Copyright 2019 J.Hamon
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
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
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
        } else if (SwingUtilities.isRightMouseButton(e)) {
          int selRow = SessionTreeView.this.sessionTree
              .getRowForLocation(e.getX(), e.getY());
          if (selRow > 0) { // no action on root node
            SessionTreeView.this.sessionTree.setSelectionRow(selRow);
          }
        }
      }
    };
    this.sessionTree.addMouseListener(ml);

    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem renameItem = new JMenuItem(PropertiesUtils
        .getViewProperty("scpbrowser.sessiontree.action.rename"));
    renameItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        int selRow = SessionTreeView.this.sessionTree.getSelectionRows()[0];
        TreePath selPath = SessionTreeView.this.sessionTree
            .getPathForRow(selRow);
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selPath
            .getLastPathComponent();
        final Object nodeObject = treeNode.getUserObject();
        if (nodeObject != null && nodeObject instanceof SessionConfigModel) {
          String newName = JOptionPane.showInputDialog(null,
              PropertiesUtils.getViewProperty(
                  "scpbrowser..dialog.sessiontree.rename.session.message"),
              ((SessionConfigModel) nodeObject).getName());

          if (StringUtils.isNotBlank(newName)) {
            // remove previous config
            List<SessionConfigModel> configs = ConfigUtils.getSessionConfigs();
            configs.remove(nodeObject);
            // update config
            ((SessionConfigModel) nodeObject).setName(newName);
            configs.add((SessionConfigModel) nodeObject);
            // save configs
            Collections.sort(configs);
            ConfigUtils.setSessionConfigs(configs);
            refreshTree(configs);
          }
        } else {
          String newName = JOptionPane.showInputDialog(null,
              PropertiesUtils.getViewProperty(
                  "scpbrowser..dialog.sessiontree.rename.group.message"),
              nodeObject);
          if (newName != null) {
            // get partial name
            String partialName = "";
            for (TreeNode node : treeNode.getPath()) {
              if (node.equals(treeNode)) {
                newName = partialName + newName + "/";
              }
              partialName = partialName
                  + ((DefaultMutableTreeNode) node).getUserObject() + "/";
            }
            // remove matching configs
            List<SessionConfigModel> configs = ConfigUtils.getSessionConfigs();
            Iterator<SessionConfigModel> iter = configs.iterator();
            while (iter.hasNext()) {
              SessionConfigModel config = iter.next();
              config
              .setName(config.getName().replaceFirst(partialName, newName));
            }
            // save configs
            Collections.sort(configs);
            ConfigUtils.setSessionConfigs(configs);
            refreshTree(configs);
          }
        }
      }
    });
    popupMenu.add(renameItem);
    JMenuItem deleteItem = new JMenuItem(PropertiesUtils
        .getViewProperty("scpbrowser.sessiontree.action.remove"));
    deleteItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        int selRow = SessionTreeView.this.sessionTree.getSelectionRows()[0];
        TreePath selPath = SessionTreeView.this.sessionTree
            .getPathForRow(selRow);
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selPath
            .getLastPathComponent();
        final Object nodeObject = treeNode.getUserObject();
        if (nodeObject != null && nodeObject instanceof SessionConfigModel) {
          int dialogResult = JOptionPane.showConfirmDialog(null,
              PropertiesUtils.getViewProperty(
                  "scpbrowser.dialog.sessiontree.remove.session.message",
                  ((SessionConfigModel) nodeObject).getName()),
              PropertiesUtils.getViewProperty(
                  "scpbrowser.dialog.sessiontree.remove.session.title"),
              JOptionPane.YES_NO_OPTION);
          if (dialogResult == JOptionPane.YES_OPTION) {
            // remove previous config
            List<SessionConfigModel> configs = ConfigUtils.getSessionConfigs();
            configs.remove(nodeObject);
            // save configs
            ConfigUtils.setSessionConfigs(configs);
            refreshTree(configs);
          }
        } else {
          int dialogResult = JOptionPane.showConfirmDialog(null,
              PropertiesUtils.getViewProperty(
                  "scpbrowser.dialog.sessiontree.remove.group.message",
                  nodeObject),
              PropertiesUtils.getViewProperty(
                  "scpbrowser.dialog.sessiontree.remove.group.title"),
              JOptionPane.YES_NO_OPTION);
          if (dialogResult == JOptionPane.YES_OPTION) {
            // get partial name
            String partialName = "";
            for (TreeNode node : treeNode.getPath()) {
              if (!((DefaultMutableTreeNode)node).isRoot()) {
                partialName = partialName
                    + ((DefaultMutableTreeNode) node).getUserObject() + "/";
              }
            }
            // remove matching configs
            List<SessionConfigModel> configs = ConfigUtils.getSessionConfigs();
            Iterator<SessionConfigModel> iter = configs.iterator();
            while (iter.hasNext()) {
              SessionConfigModel config = iter.next();
              if (config.getName().startsWith(partialName)) {
                iter.remove();
              }
            }
            // save configs
            ConfigUtils.setSessionConfigs(configs);
            refreshTree(configs);
          }
        }
      }
    });
    popupMenu.add(deleteItem);
    this.sessionTree.setComponentPopupMenu(popupMenu);
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
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
                modelPathName[id]);
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
