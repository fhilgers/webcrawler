package com.github.webcrawler.webpage.provider;

import com.github.webcrawler.webpage.component.Document;
import com.github.webcrawler.webpage.component.JsoupDocument;
import java.io.IOException;
import org.jsoup.Jsoup;

public record JsoupDocumentProvider() implements DocumentProvider {

  @Override
  public Document getDocument(String location) throws IOException {
    org.jsoup.nodes.Document doc = Jsoup.connect(location).get();

    return new JsoupDocument(doc);
  }
}
