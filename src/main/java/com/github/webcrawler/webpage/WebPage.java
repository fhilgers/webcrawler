package com.github.webcrawler.webpage;

import com.github.webcrawler.translator.Translator;
import com.github.webcrawler.webpage.component.*;
import com.github.webcrawler.webpage.provider.*;
import java.io.IOException;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WebPage implements Markdownable {

  private final Link link;
  private final Set<Link> seenLinks;
  private final int maxDepth;
  private final int depth;

  private final DocumentProvider provider;
  private final Document document;

  private final List<Heading> headings = new ArrayList<>();
  private final Set<Link> links = new LinkedHashSet<>();

  private final Set<Link> brokenLinks = new LinkedHashSet<>();
  private final Set<WebPage> children = new LinkedHashSet<>();

  private String sourceLanguage;
  private String targetLanguage;

  public WebPage(String url, int maxDepth, DocumentProvider provider) throws IOException {
    this(Link.fromString(url), new LinkedHashSet<>(), maxDepth, provider, 0);
  }

  public WebPage(Link link, Set<Link> seenLinks, int maxDepth, DocumentProvider provider, int depth)
      throws IOException {
    this.link = link;
    this.seenLinks = seenLinks;
    this.seenLinks.add(link);
    this.maxDepth = maxDepth;
    this.provider = provider;
    this.depth = depth;

    this.document = loadHtmlDocument();

    analyze();
  }

  private void analyze() {

    extractHeadings();
    extractLinks();
    updateSeenLinks();

    if (depth < maxDepth) {
      analyzeChildren();
    }
  }

  private Document loadHtmlDocument() throws IOException {
    return provider.getDocument(this.link.toString());
  }

  private void extractHeadings() {
    this.headings.addAll(document.getHeadings());
  }

  private void extractLinks() {
    List<Link> allLinks = document.getLinks();
    allLinks.removeAll(seenLinks);

    this.links.addAll(allLinks);
  }

  private void updateSeenLinks() {
    seenLinks.add(this.link);
    seenLinks.addAll(this.links);
  }

  private void analyzeChildren() {
    children.clear();
    brokenLinks.clear();

    for (Link link : this.links) {
      try {
        WebPage child = new WebPage(link, seenLinks, maxDepth, provider, depth + 1);
        children.add(child);
      } catch (IOException e) {
        brokenLinks.add(new Link(link.scheme(), link.host(), link.path(), true));
      }
    }
  }

  private List<Heading> aggregateHeadings() {
    return Stream.concat(
            this.headings.stream(),
            children.stream().map(WebPage::aggregateHeadings).flatMap(Collection::stream))
        .toList();
  }

  private void subdivideHeadings(ArrayList<Heading> aggegatedHeadings) {
    int headingSize = this.headings.size();
    this.headings.clear();

    this.headings.addAll(aggegatedHeadings.stream().limit(headingSize).toList());

    IntStream.range(0, headingSize).forEach(i -> aggegatedHeadings.remove(0));

    children.forEach(child -> child.subdivideHeadings(aggegatedHeadings));
  }

  public void translate(Translator translator) throws IOException {
    List<Heading> allHeadings = aggregateHeadings();
    List<String> allHeadingTexts = allHeadings.stream().map(Heading::text).toList();

    Translator.Result translationResult = translator.translate(allHeadingTexts);

    List<String> translatedTexts = translationResult.translatedTexts();
    ArrayList<Heading> translatedHeadings =
        IntStream.range(0, allHeadings.size())
            .mapToObj(i -> new Heading(allHeadings.get(i).level(), translatedTexts.get(i)))
            .collect(Collectors.toCollection(ArrayList::new));

    subdivideHeadings(translatedHeadings);

    this.sourceLanguage = translationResult.sourceLanguage().toString();
    this.targetLanguage = translationResult.targetLanguage().toString();
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

  @Override
  public String toMarkdown(int nestingLevel) {
    return toMarkdown();
  }
}
