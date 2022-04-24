package com.github.webcrawler.webpage.component;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class MetadataTest {

  private static final String rootMarkdownTemplate =
      """
      input: <a>%s</a>
      <br>max depth: %d
      <br>source language: %s
      <br>target language: %s
      <br>summary:""";

  private static final Metadata dummyMetadata =
      new Metadata(new Link("https", "host", "/path"), 0, "english", "german");

  @Test
  public void testRootToMarkdown() {
    String expectedResult =
        rootMarkdownTemplate.formatted(
            dummyMetadata.link(),
            dummyMetadata.maxDepth(),
            dummyMetadata.sourceLanguage(),
            dummyMetadata.targetLanguage());

    String result = dummyMetadata.toMarkdown();

    assertEquals(expectedResult, result);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 5, 10, 100})
  public void testNestedToMarkdown(int nestingLevel) {
    String expectedResult = dummyMetadata.link().toMarkdown(nestingLevel);

    String result = dummyMetadata.toMarkdown(nestingLevel);

    assertEquals(expectedResult, result);
  }
}
