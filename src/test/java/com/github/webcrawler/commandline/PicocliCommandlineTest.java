package com.github.webcrawler.commandline;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PicocliCommandlineTest {

  CommandLine commandLine = new PicocliCommandline();

  @Test
  void getUsageHelp() {
    String expectedUsageHelp =
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

    String actualUsageHelp = commandLine.getUsageHelp();

    assertEquals(expectedUsageHelp, actualUsageHelp);
  }

  @Test
  void getVersionHelp() {
    String expectedVersionHelp = """
    2.0
    """;

    String actualVersionHelp = commandLine.getVersionHelp();

    assertEquals(expectedVersionHelp, actualVersionHelp);
  }

  @Test
  void isUsageHelpRequestedShort() {
    commandLine.parseArgs(new String[] {"-h"});

    assertTrue(commandLine.isUsageHelpRequested());
  }

  @Test
  void isUsageHelpRequestedLong() {
    commandLine.parseArgs(new String[] {"--help"});

    assertTrue(commandLine.isUsageHelpRequested());
  }

  @Test
  void isVersionHelpRequestedShort() {
    commandLine.parseArgs(new String[] {"-V"});

    assertTrue(commandLine.isVersionHelpRequested());
  }

  @Test
  void isVersionHelpRequestedLong() {
    commandLine.parseArgs(new String[] {"--version"});

    assertTrue(commandLine.isVersionHelpRequested());
  }

  @Test
  void parseArgsValid() {
    Arguments arguments = commandLine.parseArgs(new String[] {"--auth-key=xyz", "url1", "url2"});

    assertEquals("xyz", arguments.getDeeplAuthKey());
    assertEquals(2, arguments.getUrls().size());
    assertEquals("url1", arguments.getUrls().get(0));
    assertEquals("url2", arguments.getUrls().get(1));
  }

  @Test
  void parseArgsInvalid() {
    Throwable t =
        assertThrows(CommandlineException.class, () -> commandLine.parseArgs(new String[] {}));

    assertEquals(
        "picocli.CommandLine$MissingParameterException: Missing required options and parameters: '--auth-key=<deeplAuthKey>', 'URLS'",
        t.getMessage());
  }
}
