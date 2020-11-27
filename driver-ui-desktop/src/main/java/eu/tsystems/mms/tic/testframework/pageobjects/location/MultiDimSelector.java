package eu.tsystems.mms.tic.testframework.pageobjects.location;

import org.openqa.selenium.Rectangle;

import java.io.Serializable;

public class MultiDimSelector implements Serializable {

    private String xpath;

    private int x;
    private int y;
    private int width;
    private int height;

    public MultiDimSelector(String xpath, Rectangle rect) {
        this.xpath = xpath;

        this.x = rect.x;
        this.y = rect.y;
        this.width = rect.width;
        this.height = rect.height;
    }

    public String getXpath() {
        return xpath;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, height, width);
    }
}
