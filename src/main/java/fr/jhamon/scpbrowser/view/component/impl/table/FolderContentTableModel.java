package fr.jhamon.scpbrowser.view.component.impl.table;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Icon;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.StringUtils;

import fr.jhamon.scpbrowser.model.ContentModel;
import fr.jhamon.scpbrowser.model.FileModel;
import fr.jhamon.scpbrowser.utils.ConfigUtils;
import fr.jhamon.scpbrowser.utils.Constantes;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;

/**
 * Table model to used to display ContentModel in a JTable
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class FolderContentTableModel extends DefaultTableModel {

  private static final long serialVersionUID = 3248354963494057475L;

  private List<ContentModel> content = new ArrayList<ContentModel>();

  public FolderContentTableModel() {
    super();
    this.setColumnIdentifiers(new String[] {
        PropertiesUtils.getViewProperty("scpbrowser.table.column.file.icon"),
        PropertiesUtils.getViewProperty("scpbrowser.table.column.file.name"),
        PropertiesUtils.getViewProperty("scpbrowser.table.column.file.size"),
        PropertiesUtils.getViewProperty("scpbrowser.table.column.last.edit") });
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    switch (columnIndex) {
      case 0:
        return Icon.class;
      case 1:
        return String.class;
      case 2:
        return String.class;
      case 3:
        return Date.class;
      default:
        return super.getColumnClass(columnIndex);
    }
  }

  @Override
  public Object getValueAt(int row, int column) {
    if (this.content.size() > row) {
      switch (column) {
        case 0:
          ContentModel rowContent = this.content.get(row);
          if (rowContent instanceof FileModel) {
            if (((FileModel) rowContent).getType() != null) {
              return ((FileModel) rowContent).getType().getIcon();
            }
            return "1".equals(ConfigUtils.getConfigProperty("app.iconSize"))
                ? Constantes.FILE_ICON_32
                    : Constantes.FILE_ICON_16;
          }
          if ("..".equals(rowContent.getName())) {
            return "1".equals(ConfigUtils.getConfigProperty("app.iconSize"))
                ? Constantes.UPFOLDER_ICON_32
                    : Constantes.UPFOLDER_ICON_16;
          }
          return "1".equals(ConfigUtils.getConfigProperty("app.iconSize"))
              ? Constantes.FOLDER_ICON_32
                  : Constantes.FOLDER_ICON_16;
        case 1:
          return this.content.get(row).getName();
        case 2:
          if (this.content.get(row) instanceof FileModel) {
            return ((FileModel) this.content.get(row)).getFormatedSize();
          }
          return StringUtils.EMPTY;
        case 3:
          return this.content.get(row).getLastModifiedDate();
      }
    }
    return super.getValueAt(row, column);
  }

  /**
   * Replace the table model content
   *
   * @param list content to set
   */
  public void setContent(List<ContentModel> list) {
    this.content = list;
    this.fireTableDataChanged();
  }

  /**
   * Clear the table model content
   */
  public void clearContent() {
    this.content = new ArrayList<ContentModel>();
    this.fireTableDataChanged();
  }

  /**
   * Return the element at given index
   *
   * @param rowId
   * @return
   */
  public ContentModel getContentAt(int rowId) {
    return this.content.get(rowId);
  }

  /*
   * (non-Javadoc)
   *
   * @see javax.swing.table.DefaultTableModel#getRowCount()
   */
  @Override
  public int getRowCount() {
    if (this.content != null) {
      return this.content.size();
    }
    return 0;
  }

  /*
   * (non-Javadoc)
   *
   * @see javax.swing.table.DefaultTableModel#getColumnCount()
   */
  @Override
  public int getColumnCount() {
    return 4;
  }
}
