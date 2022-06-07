package com.github.webcrawler.commandline;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PicocliCommandlineTest {

  @Test
  public void usageMessage() {
    CommandLine cli = new PicocliCommandline();

    String expectedUsageMessage =
        """
        Usage: crawler [-hpV] [-d=<maxDepth>] -k=<deeplAuthKey> [-l=<targetLanguage>]
                       [-o=<outputFilePath>] URLS...
              URLS...              The URL for the webpage to crawl
          -d, --depth=<maxDepth>   The depth of webpages to crawl recursively (default:
                                     2)
          -h, --help               Show this help message and exit.
          -k, --auth-key=<deeplAuthKey>
                                   The auth key for the DeepL API.
          -l, --language=<targetLanguage>
                                   The language to translate the headings to (default:
                                     EN)
          -o, --output-file=<outputFilePath>
                                   The path to the generated markdown report. Use '-'
                                     to print to console (default: -)
          -p, --pro                Whether to use DeepL pro version or not (default:
                                     false)
          -V, --version            Print version information and exit.
        """;

    String usageMessage = cli.getUsageHelp();

    assertEquals(expectedUsageMessage, usageMessage);
  }
}
