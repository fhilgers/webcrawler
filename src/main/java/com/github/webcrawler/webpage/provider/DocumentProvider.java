package com.github.webcrawler.webpage.provider;

import com.github.webcrawler.webpage.component.Document;
import java.io.IOException;

/** Interface to make it convenient to fetch Documents from an arbitrary location. */
public interface DocumentProvider {

  /**
   * Retrieve data from an arbitrary location and convert it to a class which implements the
   * Document interface.
   *
   * @param location The location to retrieve the data from.
   * @return The document containing the data.
   * @throws IOException If an error retrieving the data occurs.
   */
  Document getDocument(String location) throws IOException;
}
