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

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirstScriptTest {

    private final static List<String> Steps = new ArrayList<>(Arrays.asList(
            "Click Hydra",
            "abc"
    ));

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

        Document document = Jsoup.parse(driver.getPageSource());
        Element element = document.selectFirst(":containsOwn("+clickOnElement+")");

        System.out.println("Element: " + element);
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

        driver.quit();
    }

    public static String getXPath(Element el) {
        int index = el.elementSiblingIndex();
        String tagName = el.tagName();
        String xPath = "/" + tagName;

        for (Element parent = el.parent(); parent != null; parent = parent.parent()) {
            xPath = "/" + parent.tagName() + "[" + (parent.elementSiblingIndex() + 1) + "]" + xPath;
        }

        return xPath;
    }


    public static String jsoupToXpath(Element element) {
        String xpath = "/";
        List<String> components = new ArrayList<>();

        Element child = element.tagName().isEmpty() ? element.parent() : element;
        System.out.println(child.tag());
        while (child.parent() != null){
            Element parent = child.parent();
            Elements siblings = parent.children();
            String componentToAdd = null;

            if (siblings.size() == 1) {
                componentToAdd = child.tagName();
            } else {
                int x = 1;
                for(Element sibling: siblings){
                    if (child.tagName().equals(sibling.tagName())){
                        if (child == sibling){
                            break;
                        } else {
                            x++;
                        }
                    }
                }
                componentToAdd = String.format("%s[%d]", child.tagName(), x);
            }
            components.add(componentToAdd);
            child = parent;
        }

        List<String> reversedComponents = new ArrayList<>();
        for (int i = components.size()-1; i > 0; i--){
            reversedComponents.add(components.get(i));
        }
        xpath = xpath + String.join("/", reversedComponents);

        return xpath;
    }

}