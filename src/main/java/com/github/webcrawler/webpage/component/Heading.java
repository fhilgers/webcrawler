package com.github.webcrawler.webpage.component;

import com.github.webcrawler.webpage.util.AsciiArrow;
import java.util.Objects;

public record Heading(Level level, String text) implements Markdownable {

  public enum Level implements Markdownable {
    H1,
    H2,
    H3,
    H4,
    H5,
    H6;

    public static Level fromString(String level) {
      return Level.valueOf(level.toUpperCase());
    }

    @Override
    public String toString() {
      return name().toLowerCase();
    }

    @Override
    public String toMarkdown() {
      return toMarkdown(0);
    }

    @Override
    public String toMarkdown(int nestingLevel) {
      return "%s %s".formatted("#".repeat(ordinal() + 1), new AsciiArrow(nestingLevel));
    }
  }

  @Override
  public String toMarkdown() {
    return toMarkdown(0);
  }

  @Override
  public String toMarkdown(int nestingLevel) {
    return "%s %s".formatted(level.toMarkdown(nestingLevel), text);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Heading heading = (Heading) o;

    return Objects.equals(level, heading.level) && Objects.equals(text, heading.text);
  }

  @Override
  public int hashCode() {
    int levelHash = level == null ? 0 : level.hashCode();
    int textHash = text == null ? 0 : text.hashCode();

    return 31 * levelHash + textHash;
  }
}
