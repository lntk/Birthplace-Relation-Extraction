package feature;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nerd.nlp.api.BasicLanguageUnit;
import utils.NLPUtils;
import utils.Utils;


public class FeatureExtractor {
    private static NLPUtils nlpUtils = NLPUtils.getInstance();

    public static void extractFeature(DataInstance dataInstance, String[] features) {
        FeatureInstance featureInstance = dataInstance.getFeatureInstance();
        for (String feature : features) {
            switch (feature) {
                case "distance":
                    featureInstance.setDistance(getDistanceFeature(dataInstance));
                    break;
                case "ngram":
                    featureInstance.setNgram(getNgramFeature(dataInstance, 2));
                    break;
                case "same sentence":
                    featureInstance.setSameSentence(getSameSentenceFeature(dataInstance));
                    break;
                default:
                    break;
            }
        }
    }


    private static void extractFeature(DataInstance dataInstance) {
        String[] features = {"distance", "same sentence", "ngram"};
        extractFeature(dataInstance, features);
    }

    @SuppressWarnings("unused")
    private static List<Integer> getNerFeature(DataInstance dataInstance) {
        List<Integer> feature = new ArrayList<>();
        List<BasicLanguageUnit> unitList = dataInstance.getUnitList();
        int posSubject = dataInstance.getPosSubject();
        int posObject = dataInstance.getPosObject();
        int subjectType = nlpUtils.getNerID(unitList.get(posSubject).getNerType());
        int objectType = nlpUtils.getNerID(unitList.get(posObject).getNerType());
        feature.add(subjectType);
        feature.add(objectType);
        return feature;
    }

    private static List<Integer> getSameSentenceFeature(DataInstance dataInstance) {
        List<Integer> feature = new ArrayList<>();
        List<BasicLanguageUnit> unitList = dataInstance.getUnitList();
        int posSubject = dataInstance.getPosSubject();
        int posObject = dataInstance.getPosObject();
        int unitIndex = 0;
        int lowerBound = Math.min(posSubject, posObject);
        int upperBound = Math.max(posSubject, posObject);
        for (BasicLanguageUnit unit : unitList) {
            if (unitIndex <= lowerBound) {
                unitIndex++;
                continue;
            }
            if (unitIndex >= upperBound) {
                break;
            }
            String text = unit.getContent();
            if (text.equals(".")) {
                feature.add(0);
                return feature;
            }
            unitIndex++;
        }
        feature.add(1);
        return feature;
    }

    @SuppressWarnings("unused")
    private static List<Integer> getTextBetweenPairFeature(DataInstance dataInstance) {
        List<Integer> feature = new ArrayList<>();
        List<BasicLanguageUnit> unitList = dataInstance.getUnitList();
        int posSubject = dataInstance.getPosSubject();
        int posObject = dataInstance.getPosObject();
        int distance = Math.abs(posSubject - posObject);
        int lowerBound = Math.min(posSubject, posObject);
        int upperBound = Math.max(posSubject, posObject);
        int closeThreshold = 5;
        if (distance < closeThreshold) {
            for (int i = lowerBound + 1; i <= upperBound - 1; i++) {
                String word = unitList.get(i).getContent();
                int id = nlpUtils.getWordId(word);
                feature.add(id);
            }

            // zero padding
            for (int i = upperBound - lowerBound - 1; i < closeThreshold; i++) {
                feature.add(0);
            }
        } else {
            for (int i = 0; i < closeThreshold; i++) {
                feature.add(0);
            }
        }
        return feature;
    }

//    @SuppressWarnings("unused")
    public static List<Integer> getKeywordFeature(DataInstance dataInstance, String[] keywords) {
        List<Integer> feature = new ArrayList<>();
        List<BasicLanguageUnit> unitList = dataInstance.getUnitList();
        int posSubject = dataInstance.getPosSubject();
        int posObject = dataInstance.getPosObject();
        int lowerBound = Math.min(posSubject, posObject);
        int upperBound = Math.max(posSubject, posObject);
        StringBuilder textBetweenEntitiesBuilder = new StringBuilder();
        for (int i = lowerBound; i <= upperBound; i++) {
            textBetweenEntitiesBuilder.append(unitList.get(i).getContent()).append(" ");
        }
        String textBetweenEntities = textBetweenEntitiesBuilder.toString().trim();
        for (String keyword : keywords) {
            if (textBetweenEntities.contains(keyword)) {
                feature.add(1);
                return feature;
            }
        }

        feature.add(0);
        return feature;
    }

    private static List<Integer> getDistanceFeature(DataInstance dataInstance) {
        List<Integer> feature = new ArrayList<>();
        int posSubject = dataInstance.getPosSubject();
        int posObject = dataInstance.getPosObject();
        int distance = Math.abs(posSubject - posObject);
        feature.add(distance);
        return feature;
    }

    private static List<Integer> getNgramFeature(DataInstance dataInstance, int n) {
        List<Integer> ngram = new ArrayList<>();
        ngram.addAll(getLeftNgramSubjectFeature(dataInstance, n));
        ngram.addAll(getRightNgramSubjectFeature(dataInstance, n));
        ngram.addAll(getLeftNgramObjectFeature(dataInstance, n));
        ngram.addAll(getRightNgramObjectFeature(dataInstance, n));
        return ngram;
    }

    private static List<Integer> getLeftNgramSubjectFeature(DataInstance dataInstance, int n) {
        return getLeftNgramSkipRedundancyFeature(dataInstance, dataInstance.getPosSubject(), n);
    }

    private static List<Integer> getRightNgramSubjectFeature(DataInstance dataInstance, int n) {
        return getRightNgramSkipRedundancyFeature(dataInstance, dataInstance.getPosSubject(), n);
    }

    private static List<Integer> getLeftNgramObjectFeature(DataInstance dataInstance, int n) {
        return getLeftNgramSkipRedundancyFeature(dataInstance, dataInstance.getPosObject(), n);
    }

    private static List<Integer> getRightNgramObjectFeature(DataInstance dataInstance, int n) {
        return getRightNgramSkipRedundancyFeature(dataInstance, dataInstance.getPosObject(), n);
    }


    @SuppressWarnings("unused")
    private static List<Integer> getLeftNgramFeature(DataInstance dataInstance, int entityPos, int n) {
        List<Integer> feature = new ArrayList<>();
        List<BasicLanguageUnit> unitList = dataInstance.getUnitList();
        String currentWord;
        int counter = n;
        int lowerBound = Math.max(entityPos - n, 0);
        for (int i = entityPos - 1; i >= lowerBound; i--) {
            currentWord = unitList.get(i).getContent();
            int id = nlpUtils.getWordId(currentWord);
            feature.add(0, id);
            counter--;
        }

        // padding 0 to the rest
        for (int i = counter; i > 0; i--) {
            feature.add(0, 0);
        }

        return feature;
    }


    @SuppressWarnings("unused")
    private static List<Integer> getRightNgramFeature(DataInstance dataInstance, int entityPos, int n) {
        List<Integer> feature = new ArrayList<>();
        List<BasicLanguageUnit> unitList = dataInstance.getUnitList();
        String currentWord;
        int counter = n;
        int upperBound = Math.min(entityPos + n, unitList.size() - 1);
        for (int i = entityPos + 1; i <= upperBound; i++) {
            currentWord = unitList.get(i).getContent();
            int id = nlpUtils.getWordId(currentWord);
            feature.add(id);
            counter--;
        }

        // padding 0 to the rest
        for (int i = counter; i > 0; i--) {
            feature.add(0);
        }

        return feature;
    }

    private static List<Integer> getLeftNgramSkipRedundancyFeature(DataInstance dataInstance, int entityPos, int n) {
        List<Integer> feature = new ArrayList<>();
        List<BasicLanguageUnit> unitList = dataInstance.getUnitList();
        String currentWord;
        int counter = n;
        String entityType = unitList.get(entityPos).getNerType();
        int pos = entityPos - 1;
        while (pos >= 0 && counter > 0) {
            currentWord = unitList.get(pos).getContent();
            String currentType = unitList.get(pos).getNerType();
            if (nlpUtils.isStopword(currentWord) | nlpUtils.isPunctuation(currentWord)
                    | currentType.equals(entityType)) {
                pos--;
                continue;
            }
            // if current word is an entity, replace it by its type
            if (!currentType.equals("O")) currentWord = currentType.toLowerCase();
            int id = nlpUtils.getWordId(currentWord);
            feature.add(0, id);
            counter--;
            pos--;
        }

        // padding 0 to the rest
        for (int i = counter; i > 0; i--) {
            feature.add(0, 0);
        }

        return feature;
    }

    private static List<Integer> getRightNgramSkipRedundancyFeature(DataInstance dataInstance, int entityPos, int n) {
        List<Integer> feature = new ArrayList<>();
        List<BasicLanguageUnit> unitList = dataInstance.getUnitList();
        String currentWord;
        int counter = n;
        String entityType = unitList.get(entityPos).getNerType();
        int pos = entityPos + 1;
        while (pos <= unitList.size() - 1 && counter > 0) {
            currentWord = unitList.get(pos).getContent();
            String currentType  = unitList.get(pos).getNerType();
            if (nlpUtils.isStopword(currentWord) | nlpUtils.isPunctuation(currentWord)
                    | currentType.equals(entityType)) {
                pos++;
                continue;
            }
            // if current word is an entity, replace it by its type
            if (!currentType.equals("O")) currentWord = currentType.toLowerCase();
            int id = nlpUtils.getWordId(currentWord);
            feature.add(id);
            counter--;
            pos++;
        }

        // padding 0 to the rest
        for (int i = counter; i > 0; i--) {
            feature.add(0);
        }

        return feature;
    }

    public static void dataFileToFeatureFile(String[] features, String fileIn, String fileOut) {
        List<String> lines = Utils.readFileByLines(fileIn);
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(fileOut));
            for (String line : lines) {
                // extract info from line into data instance
                String[] parts = line.split("\t");
                String label = parts[0];
                String posSubject = parts[1];
                String posObject = parts[2];
                String text = parts[3].trim();
                DataInstance dataInstance = new DataInstance(text, Integer.parseInt(posSubject), Integer.parseInt(posObject));

                // extract feature from data
                extractFeature(dataInstance);
                writer.write(label + "\t" + dataInstance.getFeatureInstance().toString(features) + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
