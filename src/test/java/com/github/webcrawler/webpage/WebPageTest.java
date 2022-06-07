package com.github.webcrawler.webpage;

import static org.junit.jupiter.api.Assertions.*;

import com.github.webcrawler.translator.Language;
import com.github.webcrawler.translator.TranslationException;
import com.github.webcrawler.translator.Translator;
import com.github.webcrawler.webpage.component.Document;
import com.github.webcrawler.webpage.component.JsoupDocument;
import com.github.webcrawler.webpage.provider.DocumentProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

class WebPageTest {

  private static final String PAGE_DIRECTORY =
      WebPageTest.class.getResource("/dummy-pages").getPath();
  private static final String MARKDOWN_DIRECTORY = PAGE_DIRECTORY + "/markdown";

  private static final String EXPECTED_MARKDOWN_WITH_CHILDREN =
      resourceToString(MARKDOWN_DIRECTORY + "/to-markdown-with-children.md");
  private static final String EXPECTED_TRANSLATED_MARKDOWN_WITH_CHILDREN =
      resourceToString(MARKDOWN_DIRECTORY + "/to-markdown-translated-with-children.md");
  private static final String EXPECTED_MARKDOWN_WITHOUT_CHILDREN =
      resourceToString(MARKDOWN_DIRECTORY + "/to-markdown-without-children.md");

  static class JsoupLocalDocumentProvider implements DocumentProvider {

    @Override
    public Document getDocument(String location) throws IOException {
      String path = location.replace("https://testpage", PAGE_DIRECTORY);

      File file = new File(path);

      return new JsoupDocument(Jsoup.parse(file, null));
    }
  }

  static class InvalidDocumentProvider implements DocumentProvider {

    @Override
    public Document getDocument(String location) throws IOException {
      throw new IOException("document could not be fetched");
    }
  }

  static class DummyTranslator implements Translator {

    @Override
    public Result translate(List<String> texts) {
      List<String> translatedTexts =
          texts.stream().map(text -> "This text was translated.").toList();
      return new Result(Language.ENGLISH, Language.ENGLISH, translatedTexts);
    }
  }

  static class InvalidTranslator implements Translator {

    @Override
    public Result translate(List<String> texts) throws TranslationException {
      throw new TranslationException("texts could not be translated");
    }
  }

  private static String resourceToString(String path) {
    try {
      return new String(Files.readAllBytes(Paths.get(path)));
    } catch (IOException ignored) {
      return null;
    }
  }

  @Test
  void toMarkdownTranslatedWithChildren() throws IOException {
    WebPage webPage =
        new WebPage(
            "https://testpage/10-three-child-links-with-headings-and-broken.html",
            5,
            new JsoupLocalDocumentProvider(),
            new DummyTranslator());

    webPage.fetch();
    webPage.analyze();
    webPage.translate();
    String generatedMarkdown = webPage.toMarkdown();

    assertEquals(EXPECTED_TRANSLATED_MARKDOWN_WITH_CHILDREN, generatedMarkdown);
  }

  @Test
  void toMarkdownWithoutChildren() throws IOException {
    WebPage webPage =
        new WebPage(
            "https://testpage/10-three-child-links-with-headings-and-broken.html",
            0,
            new JsoupLocalDocumentProvider(),
            new DummyTranslator());

    webPage.fetch();
    webPage.analyze();
    String generatedMarkdown = webPage.toMarkdown();

    assertEquals(EXPECTED_MARKDOWN_WITHOUT_CHILDREN, generatedMarkdown);
  }

  @Test
  void toMarkdownWithChildren() throws IOException {
    WebPage webPage =
        new WebPage(
            "https://testpage/10-three-child-links-with-headings-and-broken.html",
            5,
            new JsoupLocalDocumentProvider(),
            new DummyTranslator());

    webPage.fetch();
    webPage.analyze();
    String generatedMarkdown = webPage.toMarkdown();

    assertEquals(EXPECTED_MARKDOWN_WITH_CHILDREN, generatedMarkdown);
  }
}
