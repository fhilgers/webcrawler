package com.github.webcrawler.webpage.component;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

public class JsoupDocumentTest {

  public static class HtmlSnippets {
    public static final String headingFromOneToSix =
        """
            <h1>Heading 1</h1>
            <h2>Heading 2</h2>
            <h3>Heading 3</h3>
            <h4>Heading 4</h4>
            <h5>Heading 5</h5>
            <h6>Heading 6</h6>
            """;

    public static final String invalidHeadings =
        """
            <h7>Heading 7</h7>
            <h13>Heading 13</h13>
            """;

    public static final String webLink = "<a href=\"http://host/\"></a>";
    public static final String webLinkWithPath = "<a href=\"http://host/path\"></a>";
    public static final String webSecureLink = "<a href=\"https://host/\"></a>";

    public static final String linkWithoutProtocol = "<a href=\"host/path\"></a>";
    public static final String linkWithUnknownProtocol = "<a href=\"ssh://host/path\"></a>";
    public static final String emptyLink = "";
    public static final String nullLink = null;

    public static final String validLinks =
        String.join("\n", webLink, webLinkWithPath, webSecureLink);

    public static final String invalidLinks =
        String.join("\n", linkWithoutProtocol, emptyLink, nullLink, linkWithUnknownProtocol);
  }

  @Test
  public void testValidHeadings() {

    List<Heading> expectedHeadings =
        Stream.of(
                new Heading(Heading.Level.H1, "Heading 1"),
                new Heading(Heading.Level.H2, "Heading 2"),
                new Heading(Heading.Level.H3, "Heading 3"),
                new Heading(Heading.Level.H4, "Heading 4"),
                new Heading(Heading.Level.H5, "Heading 5"),
                new Heading(Heading.Level.H6, "Heading 6"))
            .toList();
    JsoupDocument document = new JsoupDocument(Jsoup.parse(HtmlSnippets.headingFromOneToSix));

    List<Heading> headings = document.getHeadings();

    assertEquals(expectedHeadings, headings);
  }

  @Test
  public void testInvalidHeadings() {
    List<Heading> expectedHeadings = new ArrayList<>();
    JsoupDocument document = new JsoupDocument(Jsoup.parse(HtmlSnippets.invalidHeadings));

    List<Heading> headings = document.getHeadings();

    assertEquals(expectedHeadings, headings);
  }

  @Test
  public void testValidLinks() {
    List<Link> expectedLinks =
        Stream.of(
                new Link("http", "host", "/"),
                new Link("http", "host", "/path"),
                new Link("https", "host", "/"))
            .toList();
    JsoupDocument document = new JsoupDocument(Jsoup.parse(HtmlSnippets.validLinks));

    List<Link> links = document.getLinks();

    assertEquals(expectedLinks, links);
  }

  @Test
  public void testInvalidLinks() {
    List<Link> expectedLinks = new ArrayList<>();
    JsoupDocument document = new JsoupDocument(Jsoup.parse(HtmlSnippets.invalidLinks));

    List<Link> links = document.getLinks();

    assertEquals(expectedLinks, links);
  }
}
