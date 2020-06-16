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
 package eu.tsystems.mms.tic.testframework.pageobjects.location;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.sikuli.WebDriverScreen;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.utils.WebDriverUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.sikuli.api.DefaultScreenRegion;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.ScreenLocation;
import org.sikuli.api.ScreenRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public class ByImage extends By {

    private final URL url;
    private TakesScreenshot takesScreenshotDriver;
    private WebDriver driver;
    private Point center = new Point(0, 0);
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * decision for using driver or webdriver by image
     *
     * @param driver .
     * @param url    .
     */
    public ByImage(final WebDriver driver, final URL url) {
        if (driver != null) {
            checkDriver(driver);
        } else {
            checkDriver(WebDriverManager.getWebDriver());
        }

        this.url = url;
    }

    private void checkDriver(WebDriver driver) {
        if (driver instanceof TakesScreenshot) {
            this.takesScreenshotDriver = (TakesScreenshot) driver;
            this.driver = driver;
        } else {
            throw new TesterraSystemException("Your WebDriver instance is not a TakesScreenshot instance. " +
                    "Only TakesScreenshot webdrivers are supported for this action.");
        }
    }

    @Override
    public List<WebElement> findElements(SearchContext searchContext) {
        WebDriverScreen webDriverScreen;
        webDriverScreen = new WebDriverScreen(driver);
        ScreenRegion webdriverRegion = new DefaultScreenRegion(webDriverScreen);

        ImageTarget target = new ImageTarget(url);
        final ScreenRegion imageRegion = webdriverRegion.find(target); //wait(target, DEFAULT_WAIT_TIMEOUT_MSECS);
        // timing is made in here - pele 10.02.2014

        if (imageRegion != null) {
            Rectangle r = imageRegion.getBounds();
            logger.debug("image " + url + " found at " + r.x + "," + r.y + " with dimension " + r.width + "," + r.height);
        } else {
            throw new RuntimeException("Element not found similar to " + url);
        }

        ScreenLocation center = imageRegion.getCenter();
        this.center.x = center.getX();
        this.center.y = center.getY();
        driver.switchTo().defaultContent();
        WebElement webElement = WebDriverUtils.findElementByLocation(driver, this.center.x, this.center.y); // x and y are switched

        List<WebElement> webElements = new ArrayList<WebElement>(1);
        if (webElement != null) {

            int nr = 0;
            String tagName = webElement.getTagName();
            while (!StringUtils.isStringEmpty(tagName) && tagName.contains("frame")) {
                /*
                 this is a frame element
                 */
                nr++;
                logger.info("Found underlying frame #" + nr + ", switching to it (" + webElement + ")");

                // calculate offset
                Point location = webElement.getLocation();
                this.center.x -= location.getX();
                this.center.y -= location.getY();

                driver.switchTo().frame(webElement);
                webElement = WebDriverUtils.findElementByLocation(driver, this.center.x, this.center.y);

                if (webElement == null) {
                    tagName = null;
                } else {
                    tagName = webElement.getTagName();
                }
            }

            // finally add the element
            webElements.add(webElement);
        } else {
            return webElements;
        }

        return webElements;
    }

    public Point getCenter() {
        return this.center;
    }

    /**
     * @deprecated
     */
    public int getCenterX() {
        return this.center.x;
    }

    /**
     * @deprecated
     */
    public int getCenterY() {
        return this.center.y;
    }

    @Override
    public String toString() {
        try {
            if (url == null) {
                throw new TesterraSystemException("url is null, ensure to have an URL set!");
            }
            URI uri = url.toURI();
            if (uri == null) {
                throw new TesterraSystemException("Cannot build uri from url: " + url);
            }
            File file = new File(uri);
            if (file == null) {
                throw new TesterraSystemException("Cannot find file: " + uri);
            }
            return "ByImage{" +
                    "url=" + file.getName() +
                    '}';
        } catch (URISyntaxException e) {
            return "Unknown Image";
        }
    }
}
