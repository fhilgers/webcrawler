package com.github.webcrawler.webpage.util;

public record AsciiArrow(int nestingLevel) {
  static final int DEFAULT_MULTIPLIER = 2;

  static int multiplier = DEFAULT_MULTIPLIER;

  public static void setMultiplier(int newMultiplier) {
    multiplier = newMultiplier;
  }

  @Override
  public String toString() {
    if (nestingLevel <= 0) {
      return "";
    }

    return "-".repeat(nestingLevel * multiplier) + ">";
  }
}
