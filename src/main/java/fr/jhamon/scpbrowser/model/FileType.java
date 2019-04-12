package fr.jhamon.scpbrowser.model;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import org.apache.commons.lang3.StringUtils;

import fr.jhamon.scpbrowser.utils.ConfigUtils;
import fr.jhamon.scpbrowser.utils.IconUtils;

/**
 * File extension type and associated icon File types are put in a cache when
 * they are created. FileType are compared by their extension
 * (lexicographically)
 *
 * @author J.Hamon
 * Copyright 2019 J.Hamon
 *
 */
public class FileType implements Comparable<FileType> {

  private static Map<String, FileType> loadedTypes = new HashMap<String, FileType>();

  private final Icon icon;

  private final String extension;

  /**
   * @param extension file extension
   * @param icon      associated icon
   */
  private FileType(String extension, Icon icon) {
    this.extension = extension;
    this.icon = icon;
  }

  /**
   * @param extension extension to look for
   * @return the cached FileType matching the extension or a new one if not
   *         found
   */
  public static FileType getFileType(String extension) {
    FileType type = loadedTypes.get(extension);
    if (type != null) {
      return type;
    } else {
      Icon icon = IconUtils.createImageIcon(
          String.format("/file-extension_%s/file_extension_%s.png",
              "1".equals(ConfigUtils.getConfigProperty("app.iconSize")) ? "32"
                  : "16",
                  extension));
      if (icon == null) {
        icon = IconUtils.createImageIcon(
            String.format("/file-extension_%s/file_extension_file.png",
                "1".equals(ConfigUtils.getConfigProperty("app.iconSize")) ? "32"
                    : "16"));
      }
      type = new FileType(extension, icon);
      loadedTypes.put(extension, type);
      return type;
    }
  }

  /**
   * @return the default FileType
   */
  public static FileType getDefaultFileType() {
    return getFileType("file");
  }

  @Override
  public int compareTo(FileType o) {
    if (o == null) {
      return 1;
    }
    return StringUtils.compare(this.extension, o.getExtension());
  }

  /**
   * @return the icon
   */
  public Icon getIcon() {
    return icon;
  }

  /**
   * @return the extension
   */
  public String getExtension() {
    return extension;
  }
}
