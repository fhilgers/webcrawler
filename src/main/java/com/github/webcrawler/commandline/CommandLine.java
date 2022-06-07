package com.github.webcrawler.commandline;

public interface CommandLine {
  Arguments parseArgs(String[] args) throws CommandlineException;

  boolean isUsageHelpRequested();

  String getUsageHelp();

  boolean isVersionHelpRequested();

  String getVersionHelp();
}
