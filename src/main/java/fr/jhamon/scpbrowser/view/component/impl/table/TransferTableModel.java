package fr.jhamon.scpbrowser.view.component.impl.table;

import java.util.Date;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

import fr.jhamon.scpbrowser.model.TransferModel;
import fr.jhamon.scpbrowser.utils.IconUtils;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;
import fr.jhamon.scpbrowser.utils.TransferUtils;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class TransferTableModel extends DefaultTableModel {

  private static final long serialVersionUID = -6574669841142610225L;

  private static final ImageIcon DOWNLOAD_ICON = IconUtils
      .createImageIcon("/download.png", 16, 16);
  private static final ImageIcon UPLOAD_ICON = IconUtils
      .createImageIcon("/upload.png", 16, 16);

  private static final String[] COLUMN_IDS = new String[] {
      PropertiesUtils.getViewProperty("scpbrowser.transfer.table.column.type"),
      PropertiesUtils
      .getViewProperty("scpbrowser.transfer.table.column.file.local"),
      PropertiesUtils
      .getViewProperty("scpbrowser.transfer.table.column.file.remote"),
      PropertiesUtils
      .getViewProperty("scpbrowser.transfer.table.column.file.size"),
      PropertiesUtils
      .getViewProperty("scpbrowser.transfer.table.column.date.start"),
      PropertiesUtils
      .getViewProperty("scpbrowser.transfer.table.column.file.end") };

  private LinkedList<TransferModel> content = new LinkedList<TransferModel>();

  public TransferTableModel() {
    super();
    this.setColumnIdentifiers(COLUMN_IDS);
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
        return String.class;
      case 4:
        return Date.class;
      case 5:
        return Date.class;
      default:
        return super.getColumnClass(columnIndex);
    }
  }

  @Override
  public Object getValueAt(int row, int column) {
    if (this.content.size() > row) {
      TransferModel rowContent = this.content.get(row);
      switch (column) {
        case 0:
          if (TransferModel.Type.DOWNLOAD.equals(rowContent.getType())) {
            return DOWNLOAD_ICON;
          } else {
            return UPLOAD_ICON;
          }
        case 1:
          return rowContent.getLocalDir();
        case 2:
          return rowContent.getRemoteDir();
        case 3:
          return TransferUtils.getFormatedSize(rowContent.getSize());
        case 4:
          return rowContent.getStartDate();
        case 5:
          return rowContent.getEndDate();
        default:
          return super.getValueAt(row, column);
      }
    }
    return null;
  }

  /**
   * Replace the table model content
   *
   * @param list content to set
   */
  public void setContent(LinkedList<TransferModel> list) {
    this.content = list;
    this.fireTableDataChanged();
  }

  /**
   * Clear the table model content
   */
  public void clearContent() {
    this.content = new LinkedList<TransferModel>();
    this.fireTableDataChanged();
  }

  /**
   * Return the element at given index
   *
   * @param rowId
   * @return
   */
  public TransferModel getContentAt(int rowId) {
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
      //      return Math.max(this.content.size(), 4);
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
    return COLUMN_IDS.length;
  }

  public void add(TransferModel transfer) {
    this.content.add(0, transfer);
    this.fireTableRowsInserted(0, 0);
  }
}
