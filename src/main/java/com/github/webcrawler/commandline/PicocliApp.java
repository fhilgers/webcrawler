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

  @Parameters(paramLabel = "URL", description = "The URL for the webpage to crawl")
  private String url;

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
