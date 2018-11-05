package main;


import config.Constants;
import feature.FeatureExtractor;
import utils.DataProcessing;
import utils.DistantSupervision;

public class Main {
    public static void main(String... args) {
        // Step 1: Create a text corpus
        // Constants.RAW_TEXT;

        // Step 2: Perform weak supervision on the text corpus
//        String[] criteria = {"keyword", "same sentence"};
//        DistantSupervision.generateExamples(criteria, Constants.RAW_TEXT, Constants.TRAINING_DATA);
//        System.out.println("Weak supervision is done.");
//
//        // Step 3: Balance classes
//        DataProcessing.balanceClassesByDownSampling(Constants.TRAINING_DATA, Constants.BALANCED_TRAINING_DATA);
//        System.out.println("Balancing classes is done.");

        // Step 4: Extract feature vectors from training data
        String[] features = {"distance", "same sentence", "ngram"};
        FeatureExtractor.dataFileToFeatureFile(features, Constants.BALANCED_TRAINING_DATA, Constants.FEATURE_VECTOR_TRAINING_DATA);
        System.out.println("Extracting features is done.");

        // Step 5: Turn to python
    }
}
