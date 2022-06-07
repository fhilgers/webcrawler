package com.github.webcrawler.commandline;

import java.util.List;
import picocli.CommandLine;

@CommandLine.Command(name = "crawler", mixinStandardHelpOptions = true)
public class PicocliArguments implements Arguments {
  @CommandLine.Parameters(
      paramLabel = "URLS",
      arity = "1..*",
      description = "The URL for the webpage to crawl")
  private List<String> urls;

  @CommandLine.Option(
      names = {"-d", "--depth"},
      defaultValue = "2",
      description = "The depth of webpages to crawl recursively (default: ${DEFAULT-VALUE})")
  private int maxDepth;

  @CommandLine.Option(
      names = {"-l", "--language"},
      defaultValue = "EN",
      description = "The language to translate the headings to (default: ${DEFAULT-VALUE})")
  private String targetLanguage;

  @CommandLine.Option(
      names = {"-k", "--auth-key"},
      required = true,
      description = "The auth key for the DeepL API.")
  private String deeplAuthKey;

  @CommandLine.Option(
      names = {"-p", "--pro"},
      defaultValue = "false",
      description = "Whether to use DeepL pro version or not (default: ${DEFAULT-VALUE})")
  private boolean deeplIsPro;

  @CommandLine.Option(
      names = {"-o", "--output-file"},
      defaultValue = "-",
      description =
          "The path to the generated markdown report. Use '-' to print to console (default: ${DEFAULT-VALUE})")
  private String outputFilePath;

  @Override
  public List<String> getUrls() {
    return urls;
  }

  @Override
  public int getMaxDepth() {
    return maxDepth;
  }

  @Override
  public String getTargetLanguage() {
    return targetLanguage;
  }

  @Override
  public String getDeeplAuthKey() {
    return deeplAuthKey;
  }

  @Override
  public boolean getDeeplIsPro() {
    return deeplIsPro;
  }

  @Override
  public String getOutputFilePath() {
    return outputFilePath;
  }
}
