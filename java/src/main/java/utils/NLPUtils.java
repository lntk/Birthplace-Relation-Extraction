package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import config.Constants;
import nerd.nlp.api.*;
import vn.pipeline.Annotation;
import vn.pipeline.VnCoreNLP;

// singleton object
public class NLPUtils {
    private static NLPUtils instance = null;
    private static NLPToolkit nlpToolkit = null;
    private HashMap<String, Integer> wordDictionary = new HashMap<>();
    private HashMap<String, Integer> stopwordDictionary = new HashMap<>();
    private HashMap<String, Integer> punctuations = new HashMap<>();

    private NLPUtils() {
        try {
            wordDictionary = DataProcessing.loadWordDictionary(Constants.VIETNAMESE_WORDS);
            stopwordDictionary = DataProcessing.loadWordDictionary(Constants.STOP_WORDS);
            punctuations = DataProcessing.loadWordDictionary(Constants.PUNCTUATIONS);
            nlpToolkit = NLPManager.getNLPToolkit(NLPManager.NLP_NEWS_VERSION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static NLPUtils getInstance() {
        if (instance == null)
            instance = new NLPUtils();
        return instance;
    }

    public int getWordId(String word) {
        if (wordDictionary.get(word) == null) {
            return 0;
        }
        return wordDictionary.get(word);
    }

    public int getNerID(String type) {
        switch (type) {
            case "PERSON":
                return 1;
            case "LOCATION":
                return 2;
            default:
                return 0;
        }
    }

    static Annotation getDependencyParsing(String sentence) throws IOException {
        String[] annotators = {"wseg", "pos", "parse"};
        VnCoreNLP pipeline = new VnCoreNLP(annotators);
        Annotation annotation = new Annotation(sentence);
        pipeline.annotate(annotation);

        return annotation;
    }

    Document tagNERBasic(String text) {
        return nlpToolkit.tagNER(text);
    }

    public Document tagNER(String text) {
        Document document = nlpToolkit.tagNER(text);
        Document tempDocument = nlpToolkit.tagNER(text);
        removeNullNerType(document);
        removeNullNerType(tempDocument);
        List<BasicLanguageUnit> units = document.getUnitList();
        List<BasicLanguageUnit> tempUnits = tempDocument.getUnitList();

        // join consecutive words with same NER types
        List<BasicLanguageUnit> newUnits = new ArrayList<>();
        String currentType = "NA";
        int currentOffset = -1;
        StringBuilder currentContent = new StringBuilder();

        int counter = 0;
        for (BasicLanguageUnit unit : units) {
            // prepare a temporary unit
            BasicLanguageUnit tempUnit = tempUnits.get(counter);

            String content = unit.getContent();
            String type = unit.getNerType();
            int offset = unit.getOffSet();
            // System.out.println("Checking: " + content + "\t" + offset + "\t" + type);
            if (!type.equals("O")) {
                currentContent.append(content).append(" ");
                if (currentType.equals("NA")) {
                    // set current type and offset to those of the first word
                    currentType = type;
                    currentOffset = offset;
                }
            } else {
                // create a new unit if current content is not empty (meaning it is not the start of
                // sentence)
                if (!currentContent.toString().equals("")) {
                    // create by change current unit
                    tempUnit.setContent(currentContent.toString().trim());
                    tempUnit.setNerType(currentType);
                    tempUnit.setOffSet(currentOffset);

                    // add to new unit list
                    newUnits.add(tempUnit);
                    counter++; // only increase counter if tempUnit is used

                    // add the current normal unit to the new list
                    newUnits.add(unit);
                } else {
                    // then the unit is a normal unit (not NER)
                    // just add to the new unit list
                    newUnits.add(unit);
                }

                // reset current type, offset, content
                currentType = "NA";
                currentOffset = -1;
                currentContent = new StringBuilder();
            }
        }

        // handle the case where last contents are tagged
        if (!currentType.equals("NA")) {
            // get a random unit (for this case, get the first from unit list
            BasicLanguageUnit tempUnit = tempUnits.get(counter);
            tempUnit.setContent(currentContent.toString().trim());
            tempUnit.setNerType(currentType);
            tempUnit.setOffSet(currentOffset);

            // add to new unit list
            newUnits.add(tempUnit);
        }

        document.setUnitList(newUnits);

        return document;
    }


    private static void removeNullNerType(Document document) {
        List<BasicLanguageUnit> unitList = document.getUnitList();
        for (BasicLanguageUnit unit : unitList) {
            if (unit.getNerType() == null || unit.getNerType().equals("null")) {
                unit.setNerType("O");
            }
        }
    }

    public boolean isStopword(String word) {
        return stopwordDictionary.containsKey(word);
    }

    public boolean isPunctuation(String unit) {
        return punctuations.containsKey(unit);
    }
}
