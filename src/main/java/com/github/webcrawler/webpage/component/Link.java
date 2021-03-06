package com.github.webcrawler.webpage.component;

import com.github.webcrawler.webpage.util.AsciiArrow;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * A stripped version of a URL without query and fragment.
 *
 * @param scheme The protocol of a URL (example: https).
 * @param host The host of a URL (example: www.google.com).
 * @param path The path of a URL (example: /index.html).
 * @param isBroken Whether the URL is reachable.
 */
public record Link(String scheme, String host, String path, boolean isBroken)
    implements Markdownable {

  public static final String MARKDOWN_TEMPLATE = """
      <br>%s link to <a>%s</a>
      """;

  /** The template for converting a broken link to Markdown */
  public static final String BROKEN_MARKDOWN_TEMPLATE =
      """
      <br>%s broken link <a>%s</a>
      """;

  /**
   * Creates a new Link which is not broken.
   *
   * @param scheme The protocol of a URL (example: https).
   * @param host The host of a URL (example: www.google.com).
   * @param path The path of a URL (example: /index.html).
   */
  public Link(String scheme, String host, String path) {
    this(scheme, host, path, false);
  }

  public Link {
    try {
      new URL(scheme + "://" + host + path);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  /**
   * Create a new Link from an url.
   *
   * @param urlString The url to strip and convert to a Link.
   * @return A new Link representing the stripped URL.
   */
  public static Link fromString(String urlString) {
    try {
      URL url = new URL(urlString);
      return new Link(url.getProtocol(), url.getHost(), url.getPath(), false);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  /**
   * Similar to the toString() methods, but for Markdown.
   *
   * @return The Object as Markdown string.
   */
  @Override
  public String toMarkdown() {
    return toMarkdown(0);
  }

  /**
   * Similar to the toString() methods, but for Markdown.
   *
   * @param nestingLevel The indentation of the generated strings.
   * @return The Object as Markdown string.
   */
  @Override
  public String toMarkdown(int nestingLevel) {
    if (isBroken) {
      return BROKEN_MARKDOWN_TEMPLATE.formatted(new AsciiArrow(nestingLevel), this);
    } else {
      return MARKDOWN_TEMPLATE.formatted(new AsciiArrow(nestingLevel), this);
    }
  }

  @Override
  public String toString() {
    return scheme + "://" + host + path;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Link link = (Link) o;

    return Objects.equals(scheme, link.scheme)
        && Objects.equals(host, link.host)
        && Objects.equals(path, link.path)
        && Objects.equals(isBroken, link.isBroken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scheme, host, path, isBroken);
  }
}
