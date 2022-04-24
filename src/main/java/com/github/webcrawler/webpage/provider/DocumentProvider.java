package com.github.webcrawler.webpage.provider;

import com.github.webcrawler.webpage.component.Document;
import java.io.IOException;

public interface DocumentProvider {
  Document getDocument(String location) throws IOException;
}
