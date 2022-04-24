package com.github.webcrawler.translator;

import com.google.gson.*;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import okhttp3.*;

public record DeepLTranslator(
    OkHttpClient client, Language targetLanguage, String authKey, String apiUrl)
    implements Translator {

  public DeepLTranslator(
      OkHttpClient client, String targetLanguage, String authKey, boolean isPro) {
    this(
        client,
        Language.fromString(targetLanguage),
        authKey,
        isPro ? DEEPL_PRO_API_URL : DEEPL_FREE_API_URL);
  }

  public DeepLTranslator(String targetLanguage, String authKey, boolean isPro) {
    this(new OkHttpClient(), targetLanguage, authKey, isPro);
  }

  static final String DEEPL_FREE_API_URL = "https://api-free.deepl.com/v2/translate";
  static final String DEEPL_PRO_API_URL = "https://api.deepl.com/v2/translate";

  public static final class JsonTranslations {
    private List<JsonTranslation> translations;

    public JsonTranslations(List<JsonTranslation> translations) {
      this.translations = translations;
    }

    public static JsonTranslations fromLanguageAndTexts(Language language, List<String> texts) {
      String languageTag = language.tag;
      List<JsonTranslation> translations =
          texts.stream().map(text -> new JsonTranslation(languageTag, text)).toList();

      return new JsonTranslations(translations);
    }

    public String toJsonString() {
      return new Gson().toJson(this);
    }

    public static JsonTranslations fromJsonString(String json) {
      return new Gson().fromJson(json, JsonTranslations.class);
    }

    public List<Language> getSourceLanguages() {
      return translations.stream()
          .map(t -> t.detected_source_language)
          .map(Language::fromString)
          .toList();
    }

    private static <T> T getElementWithHighestOccurrence(Collection<T> collection) {
      return collection.stream()
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
          .entrySet()
          .stream()
          .max(Map.Entry.comparingByValue())
          .get()
          .getKey();
    }

    public Language getDominantSourceLanguage() {
      return getElementWithHighestOccurrence(getSourceLanguages());
    }

    public List<String> getTranslatedTexts() {
      return translations.stream().map(t -> t.text).toList();
    }
  }

  public static final class JsonTranslation {
    private String detected_source_language;
    private String text;

    public JsonTranslation(String detected_source_language, String text) {
      this.detected_source_language = detected_source_language;
      this.text = text;
    }
  }

  private class DeeplFormBodyBuilder {

    private final FormBody.Builder builder = new FormBody.Builder();

    public void setAuthKey() {
      builder.add("auth_key", authKey);
    }

    public void setLanguage() {
      builder.add("target_lang", targetLanguage.tag);
    }

    public void setText(String text) {
      builder.add("text", text);
    }

    public void setTexts(List<String> texts) {
      if (texts == null) return;

      texts.forEach(this::setText);
    }

    public FormBody build() {
      return builder.build();
    }
  }

  private RequestBody buildRequestBody(List<String> texts) {
    DeeplFormBodyBuilder builder = new DeeplFormBodyBuilder();

    builder.setAuthKey();
    builder.setLanguage();
    builder.setTexts(texts);

    return builder.build();
  }

  private Request buildRequest(RequestBody body) {
    return new Request.Builder().url(apiUrl).post(body).build();
  }

  private ResponseBody doRequest(List<String> texts) throws IOException {
    RequestBody requestBody = buildRequestBody(texts);
    Request request = buildRequest(requestBody);
    Response response = client.newCall(request).execute();

    if (response.code() == 403) {
      throw new IOException("forbidden: invalid auth_key");
    }

    return response.body();
  }

  private JsonTranslations extractTranslationResults(ResponseBody body) throws IOException {
    return JsonTranslations.fromJsonString(body.string());
  }

  @Override
  public Result translate(List<String> texts) throws IOException {
    if (texts == null || texts.size() == 0)
      return new Result(targetLanguage, targetLanguage, new ArrayList<>());

    ResponseBody responseBody = doRequest(texts);
    JsonTranslations translationResults = extractTranslationResults(responseBody);

    Language sourceLanguage = translationResults.getDominantSourceLanguage();
    List<String> translatedTexts = translationResults.getTranslatedTexts();

    return new Result(sourceLanguage, targetLanguage, translatedTexts);
  }
}
