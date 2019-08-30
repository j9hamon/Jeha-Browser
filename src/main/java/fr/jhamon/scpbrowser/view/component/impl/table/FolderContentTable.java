package fr.jhamon.scpbrowser.view.component.impl.table;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableRowSorter;

import fr.jhamon.scpbrowser.model.ContentModel;
import fr.jhamon.scpbrowser.model.FolderModel;
import fr.jhamon.scpbrowser.model.SizeExtension;
import fr.jhamon.scpbrowser.utils.ConfigUtils;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;
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

    TableRowSorter <FolderContentTableModel> sorter = new TableRowSorter<FolderContentTableModel>((FolderContentTableModel) this.getModel());
    this.setRowSorter(sorter);
    sorter.setComparator(2, new Comparator<String>() {

      @Override
      public int compare(String o1, String o2) {
        if (o2 == null) {
          return 1;
        }
        if (o1 == null) {
          return -1;
        }
        if (o2.isEmpty()) {
          return 1;
        }
        if (o1.isEmpty()) {
          return -1;
        }
        String[] splitO1 = o1.split(" ");
        String[] splitO2 = o2.split(" ");
        SizeExtension o1Range = SizeExtension.fromString(splitO1[1]);
        SizeExtension o2Range = SizeExtension.fromString(splitO2[1]);

        if (o1Range == o2Range) {
          return Float.valueOf(splitO1[0].replace(",", ".")).compareTo(Float.valueOf(splitO2[0].replace(",", ".")));
        } else {
          return o1Range.compareTo(o2Range);
        }
      }
    });

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
        if (row >= 0) {
          if (SwingUtilities.isRightMouseButton(mouseEvent)) {
            FolderContentTable.this.setRowSelectionInterval(row, row);
          }
          if (SwingUtilities.isLeftMouseButton(mouseEvent) && mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
            int rowModelId = FolderContentTable.this.convertRowIndexToModel(row);
            final ContentModel content = ((FolderContentTableModel) FolderContentTable.this
                .getModel()).getContentAt(rowModelId);
            if (content instanceof FolderModel) {
              new Thread(new Runnable() {
                @Override
                public void run() {
                  sessionPresenter.onOpenSelectedContent(content);
                }
              }).start();
            }
          }
        }
      }
    });

    this.addPopupMenu(sessionPresenter);
  }

  private void addPopupMenu(final ContentEventHandler sessionPresenter) {
    final JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem downloadItem = new JMenuItem(PropertiesUtils
        .getViewProperty("scpbrowser.table.content.popup.download"));
    downloadItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        int rowModelId = FolderContentTable.this.convertRowIndexToModel(FolderContentTable.this.getSelectedRow());
        final ContentModel content = ((FolderContentTableModel) FolderContentTable.this
            .getModel()).getContentAt(rowModelId);
        new Thread(new Runnable() {
          @Override
          public void run() {
            sessionPresenter.onDownloadSelectedContent(content, true);
          }
        }).start();
      }
    });
    popupMenu.add(downloadItem);
    JMenuItem downloadAsItem = new JMenuItem(PropertiesUtils
        .getViewProperty("scpbrowser.table.content.popup.download.as"));
    downloadAsItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        int rowModelId = FolderContentTable.this.convertRowIndexToModel(FolderContentTable.this.getSelectedRow());
        final ContentModel content = ((FolderContentTableModel) FolderContentTable.this
            .getModel()).getContentAt(rowModelId);
        new Thread(new Runnable() {
          @Override
          public void run() {
            sessionPresenter.onDownloadSelectedContent(content, false);
          }
        }).start();
      }
    });
    popupMenu.add(downloadAsItem);
    JMenuItem renameItem = new JMenuItem(PropertiesUtils
        .getViewProperty("scpbrowser.table.content.popup.rename"));
    renameItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        int rowModelId = FolderContentTable.this.convertRowIndexToModel(FolderContentTable.this.getSelectedRow());
        final ContentModel content = ((FolderContentTableModel) FolderContentTable.this
            .getModel()).getContentAt(rowModelId);
        String newName = JOptionPane.showInputDialog(null, PropertiesUtils.getViewProperty("scpbrowser.dialog.content.rename.message"),content.getName());
        if (newName != null) {
          final ContentModel newContent = new ContentModel(newName, content.getPath(), null);
          new Thread(new Runnable() {
            @Override
            public void run() {
              sessionPresenter.onMoveSelectedContent(content, newContent);
            }
          }).start();
        }
      }
    });
    popupMenu.add(renameItem);
    JMenuItem deleteItem = new JMenuItem(PropertiesUtils
        .getViewProperty("scpbrowser.table.content.popup.delete"));
    deleteItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        int rowModelId = FolderContentTable.this.convertRowIndexToModel(FolderContentTable.this.getSelectedRow());
        final ContentModel content = ((FolderContentTableModel) FolderContentTable.this
            .getModel()).getContentAt(rowModelId);
        int dialogResult = JOptionPane.showConfirmDialog(null,
            PropertiesUtils.getViewProperty(content instanceof FolderModel ? "scpbrowser.dialog.content.remove.folder.message"
                : "scpbrowser.dialog.content.remove.file.message", content.getName()),
            "Warning", JOptionPane.YES_NO_OPTION);

        if (dialogResult == JOptionPane.YES_OPTION) {
          new Thread(new Runnable() {
            @Override
            public void run() {
              sessionPresenter.onDeleteSelectedContent(content);
            }
          }).start();
        }
      }
    });
    popupMenu.add(deleteItem);
    this.setComponentPopupMenu(popupMenu);
  }

}
