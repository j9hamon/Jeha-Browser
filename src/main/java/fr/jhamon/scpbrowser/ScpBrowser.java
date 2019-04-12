package fr.jhamon.scpbrowser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import com.alee.laf.WebLookAndFeel;

import fr.jhamon.scpbrowser.presenter.MainPresenter;
import fr.jhamon.scpbrowser.utils.Constantes;
import fr.jhamon.scpbrowser.utils.ErrorUtils;
import fr.jhamon.scpbrowser.utils.LoggerUtils;
import fr.jhamon.scpbrowser.utils.PropertiesUtils;
import fr.jhamon.scpbrowser.view.component.MainView;
import fr.jhamon.scpbrowser.view.component.impl.MainFrame;

/**
 * Main application.
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class ScpBrowser {

  private MainView view;
  private MainPresenter presenter;
  private Context context;

  public static void main(String[] args) {
    LoggerUtils.debug("=== Application startup ===");
    File knownHostFile = new File(Constantes.KNOWNHOSTS_FILE);
    if (!knownHostFile.exists()) {
      try {
        knownHostFile.createNewFile();
      } catch (IOException e) {
        ErrorUtils.showFatalError(
            PropertiesUtils.getViewProperty(
                "scpbrowser.dialog.init.knownhosts.creation.message"),
            PropertiesUtils.getViewProperty(
                "scpbrowser.dialog.init.knownhosts.creation.title"));
        LoggerUtils.fatal(PropertiesUtils.getViewProperty(
            "scpbrowser.dialog.init.knownhosts.creation.message"), e);
      }
    }
    new ScpBrowser();
  }

  public ScpBrowser() {
    try {
      // set LaF and create view in EDT
      SwingUtilities.invokeAndWait(new Runnable() {
        @Override
        public void run() {
          try {
            WebLookAndFeel.install();
            //            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            //              if ("Nimbus".equals(info.getName())) {
            //                UIManager.setLookAndFeel(info.getClassName());
            //                UIManager.setLookAndFeel(info.getClassName());
            //                break;
            //              }
            //            }
          } catch (Exception e) {
            System.out.println("toto");
          }
          ScpBrowser.this.view = new MainFrame();
        }
      });
    } catch (InvocationTargetException e) {
      LoggerUtils.error("UI LaF init exception", e);
    } catch (InterruptedException e) {
      LoggerUtils.error("UI LaF init exception", e);
    }
    this.context = new Context();
    // start main presenter
    this.presenter = new MainPresenter(this.view, this.context);
  }
}
