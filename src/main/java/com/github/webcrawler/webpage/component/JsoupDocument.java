package com.github.webcrawler.webpage.component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public record JsoupDocument(org.jsoup.nodes.Document bareDocument) implements Document {

  private static final String HEADING_CSS_QUERY = "h1, h2, h3, h4, h5, h6";
  private static final String LINK_CSS_QUERY = "a[href]";

  @Override
  public List<Heading> getHeadings() {
    return getHeadingElements().stream()
        .map(JsoupDocument::elementToHeading)
        .collect(Collectors.toList());
  }

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
