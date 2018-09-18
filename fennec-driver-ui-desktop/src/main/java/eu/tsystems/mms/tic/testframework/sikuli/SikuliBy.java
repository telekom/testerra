package eu.tsystems.mms.tic.testframework.sikuli;

import eu.tsystems.mms.tic.testframework.pageobjects.location.ByImage;
import eu.tsystems.mms.tic.testframework.pageobjects.location.FennecBy;
import org.openqa.selenium.WebDriver;

import java.net.URL;

public abstract class SikuliBy extends FennecBy {

    /**
     * xeta by image with webdriver and url
     *
     * @param driver .
     * @param url .
     * @return .
     */
    public static FennecBy image(final WebDriver driver, final URL url) {
        return new ByImage(driver, url);
    }


}
