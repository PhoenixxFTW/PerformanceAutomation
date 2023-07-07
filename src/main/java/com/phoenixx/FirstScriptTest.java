package com.phoenixx;

/**
 * @author Junaid Talpur
 * @project PerformanceAutomation
 * @since 12:43 PM [30-06-2023]
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;

public class FirstScriptTest {

    private final static List<String> Steps = new ArrayList<>(Arrays.asList(
            "Click Hydra",
            "abc"
    ));

    // HashMap to store tags and their corresponding data
    private final static Map<String, List<String>> tagDataMap = new HashMap<>();

    private static int currentIndex = 0;

    public static void main(String[] args) {

/*
        String html = "<p>Para one</p><p>Para <b>two keyword</b></p>";
        Document doc = Jsoup.parse(html);

        Element el = doc.select(":containsOwn(keyword)").first();
        Element p = doc.select("p:contains(keyword)").first();

        System.out.println(el.html());
        System.out.println(p.html());
*/

        //TODO Read in webpage source, and simply / remove all HTML tags and leave the rendered text only. Then let GPT read that in and create a selenium call

        String clickOnElement = "Hydra";

        WebDriver driver = new ChromeDriver();
        driver.get("https://github.com/PhoenixxFTW");

       // System.out.println("Source: " + driver.getPageSource());

        String pageSource = driver.getPageSource();
        String allVisualStrings = Jsoup.parse(pageSource).text();

        // Convert the page source into our tag map
        convertToMap(pageSource, false);

        // The button the client should find and click on
        String clickedOnTag = getTagFromName(clickOnElement);

        // The element found, and clicked
        WebElement element = driver.findElement(By.xpath("//" + clickedOnTag + "[text()=\"" + clickOnElement + "\"]"));
        element.click();

        // New page source after clicking
        pageSource = driver.getPageSource();

        // Convert new page source to map
        convertToMap(pageSource, true);

        // Click on "Issues" element
        clickOnElement = "Pull Requests";

        boolean success = false;

        while (!success && currentIndex < tagDataMap.values().size()) {
            try {
                clickedOnTag = getTagFromName(clickOnElement);

                // The element found, and clicked
                //element = driver.findElement(By.xpath("//" + clickedOnTag + "[text()=\"" + clickOnElement + "\"]"));
                element = new WebDriverWait(driver, Duration.ofMillis(1000)).until(ExpectedConditions.elementToBeClickable(By.xpath("//" + clickedOnTag + "[text()=\"" + clickOnElement + "\"]")));

                System.out.println("ELEMENT: " + element.getAccessibleName() + " , " + element.getTagName() + ", " + element.getText());

                element.click();
            } catch (Exception e) {

                System.out.println("Failed to click element: '" + clickOnElement + "' with tag: '" + clickedOnTag + "' INDEX: " + currentIndex);

                // If the click failed, means we're on the wrong element, so we increment the counter and do the request again
                currentIndex++;
            } finally {

            }
        }

        if(success) {
            System.out.println("FOUND ELEMENT AT INDEX: " + currentIndex);
            currentIndex = 0;
        } else {
            System.out.println("FAILURE @@@@@@@@@@@@@@@@@@@@@@@@");
            return;
        }

     /*   System.out.println("Element: " + element);
        System.out.println("JSOUP: " + (jsoupToXpath(element)));
        System.out.println("JSOUP 2: " + (getXPath(element)));

        System.out.println("CLEAN HTML: " + allVisualStrings);

        List<String> texts = new ArrayList<>();

        Element body = document.body();
        body.forEach(element1 -> {
            for (TextNode textNode : element1.textNodes()) {
                String text = textNode.getWholeText().trim();
                if (!text.isEmpty()) {
                    texts.add(text);
                }
            }
        });

        System.out.println("ALL TITLES: " + texts);

        driver.findElement(By.xpath(getXPath(element))).click();
        //driver.findElement(By.xpath("//span[@title=\"Hydra\"]")
        String title = driver.getTitle();
        //System.out.println("Title: " + title);
*/
        //driver.quit();
    }

    public static String getTagFromName(String name) {
        int counter = 0;
        for(String tag: tagDataMap.keySet()) {
            if(counter >= currentIndex) {
                for (String val : tagDataMap.get(tag)) {
                    if (val.equalsIgnoreCase(name)) {
                        return tag;
                    }
                }
            }
            counter++;
        }
        return "";
    }

    public static void convertToMap(String pageSource, boolean print) {
        tagDataMap.clear();

        Document document = Jsoup.parse(pageSource);

        // Parse the HTML
        Elements allElements = document.getAllElements();

        for (Element element : allElements) {
            String tagName = element.tagName();

            if (!element.ownText().isEmpty()) {
                List<String> dataList = tagDataMap.getOrDefault(tagName, new ArrayList<>());
                dataList.add(element.ownText());
                tagDataMap.put(tagName, dataList);
            }
        }

        if(print) {
            for (Map.Entry<String, List<String>> entry : tagDataMap.entrySet()) {
                System.out.println("Tag: " + entry.getKey());
                System.out.println("Data: " + entry.getValue());
                System.out.println("-------------------------------------------------");
            }
        }
    }
}