package com.github.webcrawler.webpage;

import static org.junit.jupiter.api.Assertions.*;

import com.github.webcrawler.translator.TranslationException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StateTest {

  WebPage exhaustiveWebpage;
  WebPage exhaustiveTryWebpage;
  WebPage unfetchableWebpage;
  WebPage untranslatableWebpage;

  @BeforeEach
  void setup() {
    exhaustiveWebpage =
        new WebPage(
            "https://testpage/10-three-child-links-with-headings-and-broken.html",
            5,
            new WebPageTest.JsoupLocalDocumentProvider(),
            new WebPageTest.DummyTranslator());

    exhaustiveTryWebpage =
        new WebPage(
            "https://testpage/10-three-child-links-with-headings-and-broken.html",
            5,
            new WebPageTest.JsoupLocalDocumentProvider(),
            new WebPageTest.DummyTranslator());

    unfetchableWebpage =
        new WebPage(
            "https://testpage/10-three-child-links-with-headings-and-broken.html",
            5,
            new WebPageTest.InvalidDocumentProvider(),
            new WebPageTest.DummyTranslator());

    untranslatableWebpage =
        new WebPage(
            "https://testpage/10-three-child-links-with-headings-and-broken.html",
            5,
            new WebPageTest.JsoupLocalDocumentProvider(),
            new WebPageTest.InvalidTranslator());
  }

  @Nested
  class InitializedStateTest {

    static final String EXHAUSTIVE_WEBPAGE_MARKDOWN =
        """
        input: <a>https://testpage/10-three-child-links-with-headings-and-broken.html</a>
        <br>max depth: 5
        <br>source language: UNKNOWN
        <br>target language: UNKNOWN
        <br>summary:
        Webpage not fetched nor analyzed.""";
    static final String UNFETCHABLE_WEBPAGE_MARKDOWN_AFTER_EXCEPTION =
        EXHAUSTIVE_WEBPAGE_MARKDOWN
            + "\n"
            + """
              Logged Exceptions:
              java.io.IOException: document could not be fetched""";

    @Test
    void fetch() throws IOException {
      exhaustiveWebpage.fetch();

      assertInstanceOf(FetchedState.class, exhaustiveWebpage.getState());
    }

    @Test
    void failFetch() {
      assertThrows(IOException.class, () -> unfetchableWebpage.fetch());

      assertInstanceOf(InitializedState.class, unfetchableWebpage.getState());
    }

    @Test
    void tryFailFetchToMarkdown() {
      unfetchableWebpage.tryFetch();

      String actualMarkdown = unfetchableWebpage.toMarkdown();

      assertEquals(UNFETCHABLE_WEBPAGE_MARKDOWN_AFTER_EXCEPTION, actualMarkdown);
    }

    @Test
    void analyze() {
      assertThrows(IllegalStateException.class, () -> exhaustiveWebpage.analyze());

      assertInstanceOf(InitializedState.class, exhaustiveWebpage.getState());
    }

    @Test
    void translate() {
      assertThrows(IllegalStateException.class, () -> exhaustiveWebpage.translate());

      assertInstanceOf(InitializedState.class, exhaustiveWebpage.getState());
    }

    @Test
    void tryFetch() {
      exhaustiveTryWebpage.tryFetch();

      assertInstanceOf(FetchedState.class, exhaustiveTryWebpage.getState());
    }

    @Test
    void tryAnalyze() {
      exhaustiveTryWebpage.tryAnalyze();

      assertInstanceOf(InitializedState.class, exhaustiveTryWebpage.getState());
    }

    @Test
    void tryTranslate() {
      exhaustiveTryWebpage.tryTranslate();

      assertInstanceOf(InitializedState.class, exhaustiveTryWebpage.getState());
    }

    @Test
    void toMarkdown() {
      String actualMarkdown = exhaustiveWebpage.toMarkdown();

      assertEquals(EXHAUSTIVE_WEBPAGE_MARKDOWN, actualMarkdown);
    }
  }

  @Nested
  class FetchedStateTest {

    static final String EXHAUSTIVE_WEBPAGE_MARKDOWN =
        """
            input: <a>https://testpage/10-three-child-links-with-headings-and-broken.html</a>
            <br>max depth: 5
            <br>source language: UNKNOWN
            <br>target language: UNKNOWN
            <br>summary:
            Webpage not analyzed.""";

    @BeforeEach
    void setup() throws IOException {
      exhaustiveWebpage.fetch();
      exhaustiveTryWebpage.fetch();
    }

    @Test
    void fetch() {
      assertThrows(IllegalStateException.class, () -> exhaustiveWebpage.fetch());

      assertInstanceOf(FetchedState.class, exhaustiveWebpage.getState());
    }

    @Test
    void analyze() {
      exhaustiveWebpage.analyze();

      assertInstanceOf(AnalyzedState.class, exhaustiveWebpage.getState());
    }

    @Test
    void translate() {
      assertThrows(IllegalStateException.class, () -> exhaustiveWebpage.translate());

      assertInstanceOf(FetchedState.class, exhaustiveWebpage.getState());
    }

    @Test
    void toMarkdown() {
      String actualMarkdown = exhaustiveWebpage.toMarkdown();

      assertEquals(EXHAUSTIVE_WEBPAGE_MARKDOWN, actualMarkdown);
    }

    @Test
    void tryFetch() {
      exhaustiveTryWebpage.tryFetch();

      assertInstanceOf(FetchedState.class, exhaustiveTryWebpage.getState());
    }

    @Test
    void tryAnalyze() {
      exhaustiveTryWebpage.tryAnalyze();

      assertInstanceOf(AnalyzedState.class, exhaustiveTryWebpage.getState());
    }

    @Test
    void tryTranslate() {
      exhaustiveTryWebpage.tryTranslate();

      assertInstanceOf(FetchedState.class, exhaustiveTryWebpage.getState());
    }
  }

  @Nested
  class AnalyzedStateTest {

    @BeforeEach
    void setup() throws IOException {
      exhaustiveWebpage.fetch();
      exhaustiveWebpage.analyze();

      exhaustiveTryWebpage.fetch();
      exhaustiveTryWebpage.analyze();

      untranslatableWebpage.fetch();
      untranslatableWebpage.analyze();
    }

    @Test
    void fetch() {
      assertThrows(IllegalStateException.class, () -> exhaustiveWebpage.fetch());

      assertInstanceOf(AnalyzedState.class, exhaustiveWebpage.getState());
    }

    @Test
    void analyze() {
      assertThrows(IllegalStateException.class, () -> exhaustiveWebpage.analyze());

      assertInstanceOf(AnalyzedState.class, exhaustiveWebpage.getState());
    }

    @Test
    void translate() {
      exhaustiveWebpage.translate();

      assertInstanceOf(TranslatedState.class, exhaustiveWebpage.getState());
    }

    @Test
    void failTranslate() {
      assertThrows(TranslationException.class, () -> untranslatableWebpage.translate());

      assertInstanceOf(AnalyzedState.class, untranslatableWebpage.getState());
    }

    @Test
    void tryFetch() {
      exhaustiveTryWebpage.tryFetch();

      assertInstanceOf(AnalyzedState.class, exhaustiveTryWebpage.getState());
    }

    @Test
    void tryAnalyze() {
      exhaustiveTryWebpage.tryAnalyze();

      assertInstanceOf(AnalyzedState.class, exhaustiveTryWebpage.getState());
    }

    @Test
    void tryTranslate() {
      exhaustiveTryWebpage.tryTranslate();

      assertInstanceOf(TranslatedState.class, exhaustiveTryWebpage.getState());
    }
  }

  @Nested
  class TranslatedStateTest {

    @BeforeEach
    void setup() throws IOException {
      exhaustiveWebpage.fetch();
      exhaustiveWebpage.analyze();
      exhaustiveWebpage.translate();

      exhaustiveTryWebpage.fetch();
      exhaustiveTryWebpage.analyze();
      exhaustiveTryWebpage.translate();
    }

    @Test
    void fetch() {
      assertThrows(IllegalStateException.class, () -> exhaustiveWebpage.fetch());

      assertInstanceOf(TranslatedState.class, exhaustiveWebpage.getState());
    }

    @Test
    void analyze() {
      assertThrows(IllegalStateException.class, () -> exhaustiveWebpage.analyze());

      assertInstanceOf(TranslatedState.class, exhaustiveWebpage.getState());
    }

    @Test
    void translate() {
      assertThrows(IllegalStateException.class, () -> exhaustiveWebpage.translate());

      assertInstanceOf(TranslatedState.class, exhaustiveWebpage.getState());
    }

    @Test
    void tryFetch() {
      exhaustiveTryWebpage.tryFetch();

      assertInstanceOf(TranslatedState.class, exhaustiveTryWebpage.getState());
    }

    @Test
    void tryAnalyze() {
      exhaustiveTryWebpage.tryAnalyze();

      assertInstanceOf(TranslatedState.class, exhaustiveTryWebpage.getState());
    }

    @Test
    void tryTranslate() {
      exhaustiveTryWebpage.tryTranslate();

      assertInstanceOf(TranslatedState.class, exhaustiveTryWebpage.getState());
    }
  }
}
