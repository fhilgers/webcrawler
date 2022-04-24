package com.github.webcrawler.webpage.component;

public interface Markdownable {

  String toMarkdown();

  String toMarkdown(int nestingLevel);
}
