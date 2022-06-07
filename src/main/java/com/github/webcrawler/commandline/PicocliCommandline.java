package com.github.webcrawler.commandline;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class PicocliCommandline implements CommandLine {
  private final picocli.CommandLine commandLine;
  private final Arguments arguments;

  public PicocliCommandline() {
    this.arguments = new PicocliArguments();
    this.commandLine = new picocli.CommandLine(arguments);
  }

  @Override
  public Arguments parseArgs(String[] args) throws CommandlineException {
    try {
      commandLine.parseArgs(args);
    } catch (picocli.CommandLine.ParameterException ex) {
      throw new CommandlineException(ex);
    }

    return arguments;
  }

  @Override
  public boolean isUsageHelpRequested() {
    return commandLine.isUsageHelpRequested();
  }

  @Override
  public String getUsageHelp() {
    return commandLine.getUsageMessage();
  }

  @Override
  public boolean isVersionHelpRequested() {
    return commandLine.isVersionHelpRequested();
  }

  @Override
  public String getVersionHelp() {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try (PrintStream printStream =
        new PrintStream(byteArrayOutputStream, true, StandardCharsets.UTF_8)) {
      commandLine.printVersionHelp(printStream);
    }
    return byteArrayOutputStream.toString();
  }
}
