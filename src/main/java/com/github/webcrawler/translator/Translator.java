package com.github.webcrawler.translator;

import java.util.List;

public interface Translator {

  /**
   * @param sourceLanguage The detected source language.
   * @param targetLanguage The language of the translated texts.
   * @param translatedTexts The translated texts.
   */
  record Result(Language sourceLanguage, Language targetLanguage, List<String> translatedTexts) {}

  /**
   * Translate a list of texts.
   *
   * @param texts The texts to translate.
   * @return The Result containing source language, target language and the translations.
   * @throws TranslationException If errors occur on translation.
   */
  Result translate(List<String> texts) throws TranslationException;
}
