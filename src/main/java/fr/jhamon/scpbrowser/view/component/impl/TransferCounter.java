package fr.jhamon.scpbrowser.view.component.impl;

import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import fr.jhamon.scpbrowser.utils.IconUtils;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;

public class TransferCounter extends JPanel {

  private static final long serialVersionUID = -4888591994483192536L;

  private JLabel directionIcon;
  private JLabel runningIcon;
  private JLabel successIcon;
  private JLabel failureIcon;
  private JLabel runningCounterLbl;
  private JLabel successCounterLbl;
  private JLabel failureCounterLbl;

  private AtomicInteger runningCounter = new AtomicInteger(0);
  private AtomicInteger successCounter = new AtomicInteger(0);
  private AtomicInteger failureCounter = new AtomicInteger(0);

  public TransferCounter(boolean upload) {
    super();
    this.initComponent(upload);
    this.build();
  }

  private void initComponent(boolean upload) {
    this.directionIcon = new JLabel(IconUtils
        .createImageIcon(upload ? "/upload.png" : "/download.png", 16, 16));
    this.directionIcon.setToolTipText(PropertiesUtils
        .getViewProperty(upload ? "scpbrowser.footerbar.counter.upload"
            : "scpbrowser.footerbar.counter.download"));

    this.runningIcon = new JLabel(
        IconUtils.createImageIcon("/runningTransfer.png", 16, 16));
    this.runningIcon.setEnabled(false);
    this.runningIcon.setToolTipText(PropertiesUtils
        .getViewProperty("scpbrowser.footerbar.counter.running"));
    this.successIcon = new JLabel(IconUtils.createImageIcon("/ok.png", 16, 16));
    this.successIcon.setToolTipText(PropertiesUtils
        .getViewProperty("scpbrowser.footerbar.counter.success"));
    this.failureIcon = new JLabel(
        IconUtils.createImageIcon("/error.png", 16, 16));
    this.failureIcon.setToolTipText(PropertiesUtils
        .getViewProperty("scpbrowser.footerbar.counter.failure"));
    this.runningCounterLbl = new JLabel(String.valueOf(runningCounter));
    this.runningCounterLbl.setToolTipText(PropertiesUtils
        .getViewProperty("scpbrowser.footerbar.counter.running"));
    this.runningCounterLbl.setEnabled(false);
    this.successCounterLbl = new JLabel(String.valueOf(successCounter));
    this.successCounterLbl.setToolTipText(PropertiesUtils
        .getViewProperty("scpbrowser.footerbar.counter.success"));
    this.failureCounterLbl = new JLabel(String.valueOf(failureCounter));
    this.failureCounterLbl.setToolTipText(PropertiesUtils
        .getViewProperty("scpbrowser.footerbar.counter.failure"));
  }

  private void build() {
    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    this.add(this.directionIcon);
    this.add(this.runningCounterLbl);
    this.add(this.runningIcon);
    this.add(this.successCounterLbl);
    this.add(this.successIcon);
    this.add(this.failureCounterLbl);
    this.add(this.failureIcon);

    this.setBorder(BorderFactory.createEtchedBorder());

  }

  public void addFailure() {
    this.failureCounter.incrementAndGet();
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        TransferCounter.this.failureCounterLbl
            .setText(String.valueOf(TransferCounter.this.failureCounter.get()));
      }
    });
  }

  public void addRunning() {
    this.runningCounter.incrementAndGet();
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        TransferCounter.this.runningCounterLbl
            .setText(String.valueOf(TransferCounter.this.runningCounter.get()));
        TransferCounter.this.runningCounterLbl
            .setEnabled(TransferCounter.this.runningCounter.get() > 0);
        TransferCounter.this.runningIcon
            .setEnabled(TransferCounter.this.runningCounter.get() > 0);
      }
    });
  }

  public void removeRunning() {
    synchronized (this.runningCounter) {
      this.runningCounter.decrementAndGet();
      if (this.runningCounter.get() < 0) {
        this.runningCounter.set(0);
      }
    }
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        TransferCounter.this.runningCounterLbl
            .setText(String.valueOf(TransferCounter.this.runningCounter.get()));
        TransferCounter.this.runningCounterLbl
            .setEnabled(TransferCounter.this.runningCounter.get() > 0);
        TransferCounter.this.runningIcon
            .setEnabled(TransferCounter.this.runningCounter.get() > 0);
      }
    });
  }

  public void addSuccess() {
    this.successCounter.incrementAndGet();
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        TransferCounter.this.successCounterLbl
            .setText(String.valueOf(TransferCounter.this.successCounter.get()));
      }
    });
  }

}
