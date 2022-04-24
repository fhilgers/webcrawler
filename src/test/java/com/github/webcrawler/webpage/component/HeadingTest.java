package com.github.webcrawler.webpage.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class HeadingTest {

  private static Stream<Arguments> headingTagProvider() {
    return Stream.of(
        Arguments.of("h1", "H1", Heading.Level.H1),
        Arguments.of("h2", "H2", Heading.Level.H2),
        Arguments.of("h3", "H3", Heading.Level.H3),
        Arguments.of("h4", "H4", Heading.Level.H4),
        Arguments.of("h5", "H5", Heading.Level.H5),
        Arguments.of("h6", "H6", Heading.Level.H6));
  }

  @ParameterizedTest
  @MethodSource("headingTagProvider")
  public void testValidHeadingLevel(
      String lowercaseTag, String uppercaseTag, Heading.Level expectedLevel) {

    assertEquals(expectedLevel, Heading.Level.fromString(lowercaseTag));
    assertEquals(expectedLevel, Heading.Level.fromString(uppercaseTag));
  }

  @ParameterizedTest
  @ValueSource(strings = {"H7", "h13"})
  public void testInvalidHeadingLevel(String tag) {
    assertThrows(IllegalArgumentException.class, () -> Heading.Level.fromString(tag));
  }

  private static Stream<Arguments> headingLevelToMarkdownProvider() {
    return Stream.of(
        Arguments.of(Heading.Level.H1, 3, "# ------>"),
        Arguments.of(Heading.Level.H2, 1, "## -->"),
        Arguments.of(Heading.Level.H3, 0, "### "),
        Arguments.of(Heading.Level.H4, 2, "#### ---->"),
        Arguments.of(Heading.Level.H5, 1, "##### -->"),
        Arguments.of(Heading.Level.H6, 4, "###### -------->"));
  }

  @ParameterizedTest
  @MethodSource("headingLevelToMarkdownProvider")
  public void testHeadingLevelToMarkdown(
      Heading.Level headingLevel, int nestingLevel, String expectedResult) {
    String result = headingLevel.toMarkdown(nestingLevel);

    assertEquals(expectedResult, result);
  }

  private static Stream<Arguments> toMarkdownProvider() {
    return Stream.of(
        Arguments.of(
            new Heading(Heading.Level.H1, "Heading 1"),
            2,
            Heading.Level.H1.toMarkdown(2) + " " + "Heading 1"),
        Arguments.of(
            new Heading(Heading.Level.H4, "Heading 4"),
            1,
            Heading.Level.H4.toMarkdown(1) + " " + "Heading 4"));
  }

  @ParameterizedTest
  @MethodSource("toMarkdownProvider")
  public void testToMarkdown(Heading heading, int nestingLevel, String expectedResult) {
    String result = heading.toMarkdown(nestingLevel);

    assertEquals(expectedResult, result);
  }

  @Test
  public void levelEqualsContract() {
    EqualsVerifier.forClass(Heading.Level.class).verify();
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(Heading.class).verify();
  }
}
