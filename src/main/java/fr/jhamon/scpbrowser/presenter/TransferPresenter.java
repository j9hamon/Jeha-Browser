package fr.jhamon.scpbrowser.presenter;

import java.time.Instant;
import java.util.Date;

import com.google.common.eventbus.Subscribe;

import fr.jhamon.scpbrowser.eventbus.MainEventBus;
import fr.jhamon.scpbrowser.eventbus.event.DownloadEvent;
import fr.jhamon.scpbrowser.eventbus.event.TransferEvent;
import fr.jhamon.scpbrowser.eventbus.event.UploadEvent;
import fr.jhamon.scpbrowser.model.TransferModel;
import fr.jhamon.scpbrowser.view.component.TransferView;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class TransferPresenter {

  private TransferView view;

  public TransferPresenter() {
    MainEventBus.getInstance().register(this);
  }

  public TransferPresenter(TransferView transferView) {
    this();
    this.view = transferView;
  }

  @Subscribe
  public void onDownloadEvent(DownloadEvent event) {
    Date date = Date.from(Instant.now());
    TransferModel model = new TransferModel();
    model.setType(TransferModel.Type.DOWNLOAD);
    model.setLocalDir(event.getFileDest().getFullPath());
    model.setRemoteDir(event.getFileSrc().getFullPath());
    model.setSize(event.getFileSrc().getSize());
    if (TransferEvent.Status.RUNNING.equals(event.getStatus())) {
      model.setStartDate(date);
      this.view.addRunningTransfer(event.getId(), model);
    } else if (TransferEvent.Status.SUCCESS.equals(event.getStatus())) {
      model.setEndDate(date);
      TransferModel runningTransfer = this.view
          .getRunningTransfer(event.getId());
      if (runningTransfer != null) {
        model.setStartDate(runningTransfer.getStartDate());
      }
      this.view.removeRunningTransfer(event.getId());
      this.view.addSuccessTransfer(model);
    } else if (TransferEvent.Status.FAILURE.equals(event.getStatus())) {
      model.setEndDate(date);
      TransferModel runningTransfer = this.view
          .getRunningTransfer(event.getId());
      if (runningTransfer != null) {
        model.setStartDate(runningTransfer.getStartDate());
      }
      this.view.removeRunningTransfer(event.getId());
      this.view.addFailureTransfer(model);
    }
  }

  @Subscribe
  public void onUploadEvent(UploadEvent event) {
    Date date = Date.from(Instant.now());
    TransferModel model = new TransferModel();
    model.setType(TransferModel.Type.UPLOAD);
    model.setLocalDir(event.getFileSrc().getFullPath().replace('\\', '/'));
    model.setRemoteDir(event.getFileDest().getFullPath());
    model.setSize(event.getFileSrc().getSize());
    if (TransferEvent.Status.RUNNING.equals(event.getStatus())) {
      model.setStartDate(date);
      this.view.addRunningTransfer(event.getId(), model);
    } else if (TransferEvent.Status.SUCCESS.equals(event.getStatus())) {
      model.setEndDate(date);
      TransferModel runningTransfer = this.view
          .getRunningTransfer(event.getId());
      if (runningTransfer != null) {
        model.setStartDate(runningTransfer.getStartDate());
      }
      this.view.removeRunningTransfer(event.getId());
      this.view.addSuccessTransfer(model);
    } else if (TransferEvent.Status.FAILURE.equals(event.getStatus())) {
      model.setEndDate(date);
      TransferModel runningTransfer = this.view
          .getRunningTransfer(event.getId());
      if (runningTransfer != null) {
        model.setStartDate(runningTransfer.getStartDate());
      }
      this.view.removeRunningTransfer(event.getId());
      this.view.addFailureTransfer(model);
    }
  }

}
