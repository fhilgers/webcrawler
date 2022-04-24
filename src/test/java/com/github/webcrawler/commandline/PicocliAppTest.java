package com.github.webcrawler.commandline;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

public class PicocliAppTest {

  @Test
  public void translateAndWriteMarkdownReport() {}

  @Test
  public void usageMessage() {
    CommandLine cli = new CommandLine(new PicocliApp());

    String expectedUsageMessage =
        """
        Usage: crawler [-hpV] [-d=<maxDepth>] -k=<deeplAuthKey> [-l=<targetLanguage>]
                       [-o=<outputFilePath>] URL
              URL                  url for the webpage to crawl
          -d, --depth=<maxDepth>   depth to crawl recursively
          -h, --help               Show this help message and exit.
          -k, --auth-key=<deeplAuthKey>
                                   deepl api auth_key
          -l, --language=<targetLanguage>
                                   language to translate to
          -o, --output-file=<outputFilePath>
                                   path to the generated markdown, '-' for stdout
          -p, --pro                use deepl pro version
          -V, --version            Print version information and exit.
        """;

    String usageMessage = cli.getUsageMessage();

    assertEquals(expectedUsageMessage, usageMessage);
  }
}
