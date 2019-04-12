package fr.jhamon.scpbrowser.view.component.impl.table;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import fr.jhamon.scpbrowser.model.TransferModel;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class TransferTable extends JTable {

  private static final long serialVersionUID = 4556755549719142083L;

  public TransferTable() {
    super(new TransferTableModel());
    this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.setRowSorter(null);
    this.setShowGrid(false);
    //    table.setIntercellSpacing(new Dimension(0, 0));
  }

  public void clear() {
    ((TransferTableModel) this.getModel()).clearContent();
  }

  public void add(TransferModel transfer) {
    ((TransferTableModel) this.getModel()).add(transfer);
  }

}
