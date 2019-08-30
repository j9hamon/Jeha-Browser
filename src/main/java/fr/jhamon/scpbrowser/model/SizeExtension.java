package fr.jhamon.scpbrowser.model;

public enum SizeExtension {
  O(1.0f, "o"),
  KO(1024.0f, "Ko"),
  MO(1024.0f * 1024, "Mo"),
  GO(1024.0f * 1024 *1024, "Go");

  private float divider;
  private String extension;

  private SizeExtension(float divider, String extension) {
    this.divider = divider;
    this.extension = extension;
  }

  public float getDivider() {
    return this.divider;
  }

  public String getExtension() {
    return this.extension;
  }

  public static SizeExtension fromString(String extension) {
    for (SizeExtension size : SizeExtension.values()) {
      if (size.getExtension().equals(extension)) {
        return size;
      }
    }
    return SizeExtension.O;
  }
}