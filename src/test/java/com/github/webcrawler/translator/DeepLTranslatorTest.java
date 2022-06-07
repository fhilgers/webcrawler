package com.github.webcrawler.translator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeepLTranslatorTest {

  @Mock private OkHttpClient mockHttpClient;

  @Mock private Call mockCall;

  @Mock private Response mockResponse;

  private static final class RequestValidator {

    private Request request;
    private FormBody body;

    private final String expectedMethod;
    private final String expectedUrl;
    private final String expectedTargetLanguage;
    private final String expectedAuthKey;
    private final List<String> expectedTexts;

    public RequestValidator(
        String method, String url, String targetLanguage, String authKey, List<String> texts) {
      this.expectedMethod = method;
      this.expectedUrl = url;
      this.expectedTargetLanguage = Language.fromString(targetLanguage).tag;
      this.expectedAuthKey = authKey;
      this.expectedTexts = texts;
    }

    private Stream<String> getValuesByName(String name) {
      return IntStream.range(0, body.size())
          .filter(i -> body.name(i).equals(name))
          .mapToObj(i -> body.value(i));
    }

    private void validateTargetLanguage() {
      assertEquals(expectedTargetLanguage, getValuesByName("target_lang").toList().get(0));
      ;
    }

    private void validateAuthKey() {
      assertEquals(expectedAuthKey, getValuesByName("auth_key").toList().get(0));
    }

    private void validateTexts() {
      assertEquals(expectedTexts, getValuesByName("text").toList());
    }

    private void validateUrl() {
      assertEquals(expectedUrl, request.url().toString());
    }

    private void validateMethod() {
      assertEquals(expectedMethod, request.method());
    }

    public boolean validate(Request request) {

      this.request = request;
      this.body = (FormBody) request.body();

      validateMethod();
      validateUrl();
      validateTargetLanguage();
      validateAuthKey();
      validateTexts();

      return true;
    }
  }

  private static Stream<Arguments> translationArgumentsProvider() {
    return Stream.of(
        Arguments.of(
            "DE",
            List.of("word", "cat"),
            "dummy-auth",
            false,
            DeepLTranslator.DEEPL_FREE_API_URL,
            "EN",
            List.of("Wort", "Katze")),
        Arguments.of(
            "german",
            List.of("word", "cat"),
            "",
            true,
            DeepLTranslator.DEEPL_PRO_API_URL,
            "english",
            List.of("Wort", "Katze")));
  }

  private void setupMocks(
      String targetLanguage,
      List<String> texts,
      String authKey,
      String apiUrl,
      String fakeSourceLanguage,
      List<String> fakeTranslations)
      throws IOException {
    ResponseBody fakeResponseBody =
        ResponseBody.create(
            DeepLTranslator.JsonTranslations.fromLanguageAndTexts(
                    Language.fromString(fakeSourceLanguage), fakeTranslations)
                .toJsonString(),
            MediaType.parse("application/json; charset=utf-8"));

    RequestValidator requestValidator =
        new RequestValidator("POST", apiUrl, targetLanguage, authKey, texts);

    when(mockHttpClient.newCall(argThat(requestValidator::validate))).thenReturn(mockCall);
    when(mockCall.execute()).thenReturn(mockResponse);
    when(mockResponse.body()).thenReturn(fakeResponseBody);
    when(mockResponse.isSuccessful()).thenReturn(true);
  }

  private void validateTranslatorResult(
      Translator.Result translatorResult,
      String fakeSourceLanguage,
      String targetLanguage,
      List<String> fakeTranslations) {
    assertEquals(Language.fromString(fakeSourceLanguage), translatorResult.sourceLanguage());
    assertEquals(Language.fromString(targetLanguage), translatorResult.targetLanguage());
    assertEquals(fakeTranslations, translatorResult.translatedTexts());
  }

  @ParameterizedTest
  @MethodSource("translationArgumentsProvider")
  public void translation(
      String targetLanguage,
      List<String> texts,
      String authKey,
      boolean isPro,
      String apiUrl,
      String fakeSourceLanguage,
      List<String> fakeTranslations)
      throws IOException {
    DeepLTranslator translator =
        new DeepLTranslator(mockHttpClient, targetLanguage, authKey, isPro);
    setupMocks(targetLanguage, texts, authKey, apiUrl, fakeSourceLanguage, fakeTranslations);

    Translator.Result translationResult = translator.translate(texts);

    validateTranslatorResult(
        translationResult, fakeSourceLanguage, targetLanguage, fakeTranslations);
  }

  private static Stream<Arguments> noTranslationArgumentsProvider() {
    return Stream.of(Arguments.of((ArrayList<String>) null), Arguments.of(new ArrayList<String>()));
  }

  @ParameterizedTest
  @MethodSource("noTranslationArgumentsProvider")
  public void noTranslation(List<String> textsToTranslate) throws IOException {
    DeepLTranslator translator = new DeepLTranslator("german", "", false);

    Translator.Result translationResult = translator.translate(textsToTranslate);

    assertEquals(Language.GERMAN, translationResult.sourceLanguage());
    assertEquals(Language.GERMAN, translationResult.targetLanguage());
    assertEquals(0, translationResult.translatedTexts().size());
  }

  @Test
  public void invalidAuthKey() throws IOException {
    DeepLTranslator translator = new DeepLTranslator(mockHttpClient, "german", "", false);

    when(mockHttpClient.newCall(any())).thenReturn(mockCall);
    when(mockCall.execute()).thenReturn(mockResponse);
    when(mockResponse.code()).thenReturn(403);

    assertThrows(
        TranslationException.class,
        () -> translator.translate(List.of("element")),
        "forbidden: invalid auth_key");
  }
}
