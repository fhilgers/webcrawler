# Build, Test and Run

See `gradle tasks` for a list of available tasks. Most notably:

- `gradle build` for building
- `gradle test` for testing
- `gradle run` for running

# Usage

```
Usage: crawler [-hpV] [-d=<maxDepth>] -k=<deeplAuthKey> [-l=<targetLanguage>]
               [-o=<outputFilePath>] URL
      URL                  The URL for the webpage to crawl
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
```

# Required Features

- Accept URL, depth of websites to crawl and target language as command line argument
- Provide overview of a website by listing headings and links in the specified target language

  - Show the translated headings
  - Represent depths with proper indentation
  - Record the URL of the crawled sites
  - Highlight broken links

- Recursively analyze links up to a certain depth
- Save the generated overview in a single file
