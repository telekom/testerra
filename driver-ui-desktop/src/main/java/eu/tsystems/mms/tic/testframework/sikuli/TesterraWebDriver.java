/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
 package eu.tsystems.mms.tic.testframework.sikuli;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.testing.TestController;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import java.awt.Rectangle;
import java.net.URL;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.sikuli.api.DefaultScreenRegion;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.ScreenLocation;
import org.sikuli.api.ScreenRegion;

/**
 * Testerra WebDriver is a ScreenshotWebdriver enhanced by the possibilty to find elements by coordinates and images.
 */
public class TesterraWebDriver extends RemoteWebDriver implements SikuliDriver, Loggable {

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
            log().info("Could not find web element: " + o.toString());
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
        TestController.Overrides overrides = Testerra.injector.getInstance(TestController.Overrides.class);
        final ScreenRegion imageRegion = webdriverRegion.wait(target, overrides.getElementTimeoutInSeconds()*1000);

        if (imageRegion != null) {
            Rectangle r = imageRegion.getBounds();
            log().debug("image is found at " + r.x + "," + r.y + " with dimension " + r.width + "," + r.height);
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
