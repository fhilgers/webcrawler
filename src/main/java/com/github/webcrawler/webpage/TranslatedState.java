package com.github.webcrawler.webpage;

import com.github.webcrawler.translator.Translator;

public class TranslatedState extends State {

  public TranslatedState(WebPage webPage) {
    super(webPage);
  }

  @Override
  void analyze() {
    throw new IllegalStateException("Webpage is already analyzed.");
  }

  @Override
  void translate(Translator translator) {
    throw new IllegalStateException("Webpage is already translated.");
  }
}
