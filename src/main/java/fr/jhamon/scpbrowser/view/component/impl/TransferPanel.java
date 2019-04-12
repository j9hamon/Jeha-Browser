package fr.jhamon.scpbrowser.view.component.impl;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import fr.jhamon.scpbrowser.model.TransferModel;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;
import fr.jhamon.scpbrowser.view.component.TransferView;
import fr.jhamon.scpbrowser.view.component.impl.table.MappedTransferTable;
import fr.jhamon.scpbrowser.view.component.impl.table.TransferTable;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class TransferPanel extends JPanel implements TransferView {

  private static final long serialVersionUID = -28215468873134819L;

  private MappedTransferTable runningTransferTable;
  private TransferTable successTransferTable;
  private TransferTable failureTransferTable;

  public TransferPanel() {
    super(new BorderLayout());
    this.initComponents();
    this.build();
  }

  private void initComponents() {
    this.runningTransferTable = new MappedTransferTable();
    this.successTransferTable = new TransferTable();
    this.failureTransferTable = new TransferTable();
  }

  private void build() {
    JTabbedPane tabs = new JTabbedPane();
    this.add(tabs, BorderLayout.CENTER);

    JScrollPane runningScrollPane = new JScrollPane(
        this.runningTransferTable,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    //    ((JTable) this.runningTransferTable).setFillsViewportHeight(true);
    //    this.runningTransferTable.setPreferredScrollableViewportSize(new Dimension(
    //        this.runningTransferTable.getPreferredScrollableViewportSize().width,
    //        this.runningTransferTable.getRowHeight()
    //            * this.runningTransferTable.getRowCount()));

    JScrollPane successScrollPane = new JScrollPane(
        this.successTransferTable,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    //    ((JTable) this.successTransferTable).setFillsViewportHeight(true);
    //    this.successTransferTable.setPreferredScrollableViewportSize(new Dimension(
    //        this.successTransferTable.getPreferredScrollableViewportSize().width,
    //        this.successTransferTable.getRowHeight()
    //            * this.successTransferTable.getRowCount()));

    JScrollPane failureScrollPane = new JScrollPane(
        this.failureTransferTable,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    //    ((JTable) this.failureTransferTable).setFillsViewportHeight(true);
    //    this.failureTransferTable.setPreferredScrollableViewportSize(new Dimension(
    //        this.failureTransferTable.getPreferredScrollableViewportSize().width,
    //        this.failureTransferTable.getRowHeight()
    //            * this.failureTransferTable.getRowCount()));

    tabs.addTab(
        PropertiesUtils.getViewProperty("scpbrowser.transfer.tab.running"),
        runningScrollPane);
    tabs.addTab(
        PropertiesUtils.getViewProperty("scpbrowser.transfer.tab.success"),
        successScrollPane);
    tabs.addTab(
        PropertiesUtils.getViewProperty("scpbrowser.transfer.tab.failure"),
        failureScrollPane);
  }

  @Override
  public void addSuccessTransfer(TransferModel transfer) {
    this.successTransferTable.add(transfer);
  }

  @Override
  public void addFailureTransfer(TransferModel transfer) {
    this.failureTransferTable.add(transfer);
  }

  @Override
  public void addRunningTransfer(long id, TransferModel transfer) {
    this.runningTransferTable.add(id, transfer);
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
  }

  @Override
  public TransferModel getRunningTransfer(long id) {
    return this.runningTransferTable.getTransfer(id);
  }

}
