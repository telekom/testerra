/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann
 *     pele
*/
package eu.tsystems.mms.tic.testframework.sikuli;

import eu.tsystems.mms.tic.testframework.pageobjects.POConfig;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.sikuli.api.DefaultScreenRegion;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.ScreenLocation;
import org.sikuli.api.ScreenRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URL;

/**
 * Testerra WebDriver is a ScreenshotWebdriver enhanced by the possibilty to find elements by coordinates and images.
 */
public class TesterraWebDriver extends RemoteWebDriver implements SikuliDriver {

    /**
     * Logger.
     */
    private static Logger logger = LoggerFactory.getLogger(TesterraWebDriver.class);

    /**
     * Default waiter.
     */
    private static final int DEFAULT_WAIT_TIMEOUT_MSECS = POConfig.getUiElementTimeoutInSeconds() * 1000;

    /**
     * Stored webdriver region.
     */
    private ScreenRegion webdriverRegion;

    /**
     * Constructor.
     *
     * @param executor            .
     * @param desiredCapabilities .
     */
    public TesterraWebDriver(CommandExecutor executor, Capabilities desiredCapabilities) {
        super(executor, desiredCapabilities);
        init();
    }

    /**
     * Constructor.
     *
     * @param desiredCapabilities .
     */
    public TesterraWebDriver(Capabilities desiredCapabilities) {
        super(desiredCapabilities);
        init();
    }

    /**
     * Constructor.
     *
     * @param remoteAddress       .
     * @param desiredCapabilities .
     */
    public TesterraWebDriver(URL remoteAddress, Capabilities desiredCapabilities) {
        super(remoteAddress, desiredCapabilities);
        init();
    }

    /**
     * Init.
     */
    public void init() {
        WebDriverScreen webDriverScreen;
        webDriverScreen = new WebDriverScreen(this);
        webdriverRegion = new DefaultScreenRegion(webDriverScreen);
    }

    /**
     * Find by element location.
     *
     * @param x .
     * @param y .
     *
     * @return .
     */
    public WebElement findElementByLocation(int x, int y) {
        Object o = JSUtils.executeScript(this, "return document.elementFromPoint(" + x + "," + y + ")");
        if (o instanceof WebElement) {
            return (WebElement) o;
        } else {
            logger.info("Could not find web element: " + o.toString());
        }
        return null;
    }

    /**
     * Find by picture.
     *
     * @param imageUrl .
     *
     * @return .
     */
    public ImageElement findImageElement(URL imageUrl) {
        ImageTarget target = new ImageTarget(imageUrl);
        final ScreenRegion imageRegion = webdriverRegion.wait(target, DEFAULT_WAIT_TIMEOUT_MSECS);

        if (imageRegion != null) {
            Rectangle r = imageRegion.getBounds();
            logger.debug("image is found at " + r.x + "," + r.y + " with dimension " + r.width + "," + r.height);
        } else {
            throw new RuntimeException("Element not found similar to " + imageUrl);
        }

        ScreenLocation center = imageRegion.getCenter();
        WebElement foundWebElement = findElementByLocation(center.getX(), center.getY());
        Rectangle r = imageRegion.getBounds();
        return new DefaultImageElement(this, foundWebElement,
                r.x,
                r.y,
                r.width,
                r.height);
    }
}
