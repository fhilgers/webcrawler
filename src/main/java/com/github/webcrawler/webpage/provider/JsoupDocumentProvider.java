package com.github.webcrawler.webpage.provider;

import com.github.webcrawler.webpage.component.Document;
import com.github.webcrawler.webpage.component.JsoupDocument;
import java.io.IOException;
import org.jsoup.Jsoup;

/** Implementation of the DocumentProvider to fetch websites with Jsoup. */
public record JsoupDocumentProvider() implements DocumentProvider {

  /**
   * Retrieve data from an url and convert it to a JSoupDocument which implements the Document
   * interface.
   *
   * @param url The URL for Jsoup to fetch.
   * @return A new Document containing the fetched data.
   * @throws IOException If Jsoup encounters an error while fetching the URL.
   */
  @Override
  public Document getDocument(String url) throws IOException {
    org.jsoup.nodes.Document doc = Jsoup.connect(url).get();

    return new JsoupDocument(doc);
  }
}
