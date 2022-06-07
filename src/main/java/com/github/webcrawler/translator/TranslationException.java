package com.github.webcrawler.translator;

public class TranslationException extends RuntimeException {
  public TranslationException(String s) {
    super(s);
  }

  public TranslationException(Throwable t) {
    super(t);
  }
}
