package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nerd.nlp.api.BasicLanguageUnit;
import nerd.nlp.api.Document;
import nerd.nlp.api.NLPManager;
import nerd.nlp.api.NLPToolkit;

public class DataProcessing {
    @SuppressWarnings("unused")
    public static void fileToLowerCase(String fileIn, String fileOut) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(fileOut));
            ArrayList<String> content = Utils.readFileByLines(fileIn);
            for (String line : content) {
                writer.write(line.toLowerCase() + "\n");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static ArrayList<ArrayList<String>> fileToLabeledData(String filename) {
        ArrayList<String> content = Utils.readFileByLines(filename);
        ArrayList<ArrayList<String>> labelHolder = new ArrayList<>();
        ArrayList<String> positive = new ArrayList<>();
        ArrayList<String> negative = new ArrayList<>();
        for (String line : content) {
            if (line.startsWith("T -")) {
                positive.add(line.substring(4));
            }
            if (line.startsWith("F -")) {
                negative.add(line.substring(4));
            }
        }
        labelHolder.add(negative);
        labelHolder.add(positive);

        return labelHolder;
    }

    static HashMap<String, Integer> loadWordDictionary(String filename) {
        ArrayList<String> words = Utils.readFileByLines(filename);
        HashMap<String, Integer> wordDictionary = new HashMap<>();
        int id = 1;
        for (String word : words) {
            wordDictionary.put(word, id);
            id++;
        }

        return wordDictionary;
    }

    public static Map<String, List<String>> loadNgramFileToMap(String filename, String separator) {
        List<String> lines = Utils.readFileByLines(filename);
        Map<String, List<String>> map = new HashMap<>();
        List<String> negative = new ArrayList<>();
        List<String> positive = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(separator);
            String label = parts[0];
            if (label.equals("1")) {
                for (int i = 1; i < parts.length; i++) {
                    String word = parts[i].trim().toLowerCase();
                    positive.add(word);
                }
            } else {
                for (int i = 1; i < parts.length; i++) {
                    String word = parts[i].trim().toLowerCase();
                    negative.add(word);
                }
            }
        }

        map.put("negative", negative);
        map.put("positive", positive);

        return map;
    }

    @SuppressWarnings("unused")
    public static void rawDataFileToAnnotatedDataFile(String fileIn, String fileOut) {
        NLPToolkit nlpToolkit = NLPManager.getNLPToolkit(NLPManager.NLP_NEWS_VERSION);
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(fileOut));
            List<String> lines = Utils.readFileByLines(fileIn);
            for (String line : lines) {
                String[] parts = line.split("\t");
                String label = parts[0];
                if (label.equals("NA")) continue;
                String numericalLabel = label.equals("T") ? "1" : "0";
                String posE1 = parts[1];
                String posE2 = parts[2];
                writer.write(numericalLabel + "\t" + posE1 + "\t" + posE2 + "\t");
                String text = parts[3];
                Document document = nlpToolkit.tagNER(text);
                List<BasicLanguageUnit> unitList = document.getUnitList();
                StringBuilder newLine = new StringBuilder();
                int counter = 0;
                for (BasicLanguageUnit unit : unitList) {
                    if (counter < unitList.size()) {
                        newLine.append(unit.getContent()).append("~").append(unit.getOffSet()).append("~").append(unit.getNerType()).append("|");
                    } else {
                        newLine.append(unit.getContent()).append("~").append(unit.getOffSet()).append("~").append(unit.getNerType());
                    }
                    counter++;
                }
                writer.write(newLine.toString() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public static void rawDataFileToRawTextFile(String fileIn, String fileOut) {
        List<String> lines = Utils.readFileByLines(fileIn);
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(fileOut));
            for (String line : lines) {
                String[] parts = line.split("\t");
                String text = parts[3].trim();
                writer.write(text + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public static void rawTextFileToRawDataFile(String fileIn, String fileOut) {
        String[] criteria = {"keyword", "same sentence"};
        DistantSupervision.generateExamples(criteria, fileIn, fileOut);
    }

    public static void balanceClassesByDownSampling(String fileIn, String fileOut) {
        List<String> lines = Utils.readFileByLines(fileIn);
        List<String> positive = new ArrayList<>();
        List<String> negative = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("1")) positive.add(line);
            else negative.add(line);
        }
        int balance = Math.min(positive.size(), negative.size());
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(fileOut));
            Collections.shuffle(positive);
            Collections.shuffle(negative);
            for (int i = 0; i < balance; i++) {
                writer.write(positive.get(i) + "\n");
            }
            for (int i = 0; i < balance; i++) {
                writer.write(negative.get(i) + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
