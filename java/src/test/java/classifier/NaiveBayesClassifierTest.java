package classifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import config.Constants;
import utils.DataProcessing;

public class NaiveBayesClassifierTest {
    @Test   
    public void test_overall_1gramBirthplace() throws FileNotFoundException, IOException {
        String wordSeparator = "\t";
        Map<String, List<String>> labelHolder = DataProcessing.loadNgramFileToMap(Constants.NGRAM_RAW_TRAINING_DATA, wordSeparator);
        NaiveBayesClassifier classifier = new NaiveBayesClassifier(labelHolder, wordSeparator, Constants.VIETNAMESE_WORDS);
        classifier.train();
        classifier.test("0\t3\tNguyễn Xuân Phúc làm việc tại Quảng Nam.");
    }
}
