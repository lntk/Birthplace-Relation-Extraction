package feature;

import java.util.List;

public class FeatureInstance {
    private List<Integer> leftNgramSubject;
    private List<Integer> rightNgramSubject;
    private List<Integer> leftNgramObject;
    private List<Integer> rightNgramObject;
    private List<Integer> ngram;
    private List<Integer> distance;
    private List<Integer> sameSentence;

    void setDistance(List<Integer> distance) {
        this.distance = distance;
    }

    public void setNgram(List<Integer> ngram) {
        this.ngram = ngram;
    }

    void setSameSentence(List<Integer> sameSentence) {
        this.sameSentence = sameSentence;
    }

    public String toString (String featureType) {
        StringBuilder feature = new StringBuilder();
        switch (featureType) {
            case "ngram":
                for (int id : ngram) {
                    feature.append(id).append(" ");
                }
                return feature.toString();
            case "distance":
                for (int id : distance) {
                    feature.append(id).append(" ");
                }
                return feature.toString();
            case "same sentence":
                for (int id : sameSentence) {
                    feature.append(id).append(" ");
                }
                return feature.toString();
            default: return null;
        }
    }

    public String toString(String[] featureList) {
        StringBuilder features = new StringBuilder();
        for (String feature : featureList) {
            features.append(toString(feature));
        }
        return features.toString();
    }
}
