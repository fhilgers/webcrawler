package com.github.webcrawler.webpage;

import com.github.webcrawler.webpage.provider.DocumentProvider;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InitializedState extends State {

  public InitializedState(WebPage webPage) {
    super(webPage);
  }

  @Override
  void fetch(DocumentProvider provider) throws IOException {
    webPage.setDocument(provider.getDocument(webPage.getLink().toString()));
    webPage.changeState(new FetchedState(webPage));
  }

  @Override
  void analyze() {
    throw new IllegalStateException("Webpage has to fetched prior to analyzing.");
  }

  @Override
  void translate() {
    throw new IllegalStateException("Webpage has to be fetched and analyzed prior to translation.");
  }

  @Override
  String toMarkdown() {
    String markdownMetadata = webPage.metadataToMarkdown();
    String markdownExceptions = webPage.exceptionsToMarkdown();

    return Stream.of(markdownMetadata, "Webpage not fetched nor analyzed.", markdownExceptions)
        .filter(Predicate.not(String::isBlank))
        .collect(Collectors.joining("\n"));
  }
}
