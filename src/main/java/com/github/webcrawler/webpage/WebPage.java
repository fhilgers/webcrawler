package com.github.webcrawler.webpage;

import com.github.webcrawler.translator.TranslationException;
import com.github.webcrawler.translator.Translator;
import com.github.webcrawler.webpage.component.*;
import com.github.webcrawler.webpage.provider.*;
import java.io.IOException;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * WebPage recursively crawls Documents from a Provider, extracts Links and Headings and checks
 * whether Links are broken or not. It also provides a way to recursively translate all Headings
 * with a Translator.
 */
public class WebPage implements Markdownable {
  private final Link link;
  private final Set<Link> seenLinks;
  private final int maxDepth;
  private final int depth;

  private final DocumentProvider provider;

  private final List<Heading> headings = new ArrayList<>();
  private final Set<Link> links = new LinkedHashSet<>();

  private final Set<Link> brokenLinks = new LinkedHashSet<>();
  private final Set<WebPage> children = new LinkedHashSet<>();

  private State state;
  private Document document;
  String sourceLanguage;
  String targetLanguage;

  public WebPage(String url, int maxDepth, DocumentProvider provider) {
    this(Link.fromString(url), new LinkedHashSet<>(), maxDepth, provider, 0);
  }

  public WebPage(
      Link link, Set<Link> seenLinks, int maxDepth, DocumentProvider provider, int depth) {
    this.state = new InitializedState(this);
    this.link = link;
    this.seenLinks = seenLinks;
    this.seenLinks.add(link);
    this.maxDepth = maxDepth;
    this.provider = provider;
    this.depth = depth;
  }

  /**
   * Fetches the Website and then extracts the headings and the links present. Then recursively
   * visit the links up to the depth configured in the WebPage object and analyze them as well.
   *
   * @throws IOException If an error occurs, fetching the website or any of the children.
   */
  public void analyze() throws IOException {
    this.state.analyze();
  }

  /**
   * Aggregates the headings of itself and its children, translates them and updates all the
   * headings.
   *
   * @param translator The translator to use.
   * @throws TranslationException If error occurs translating.
   */
  public void translate(Translator translator) throws TranslationException {
    this.state.translate(translator);
  }

  /**
   * Similar to the toString() methods, but for Markdown.
   *
   * @return The WebPage as Markdown string.
   */
  @Override
  public String toMarkdown() {

    String markdownMetadata = metadataToMarkdown();
    String markdownHeaders = headingsToMarkdown();
    String markdownBrokenLinks = brokenLinksToMarkdown();
    String markdownChildren = childrenToMarkdown();

    return Stream.of(markdownMetadata, markdownHeaders, markdownBrokenLinks, markdownChildren)
        .filter(Predicate.not(String::isBlank))
        .collect(Collectors.joining("\n\n"));
  }

  /**
   * Similar to the toString() methods, but for Markdown.
   *
   * @param nestingLevel The indentation of the generated strings.
   * @return The WebPage as Markdown string.
   */
  @Override
  public String toMarkdown(int nestingLevel) {
    return toMarkdown();
  }

  public int getDepth() {
    return depth;
  }

  public int getMaxDepth() {
    return maxDepth;
  }

  public void setSourceLanguage(String sourceLanguage) {
    this.sourceLanguage = sourceLanguage;
  }

  public void setTargetLanguage(String targetLanguage) {
    this.targetLanguage = targetLanguage;
  }

  void changeState(State state) {
    this.state = state;
  }

  void loadHtmlDocument() throws IOException {
    this.document = provider.getDocument(this.link.toString());
  }

  void extractHeadings() {
    this.headings.addAll(document.getHeadings());
  }

  void extractLinks() {
    List<Link> allLinks = document.getLinks();
    allLinks.removeAll(seenLinks);

    this.links.addAll(allLinks);
  }

  void updateSeenLinks() {
    seenLinks.add(this.link);
    seenLinks.addAll(this.links);
  }

  void analyzeChildren() {
    children.clear();
    brokenLinks.clear();

    for (Link link : this.links) {
      try {
        WebPage child = new WebPage(link, seenLinks, maxDepth, provider, depth + 1);
        child.analyze();
        children.add(child);
      } catch (IOException e) {
        brokenLinks.add(new Link(link.scheme(), link.host(), link.path(), true));
      }
    }
  }

  List<Heading> aggregateHeadings() {
    return Stream.concat(
            this.headings.stream(),
            children.stream().map(WebPage::aggregateHeadings).flatMap(Collection::stream))
        .toList();
  }

  void subdivideHeadings(ArrayList<Heading> aggegatedHeadings) {
    int headingSize = this.headings.size();
    this.headings.clear();

    this.headings.addAll(aggegatedHeadings.stream().limit(headingSize).toList());

    IntStream.range(0, headingSize).forEach(i -> aggegatedHeadings.remove(0));

    children.forEach(child -> child.subdivideHeadings(aggegatedHeadings));
  }

  private static <T extends Markdownable> String collectionToMarkdown(
      Collection<T> collection, int nestingLevel, String delimiter) {
    return collection.stream()
        .map(markdownable -> markdownable.toMarkdown(nestingLevel))
        .filter(Predicate.not(String::isEmpty))
        .collect(Collectors.joining(delimiter));
  }

  private String headingsToMarkdown() {
    return collectionToMarkdown(headings, depth, "\n");
  }

  private String brokenLinksToMarkdown() {
    return collectionToMarkdown(brokenLinks, depth + 1, "\n");
  }

  private String childrenToMarkdown() {
    return collectionToMarkdown(children, depth + 1, "\n\n<br>\n\n");
  }

  private String metadataToMarkdown() {
    Metadata metadata = new Metadata(link, maxDepth, sourceLanguage, targetLanguage);

    return metadata.toMarkdown(depth);
  }
}
