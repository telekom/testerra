package eu.tsystems.mms.tic.testframework.sikuli;

import eu.tsystems.mms.tic.testframework.pageobjects.location.ByImage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.net.URL;

public abstract class SikuliBy extends By {

    /**
     * xeta by image with webdriver and url
     *
     * @param driver .
     * @param url .
     * @return .
     */
    public static ByImage image(final WebDriver driver, final URL url) {
        return new ByImage(driver, url);
    }
}
