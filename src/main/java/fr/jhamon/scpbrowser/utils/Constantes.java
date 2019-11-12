package fr.jhamon.scpbrowser.utils;

import java.util.regex.Pattern;

import javax.swing.ImageIcon;

/**
 * Application constantes
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public final class Constantes {

  public static final String VERSION = "v:1.4.3";

  public static final int CONSOLE_MAX_CHARACTERS = 1000;

  public static final ImageIcon FILE_ICON_16 = IconUtils
      .createImageIcon("/file-extension_16/file_extension_file.png");
  public static final ImageIcon FILE_ICON_32 = IconUtils
      .createImageIcon("/file-extension_16/file_extension_file.png");
  public static final ImageIcon FOLDER_ICON_16 = IconUtils
      .createImageIcon("/file-extension_16/file_extension_folder.png");
  public static final ImageIcon FOLDER_ICON_32 = IconUtils
      .createImageIcon("/file-extension_32/file_extension_folder.png");
  public static final ImageIcon UPFOLDER_ICON_16 = IconUtils
      .createImageIcon("/file-extension_16/file_extension_upfolder.png");
  public static final ImageIcon UPFOLDER_ICON_32 = IconUtils
      .createImageIcon("/file-extension_32/file_extension_upfolder.png");

  public static final String KNOWNHOSTS_FILE = ".known_hosts";

  public static final Pattern WINDOWS_DRIVE_PATTERN = Pattern
      .compile("(([a-zA-Z]):\\/)");
  public static final String UNIX_DRIVE_TEMPLATE = "/cygdrive/%s/";

  public static final int TIMEOUT_DOWNLOAD = 2500;
  public static final int TIMEOUT_UPLOAD = 2500;
  public static final int TIMEOUT_CONNECT = 2500;
  public static final int TIMEOUT_COMMAND = 2500;

  private Constantes() {
  }
}
