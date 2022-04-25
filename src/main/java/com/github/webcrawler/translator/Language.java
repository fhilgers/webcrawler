package com.github.webcrawler.translator;

/** Available languages for DeepL */
public enum Language {
  BULGARIAN("BG"),
  CZECH("CS"),
  DANISH("DA"),
  GERMAN("DE"),
  GREEK("EL"),
  ENGLISH("EN"),
  SPANISH("ES"),
  ESTONIAN("ET"),
  FINNISH("FI"),
  FRENCH("FR"),
  HUNGARIAN("HU"),
  ITALIAN("IT"),
  JAPANESE("JA"),
  LITHUANIAN("LT"),
  LATVIAN("LV"),
  DUTCH("NL"),
  POLISH("PL"),
  PORTUGUESE("PT"),
  ROMANIAN("RO"),
  RUSSIAN("RU"),
  SLOVAK("SK"),
  SLOVENIAN("SL"),
  SWEDISH("SV"),
  CHINESE("ZH");

  final String tag;

  Language(String tag) {
    this.tag = tag;
  }

  /**
   * Turns either the language tag or the actual language name into an enum value.
   *
   * @param nameOrTag The language or tag.
   * @return The matching language.
   */
  public static Language fromString(String nameOrTag) {
    String uppercaseNameOrTag = nameOrTag.toUpperCase();

    for (Language language : values()) {
      if (language.tag.equals(uppercaseNameOrTag)) {
        return language;
      }
    }

    return Language.valueOf(uppercaseNameOrTag);
  }

  /** @return The lowercase language name. */
  @Override
  public String toString() {
    return name().toLowerCase();
  }
}
