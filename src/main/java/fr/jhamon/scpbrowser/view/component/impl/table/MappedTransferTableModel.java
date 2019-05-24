package fr.jhamon.scpbrowser.view.component.impl.table;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import fr.jhamon.scpbrowser.model.TransferModel;
import fr.jhamon.scpbrowser.utils.IconUtils;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;
import fr.jhamon.scpbrowser.utils.TransferUtils;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class MappedTransferTableModel extends DefaultTableModel {

  private static final long serialVersionUID = -5175941331288756023L;

  private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(
      "yyyy-MM-dd HH:mm:ss.SSS");

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
      .getViewProperty("scpbrowser.transfer.table.column.date.start") };

  private LinkedList<Pair<Long, TransferModel>> content = new LinkedList<Pair<Long, TransferModel>>();

  public MappedTransferTableModel() {
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
        return String.class;
      case 5:
        return String.class;
      default:
        return super.getColumnClass(columnIndex);
    }
  }

  @Override
  public Object getValueAt(int row, int column) {
    if (this.content.size() > row) {
      TransferModel rowContent = this.content.get(row).getRight();
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
          if (rowContent.getEndDate() != null) {
            return DATE_FORMATTER.format(rowContent.getStartDate());
          }
          return StringUtils.EMPTY;
      }
    }
    return null;
  }

  /**
   * Replace the table model content
   *
   * @param list content to set
   */
  public void setContent(LinkedList<Pair<Long, TransferModel>> list) {
    this.content = list;
    this.fireTableDataChanged();
  }

  /**
   * Clear the table model content
   */
  public void clearContent() {
    this.content = new LinkedList<Pair<Long, TransferModel>>();
    this.fireTableDataChanged();
  }

  /**
   * Return the element at given index
   *
   * @param rowId
   * @return
   */
  public Pair<Long, TransferModel> getContentAt(int rowId) {
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

  public void add(long id, TransferModel transfer) {
    this.content.add(0, Pair.of(id, transfer));
    this.fireTableRowsInserted(0, 0);
  }

  public void removeById(long id) {
    for (Pair<Long, TransferModel> pair : this.content) {
      if (pair.getKey() == id) {
        this.content.remove(pair);
        this.fireTableRowsDeleted(0, 0);
        return;
      }
    }
  }

  public TransferModel getById(long id) {
    for (Pair<Long, TransferModel> pair : this.content) {
      if (pair.getKey() == id) {
        return pair.getValue();
      }
    }
    return null;
  }
}
