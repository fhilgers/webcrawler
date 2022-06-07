package com.github.webcrawler.webpage;

import com.github.webcrawler.translator.Translator;
import java.io.IOException;

public abstract class State {

  protected WebPage webPage;

  public State(WebPage webPage) {
    this.webPage = webPage;
  }

  abstract void analyze() throws IOException;

  abstract void translate(Translator translator) throws IOException;
}
