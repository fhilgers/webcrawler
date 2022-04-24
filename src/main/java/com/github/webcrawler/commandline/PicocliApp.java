package com.github.webcrawler.commandline;

import com.github.webcrawler.translator.DeepLTranslator;
import com.github.webcrawler.translator.Translator;
import com.github.webcrawler.webpage.WebPage;
import com.github.webcrawler.webpage.provider.JsoupDocumentProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@CommandLine.Command(name = "crawler", mixinStandardHelpOptions = true)
public class PicocliApp implements App, Callable<Integer> {

  @Parameters(paramLabel = "URL", description = "url for the webpage to crawl")
  private String url;

  @Option(
      names = {"-d", "--depth"},
      defaultValue = "2",
      description = "depth to crawl recursively")
  private int maxDepth;

  @Option(
      names = {"-l", "--language"},
      defaultValue = "EN",
      description = "language to translate to")
  private String targetLanguage;

  @Option(
      names = {"-k", "--auth-key"},
      required = true,
      description = "deepl api auth_key")
  private String deeplAuthKey;

  @Option(
      names = {"-p", "--pro"},
      defaultValue = "false",
      description = "use deepl pro version")
  private boolean deeplIsPro;

  @Option(
      names = {"-o", "--output-file"},
      defaultValue = "-",
      description = "path to the generated markdown, '-' for stdout")
  private String outputFilePath;

  private void translateAndWriteMarkdownReport(WebPage webPage, Translator translator)
      throws IOException {
    webPage.translate(translator);
    String generatedMarkdown = webPage.toMarkdown();

    if (outputFilePath.equals("-")) System.out.println(generatedMarkdown);
    else {
      Files.write(Paths.get(outputFilePath), generatedMarkdown.getBytes());
    }
  }

  @Override
  public Integer call() throws IOException {

    WebPage webPage = new WebPage(url, maxDepth, new JsoupDocumentProvider());
    Translator translator = new DeepLTranslator(targetLanguage, deeplAuthKey, deeplIsPro);

    translateAndWriteMarkdownReport(webPage, translator);

    return 0;
  }

  public int execute(String[] args) {
    return new CommandLine(this).execute(args);
  }
}
