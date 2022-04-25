package com.github.webcrawler.commandline;

public interface App {

  /**
   * Parse commandline arguments and run the application.
   *
   * @param args The commandline arguments.
   * @return The exit code of the application.
   */
  int execute(String[] args);
}
