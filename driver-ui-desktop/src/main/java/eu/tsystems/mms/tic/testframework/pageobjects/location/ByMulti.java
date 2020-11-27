package eu.tsystems.mms.tic.testframework.pageobjects.location;

import org.openqa.selenium.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ByMulti extends By {

    private final WebDriver driver;

    private final MultiDimSelector selector;

    public ByMulti(WebDriver driver, String encodedSelector) {
        this.driver = driver;

        this.selector = deserialize(encodedSelector);
    }

    public static String serialize(Serializable data) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(data);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public static MultiDimSelector deserialize(String s) {
        byte [] data = Base64.getDecoder().decode(s);
        Object o;
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(data));
            o = ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return (MultiDimSelector) o;
    }

    private static class ByBoundingBox extends By {

        private final WebDriver webDriver;
        private final Rectangle rect;

        private final double THRESHOLD = 0.8;

        public ByBoundingBox(WebDriver webDriver, Rectangle rect) {
            this.rect = rect;
            this.webDriver = webDriver;
        }

        @Override
        public List<WebElement> findElements(SearchContext searchContext) {
            List<WebElement> webElements = new ByCoordinates(webDriver,
                    rect.x + rect.width / 2,
                    rect.y + rect.height / 2).findElements(searchContext);

            List<WebElement> overlappingElements = new ArrayList<>();
            System.out.println("aha");
            for (WebElement element : webElements) {
                Rectangle rect2 = element.getRect();
                int intersection = Math.max(0, Math.min(rect.x+rect.width,  rect2.x+rect2.width)  - Math.max(rect.x, rect2.x)) *
                                   Math.max(0, Math.min(rect.y+rect.height, rect2.y+rect2.height) - Math.max(rect.y, rect2.y));

                int union = (rect.width* rect.height) + (rect2.width * rect2.height) - intersection;

                float overlap = (float) intersection / union;
                System.out.println("Overlap of: " + overlap);
                if (overlap >= THRESHOLD) {
                    overlappingElements.add(element);
                }
            }

            return overlappingElements;
        }
    }

    @Override
    public List<WebElement> findElements(SearchContext searchContext) {
        List<WebElement> xpathElements = new ByXPath(selector.getXpath()).findElements(searchContext);

        if (xpathElements.size() > 0) {
            return xpathElements;
        }

        if (selector.getRect() != null) {
            return new ByBoundingBox(driver, selector.getRect()).findElements(searchContext);
        } else {
            return new ArrayList<>();
        }
    }
}
