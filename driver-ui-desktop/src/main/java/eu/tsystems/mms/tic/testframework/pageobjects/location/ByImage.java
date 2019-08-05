/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
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
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
/*
 * Created on 10.02.14
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.pageobjects.location;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.sikuli.WebDriverScreen;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.utils.WebDriverUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.*;
import org.sikuli.api.DefaultScreenRegion;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.ScreenLocation;
import org.sikuli.api.ScreenRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public class ByImage extends TesterraBy {

    private final URL url;
    private TakesScreenshot takesScreenshotDriver;
    private WebDriver driver;
    private int centerX;
    private int centerY;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * decision for using driver or webdriver by image
     *
     * @param driver .
     * @param url .
     */
    public ByImage(final WebDriver driver, final URL url) {
        if (driver != null) {
            checkDriver(driver);
        }
        else {
            checkDriver(WebDriverManager.getWebDriver());
        }

        this.url = url;
    }

    private void checkDriver(WebDriver driver) {
        if (driver instanceof TakesScreenshot) {
            this.takesScreenshotDriver = (TakesScreenshot) driver;
            this.driver = driver;
        }
        else {
            throw new TesterraSystemException("Your WebDriver instance is not a TakesScreenshot instance. " +
                    "Only TakesScreenshot webdrivers are supported for this action.");
        }
    }

    @Override
    public List<WebElement> findElements(SearchContext searchContext) {
        WebDriverScreen webDriverScreen;
        try {
            webDriverScreen = new WebDriverScreen(takesScreenshotDriver);
        } catch (IOException e1) {
            throw new RuntimeException("unable to initialize SikuliFirefoxDriver");
        }
        ScreenRegion webdriverRegion = new DefaultScreenRegion(webDriverScreen);

        ImageTarget target = new ImageTarget(url);
        final ScreenRegion imageRegion = webdriverRegion.find(target); //wait(target, DEFAULT_WAIT_TIMEOUT_MSECS);
                                                                       // timing is made in here - pele 10.02.2014

        if (imageRegion != null) {
            java.awt.Rectangle r = imageRegion.getBounds();
            logger.debug("image " + url + " found at " + r.x + "," + r.y + " with dimension " + r.width + "," + r.height);
        }
        else {
            throw new RuntimeException("Element not found similar to " + url);
        }

        ScreenLocation center = imageRegion.getCenter();
        centerX = center.getX();
        centerY = center.getY();
        driver.switchTo().defaultContent();
        WebElement webElement = WebDriverUtils.findElementByLocation(driver, centerX, centerY); // x and y are switched

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
                centerX = centerX - location.getX();
                centerY = centerY - location.getY();

                driver.switchTo().frame(webElement);
                webElement = WebDriverUtils.findElementByLocation(driver, centerX, centerY);

                if (webElement == null) {
                    tagName = null;
                }
                else {
                    tagName = webElement.getTagName();
                }
            }

            // finally add the element
            webElements.add(webElement);
        }
        else {
            return webElements;
        }

        return webElements;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    @Override
    public String toString() {
        return "ByImage{" + url + '}';
    }
}
