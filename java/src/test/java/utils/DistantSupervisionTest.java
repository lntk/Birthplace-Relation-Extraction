package utils;

import static org.junit.Assert.assertEquals;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.junit.Test;
import config.Constants;

public class DistantSupervisionTest {
    private String fileIn = Constants.TEST_RAW_TEXT;
    private String fileOut = Constants.TEST_FILE;

    @Test
    public void test_generateExamples_keyword() {
        String[] criteria = {"keyword"};
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(Constants.TEST_RAW_TEXT));
            writer.write("ông Nguyễn Xuân Phúc quê tại Quảng Nam.\n");
            writer.write("ông Nguyễn Xuân Phúc làm việc tại Quảng Nam.\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        DistantSupervision.generateExamples(criteria, fileIn, fileOut);
        
        List<String> lines = Utils.readFileByLines(fileOut);
        StringBuilder actualBuilder = new StringBuilder();
        for (String line : lines) {
            actualBuilder.append(line).append("\n");
        }
        String expected = "1\t1\t4\tông Subject quê tại Object.\n" + "0\t1\t4\tông Subject làm việc tại Object.\n";
        String actual = actualBuilder.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void test_generateExamples_sameSentence() {
        String[] criteria = {"same sentence"};
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(Constants.TEST_RAW_TEXT));
            writer.write("ông Nguyễn Xuân Phúc quê tại Quảng Nam. ông làm việc tại Hà Nội.\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        DistantSupervision.generateExamples(criteria, fileIn, fileOut);
        
        List<String> lines = Utils.readFileByLines(fileOut);
        StringBuilder actualBuilder = new StringBuilder();
        for (String line : lines) {
            actualBuilder.append(line).append("\n");
        }
        String expected = "1\t1\t4\tông Subject quê tại Object. ông làm việc tại Hà Nội.\n" + "0\t1\t9\tông Subject quê tại Quảng Nam. ông làm việc tại Object.\n";
        String actual = actualBuilder.toString();
        assertEquals(expected, actual);
    }
    
    @Test
    public void test_generateExamples_pairAppearance() {
        String[] criteria = {"pair appearance"};
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(Constants.TEST_RAW_TEXT));
            writer.write("ông Nguyễn Xuân Phúc làm việc tại Quảng Nam.\n");
            writer.write("ông Nguyễn Trường Giang sinh tại Nghệ An.\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        DistantSupervision.generateExamples(criteria, fileIn, fileOut);
        
        List<String> lines = Utils.readFileByLines(fileOut);
        StringBuilder actualBuilder = new StringBuilder();
        for (String line : lines) {
            actualBuilder.append(line).append("\n");
        }
        String expected = "1\t1\t4\tông Subject làm việc tại Object.\n" + "0\t1\t4\tông Subject sinh tại Object.\n";
        String actual = actualBuilder.toString();
        assertEquals(expected, actual);
    }
}
