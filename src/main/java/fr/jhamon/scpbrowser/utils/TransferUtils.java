package fr.jhamon.scpbrowser.utils;

import java.util.Random;
import java.util.regex.Matcher;

/**
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class TransferUtils {

  private static Random random = new Random();

  private static final float KB = 1024.0f;
  private static final float MB = 1024.0f * KB;
  private static final float GB = 1024.0f * MB;

  public static long getUniqueId() {
    return random.nextLong();
  }

  public static String getFormatedSize(long size) {
    if (size > GB) {
      return String.format("%.1f Go", size / GB);
    } else if (size > MB) {
      return String.format("%.1f Mo", size / MB);
    } else if (size > KB) {
      return String.format("%.1f Ko", size / KB);
    }
    return String.format("%d o", size);
  }

  public static String getCygwinPath(String path) {
    Matcher driveMatcher = Constantes.WINDOWS_DRIVE_PATTERN.matcher(path);
    if (driveMatcher.find()) {
      path = path.replace(driveMatcher.group(1), String.format(
          Constantes.UNIX_DRIVE_TEMPLATE, driveMatcher.group(2).toLowerCase()));
    }
    return path;
  }
}
