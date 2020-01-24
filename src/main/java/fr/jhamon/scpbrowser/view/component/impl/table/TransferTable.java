package fr.jhamon.scpbrowser.view.component.impl.table;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import fr.jhamon.scpbrowser.model.TransferModel;
import fr.jhamon.scpbrowser.utils.ErrorUtils;
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
    this.setDefaultRenderer(Date.class, new DateCellRenderer());
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

      if (Desktop.isDesktopSupported()) {

        JMenuItem openItem = new JMenuItem(PropertiesUtils
            .getViewProperty("scpbrowser.transfer.table.action.open"));
        openItem.addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            if (TransferTable.this.getSelectedRow() != -1) {
              TransferModel transfer = ((TransferTableModel) TransferTable.this
                  .getModel())
                      .getContentAt(TransferTable.this.getSelectedRow());
              try {
                Desktop.getDesktop().open(new File(transfer.getLocalDir()));
              } catch (IOException ex) {
                ErrorUtils.showError(
                    PropertiesUtils.getViewProperty(
                        "scpbrowser.dialog.explorer.open.error.message",
                        transfer.getLocalDir()),
                    PropertiesUtils.getViewProperty(
                        "scpbrowser.dialog.explorer.open.error.title"));
              } catch (UnsupportedOperationException ex) {
                ErrorUtils.showError(
                    PropertiesUtils.getViewProperty(
                        "scpbrowser.dialog.explorer.open.error.message",
                        transfer.getLocalDir()),
                    PropertiesUtils.getViewProperty(
                        "scpbrowser.dialog.explorer.open.error.title"));
              } catch (SecurityException ex) {
                ErrorUtils.showError(
                    PropertiesUtils.getViewProperty(
                        "scpbrowser.dialog.explorer.open.error.message",
                        transfer.getLocalDir()),
                    PropertiesUtils.getViewProperty(
                        "scpbrowser.dialog.explorer.open.error.title"));
              }
            }
          }
        });
        popupMenu.add(openItem);

        JMenuItem gotoItem = new JMenuItem(PropertiesUtils
            .getViewProperty("scpbrowser.transfer.table.action.goto"));
        gotoItem.addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            if (TransferTable.this.getSelectedRow() != -1) {
              TransferModel transfer = ((TransferTableModel) TransferTable.this
                  .getModel())
                      .getContentAt(TransferTable.this.getSelectedRow());
              try {
                Desktop.getDesktop().browse(
                    Paths.get(transfer.getLocalDir()).getParent().toUri());
              } catch (IOException ex) {
                ErrorUtils.showError(
                    PropertiesUtils.getViewProperty(
                        "scpbrowser.dialog.explorer.browse.error.message",
                        transfer.getLocalDir()),
                    PropertiesUtils.getViewProperty(
                        "scpbrowser.dialog.explorer.browse.error.title"));
              } catch (UnsupportedOperationException ex) {
                ErrorUtils.showError(
                    PropertiesUtils.getViewProperty(
                        "scpbrowser.dialog.explorer.browse.error.message",
                        transfer.getLocalDir()),
                    PropertiesUtils.getViewProperty(
                        "scpbrowser.dialog.explorer.browse.error.title"));
              } catch (SecurityException ex) {
                ErrorUtils.showError(
                    PropertiesUtils.getViewProperty(
                        "scpbrowser.dialog.explorer.browse.error.message",
                        transfer.getLocalDir()),
                    PropertiesUtils.getViewProperty(
                        "scpbrowser.dialog.explorer.browse.error.title"));
              }
            }
          }
        });
        popupMenu.add(gotoItem);
      }

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
