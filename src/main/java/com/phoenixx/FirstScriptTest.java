package com.phoenixx;

/**
 * @author Junaid Talpur
 * @project PerformanceAutomation
 * @since 12:43 PM [30-06-2023]
 */

import org.checkerframework.checker.units.qual.C;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;

public class FirstScriptTest {

    private final static List<String> Steps = new ArrayList<>(Arrays.asList(
            "Hydra",
            "src",
            "main",
            "java/com/phoenixx",
            "HydraApp.java"
    ));

    // HashMap to store tags and their corresponding data
    private final static Map<String, List<String>> tagDataMap = new HashMap<>();

    private final static List<WebElement> currentElements = new ArrayList<>();

    private static int currentIndex = 0;

    public static void main(String[] args) throws InterruptedException {

/*
        String html = "<p>Para one</p><p>Para <b>two keyword</b></p>";
        Document doc = Jsoup.parse(html);

        Element el = doc.select(":containsOwn(keyword)").first();
        Element p = doc.select("p:contains(keyword)").first();

        System.out.println(el.html());
        System.out.println(p.html());
*/

        //TODO Read in webpage source, and simply / remove all HTML tags and leave the rendered text only. Then let GPT read that in and create a selenium call

        ChromeOptions options = new ChromeOptions();
        options.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
        WebDriver driver = new ChromeDriver();
        driver.get("https://github.com/PhoenixxFTW");

        for(String clickOnElement: Steps) {
            String pageSource = driver.getPageSource();

            // Convert the page source into our tag map
            convertToMap(pageSource, false);

            // This is required sometimes, not even sure why
            //driver.navigate().refresh();

            boolean success = false;
            int limit = 0;
            while(!success && limit < 5) {
                try {
                    //driver.navigate().refresh();

                    retrieveAllInteractables(driver, true);

                    // Find the clickable element with the given name
                    WebElement element = getTagFromName(clickOnElement);

                    if(element != null) {
                        element.click();
                        success = true;
                        System.out.println("Successfully clicked element: " + clickOnElement);
                    } else {
                        System.out.println("Could not match element with text: " + clickOnElement);
                        limit++;

                    }
                } catch (Exception e) {
                    System.out.println("Failure while retrieving intractable element, calling again...");
                    e.printStackTrace();

                    success = false;
                    limit++;

                    // This is required sometimes, not even sure why
                    driver.navigate().refresh();
                }

            }
        }

        //driver.quit();
    }

    public static void retrieveAllInteractables(WebDriver driver, boolean print) {
        System.out.println("----------------------");

        List<String> clickableTexts = new ArrayList<>();

        currentElements.clear();

        // Fetch and process button elements
        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        currentElements.addAll(buttons);
        System.out.println("BUTTON SIZE: " + buttons.size());
        if (print) {
            for (WebElement button : buttons) {
                String text = button.getText();
                if (!text.trim().isEmpty()) {
                    clickableTexts.add("BUTTON: " + text);
                }
            }
        }

        // Fetch and process link elements
        List<WebElement> links = driver.findElements(By.tagName("a"));
        currentElements.addAll(links);
        System.out.println("links ELEM SIZE: " + links.size());
        if (print) {
            for (WebElement link : links) {
                String text = link.getText();
                if (!text.trim().isEmpty()) {
                    clickableTexts.add("links: " + text);
                }
            }
        }

        // Fetch and process elements with onclick attribute
        List<WebElement> onClickElems = driver.findElements(By.xpath("//*[@onclick]"));
        currentElements.addAll(onClickElems);
        System.out.println("CLICK ELEM SIZE: " + onClickElems.size());
        if (print) {
            for (WebElement elem : onClickElems) {
                String text = elem.getText();
                if (!text.trim().isEmpty()) {
                    clickableTexts.add("onClickElems: " + text);
                }
            }
        }

        // Print all clickable texts
        for (String text : clickableTexts) {
            System.out.println(text);
        }


    }

    public static WebElement getTagFromName(String name) {
        for(WebElement element: currentElements) {
            if(element.getText().equalsIgnoreCase(name)) {
                return element;
            }
        }
        /*int counter = 0;
        for(String tag: tagDataMap.keySet()) {
            if(counter >= currentIndex) {
                for (String val : tagDataMap.get(tag)) {
                    if (val.equalsIgnoreCase(name)) {
                        return tag;
                    }
                }
            }
            counter++;
        }*/
        return null;
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