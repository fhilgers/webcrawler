package com.github.webcrawler.webpage.component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * A facade to the Jsoup Document implementation which provides convenient access to Heading and
 * Link elements.
 *
 * @param bareDocument The Jsoup Document Node to wrap.
 */
public record JsoupDocument(org.jsoup.nodes.Document bareDocument) implements Document {

  public static final String HEADING_CSS_QUERY = "h1, h2, h3, h4, h5, h6";
  public static final String LINK_CSS_QUERY = "a[href]";

  /**
   * Get all headings from a Jsoup Document Node and convert them to a List of Heading objects.
   *
   * @return The List of Headings objects.
   */
  @Override
  public List<Heading> getHeadings() {
    return getHeadingElements().stream()
        .map(JsoupDocument::elementToHeading)
        .collect(Collectors.toList());
  }

  /**
   * Get all headings from a Jsoup Document Node and convert them to a List of Links.
   *
   * @return The List of Links.
   */
  @Override
  public List<Link> getLinks() {
    return getLinkElements().stream()
        .map(JsoupDocument::elementToLink)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private Elements getHeadingElements() {
    return bareDocument.select(HEADING_CSS_QUERY);
  }

  private Elements getLinkElements() {
    return bareDocument.select(LINK_CSS_QUERY);
  }

  private static Heading elementToHeading(Element e) {
    Heading.Level headingLevel = Heading.Level.fromString(e.tagName());

    return new Heading(headingLevel, e.text());
  }

  private static Link elementToLink(Element e) {
    String urlString = e.absUrl("href");

    try {
      return Link.fromString(urlString);
    } catch (IllegalArgumentException ignored) {
      // In this case the urlString is either empty,
      // has no protocol or an invalid protocol and
      // is neither considered as broken link nor as
      // valid link. It is just ignored.
      return null;
    }
  }
}
