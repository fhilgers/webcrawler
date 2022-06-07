package com.github.webcrawler.webpage;

import com.github.webcrawler.webpage.provider.DocumentProvider;

public class TranslatedState extends State {

  public TranslatedState(WebPage webPage) {
    super(webPage);
  }

  @Override
  void fetch(DocumentProvider provider) {
    throw new IllegalStateException("Webpage is already fetched.");
  }

  @Override
  void analyze() {
    throw new IllegalStateException("Webpage is already analyzed.");
  }

  @Override
  void translate() {
    throw new IllegalStateException("Webpage is already translated.");
  }
}
