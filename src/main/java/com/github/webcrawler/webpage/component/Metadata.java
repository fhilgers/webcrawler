package com.github.webcrawler.webpage.component;

/**
 * Metadata bundles the important metadata of a Webpage and turns it into a Markdown string.
 *
 * @param link The link of a webpage.
 * @param maxDepth The maximum crawling depth configured for a webpage.
 * @param sourceLanguage The source language of the webpage.
 * @param targetLanguage The target language of the webpage.
 */
public record Metadata(Link link, int maxDepth, String sourceLanguage, String targetLanguage)
    implements Markdownable {

  /** A template for convenient Markdown conversion with .format() */
  private static final String ROOT_MARKDOWN_TEMPLATE =
      """
      input: <a>%s</a>
      <br>max depth: %s
      <br>source language: %s
      <br>target language: %s
      <br>summary:""";

  /**
   * Similar to the toString() methods, but for Markdown.
   *
   * @return The Object as Markdown string.
   */
  @Override
  public String toMarkdown() {
    return toMarkdown(0);
  }

  /**
   * Similar to the toString() methods, but for Markdown.
   *
   * @param nestingLevel The indentation of the generated strings.
   * @return The Object as Markdown string.
   */
  @Override
  public String toMarkdown(int nestingLevel) {
    if (nestingLevel == 0) {
      return ROOT_MARKDOWN_TEMPLATE.formatted(link, maxDepth, sourceLanguage, targetLanguage);
    } else {
      return link.toMarkdown(nestingLevel);
    }
  }
}
