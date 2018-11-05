package classifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import feature.DataInstance;
import feature.FeatureExtractor;
import feature.FeatureInstance;
import utils.Utils;


public class NaiveBayesClassifier {
    private double logPrior[]; // P(label)
    private double logLikehood[][]; // P(word | label)
    private Map<String, List<String>> labelHolder; // [("positive", [sentence1, sentence2, ...]), ...]
    private CountingMap<String> dictionary; // [word_1, word_2, ... , word_n]
    private int dictionarySize; // n
    private int numDoc;
    private int numLabel;
    private String wordSeparator = " ";
    private List<String> labelList;

    private NaiveBayesClassifier(Map<String, List<String>> labelHolder, String wordSeperator) throws IOException {
        this.labelHolder = labelHolder;
        this.wordSeparator = wordSeperator;
        
        // get label list
        Set<String> labelSet = labelHolder.keySet();
        labelList = new ArrayList<>();
        labelList.add("none"); // for labelId start with 1
        labelList.addAll(labelSet);
        
        // create dictionary
        // iterate over all documents to gather words
        StringBuilder bigDoc = new StringBuilder();
        for (List<String> doc : labelHolder.values()) {
            for (String sentence : doc) {
                bigDoc.append(sentence).append(wordSeperator);
            }
        }
        dictionary = createDictionary(bigDoc.toString());
        dictionarySize = dictionary.size();

        // initialize variables
        numLabel = labelHolder.size();

        // calculate number of total documents
        numDoc = 0;
        for (List<String> label : labelHolder.values()) {
            numDoc += label.size();
        }

        // note: label and word start with index 1
        logPrior = new double[numLabel + 1];
        logLikehood = new double[dictionarySize + 1][numLabel + 1];
    }
    
    NaiveBayesClassifier(Map<String, List<String>> labelHolder, String wordSeperator, String dictionaryFile) throws FileNotFoundException, IOException {
        this.labelHolder = labelHolder;
        this.wordSeparator = wordSeperator;
        
        // get label list
        Set<String> labelSet = labelHolder.keySet();
        labelList = new ArrayList<>();
        labelList.add("none"); // for labelId start with 1
        labelList.addAll(labelSet);
        
        // create dictionary
        // iterate over all documents to gather words
        dictionary = createDictionaryFromFile(dictionaryFile);
        dictionarySize = dictionary.size();

        // initialize variables
        numLabel = labelHolder.size();

        // calculate number of total documents
        numDoc = 0;
        for (List<String> label : labelHolder.values()) {
            numDoc += label.size();
        }

        // note: label and word start with index 1
        logPrior = new double[numLabel + 1];
        logLikehood = new double[dictionarySize + 1][numLabel + 1];
        System.out.println("Dictionary size: " + dictionarySize);
        System.out.println("Number of label: " + numLabel);
    }

    public void train() {
        double count[][] = new double[dictionarySize + 1][numLabel + 1]; // count[word][label] 

        int labelId = 1;

        // iterate over labels
        for (List<String> label : labelHolder.values()) {
            int numDocWithLabel = label.size();
//            System.out.println("Number of documents: " + numDocWithLabel);
            logPrior[labelId] = Math.log((double) numDocWithLabel / numDoc);

            // create dictionary within label
            StringBuilder bigDocWithLabel = new StringBuilder(); // join of all documents with this label
            for (String doc : label) {
                bigDocWithLabel.append(doc).append(wordSeparator);
            }
            CountingMap<String> dictionaryWithLabel = createDictionary(bigDocWithLabel.toString());
//            System.out.println("Dictionary: ");
//            printDictionary(dictionaryWithLabel);

            // get total word count within label + dictionary size
            double totalCount = dictionaryWithLabel.getTotalCount() + dictionarySize;

            // calculate log likelihood
            for (String word : dictionary.entrySet()) {
                int wordId = dictionary.getId(word);
                count[wordId][labelId] = dictionaryWithLabel.count(word);
                logLikehood[wordId][labelId] = Math.log((count[wordId][labelId] + 1) / totalCount);
//                if (count[wordId][labelId] > 0) {
//                    System.out.println(String.format("Word: %20s\tLabel: %5d\tWordCount: %5f\tTotalCount: %5f", word, labelId, count[wordId][labelId], totalCount));
//                }
            }

            labelId++;
        }
    }

    public void test(String content) {
        String[] parts = content.split(wordSeparator);
        System.out.println(content);
        int posSubject = Integer.parseInt(parts[0]);
        int posObject = Integer.parseInt(parts[1]);
        String sentence = parts[2];
        DataInstance dataInstance = new DataInstance(sentence, posSubject, posObject);
        String[] features = {"leftNgramSubject", "rightNgramSubject", "leftNgramObject", "rightNgramObject"};
        FeatureExtractor.extractFeature(dataInstance, features);
        // WANT TO UPDATE HERE
        FeatureInstance featureInstance = dataInstance.getFeatureInstance();
        String[] words = {};
        
        double sum[] = new double[numLabel + 1]; // ~ log(P(label | document))
        for (int labelId = 1; labelId <= numLabel; labelId++) {
            sum[labelId] = logPrior[labelId];
            for (String word : words) {
                word = word.toLowerCase().trim();
                getLogLikelihood(word);
                if (dictionary.contains(word)) {
                    int wordId = dictionary.getId(word);
                    System.out.println(logLikehood[wordId][labelId]);
                    sum[labelId] += logLikehood[wordId][labelId];
                }
            }
        } 

        // Print result
        double maxScore = -1e10; // MAP 
        int maxLabel = 0; // label with regard to MAP  
        for (int i = 1; i <= numLabel; i++) {
            System.out.println("Label " + getLabel(i) + " score: " + sum[i]);
            if (sum[i] > maxScore) {
                maxLabel = i;
                maxScore = sum[i];
            }
        }
        System.out.println("Predicted label: " + getLabel(maxLabel));
    }
    
    private List<Double> getLogLikelihood(String word) {
        List<Double> list = new ArrayList<>();
        int wordId = dictionary.getId(word);
        System.out.println("[");
        System.out.println("\tWord: " + word);
        for (int i = 1; i <= numLabel; i++) {
            list.add(logLikehood[wordId][i]);
            System.out.println("\tLabel " + getLabel(i) + " : " + logLikehood[wordId][i]);
        }
        System.out.println("]");
        return list;
    }
    
    private String getLabel(int labelId) {
        return labelList.get(labelId);
    }

    private CountingMap<String> createDictionary(String doc) {
        CountingMap<String> map = new CountingMap<>();
        String words[] = doc.trim().split(wordSeparator);
        for (String word : words) {
            map.add(word.trim().toLowerCase());
        }
        return map;
    }
    
    private CountingMap<String> createDictionaryFromFile(String filename) throws FileNotFoundException, IOException {
        CountingMap<String> map = new CountingMap<>();
        List<String> words = Utils.readFileByLines(filename);
        for (String word : words) {
            map.addFirstTime(word.trim());
        }        
        return map;
    }
    
    public void printDictionary(CountingMap<String> dictionary) {
        System.out.println("[");
        for (String word : dictionary.entrySet()) {
            System.out.println("\t" + word);
        }
        System.out.println("]");
    }
    
    private class CountingMap<K> {
        Map<K, Integer> countingMap;
        Map<K, Integer> idMap;
        Map<Integer, K> idMapReverse;
        int currentId;

        CountingMap() {
            countingMap = new HashMap<>();
            idMap = new HashMap<>();
            idMapReverse = new HashMap<>();
            currentId = 1;
        }

        void add(K key) {
            if (countingMap.containsKey(key)) {
                Integer value = countingMap.get(key);
                value++;
                countingMap.put(key, value);
            } else {
                countingMap.put(key, 1);
                idMap.put(key, currentId);
                idMapReverse.put(currentId, key);
                currentId++;
            }
        }
        
        void addFirstTime(K key) {
            if (!countingMap.containsKey(key)) {
                countingMap.put(key, 0);
                idMap.put(key, currentId);
                idMapReverse.put(currentId, key);
                currentId++;
            }
        }

        public int count(K key) {
            if (countingMap.containsKey(key))
                return countingMap.get(key);
            return 0;
        }

        int getTotalCount() {
            int totalCount = 0;
            for (Integer count : countingMap.values()) {
                totalCount += count;
            }
            return totalCount;
        }

        boolean contains(K key) {
            return idMap.containsKey(key);
        }

        int size() {
            return countingMap.size();
        }

        Set<K> entrySet() {
            return countingMap.keySet();
        }

        int getId(K key) {
            if (idMap.containsKey(key)) {
                return idMap.get(key);
            }
            return 0;
        }

        public String toString() {
            StringBuilder result = new StringBuilder();

            result.append("[\n");
            for (K key : countingMap.keySet()) {
                result.append("\t").append(key).append("\t").append(countingMap.get(key))
                        .append("\t").append(idMap.get(key)).append("\n");
            }
            result.append("]\n");

            return result.toString();
        }
    }

    public static void main(String[] args) throws IOException {
        Map<String, List<String>> labelHolder = new HashMap<>();
        List<String> negative = new ArrayList<>();
        String n1 = "just plain boring";
        String n2 = "entirely predictable and lacks energy";
        String n3 = "no suprises and very few laughs";
        negative.add(n1);
        negative.add(n2);
        negative.add(n3);
        List<String> positive = new ArrayList<>();
        String p1 = "very powerful";
        String p2 = "the most fun film of summer";
        positive.add(p1);
        positive.add(p2);
        labelHolder.put("negative", negative);
        labelHolder.put("positive", positive);
        
        NaiveBayesClassifier classifier = new NaiveBayesClassifier(labelHolder, " ");
        classifier.train();
        classifier.test("predictable with no fun");
    }
}
