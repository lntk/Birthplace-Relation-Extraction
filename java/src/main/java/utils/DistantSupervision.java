package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import config.Constants;
import feature.FeatureExtractor;
import feature.DataInstance;
import nerd.nlp.api.BasicLanguageUnit;
import nerd.nlp.api.Document;

public class DistantSupervision {
    private static boolean checkKeywordCriterion(DataInstance dataInstance) {
        String[] keywords = {"quê tại", "nơi ở", "quê hương", "sinh ra", "sinh ra tại", "sinh ra ở",
                "quê", "quê nhà", "trở về quê nhà", "ở quê nhà", "quê tại", "nhưng quê gốc ở",
                "quê ở", "là quê hương của", "ở quê hương", "nhưng quê ở", "sinh tại", "sinh ở",
                "được sinh ra tại", "sinh"};
        List<Integer> result = FeatureExtractor.getKeywordFeature(dataInstance, keywords);
        return result.get(0) == 1;
    }

    private static boolean checkSameSentenceCriterion(DataInstance dataInstance) {
        List<BasicLanguageUnit> unitList = dataInstance.getUnitList();
        int unitIndex = 0;
        int posSubject = dataInstance.getPosSubject();
        int posObject = dataInstance.getPosObject();
        int lowerBound = Math.min(posSubject, posObject);
        int uppperBound = Math.max(posSubject, posObject);
        for (BasicLanguageUnit unit : unitList) {
            if (unitIndex <= lowerBound) {
                unitIndex++;
                continue;
            }
            if (unitIndex >= uppperBound) {
                break;
            }
            String text = unit.getContent();
            if (text.equals(".")) {
                return false;
            }
            unitIndex++;
        }
        return true;
    }

    private static boolean checkPairAppearanceCriterion(DataInstance dataInstance, Set<String> entityPairs) {
        List<BasicLanguageUnit> unitList = dataInstance.getUnitList();
        int posSubject = dataInstance.getPosSubject();
        int posObject = dataInstance.getPosObject();

        String pair = unitList.get(posSubject).getContent().toLowerCase() + "\t"
                + unitList.get(posObject).getContent().toLowerCase();
        return entityPairs.contains(pair);
    }

    @SuppressWarnings("unused")
    public static String replaceEntities(DataInstance dataInstance) {
        String rawSentence = dataInstance.getSentence();
        List<BasicLanguageUnit> unitList = dataInstance.getUnitList();
        BasicLanguageUnit subjectUnit = unitList.get(dataInstance.getPosSubject());
        BasicLanguageUnit objectUnit = unitList.get(dataInstance.getPosObject());
        int subjectBegin = subjectUnit.getOffSet();
        int subjectEnd = subjectBegin + subjectUnit.getContent().length();
        int objectBegin = objectUnit.getOffSet();
        int objectEnd = objectBegin + objectUnit.getContent().length();
        String newSentence = Utils.replaceTwoSubstrings(rawSentence, subjectUnit.getContent(),
                objectUnit.getContent(), subjectBegin, subjectEnd, objectBegin, objectEnd,
                "Nguyễn Trường Giang", "Nghệ An");
        return newSentence;
    }

    private static List<Entity> getEntityList(Document document, String type) {
        List<Entity> entityList = new ArrayList<>();
        int unitIndex = 0;
        List<BasicLanguageUnit> unitList = document.getUnitList();
        for (BasicLanguageUnit unit : unitList) {
            if (unit.getNerType().equals(type)) {
                entityList.add(new Entity(unit, unitIndex));
            }
            unitIndex++;
        }
        return entityList;
    }


    public static void generateExamples(String[] criteria, String fileIn,
                                        String fileOut) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(fileOut));
            NLPUtils nlpUtils = NLPUtils.getInstance();
            Set<String> entityPairs = getEntityPairs();
            ArrayList<String> lines = Utils.readFileByLines(fileIn);
            for (String line : lines) {
                Document document = nlpUtils.tagNER(line);
                List<Entity> subjectList = getEntityList(document, "PERSON");
                List<Entity> objectList = getEntityList(document, "LOCATION");

                for (Entity subject : subjectList) {
                    for (Entity object : objectList) {
                        DataInstance dataInstance = new DataInstance(line, subject.getPosition(), object.getPosition());
//                    String newSentence = replaceEntities(document, subject, object);
                        boolean isPositive = true;
                        for (String criterion : criteria) {
                            switch (criterion) {
                                case "keyword":
                                    isPositive = isPositive
                                            & checkKeywordCriterion(dataInstance);
                                    break;
                                case "pair appearance":
                                    isPositive = isPositive
                                            & checkPairAppearanceCriterion(dataInstance, entityPairs);
                                    break;
                                case "same sentence":
                                    isPositive = isPositive
                                            & checkSameSentenceCriterion(dataInstance);
                                    break;
                                default:
                                    break;
                            }
                        }
                        if (isPositive) {
                            writer.write("1\t" + subject.getPosition() + "\t" + object.getPosition()
                                    + "\t" + line + "\n");
                        } else {
                            writer.write("0\t" + subject.getPosition() + "\t" + object.getPosition()
                                    + "\t" + line + "\n");
                        }
                    }
                }
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Set<String> getEntityPairs() {
        ArrayList<String> pairs = Utils.readFileByLines(Constants.BIRTHPLACE_MAP);
        return new HashSet<>(pairs);
    }

    private static class Entity {
        private BasicLanguageUnit unit;
        private int position;

        Entity(BasicLanguageUnit unit, int position) {
            this.unit = unit;
            this.position = position;
        }

        int getPosition() {
            return position;
        }
    }
}
