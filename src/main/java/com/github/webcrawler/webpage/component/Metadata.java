package com.github.webcrawler.webpage.component;

public record Metadata(Link link, int maxDepth, String sourceLanguage, String targetLanguage)
    implements Markdownable {

  private static final String ROOT_MARKDOWN_TEMPLATE =
      """
      input: <a>%s</a>
      <br>max depth: %s
      <br>source language: %s
      <br>target language: %s
      <br>summary:""";

  @Override
  public String toMarkdown() {
    return toMarkdown(0);
  }

  @Override
  public String toMarkdown(int nestingLevel) {
    if (nestingLevel == 0) {
      return ROOT_MARKDOWN_TEMPLATE.formatted(link, maxDepth, sourceLanguage, targetLanguage);
    } else {
      return link.toMarkdown(nestingLevel);
    }
  }
}
