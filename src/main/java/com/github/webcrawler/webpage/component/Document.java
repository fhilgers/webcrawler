package com.github.webcrawler.webpage.component;

import java.util.List;

public interface Document {
  List<Heading> getHeadings();

  List<Link> getLinks();
}
