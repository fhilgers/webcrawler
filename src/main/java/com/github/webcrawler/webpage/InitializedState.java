package com.github.webcrawler.webpage;

import com.github.webcrawler.translator.Translator;
import java.io.IOException;

public class InitializedState extends State {

  public InitializedState(WebPage webPage) {
    super(webPage);
  }

  @Override
  void analyze() throws IOException {

    webPage.loadHtmlDocument();
    webPage.extractHeadings();
    webPage.extractLinks();
    webPage.updateSeenLinks();

    if (webPage.getDepth() < webPage.getMaxDepth()) {
      webPage.analyzeChildren();
    }

    webPage.changeState(new AnalyzedState(webPage));
  }

  @Override
  void translate(Translator translator) {
    throw new IllegalStateException("Webpage has to be analyzed prior to translation.");
  }
}
