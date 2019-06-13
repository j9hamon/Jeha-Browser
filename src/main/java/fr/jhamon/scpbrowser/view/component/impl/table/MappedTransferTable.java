package fr.jhamon.scpbrowser.view.component.impl.table;

import java.util.Date;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import fr.jhamon.scpbrowser.model.TransferModel;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class MappedTransferTable extends JTable {

  private static final long serialVersionUID = 4556755549719142083L;

  public MappedTransferTable() {
    super(new MappedTransferTableModel());
    this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.setRowSorter(null);
    this.setShowGrid(false);
    //        this.setIntercellSpacing(new Dimension(0, 0));
    this.setDefaultRenderer(Date.class, new DateCellRenderer());
  }

  public void clear() {
    ((MappedTransferTableModel) this.getModel()).clearContent();
  }

  public void add(long id, TransferModel transfer) {
    ((MappedTransferTableModel) this.getModel()).add(id, transfer);
  }

  public void removeTransfer(long id) {
    ((MappedTransferTableModel) this.getModel()).removeById(id);
  }

  public TransferModel getTransfer(long id) {
    return ((MappedTransferTableModel) this.getModel()).getById(id);
  }

}
