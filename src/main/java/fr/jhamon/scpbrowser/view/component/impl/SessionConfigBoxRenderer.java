package fr.jhamon.scpbrowser.view.component.impl;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import fr.jhamon.scpbrowser.model.SessionConfigModel;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class SessionConfigBoxRenderer extends DefaultListCellRenderer {

  private static final long serialVersionUID = -5949833386034801002L;

  @Override
  public Component getListCellRendererComponent(JList<?> list, Object value,
      int index, boolean isSelected, boolean cellHasFocus) {
    super.getListCellRendererComponent(list, value, index, isSelected,
        cellHasFocus);

    SessionConfigModel item = (SessionConfigModel) value;
    if (item != null && item.getName() != null) {
      setText(item.getName().toUpperCase());
    }

    return this;
  }

}
