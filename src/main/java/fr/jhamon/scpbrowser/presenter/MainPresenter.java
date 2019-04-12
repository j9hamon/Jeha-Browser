package fr.jhamon.scpbrowser.presenter;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.eventbus.Subscribe;

import fr.jhamon.scpbrowser.Context;
import fr.jhamon.scpbrowser.eventbus.MainEventBus;
import fr.jhamon.scpbrowser.eventbus.event.DownloadEvent;
import fr.jhamon.scpbrowser.eventbus.event.UploadEvent;
import fr.jhamon.scpbrowser.model.SessionConfigModel;
import fr.jhamon.scpbrowser.model.SessionModel;
import fr.jhamon.scpbrowser.model.exception.SessionException;
import fr.jhamon.scpbrowser.ssh.SessionManager;
import fr.jhamon.scpbrowser.ssh.SessionUtils;
import fr.jhamon.scpbrowser.utils.ConfigUtils;
import fr.jhamon.scpbrowser.utils.ErrorUtils;
import fr.jhamon.scpbrowser.utils.LoggerUtils;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;
import fr.jhamon.scpbrowser.view.SessionSelector;
import fr.jhamon.scpbrowser.view.component.MainView;
import fr.jhamon.scpbrowser.view.component.SessionView;
import fr.jhamon.scpbrowser.view.component.event.handler.ManagementUserEventHandler;
import fr.jhamon.scpbrowser.view.component.impl.MainFrame;

/**
 * Main view presenter
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class MainPresenter implements ManagementUserEventHandler {

  private MainView view;
  private Context context;

  private SessionManager sessionManager;
  private BiMap<SessionModel, SessionPresenter> sessionPresenterMap = HashBiMap
      .create();
  private TransferPresenter transferPresenter;

  /**
   * @param view    main view
   * @param context application context
   */
  public MainPresenter(MainView view, Context context) {
    this.sessionManager = new SessionManager();
    this.view = view;
    this.view.setEventHandler(this);
    this.context = context;
    MainEventBus.getInstance().register(this);

    this.transferPresenter = new TransferPresenter(this.view.getTransferView());

    this.view.start(this.context);

    // test if ssh key file exists
    if (ConfigUtils.getConfigProperty("sshKey") == null
        || StringUtils.isBlank(ConfigUtils.getConfigProperty("sshKey"))) {
      LoggerUtils.fatal(PropertiesUtils.getViewProperty(
          "scpbrowser.dialog.config.property.missing.message", "sshKey"));
      ErrorUtils.showFatalError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.config.property.missing.message", "sshKey"),
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.config.property.missing.title"));
    } else {
      File sshKeyFile = new File(ConfigUtils.getConfigProperty("sshKey"));
      if (!sshKeyFile.exists() || sshKeyFile.isDirectory()) {
        LoggerUtils.fatal(PropertiesUtils.getViewProperty(
            "scpbrowser.dialog.key.file.missing.message",
            ConfigUtils.getConfigProperty("sshKey")));
        ErrorUtils.showFatalError(
            PropertiesUtils.getViewProperty(
                "scpbrowser.dialog.key.file.missing.message",
                ConfigUtils.getConfigProperty("sshKey")),
            PropertiesUtils
            .getViewProperty("scpbrowser.dialog.key.file.missing.title"));
      } else {
        // ask for ssh passhphrase
        try {
          final JPasswordField passField = new JPasswordField();
          do {
            SwingUtilities.invokeAndWait(new Runnable() {
              @Override
              public void run() {
                int dialogCode = JOptionPane.showConfirmDialog(
                    (MainFrame) MainPresenter.this.view, passField,
                    PropertiesUtils
                    .getViewProperty("scpbrowser.dialog.passphrase"),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (dialogCode == JOptionPane.CANCEL_OPTION) {
                  LoggerUtils.debug("Startup cancelled by user");
                  System.exit(0);
                }
              }
            });
            LoggerUtils.debug("Passphrase verification...");
            // loop while passphrase is not verified
          } while (!SessionUtils.testPassphrase(passField.getPassword()));
          LoggerUtils.debug("Passphrase verification passed");
          // save passphrase in context
          MainPresenter.this.context.setPassphrase(passField.getPassword());
          this.view.getFooterbar().togglePassphrase(true);
        } catch (InvocationTargetException e) {
          LoggerUtils.fatal("Fatal error during passphrase verification", e);
          System.exit(1);
        } catch (InterruptedException e) {
          LoggerUtils.fatal("Fatal error during passphrase verification", e);
          System.exit(1);
        }
      }
    }
  }

  /**
   * Open the connection selector
   */
  private void openConnectionView() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        SessionSelector selectorView = new SessionSelector();
        final SessionSelectorPresenter presenter = new SessionSelectorPresenter(
            selectorView);
        presenter.start();
        selectorView.addWindowListener(new WindowListener() {
          @Override
          public void windowClosed(WindowEvent e) {
            Thread t = new Thread() {
              @Override
              public void run() {
                SessionConfigModel configModel = presenter.getOutputModel();
                if (configModel != null) {
                  openNewConnection(configModel);
                }
              }
            };
            t.start();
          }

          @Override
          public void windowIconified(WindowEvent e) {
          }

          @Override
          public void windowDeiconified(WindowEvent e) {
          }

          @Override
          public void windowDeactivated(WindowEvent e) {
          }

          @Override
          public void windowClosing(WindowEvent e) {
          }

          @Override
          public void windowActivated(WindowEvent e) {
          }

          @Override
          public void windowOpened(WindowEvent e) {
          }
        });
      }
    });

  }

  /**
   * Start a new session if it doesn't exist and open a new session view
   *
   * @param configModel session configuration to use
   */
  public void openNewConnection(SessionConfigModel configModel) {
    for (Entry<SessionModel, SessionPresenter> entry : this.sessionPresenterMap
        .entrySet()) {
      SessionModel sessionModel = entry.getKey();
      // if session already exists, set focus on view
      if (sessionModel.getConfiguration().equals(configModel)) {
        LoggerUtils.debug("Session already exists for " + configModel);
        this.view.showSessionView(entry.getValue().getView());
        return;
      }
    }
    // create new view
    SessionView newView = this.view.createNewSessionView(configModel.getName());
    try {
      LoggerUtils.debug("Openning new session for " + configModel);
      // open ssh session
      SessionModel newSessionModel = this.sessionManager
          .openNewSession(configModel, context.getPassphrase());
      // start the presenter
      SessionPresenter sessionPresenter = new SessionPresenter(newView,
          newSessionModel, this.context, this.sessionManager);
      this.sessionPresenterMap.put(newSessionModel, sessionPresenter);
      this.view.showSessionView(newView);
    } catch (SessionException e) {
      this.view.removeSessionView(newView);
      ErrorUtils.showError(
          PropertiesUtils.getViewProperty(
              "scpbrowser.dialog.session.create.error.message"),
          PropertiesUtils
          .getViewProperty("scpbrowser.dialog.session.create.error.title"));
      LoggerUtils.error(PropertiesUtils.getViewProperty(
          "scpbrowser.dialog.session.create.error.message") + " " + configModel,
          e);
    }
  }

  @Override
  public void onCloseSessionEvent() {
    final SessionPresenter presenter = this.getActiveSessionViewPresenter();
    if (presenter != null) {
      new Thread(new Runnable() {

        @Override
        public void run() {
          presenter.closeView();
          MainPresenter.this.view.removeSessionView(presenter.getView());
          MainPresenter.this.sessionPresenterMap
          .remove(presenter.getSessionModel());
        }
      }).start();
    }
  }

  @Override
  public void onNewSessionEvent() {
    this.openConnectionView();
  }

  @Override
  public void onRefreshSessionEvent() {
    final SessionPresenter presenter = this.getActiveSessionViewPresenter();
    if (presenter != null) {
      new Thread(new Runnable() {

        @Override
        public void run() {
          presenter.refreshContent();
        }
      }).start();
    }
  }

  @Override
  public void onUploadEvent() {
    final SessionPresenter presenter = this.getActiveSessionViewPresenter();
    if (presenter != null) {
      new Thread(new Runnable() {

        @Override
        public void run() {
          presenter.uploadContent();
        }
      }).start();
    }
  }

  @Override
  public void onOpenConsoleEvent() {
    final SessionPresenter presenter = this.getActiveSessionViewPresenter();
    if (presenter != null) {
      new Thread(new Runnable() {

        @Override
        public void run() {
          presenter.openConsoleView();
        }
      }).start();
    }
  }

  @Override
  public void onChangeSessionEvent() {
    // nothing to do
  }

  /**
   * @return the active session presenter, if any
   */
  private SessionPresenter getActiveSessionViewPresenter() {
    SessionView activeView = this.view.getActiveView();
    if (activeView == null) {
      return null;
    }
    for (SessionPresenter presenter : this.sessionPresenterMap.values()) {
      if (activeView.equals(presenter.getView())) {
        return presenter;
      }
    }
    return null;
  }

  @Override
  public void onExitEvent() {
    LoggerUtils.debug("Application closed by user");
    this.sessionManager.closeAllSessions();
    this.view.close();
    System.exit(0);
  }

  @Override
  public void onStartSessionEvent(SessionConfigModel model) {
    if (model != null) {
      this.openNewConnection(model);
    }
  }

  @Subscribe
  public void onDownloadEvent(DownloadEvent event) {
    switch (event.getStatus()) {
      case RUNNING:
        this.view.getFooterbar().addRunningDownload();
        break;
      case FAILURE:
        this.view.getFooterbar().removeRunningDownload();
        this.view.getFooterbar().addFailedDownload();
        break;
      case SUCCESS:
        this.view.getFooterbar().removeRunningDownload();
        this.view.getFooterbar().addSucceededDownload();
        break;
      default:
        break;
    }
  }

  @Subscribe
  public void onUploadEvent(UploadEvent event) {
    switch (event.getStatus()) {
      case RUNNING:
        this.view.getFooterbar().addRunningUpload();
        break;
      case FAILURE:
        this.view.getFooterbar().removeRunningUpload();
        this.view.getFooterbar().addFailedUpload();
        break;
      case SUCCESS:
        this.view.getFooterbar().removeRunningUpload();
        this.view.getFooterbar().addSucceededUpload();
        break;
      default:
        break;
    }
  }

}
