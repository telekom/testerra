package eu.tsystems.mms.tic.testframework.pageobjects.location;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class ByMulti extends By {

    private final static Charset ENCODING = StandardCharsets.ISO_8859_1;

    private final WebDriver driver;
    private final JSONObject selector;

    public ByMulti(WebDriver driver, String encodedSelector) {
        this.driver = driver;

        this.selector = deserialize(encodedSelector);
    }

    public static String serialize(JSONObject json) {
        byte[] compressed = compress(json.toString());

        return new String(compressed, ENCODING);
    }

    public static byte[] compress(String text) {
        byte[] bArray = text.getBytes();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            DeflaterOutputStream dos = new DeflaterOutputStream(os);
            dos.write(bArray);
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[]{};
        }
        return os.toByteArray();
    }

    public static JSONObject deserialize(String s) {
        byte[] decompressed = decompress(s.getBytes(ENCODING));

        return new JSONObject(new String(decompressed));
    }

    public static byte[] decompress(byte[] compressedTxt) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            OutputStream ios = new InflaterOutputStream(os);
            ios.write(compressedTxt);
            ios.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[]{};
        }
        return os.toByteArray();
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

                if (overlap >= THRESHOLD) {
                    overlappingElements.add(element);
                }
            }
            return overlappingElements;
        }
    }

    @Override
    public List<WebElement> findElements(SearchContext searchContext) {
        List<WebElement> xpathElements = new ByXPath(selector.getString("xpath")).findElements(searchContext);

        if (xpathElements.size() > 0) {
            return xpathElements;
        }

        if (selector.has("bb")) {
            JSONArray bb = selector.getJSONArray("bb");
            // index 3 and 2 are swapped as Rectangle takes height before width as parameter
            Rectangle rect = new Rectangle(bb.getInt(0), bb.getInt(1), bb.getInt(3), bb.getInt(2));
            return new ByBoundingBox(driver, rect).findElements(searchContext);
        } else {
            return new ArrayList<>();
        }
    }
}
