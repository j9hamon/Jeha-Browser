package fr.jhamon.scpbrowser.view.component.impl.table;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import fr.jhamon.scpbrowser.model.ContentModel;
import fr.jhamon.scpbrowser.utils.ConfigUtils;
import fr.jhamon.scpbrowser.view.component.ContentViewer;
import fr.jhamon.scpbrowser.view.component.event.handler.ContentEventHandler;

/**
 * Extends the JTable to display ContentModel object and handle user actions
 *
 * @author Trichoko
 * Copyright 2019 J.Hamon
 *
 */
public class FolderContentTable extends JTable implements ContentViewer {

  private static final long serialVersionUID = 4556755549719142083L;

  public FolderContentTable() {
    super(new FolderContentTableModel());
    this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.setAutoCreateRowSorter(true);
    if ("1".equals(ConfigUtils.getConfigProperty("app.iconSize"))) {
      this.setRowHeight(34);
    }
    this.setShowVerticalLines(false);
  }

  @Override
  public void clear() {

    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        ((FolderContentTableModel) FolderContentTable.this.getModel())
        .clearContent();
      }
    });

  }

  /*
   * (non-Javadoc)
   *
   * @see
   * fr.jhamon.scpbrowser.view.component.ContentViewer#setContent(java.util.
   * List)
   */
  @Override
  public void setContent(final List<ContentModel> content) {

    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        ((FolderContentTableModel) FolderContentTable.this.getModel())
        .setContent(content);
      }
    });

  }

  @Override
  public ContentModel getSelectectedContent() {
    if (this.getSelectedRow() == -1) {
      return null;
    }
    int rowModelId = this.convertRowIndexToModel(this.getSelectedRow());
    return ((FolderContentTableModel) this.getModel()).getContentAt(rowModelId);
  }

  @Override
  public void setEventHandler(final ContentEventHandler sessionPresenter) {
    // mouse listener to either move to the selected folder or download the
    // selected file
    this.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent mouseEvent) {
        JTable table = (JTable) mouseEvent.getSource();
        Point point = mouseEvent.getPoint();
        int row = table.rowAtPoint(point);
        if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
          int rowModelId = FolderContentTable.this.convertRowIndexToModel(row);
          final ContentModel content = ((FolderContentTableModel) FolderContentTable.this
              .getModel()).getContentAt(rowModelId);
          new Thread(new Runnable() {
            @Override
            public void run() {
              sessionPresenter.onSelectedContent(content);
            }
          }).start();
        }
      }
    });
  }

}
