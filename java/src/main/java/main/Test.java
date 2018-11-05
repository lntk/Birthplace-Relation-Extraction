package main;

import config.Constants;
import feature.FeatureExtractor;


public class Test {
    public static void main(String[] args) {
        // Step 1: Extract feature vectors from test data
        String[] features = {"distance", "same sentence", "ngram"};
        FeatureExtractor.dataFileToFeatureFile(features, Constants.TEST_DATA, Constants.FEATURE_VECTOR_TEST_DATA);
        System.out.println("Extracting features is done.");

        // Step 2: Turn to python
    }
}
