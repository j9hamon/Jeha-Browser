
package fr.jhamon.scpbrowser.presenter;

import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;

import fr.jhamon.scpbrowser.Context;
import fr.jhamon.scpbrowser.model.SessionConfigModel;
import fr.jhamon.scpbrowser.utils.ConfigUtils;
import fr.jhamon.scpbrowser.utils.Constantes;
import fr.jhamon.scpbrowser.view.SessionSelector;
import fr.jhamon.scpbrowser.view.component.event.handler.SessionSelectionEventHandler;

/**
 * Session selector presenter
 *
 * @author J.Hamon Copyright 2019 J.Hamon
 *
 */
public class SessionSelectorPresenter implements SessionSelectionEventHandler {

  private SessionConfigModel outputModel = null;

  private SessionSelector view;

  private Context context;

  /**
   * @param selectorView
   *          associated view
   */
  public SessionSelectorPresenter(SessionSelector selectorView) {
    this.view = selectorView;
    this.view.setEventHandler(this);
  }

  /**
   * Start the view
   */
  public void start() {
    List<SessionConfigModel> configs = ConfigUtils.getSessionConfigs();
    this.view.setSavedSessionsList(configs);
  }

  @Override
  public void onSaveSessionEvent() {
    SessionConfigModel model = new SessionConfigModel(this.view.getNameField().getText(), this.view.getServerField().getText(),
        this.view.getUsernameField().getText(), this.view.getPasswordField().getText(),
        (StringUtils.isNumeric(this.view.getTimeoutConnectField().getText())
            ? Integer.valueOf(this.view.getTimeoutConnectField().getText())
                : Constantes.TIMEOUT_CONNECT),
        (StringUtils.isNumeric(this.view.getTimeoutCommandField().getText())
            ? Integer.valueOf(this.view.getTimeoutCommandField().getText())
                : Constantes.TIMEOUT_COMMAND),
        (StringUtils.isNumeric(this.view.getTimeoutDownloadField().getText())
            ? Integer.valueOf(this.view.getTimeoutDownloadField().getText())
                : Constantes.TIMEOUT_DOWNLOAD),
        (StringUtils.isNumeric(this.view.getTimeoutUploadField().getText())
            ? Integer.valueOf(this.view.getTimeoutUploadField().getText())
                : Constantes.TIMEOUT_UPLOAD));
    if (!ConfigUtils.getSessionConfigs().contains(model)) {
      ConfigUtils.addSessionConfig(model);
      // reload list
      List<SessionConfigModel> configs = ConfigUtils.getSessionConfigs();
      this.view.setSavedSessionsList(configs);
      this.view.setSelectedSession(model);
    }
  }

  @Override
  public void onStartSavedSession() {
    this.outputModel = (SessionConfigModel) this.view.getSavedSessionsBox().getSelectedItem();

    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        SessionSelectorPresenter.this.view.close();
      }
    });
  }

  @Override
  public void onStartNewSession() {
    this.outputModel = new SessionConfigModel(this.view.getNameField().getText(), this.view.getServerField().getText(),
        this.view.getUsernameField().getText(), this.view.getPasswordField().getText(),
        (StringUtils.isNumeric(this.view.getTimeoutConnectField().getText())
            ? Integer.valueOf(this.view.getTimeoutConnectField().getText())
                : Constantes.TIMEOUT_CONNECT),
        (StringUtils.isNumeric(this.view.getTimeoutCommandField().getText())
            ? Integer.valueOf(this.view.getTimeoutCommandField().getText())
                : Constantes.TIMEOUT_COMMAND),
        (StringUtils.isNumeric(this.view.getTimeoutDownloadField().getText())
            ? Integer.valueOf(this.view.getTimeoutDownloadField().getText())
                : Constantes.TIMEOUT_DOWNLOAD),
        (StringUtils.isNumeric(this.view.getTimeoutUploadField().getText())
            ? Integer.valueOf(this.view.getTimeoutUploadField().getText())
                : Constantes.TIMEOUT_UPLOAD));
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        SessionSelectorPresenter.this.view.close();
      }
    });
  }


  /**
   * @return the selected event
   */
  public SessionConfigModel getOutputModel() {
    return outputModel;
  }

}
