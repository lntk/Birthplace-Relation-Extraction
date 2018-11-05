package utils;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import org.junit.Test;

public class DataProcessingTest {
    @Test
    public void test_fileToLabelData_forTestTrainingData() {
        String filename = "src/main/resources/forTest_trainingData.txt";
        ArrayList<ArrayList<String>> labelHolder = DataProcessing.fileToLabeledData(filename);
        
        ArrayList<String> expectedNegative = new ArrayList<>();
        expectedNegative.add("Sinh ngày 7/4/1907 trong một gia đình có truyền thống yêu nước và hiếu học, tại xã Triệu Đông, huyện Triệu Phong, tỉnh Quảng Trị, đồng chí Lê Duẩn (tên thật là Lê Văn Nhuận) đã sớm giác ngộ và tham gia hoạt động cách mạng.");
        ArrayList<String> expectedPositive = new ArrayList<>();
        expectedPositive.add("Trải qua thời thơ ấu với những chuỗi ngày vất vả và cay đắng ở mảnh đất Gia Lai, Phi Nhung thiệt thòi về mọi thứ. Trên sân khấu “Chuyện tối nay với Thành”, Trấn Thành hỏi Phi Nhung rằng, có phải vì trải qua tuổi thơ đầy cơ cực nên Phi Nhung muốn dành mọi thứ tốt đẹp nhất để các con của mình hạnh phúc?");
        
        ArrayList<String> actualNegative = labelHolder.get(0);
        ArrayList<String> actualPositive = labelHolder.get(1);
        
        for (int i = 0; i < actualNegative.size(); i++) {
            assertEquals(expectedNegative.get(i), actualNegative.get(i));
        }
        
        for (int i = 0; i < actualPositive.size(); i++) {
            assertEquals(expectedPositive.get(i), actualPositive.get(i));
        }
    }
}
