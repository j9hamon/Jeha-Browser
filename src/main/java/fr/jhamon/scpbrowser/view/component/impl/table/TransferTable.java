package fr.jhamon.scpbrowser.view.component.impl.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import fr.jhamon.scpbrowser.model.TransferModel;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;

/**
 * @author J.Hamon Copyright 2019 J.Hamon
 *
 */
public class TransferTable extends JTable {

  private static final long serialVersionUID = 4556755549719142083L;

  public TransferTable() {
    super(new TransferTableModel());
    this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.setRowSorter(null);
    this.setShowGrid(false);
    // table.setIntercellSpacing(new Dimension(0, 0));
  }

  public void clear() {
    ((TransferTableModel) this.getModel()).clearContent();
  }

  public void add(TransferModel transfer) {
    ((TransferTableModel) this.getModel()).add(transfer);
  }

  public void enablePopupMenu(boolean enable) {
    if (enable) {
      final JPopupMenu popupMenu = new JPopupMenu();
      JMenuItem deleteItem = new JMenuItem(PropertiesUtils
          .getViewProperty("scpbrowser.transfer.table.action.clear"));
      deleteItem.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          ((TransferTableModel) TransferTable.this.getModel()).clearContent();
        }
      });
      popupMenu.add(deleteItem);
      this.setComponentPopupMenu(popupMenu);
    } else {
      this.setComponentPopupMenu(null);
    }
  }

}
