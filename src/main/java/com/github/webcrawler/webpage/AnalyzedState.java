package com.github.webcrawler.webpage;

import com.github.webcrawler.translator.Translator;
import com.github.webcrawler.webpage.component.Heading;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AnalyzedState extends State {

  public AnalyzedState(WebPage webPage) {
    super(webPage);
  }

  @Override
  void analyze() {
    throw new IllegalStateException("Webpage is already analyzed.");
  }

  @Override
  void translate(Translator translator) {
    List<Heading> allHeadings = webPage.aggregateHeadings();
    List<String> allHeadingTexts = allHeadings.stream().map(Heading::text).toList();

    Translator.Result translationResult = translator.translate(allHeadingTexts);

    List<String> translatedTexts = translationResult.translatedTexts();
    ArrayList<Heading> translatedHeadings =
        IntStream.range(0, allHeadings.size())
            .mapToObj(i -> new Heading(allHeadings.get(i).level(), translatedTexts.get(i)))
            .collect(Collectors.toCollection(ArrayList::new));

    webPage.subdivideHeadings(translatedHeadings);

    webPage.setSourceLanguage(translationResult.sourceLanguage().toString());
    webPage.setTargetLanguage(translationResult.targetLanguage().toString());

    webPage.changeState(new TranslatedState(webPage));
  }
}
