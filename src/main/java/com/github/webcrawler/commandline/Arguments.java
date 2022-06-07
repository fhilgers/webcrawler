package com.github.webcrawler.commandline;

import java.util.List;

public interface Arguments {

  List<String> getUrls();

  int getMaxDepth();

  String getTargetLanguage();

  String getDeeplAuthKey();

  boolean getDeeplIsPro();

  String getOutputFilePath();
}
