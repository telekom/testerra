package eu.tsystems.mms.tic.testframework.utils;

import org.openqa.selenium.Rectangle;

public interface Formatter {
    default String toString(Rectangle rectangle) {
        return String.format("(left: %d, top: %d, right: %d, bottom: %d)", rectangle.x, rectangle.y, rectangle.x+rectangle.width,rectangle.y+rectangle.height);
    }
    default String cutString(String string, int maxLength) {
        return cutString(string, maxLength, "...");
    }
    default String cutString(String string, int maxLength, String replacement) {
        if (string.length()>maxLength) {
            int first = Math.round((maxLength-replacement.length())/2);
            int second = string.length()-first;
            return String.format("%s%s%s", string.substring(0, first), replacement, string.substring(second));
        } else {
            return string;
        }
    }
}
