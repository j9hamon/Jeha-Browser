package fr.jhamon.scpbrowser.utils;

import java.util.Random;
import java.util.regex.Matcher;

import fr.jhamon.scpbrowser.model.SizeExtension;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class TransferUtils {

  private static Random random = new Random();

  public static long getUniqueId() {
    return random.nextLong();
  }

  public static String getFormatedSize(long size) {
    if (size > SizeExtension.GO.getDivider()) {
      return String.format("%.1f "+SizeExtension.GO.getExtension(), size / SizeExtension.GO.getDivider());
    } else if (size > SizeExtension.MO.getDivider()) {
      return String.format("%.1f "+SizeExtension.MO.getExtension(), size / SizeExtension.MO.getDivider());
    } else if (size > SizeExtension.KO.getDivider()) {
      return String.format("%.1f "+SizeExtension.KO.getExtension(), size / SizeExtension.KO.getDivider());
    }
    return String.format("%d "+SizeExtension.O.getExtension(), size);
  }

  public static String getCygwinPath(String path) {
    path = path.replace('\\', '/');
    Matcher driveMatcher = Constantes.WINDOWS_DRIVE_PATTERN.matcher(path);
    if (driveMatcher.find()) {
      path = path.replace(driveMatcher.group(1), String.format(
          Constantes.UNIX_DRIVE_TEMPLATE, driveMatcher.group(2).toLowerCase()));
    }
    return path;
  }
}
