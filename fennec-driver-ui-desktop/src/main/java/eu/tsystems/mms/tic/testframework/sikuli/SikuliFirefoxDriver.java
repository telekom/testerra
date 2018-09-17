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
package eu.tsystems.mms.tic.testframework.sikuli;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.sikuli.api.DefaultScreenRegion;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.ScreenLocation;
import org.sikuli.api.ScreenRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 *
 */
public class SikuliFirefoxDriver extends FirefoxDriver implements SikuliDriver {

    private static Logger logger = LoggerFactory.getLogger(SikuliFirefoxDriver.class);

    private static final int DEFAULT_WAIT_TIMEOUT_MSECS = 10000;
    private ScreenRegion webdriverRegion;

    /**
     * try to initialize sikuli firefox driver
     */
    public SikuliFirefoxDriver() {
        WebDriverScreen webDriverScreen;
        try {
            webDriverScreen = new WebDriverScreen(this);
        } catch (IOException e1) {
            throw new RuntimeException("unable to initialize SikuliFirefoxDriver");
        }
        webdriverRegion = new DefaultScreenRegion(webDriverScreen);
    }

    /**
     * finds element by his location
     *
     * @param x .
     * @param y .
     * @return .
     */
    public WebElement findElementByLocation(int x, int y) {
        return (WebElement) ((JavascriptExecutor) this).executeScript("return document.elementFromPoint(" + x + "," + y + ")");
    }

    /**
     * searches for image element
     *
     * @param imageUrl .
     * @return .
     */
    public ImageElement findImageElement(URL imageUrl) {
        ImageTarget target = new ImageTarget(imageUrl);
        final ScreenRegion imageRegion = webdriverRegion.wait(target, DEFAULT_WAIT_TIMEOUT_MSECS);


        if (imageRegion != null) {
            Rectangle r = imageRegion.getBounds();
            logger.debug("image is found at {} {} {} {}" + r.x + " " + r.y + " " + r.width + " " + r.height);
        } else {
            logger.debug("image is not found");
            return null;
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
