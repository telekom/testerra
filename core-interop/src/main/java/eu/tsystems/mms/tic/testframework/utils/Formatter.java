package eu.tsystems.mms.tic.testframework.utils;

import org.openqa.selenium.Rectangle;

public interface Formatter {
    default String toString(Rectangle rectangle) {
        return String.format("(left: %d, top: %d, right: %d, bottom: %d)", rectangle.x, rectangle.y, rectangle.x+rectangle.width,rectangle.y+rectangle.height);
    }
}
