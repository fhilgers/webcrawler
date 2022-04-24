package com.github.webcrawler.webpage.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.webcrawler.webpage.util.AsciiArrow;
import java.util.stream.Stream;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class LinkTest {

  private static Stream<Arguments> toMarkdownProvider() {
    return Stream.of(
        Arguments.of(Link.MARKDOWN_TEMPLATE, 0, new Link("https", "host", "/path")),
        Arguments.of(Link.MARKDOWN_TEMPLATE, 5, new Link("https", "host", "/path")),
        Arguments.of(Link.BROKEN_MARKDOWN_TEMPLATE, 5, new Link("https", "host", "/path", true)));
  }

  @ParameterizedTest
  @MethodSource("toMarkdownProvider")
  public void testToMarkdown(String template, int nestingLevel, Link link) {
    String expectedResult = template.formatted(new AsciiArrow(nestingLevel), link);

    String result = link.toMarkdown(nestingLevel);

    assertEquals(expectedResult, result);
  }

  private static Stream<Arguments> illegalArgumentsProvider() {
    return Stream.of(
        Arguments.of(null, "", "", false),
        Arguments.of(null, "host", "/path", true),
        Arguments.of("", "host", "/path", false),
        Arguments.of("notHttpOrHttps", "host", "/path", false));
  }

  @ParameterizedTest
  @MethodSource("illegalArgumentsProvider")
  public void testIllegalArguments(String scheme, String host, String path, boolean isBroken) {
    assertThrows(IllegalArgumentException.class, () -> new Link(scheme, host, path, isBroken));
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.configure()
        .forClass(Link.class)
        .withPrefabValues(String.class, "http", "https")
        .withNonnullFields("scheme")
        .verify();
  }
}
