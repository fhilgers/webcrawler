package com.github.webcrawler.webpage;

import com.github.webcrawler.webpage.provider.DocumentProvider;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FetchedState extends State {
  public FetchedState(WebPage webPage) {
    super(webPage);
  }

  @Override
  void fetch(DocumentProvider provider) {
    throw new IllegalStateException("Webpage is already fetched.");
  }

  @Override
  void analyze() {
    webPage.extractHeadings();
    webPage.extractLinks();
    webPage.updateSeenLinks();

    if (webPage.getDepth() < webPage.getMaxDepth()) {
      webPage.analyzeChildren();
    }

    webPage.changeState(new AnalyzedState(webPage));
  }

  @Override
  void translate() {
    throw new IllegalStateException("Webpage has to be analyzed prior to translation.");
  }

  @Override
  String toMarkdown() {
    String markdownMetadata = webPage.metadataToMarkdown();
    String markdownExceptions = webPage.exceptionsToMarkdown();

    return Stream.of(markdownMetadata, "Webpage not analyzed.", markdownExceptions)
        .filter(Predicate.not(String::isBlank))
        .collect(Collectors.joining("\n"));
  }
}
