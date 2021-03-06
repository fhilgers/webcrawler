package com.github.webcrawler.webpage;

import com.github.webcrawler.webpage.provider.DocumentProvider;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class State {

  protected WebPage webPage;

  public State(WebPage webPage) {
    this.webPage = webPage;
  }

  abstract void fetch(DocumentProvider provider) throws IOException;

  abstract void analyze();

  abstract void translate();

  String toMarkdown() {
    String markdownMetadata = webPage.metadataToMarkdown();
    String markdownExceptions = webPage.exceptionsToMarkdown();
    String markdownHeaders = webPage.headingsToMarkdown();
    String markdownBrokenLinks = webPage.brokenLinksToMarkdown();
    String markdownChildren = webPage.childrenToMarkdown();

    return Stream.of(
            markdownMetadata,
            markdownHeaders,
            markdownBrokenLinks,
            markdownChildren,
            markdownExceptions)
        .filter(Predicate.not(String::isBlank))
        .collect(Collectors.joining("\n\n"));
  }
}
