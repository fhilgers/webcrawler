package com.github.webcrawler;

import com.github.webcrawler.app.DefaultApp;

public class Main {
  public static void main(String[] args) {
    System.exit(new DefaultApp().execute(args));
  }
}
