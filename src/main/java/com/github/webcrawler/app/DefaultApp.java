package com.github.webcrawler.app;

import com.github.webcrawler.commandline.Arguments;
import com.github.webcrawler.commandline.CommandLine;
import com.github.webcrawler.commandline.CommandlineException;
import com.github.webcrawler.commandline.PicocliCommandline;
import com.github.webcrawler.translator.DeepLTranslator;
import com.github.webcrawler.translator.Translator;
import com.github.webcrawler.webpage.WebPage;
import com.github.webcrawler.webpage.provider.JsoupDocumentProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultApp implements App {

  private final CommandLine commandLine = new PicocliCommandline();
  private Arguments parsedArgs;

  /**
   * Crawl Webpages recursively, translate the headings and write a Markdown report.
   *
   * @return The exit code of the application.
   */
  public int execute(String[] args) {

    try {
      this.parsedArgs = commandLine.parseArgs(args);

      if (commandLine.isUsageHelpRequested()) {
        System.out.println(commandLine.getUsageHelp());
        return 0;
      } else if (commandLine.isVersionHelpRequested()) {
        System.out.println(commandLine.getVersionHelp());
        return 0;
      }

      List<WebPage> webPages = initializeWebpages();
      analyzeTranslateAndWriteMarkdownReport(webPages);

    } catch (CommandlineException ex) {
      System.err.println(ex.getMessage());
      return 1;
    } catch (Exception ex) {
      ex.printStackTrace();
      return 1;
    }

    return 0;
  }

  private List<WebPage> initializeWebpages() {
    Translator translator =
        new DeepLTranslator(
            parsedArgs.getTargetLanguage(),
            parsedArgs.getDeeplAuthKey(),
            parsedArgs.getDeeplIsPro());

    return parsedArgs.getUrls().stream()
        .map(
            url ->
                new WebPage(url, parsedArgs.getMaxDepth(), new JsoupDocumentProvider(), translator))
        .toList();
  }

  private void analyzeTranslateAndWriteMarkdownReport(List<WebPage> webPages) throws IOException {
    analyzeAndTranslateWebpages(webPages);
    writeMarkdownReport(webPages);
  }

  private void analyzeAndTranslateWebpages(List<WebPage> webPages) {
    webPages.parallelStream()
        .forEach(
            webPage -> {
              webPage.tryFetch();
              webPage.tryAnalyze();
              webPage.tryTranslate();
            });
  }

  private void writeMarkdownReport(List<WebPage> webPages) throws IOException {
    String generatedMarkdown =
        webPages.stream().map(WebPage::toMarkdown).collect(Collectors.joining("\n\n\n"));

    if (parsedArgs.getOutputFilePath().equals("-")) System.out.println(generatedMarkdown);
    else {
      Files.write(Paths.get(parsedArgs.getOutputFilePath()), generatedMarkdown.getBytes());
    }
  }
}
