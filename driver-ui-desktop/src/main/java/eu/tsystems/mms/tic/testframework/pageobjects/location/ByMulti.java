package eu.tsystems.mms.tic.testframework.pageobjects.location;

import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

public class ByMulti extends By {

    private final WebDriver driver;
    private final String selector;

    private String xpath;
    private Rectangle boundingBox;

    public ByMulti(WebDriver driver, String selector) {
        this.selector = selector;
        this.driver = driver;

        deserialize();
    }

    private void deserialize() {
        String[] splits = selector.split(" ");

        xpath = splits[0];

        if (splits.length >= 5) {
            int minX = Integer.parseInt(splits[1]);
            int minY = Integer.parseInt(splits[2]);
            int width = Integer.parseInt(splits[3]);
            int height = Integer.parseInt(splits[4]);

            boundingBox = new Rectangle(minX, minY, height, width);
            System.out.println("Found bounding box: " + boundingBox);
        } else {
            boundingBox = null;
        }
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
        List<WebElement> xpathElements = new ByXPath(xpath).findElements(searchContext);

        if (xpathElements.size() > 0) {
            return xpathElements;
        }

        if (boundingBox != null) {
            return new ByBoundingBox(driver, boundingBox).findElements(searchContext);
        } else {
            return new ArrayList<>();
        }
    }
}
