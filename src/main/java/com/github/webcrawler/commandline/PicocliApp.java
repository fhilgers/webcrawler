package com.github.webcrawler.commandline;

import com.github.webcrawler.translator.DeepLTranslator;
import com.github.webcrawler.translator.Translator;
import com.github.webcrawler.webpage.WebPage;
import com.github.webcrawler.webpage.provider.JsoupDocumentProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@CommandLine.Command(name = "crawler", mixinStandardHelpOptions = true)
public class PicocliApp implements App, Callable<Integer> {

  @Parameters(paramLabel = "URLS", arity = "1..*", description = "The URL for the webpage to crawl")
  private List<String> urls;

  @Option(
      names = {"-d", "--depth"},
      defaultValue = "2",
      description = "The depth of webpages to crawl recursively (default: ${DEFAULT-VALUE})")
  private int maxDepth;

  @Option(
      names = {"-l", "--language"},
      defaultValue = "EN",
      description = "The language to translate the headings to (default: ${DEFAULT-VALUE})")
  private String targetLanguage;

  @Option(
      names = {"-k", "--auth-key"},
      required = true,
      description = "The auth key for the DeepL API.")
  private String deeplAuthKey;

  @Option(
      names = {"-p", "--pro"},
      defaultValue = "false",
      description = "Whether to use DeepL pro version or not (default: ${DEFAULT-VALUE})")
  private boolean deeplIsPro;

  @Option(
      names = {"-o", "--output-file"},
      defaultValue = "-",
      description =
          "The path to the generated markdown report. Use '-' to print to console (default: ${DEFAULT-VALUE})")
  private String outputFilePath;

  private void analyzeTranslateAndWriteMarkdownReport(List<WebPage> webPages, Translator translator)
      throws IOException {
    analyzeAndTranslateWebpages(webPages, translator);
    writeMarkdownReport(webPages);
  }

  private void analyzeAndTranslateWebpages(List<WebPage> webPages, Translator translator)
      throws IOException {
    webPages.parallelStream()
        .forEach(
            webPage -> {
              try {
                webPage.analyze();
                webPage.translate(translator);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }

  private void writeMarkdownReport(List<WebPage> webPages) throws IOException {
    String generatedMarkdown =
        webPages.stream().map(WebPage::toMarkdown).collect(Collectors.joining("\n\n\n"));

    if (outputFilePath.equals("-")) System.out.println(generatedMarkdown);
    else {
      Files.write(Paths.get(outputFilePath), generatedMarkdown.getBytes());
    }
  }

  /**
   * Crawl Webpages recursively, translate the headings and write a Markdown report.
   *
   * @return The exit code of the application.
   * @throws IOException If error occurs either fetching a Webpage or translating its headings.
   */
  @Override
  public Integer call() throws IOException {

    List<WebPage> webPages =
        urls.stream().map(url -> new WebPage(url, maxDepth, new JsoupDocumentProvider())).toList();
    Translator translator = new DeepLTranslator(targetLanguage, deeplAuthKey, deeplIsPro);

    analyzeTranslateAndWriteMarkdownReport(webPages, translator);

    return 0;
  }

  /**
   * Parse commandline arguments and run the application.
   *
   * @param args The commandline arguments.
   * @return The exit code of the application.
   */
  public int execute(String[] args) {
    return new CommandLine(this).execute(args);
  }
}
