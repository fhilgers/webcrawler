package com.github.webcrawler.webpage.component;

public interface Markdownable {

  /**
   * Similar to the toString() methods, but for Markdown.
   *
   * @return The Object as Markdown string.
   */
  String toMarkdown();

  /**
   * Similar to the toString() methods, but for Markdown.
   *
   * @param nestingLevel The indentation of the generated strings.
   * @return The Object as Markdown string.
   */
  String toMarkdown(int nestingLevel);
}
