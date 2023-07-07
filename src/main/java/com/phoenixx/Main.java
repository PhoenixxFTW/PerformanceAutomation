package com.phoenixx;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Junaid Talpur
 * @project ${PROJECT_NAME}
 * @since ${TIME} [${DAY}-${MONTH}-${YEAR}]
 */
public class Main {
    public static void main(String[] args) {
        String html = "<html><head><title>My title</title></head><body><p>Paragraph 1</p><p>Paragraph 2</p></body></html>";

        // HashMap to store tags and their corresponding data
        Map<String, List<String>> tagDataMap = new HashMap<>();

        // Parse the HTML
        Document doc = Jsoup.parse(html);
        Elements allElements = doc.getAllElements();

        for (Element element : allElements) {
            String tagName = element.tagName();

            if (!element.ownText().isEmpty()) {
                List<String> dataList = tagDataMap.getOrDefault(tagName, new ArrayList<>());
                dataList.add(element.ownText());
                tagDataMap.put(tagName, dataList);
            }
        }

        for (Map.Entry<String, List<String>> entry : tagDataMap.entrySet()) {
            System.out.println("Tag: " + entry.getKey());
            System.out.println("Data: " + entry.getValue());
            System.out.println("-------------------------------------------------");
        }
    }
}