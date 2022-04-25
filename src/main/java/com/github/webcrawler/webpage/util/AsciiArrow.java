package com.github.webcrawler.webpage.util;

/**
 * A utility record to provide uniform indentation for creating nested Markdown documents.
 *
 * @param nestingLevel The nesting level of the generated arrow. This is multiplied by a static
 *     DEFAULT_MULTIPLIER and defines the length of the arrow.
 */
public record AsciiArrow(int nestingLevel) {
  static final int DEFAULT_MULTIPLIER = 2;

  static int multiplier = DEFAULT_MULTIPLIER;

  /**
   * Set the multiplier to use for all objects of this class to calculate the length of the arrow.
   *
   * @param newMultiplier The new multiplier.
   */
  public static void setMultiplier(int newMultiplier) {
    multiplier = newMultiplier;
  }

  /**
   * Multiply nesting level and multiplier to generate a new arrow with this length.
   *
   * @return The new arrow as string.
   */
  @Override
  public String toString() {
    if (nestingLevel <= 0) {
      return "";
    }

    return "-".repeat(nestingLevel * multiplier) + ">";
  }
}
