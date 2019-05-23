package fr.jhamon.scpbrowser.view.component.impl;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.jhamon.scpbrowser.model.TransferModel;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;
import fr.jhamon.scpbrowser.view.component.TransferView;
import fr.jhamon.scpbrowser.view.component.impl.table.MappedTransferTable;
import fr.jhamon.scpbrowser.view.component.impl.table.TransferTable;

/**
 * @author J.Hamon Copyright 2019 J.Hamon
 *
 */
public class TransferPanel extends JPanel implements TransferView {

  private static final long serialVersionUID = -28215468873134819L;

  private MappedTransferTable runningTransferTable;
  private TransferTable successTransferTable;
  private TransferTable failureTransferTable;

  private JTabbedPane tabs;

  public TransferPanel() {
    super(new BorderLayout());
    this.initComponents();
    this.build();
  }

  private void initComponents() {
    this.runningTransferTable = new MappedTransferTable();
    this.successTransferTable = new TransferTable();
    this.successTransferTable.enablePopupMenu(true);
    this.failureTransferTable = new TransferTable();
    this.failureTransferTable.enablePopupMenu(true);

    this.tabs = new JTabbedPane();
  }

  private void build() {
    this.add(tabs, BorderLayout.CENTER);

    JScrollPane runningScrollPane = new JScrollPane(this.runningTransferTable,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    JScrollPane successScrollPane = new JScrollPane(this.successTransferTable,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    JScrollPane failureScrollPane = new JScrollPane(this.failureTransferTable,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    tabs.addTab(
        PropertiesUtils.getViewProperty("scpbrowser.transfer.tab.running"),
        runningScrollPane);
    tabs.addTab(
        PropertiesUtils.getViewProperty("scpbrowser.transfer.tab.success"),
        successScrollPane);
    tabs.addTab(
        PropertiesUtils.getViewProperty("scpbrowser.transfer.tab.failure"),
        failureScrollPane);

    // remove bold on tab selection
    tabs.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        tabs.setTitleAt(tabs.getSelectedIndex(),
            removeBoldText(tabs.getTitleAt(tabs.getSelectedIndex())));
      }
    });

  }

  @Override
  public void addSuccessTransfer(TransferModel transfer) {
    this.successTransferTable.add(transfer);
    if (tabs.getSelectedIndex() != 1) {
      tabs.setTitleAt(1, setBoldText(
          PropertiesUtils.getViewProperty("scpbrowser.transfer.tab.success")));
    }
  }

  @Override
  public void addFailureTransfer(TransferModel transfer) {
    this.failureTransferTable.add(transfer);
    if (tabs.getSelectedIndex() != 2) {
      tabs.setTitleAt(2, setBoldText(
          PropertiesUtils.getViewProperty("scpbrowser.transfer.tab.failure")));
    }
  }

  @Override
  public void addRunningTransfer(long id, TransferModel transfer) {
    this.runningTransferTable.add(id, transfer);
    if (tabs.getSelectedIndex() != 2) {
      tabs.setTitleAt(0, setBoldText(
          PropertiesUtils.getViewProperty("scpbrowser.transfer.tab.running")));
    }
  }

  @Override
  public void clearRunningTransferList() {
    this.runningTransferTable.clear();
  }

  @Override
  public void clearSuccessTransferList() {
    this.successTransferTable.clear();
  }

  @Override
  public void clearFailureTransferList() {
    this.failureTransferTable.clear();
  }

  @Override
  public void removeRunningTransfer(long id) {
    this.runningTransferTable.removeTransfer(id);
    if (this.runningTransferTable.getRowCount() == 0) {
      this.tabs.setTitleAt(0,
          PropertiesUtils.getViewProperty("scpbrowser.transfer.tab.running"));
    }
  }

  @Override
  public TransferModel getRunningTransfer(long id) {
    return this.runningTransferTable.getTransfer(id);
  }

  private String setBoldText(String text) {
    return "<html><b>" + text + "</html></b>";
  }

  private String removeBoldText(String text) {
    return text.replace("<html><b>", "").replace("<html><b>", "");
  }
}
