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
  private final Translator translator;

  private final List<Heading> headings = new ArrayList<>();
  private final Set<Link> links = new LinkedHashSet<>();

  private final Set<Link> brokenLinks = new LinkedHashSet<>();
  private final Set<Exception> exceptions = new LinkedHashSet<>();

  private final Set<WebPage> children = new LinkedHashSet<>();

  private State state;
  private Document document;
  String sourceLanguage = "UNKNOWN";
  String targetLanguage = "UNKNOWN";

  public WebPage(String url, int maxDepth, DocumentProvider provider, Translator translator) {
    this(Link.fromString(url), new LinkedHashSet<>(), maxDepth, provider, translator, 0);
  }

  public WebPage(
      Link link,
      Set<Link> seenLinks,
      int maxDepth,
      DocumentProvider provider,
      Translator translator,
      int depth) {
    this.state = new InitializedState(this);
    this.link = link;
    this.seenLinks = seenLinks;
    this.seenLinks.add(link);
    this.maxDepth = maxDepth;
    this.provider = provider;
    this.translator = translator;
    this.depth = depth;
  }

  /**
   * Fetches the Website.
   *
   * @throws IOException If an error occurs fetching the website itself.
   */
  public void fetch() throws IOException {
    this.state.fetch(provider);
  }

  /**
   * Extracts the headings and the links present. Then recursively visit the links up to the depth
   * configured in the WebPage object and analyze them as well.
   */
  public void analyze() {
    this.state.analyze();
  }

  /**
   * Aggregates the headings of itself and its children, translates them and updates all the
   * headings.
   *
   * @throws TranslationException If error occurs translating.
   */
  public void translate() throws TranslationException {
    this.state.translate();
  }

  public void tryFetch() {
    try {
      fetch();
    } catch (Exception e) {
      this.exceptions.add(e);
    }
  }

  public void tryAnalyze() {
    try {
      analyze();
    } catch (Exception e) {
      this.exceptions.add(e);
    }
  }

  public void tryTranslate() {
    try {
      translate();
    } catch (Exception e) {
      this.exceptions.add(e);
    }
  }

  /**
   * Similar to the toString() methods, but for Markdown.
   *
   * @return The WebPage as Markdown string.
   */
  @Override
  public String toMarkdown() {
    return this.state.toMarkdown();
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

  Link getLink() {
    return link;
  }

  Translator getTranslator() {
    return translator;
  }

  int getDepth() {
    return depth;
  }

  int getMaxDepth() {
    return maxDepth;
  }

  public void setDocument(Document document) {
    this.document = document;
  }

  void setSourceLanguage(String sourceLanguage) {
    this.sourceLanguage = sourceLanguage;
  }

  void setTargetLanguage(String targetLanguage) {
    this.targetLanguage = targetLanguage;
  }

  void changeState(State state) {
    this.state = state;
  }

  State getState() {
    return state;
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
        WebPage child = new WebPage(link, seenLinks, maxDepth, provider, translator, depth + 1);
        child.fetch();
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

  String headingsToMarkdown() {
    return collectionToMarkdown(headings, depth, "\n");
  }

  String brokenLinksToMarkdown() {
    return collectionToMarkdown(brokenLinks, depth + 1, "\n");
  }

  String childrenToMarkdown() {
    return collectionToMarkdown(children, depth + 1, "\n\n<br>\n\n");
  }

  String metadataToMarkdown() {
    Metadata metadata = new Metadata(link, maxDepth, sourceLanguage, targetLanguage);

    return metadata.toMarkdown(depth);
  }

  String exceptionsToMarkdown() {
    if (exceptions.size() == 0) {
      return "";
    }
    return "Logged Exceptions:\n"
        + exceptions.stream().map(Throwable::toString).collect(Collectors.joining("\n"));
  }
}
