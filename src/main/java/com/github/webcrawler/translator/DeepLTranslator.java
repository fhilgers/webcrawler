package com.github.webcrawler.translator;

import com.google.gson.*;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import okhttp3.*;

/**
 * @param client The OkHttpClient to use for communication with the api.
 * @param targetLanguage The language to which this Translator should translate.
 * @param authKey The auth_key for the DeepL api.
 * @param apiUrl The api url for the DeepL api.
 */
public record DeepLTranslator(
    OkHttpClient client, Language targetLanguage, String authKey, String apiUrl)
    implements Translator {

  /**
   * @param client client The OkHttpClient to use for communication with the api.
   * @param targetLanguage The language to which this Translator should translate.
   * @param authKey The auth_key for the DeepL api.
   * @param isPro Whether to use the pro api or the free api.
   */
  public DeepLTranslator(
      OkHttpClient client, String targetLanguage, String authKey, boolean isPro) {
    this(
        client,
        Language.fromString(targetLanguage),
        authKey,
        isPro ? DEEPL_PRO_API_URL : DEEPL_FREE_API_URL);
  }

  /**
   * Creates a new Translator with default OkHttpClient.
   *
   * @param targetLanguage The language to which this Translator should translate.
   * @param authKey The auth_key for the DeepL api.
   * @param isPro Whether to use the pro api or the free api.
   */
  public DeepLTranslator(String targetLanguage, String authKey, boolean isPro) {
    this(new OkHttpClient(), targetLanguage, authKey, isPro);
  }

  static final String DEEPL_FREE_API_URL = "https://api-free.deepl.com/v2/translate";
  static final String DEEPL_PRO_API_URL = "https://api.deepl.com/v2/translate";

  /**
   * JsonTranslations handles the DeepL api result which is a List of json encoded JsonTranslation
   * Objects.
   */
  public static final class JsonTranslations {
    private List<JsonTranslation> translations;

    public JsonTranslations(List<JsonTranslation> translations) {
      this.translations = translations;
    }

    /**
     * Creates a JsonTranslations Object from language and a List of texts.
     *
     * @param language The language to set for each JsonTranslation.
     * @param texts The texts to set for the JsonTranslations.
     * @return The JsonTranslations Object.
     */
    public static JsonTranslations fromLanguageAndTexts(Language language, List<String> texts) {
      String languageTag = language.tag;
      List<JsonTranslation> translations =
          texts.stream().map(text -> new JsonTranslation(languageTag, text)).toList();

      return new JsonTranslations(translations);
    }

    /** @return The serialized JsonTranslations. */
    public String toJsonString() {
      return new Gson().toJson(this);
    }

    /**
     * @param json Json string containing translations.
     * @return The deserialized json String.
     */
    public static JsonTranslations fromJsonString(String json) {
      return new Gson().fromJson(json, JsonTranslations.class);
    }

    /**
     * Create a list of all detected source languages.
     *
     * @return The list of source languages.
     */
    public List<Language> getSourceLanguages() {
      return translations.stream()
          .map(t -> t.detected_source_language)
          .map(Language::fromString)
          .toList();
    }

    /**
     * @param collection The collection to analyze.
     * @param <T> The type of objects in the collection.
     * @return The object with the highest occurrence.
     */
    private static <T> T getElementWithHighestOccurrence(Collection<T> collection) {
      return collection.stream()
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
          .entrySet()
          .stream()
          .max(Map.Entry.comparingByValue())
          .get()
          .getKey();
    }

    /**
     * Get the language with the highest occurrence of all detected source languages.
     *
     * @return The language with the highest occurrence.
     */
    public Language getDominantSourceLanguage() {
      return getElementWithHighestOccurrence(getSourceLanguages());
    }

    /**
     * Get the list of translated texts.
     *
     * @return The list of translated texts.
     */
    public List<String> getTranslatedTexts() {
      return translations.stream().map(t -> t.text).toList();
    }
  }

  /** JsonTranslations is used to deserialize a single translation response of the api. */
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

  private enum Error {
    BAD_REQUEST,
    AUTHORIZATION_FAILED,
    RESOURCE_NOT_FOUND,
    REQUEST_SIZE_EXCEEDS_LIMIT,
    REQUEST_URL_TOO_LONG,
    TOO_MANY_REQUESTS,
    QUOTA_EXCEEDED,
    RESOURCE_UNAVAILABLE,
    INTERNAL_ERROR;

    static Error fromCode(int code) {
      return switch (code) {
        case 400 -> BAD_REQUEST;
        case 403 -> AUTHORIZATION_FAILED;
        case 404 -> RESOURCE_NOT_FOUND;
        case 413 -> REQUEST_SIZE_EXCEEDS_LIMIT;
        case 414 -> REQUEST_URL_TOO_LONG;
        case 429, 529 -> TOO_MANY_REQUESTS;
        case 456 -> QUOTA_EXCEEDED;
        case 503 -> RESOURCE_UNAVAILABLE;
        default -> INTERNAL_ERROR;
      };
    }

    @Override
    public String toString() {
      return switch (this) {
        case BAD_REQUEST -> "Bad request. Please check error message and your parameters.";
        case AUTHORIZATION_FAILED -> "Authorization failed. Please supply a valid auth_key parameter.";
        case RESOURCE_NOT_FOUND -> "The requested resource could not be found.";
        case REQUEST_SIZE_EXCEEDS_LIMIT -> "The request size exceeds the limit.";
        case REQUEST_URL_TOO_LONG -> "The request URL is too long. You can avoid this error by using a POST request instead of a GET request, and sending the parameters in the HTTP body.";
        case TOO_MANY_REQUESTS -> "Too many requests. Please wait and resend your request.";
        case QUOTA_EXCEEDED -> "Quota exceeded. The character limit has been reached.";
        case RESOURCE_UNAVAILABLE -> "Resource currently unavailable. Try again later.";
        case INTERNAL_ERROR -> "Internal error";
      };
    }
  }

  private ResponseBody doRequest(List<String> texts) throws TranslationException {
    RequestBody requestBody = buildRequestBody(texts);
    Request request = buildRequest(requestBody);

    try {
      Response response = client.newCall(request).execute();
      if (!response.isSuccessful()) {
        throw new TranslationException(Error.fromCode(response.code()).toString());
      }

      return response.body();
    } catch (IOException e) {
      throw new TranslationException(e);
    }
  }

  private JsonTranslations extractTranslationResults(ResponseBody body)
      throws TranslationException {
    try {
      String bodyString = body.string();
      return JsonTranslations.fromJsonString(bodyString);
    } catch (IOException e) {
      throw new TranslationException(e);
    }
  }

  /**
   * Translate a list of texts with the DeepL api.
   *
   * @param texts The texts to translate.
   * @return The Result containing source language, target language and the translations.
   * @throws IOException If errors occur on DeepL api requests.
   */
  @Override
  public Result translate(List<String> texts) throws TranslationException {
    if (texts == null || texts.size() == 0)
      return new Result(targetLanguage, targetLanguage, new ArrayList<>());

    ResponseBody responseBody = doRequest(texts);
    JsonTranslations translationResults = extractTranslationResults(responseBody);

    Language sourceLanguage = translationResults.getDominantSourceLanguage();
    List<String> translatedTexts = translationResults.getTranslatedTexts();

    return new Result(sourceLanguage, targetLanguage, translatedTexts);
  }
}
