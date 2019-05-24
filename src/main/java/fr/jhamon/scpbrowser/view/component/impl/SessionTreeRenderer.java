package fr.jhamon.scpbrowser.view.component.impl;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import fr.jhamon.scpbrowser.model.SessionConfigModel;
import fr.jhamon.scpbrowser.utils.IconUtils;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class SessionTreeRenderer extends DefaultTreeCellRenderer {

  private static final long serialVersionUID = -818173280811438528L;

  private Icon serverIcon;

  public SessionTreeRenderer() {
    this.serverIcon = IconUtils.createImageIcon("/server.png");
  }

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value,
      boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

    if (value instanceof DefaultMutableTreeNode
        && ((DefaultMutableTreeNode) value)
        .getUserObject() instanceof SessionConfigModel) {
      SessionConfigModel configModel = (SessionConfigModel) ((DefaultMutableTreeNode) value)
          .getUserObject();
      String text = configModel.getName();
      if (text.contains("/")) {
        text = text.substring(text.lastIndexOf("/")+1);
      }
      super.getTreeCellRendererComponent(tree, text, sel, expanded, leaf, row,
          hasFocus);
      setIcon(serverIcon);
      setToolTipText(String.format("user: %s\n, host: %s", configModel.getUsername(), configModel.getServer()));
    } else {
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
          hasFocus);
    }

    return this;
  }

}
