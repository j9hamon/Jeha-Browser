package fr.jhamon.scpbrowser.view.component;

import fr.jhamon.scpbrowser.model.TransferModel;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public interface TransferView {

  public void addSuccessTransfer(TransferModel transfer);

  public void addFailureTransfer(TransferModel transfer);

  public void addRunningTransfer(long id, TransferModel transfer);

  public void clearRunningTransferList();

  public void clearSuccessTransferList();

  public void clearFailureTransferList();

  public void removeRunningTransfer(long id);

  public TransferModel getRunningTransfer(long id);

}
