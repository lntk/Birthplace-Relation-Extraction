package utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UtilsTest {
	@Test
	public void test_replaceTwoSubstrings_KhangsinhtaiVietNam() {
		String sentence = "Khang sinh tại Việt Nam.";
		String expected = "SUBJECT sinh tại OBJECT.";
		String actual = Utils.replaceTwoSubstrings(sentence, "Khang", "Việt Nam", 0, 5, 15, 23, "SUBJECT", "OBJECT");
		assertEquals(expected, actual);
	}
}
