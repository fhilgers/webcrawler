package com.github.webcrawler.webpage.component;

import java.util.List;

/** Facade interface to provide Headings and Links to a Webpage */
public interface Document {

  /** @return The Headings in the Document. */
  List<Heading> getHeadings();

  /** @return Links in the Document. */
  List<Link> getLinks();
}
