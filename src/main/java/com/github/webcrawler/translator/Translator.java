package com.github.webcrawler.translator;

import java.io.IOException;
import java.util.List;

public interface Translator {

  record Result(Language sourceLanguage, Language targetLanguage, List<String> translatedTexts) {}

  Result translate(List<String> texts) throws IOException;
}
