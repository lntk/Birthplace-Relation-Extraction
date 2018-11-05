package feature;

import nerd.nlp.api.BasicLanguageUnit;
import nerd.nlp.api.Document;
import utils.NLPUtils;

import java.util.List;

public class DataInstance {
    private String sentence;
    private List<BasicLanguageUnit> unitList;
    private int posSubject, posObject;
    private FeatureInstance featureInstance;

    public DataInstance(String sentence, int posSubject, int posObject) {
        this.sentence = sentence;
        this.posSubject = posSubject;
        this.posObject = posObject;
        NLPUtils nlpUtils = NLPUtils.getInstance();
        Document document = nlpUtils.tagNER(sentence);
        this.unitList = document.getUnitList();
        featureInstance = new FeatureInstance();
    }

//    public DataInstance(String sentence, int posSubject, int posObject, int label) {
//        this.sentence = sentence;
//        this.posSubject = posSubject;
//        this.posObject = posObject;
//        this.label = label;
//        NLPUtils nlpUtils = NLPUtils.getInstance();
//        Document document = nlpUtils.tagNER(sentence);
//        this.unitList = document.getUnitList();
//        featureInstance = new FeatureInstance();
//    }

    public List<BasicLanguageUnit> getUnitList() {
        return unitList;
    }

    public int getPosSubject() {
        return posSubject;
    }

    public int getPosObject() {
        return posObject;
    }

    public FeatureInstance getFeatureInstance() {
        return featureInstance;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
}