package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nerd.nlp.api.BasicLanguageUnit;

public class Utils {
	public static ArrayList<String> readFileByLines(String filename) {
		ArrayList<String> lines = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return lines;
	}

	public static String replaceTwoSubstrings(String s, String substring1, String substring2, int substring1Begin,
			int substring1End, int substring2Begin, int substring2End, String substring1Replace,
			String substring2Replace) {
		if (substring1Begin < substring2Begin) {
			return s.substring(0, substring1Begin) + substring1Replace + s.substring(substring1End, substring2Begin)
					+ substring2Replace + s.substring(substring2End);
		} else {
			return s.substring(0, substring2Begin) + substring2Replace + s.substring(substring2End, substring1Begin)
					+ substring1Replace + s.substring(substring1End);
		}
	}
	
	public static void printUnitList(List<BasicLanguageUnit> unitList) {
	    int counter = 0;
	    for (BasicLanguageUnit unit : unitList) {
	        System.out.println(String.format("%5d\t%20s\t%5d\t%10s\n", counter, unit.getContent(), unit.getOffSet(), unit.getNerType()));
	        counter++;
	    }
	}
}
