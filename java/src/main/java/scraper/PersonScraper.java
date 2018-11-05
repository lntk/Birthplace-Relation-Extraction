package scraper;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import utils.Utils;

public class PersonScraper {
	String urlFile = "src/main/resources/nguoinoitieng.txt"; 
	
	public void scrapeBirthplace()  throws FileNotFoundException, IOException {
		BufferedWriter birthplaceWriter = new BufferedWriter(new FileWriter("src/main/resources/birthplace_map.txt"));
		ArrayList<String> urlList = Utils.readFileByLines(urlFile);
		for (String url : urlList) {
			Document doc;
			
			// temporary fix to url: http://nguoinoitieng.tv/nghe-nghiep/blogger/kristine-ullebø/au5l
			// ignore if having exceptions
			try {
				doc = Jsoup.connect(url).timeout(10 * 1000).get();
			} catch (IOException e) {
				continue;
			}
			Elements elements;
			String infoText;
			
			// get name
			elements = doc.getElementsByClass("motangan");
			infoText = elements.text();
			int afterNameIndex = infoText.indexOf("Nơi sống/ làm việc:");
			String name = infoText.substring(0, afterNameIndex - 1);
			
			// get birthplace
			elements = doc.getElementsByClass("thongtin-right");
			infoText = elements.text();
			int birthplaceIndex = infoText.indexOf("Nơi sinh:");
			int ageIndex = infoText.indexOf("Tuổi:");
			String fullBirthplace = infoText.substring(birthplaceIndex + 10, ageIndex - 1);
			String[] birthplaceList = fullBirthplace.split(", ");
			
			for (String birthplace : birthplaceList) {
				birthplaceWriter.write(name + "\t\t" + birthplace.trim() +"\n");
			}
		}
		birthplaceWriter.close();
		System.out.println("Done scraping birthplace.");
	}
	
	public static void main(String[] args) throws IOException {
		new PersonScraper().scrapeBirthplace();
	}
}
