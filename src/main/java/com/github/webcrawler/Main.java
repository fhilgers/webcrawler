package com.github.webcrawler;

import com.github.webcrawler.commandline.PicocliApp;

public class Main {
  public static void main(String[] args) {

    int exitCode = new PicocliApp().execute(args);
    System.exit(exitCode);
  }
}
