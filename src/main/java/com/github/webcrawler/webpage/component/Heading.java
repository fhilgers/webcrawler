package com.github.webcrawler.webpage.component;

import com.github.webcrawler.webpage.util.AsciiArrow;
import java.util.Objects;

/**
 * Heading represents a Html Heading.
 *
 * @param level The level of the heading (H1 - H6).
 * @param text The text of the heading.
 */
public record Heading(Level level, String text) implements Markdownable {

  /** Level represents the level of a Heading. */
  public enum Level implements Markdownable {
    H1,
    H2,
    H3,
    H4,
    H5,
    H6;

    /**
     * Create a new Level from a String.
     *
     * @param level The string to convert from.
     * @return A new Heading.
     */
    public static Level fromString(String level) {
      return Level.valueOf(level.toUpperCase());
    }

    /** @return The name in lowercase letters. */
    @Override
    public String toString() {
      return name().toLowerCase();
    }

    /**
     * Similar to the toString() methods, but for Markdown.
     *
     * @return The prefix hash symbols for a Markdown header.
     */
    @Override
    public String toMarkdown() {
      return toMarkdown(0);
    }

    /**
     * Similar to the toString() methods, but for Markdown.
     *
     * @param nestingLevel The indentation of the generated headings.
     * @return The prefix hash symbols for a Markdown header with indentation arrow.
     */
    @Override
    public String toMarkdown(int nestingLevel) {
      return "%s %s".formatted("#".repeat(ordinal() + 1), new AsciiArrow(nestingLevel));
    }
  }

  /**
   * Similar to the toString() methods, but for Markdown.
   *
   * @return The Markdown heading.
   */
  @Override
  public String toMarkdown() {
    return toMarkdown(0);
  }

  /**
   * Similar to the toString() methods, but for Markdown.
   *
   * @param nestingLevel The indentation of the generated headings.
   * @return The Markdown heading.
   */
  @Override
  public String toMarkdown(int nestingLevel) {
    return "%s %s".formatted(level.toMarkdown(nestingLevel), text);
  }

  /**
   * @param o The object to compare.
   * @return True if levels and texts match.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Heading heading = (Heading) o;

    return Objects.equals(level, heading.level) && Objects.equals(text, heading.text);
  }

  /** @return The hash of the heading. */
  @Override
  public int hashCode() {
    int levelHash = level == null ? 0 : level.hashCode();
    int textHash = text == null ? 0 : text.hashCode();

    return 31 * levelHash + textHash;
  }
}
