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
package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Locatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by pele on 24.07.2015.
 */
public final class WebElementUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebElementUtils.class);

    /**
     * WebElement.isDisplayed.
     *
     * @param driver WebDriver.
     * @return true if element is displayed.
     */
    public static boolean isDisplayed(WebDriver driver, WebElement element) {
        return pIsDisplayed(driver, element);
    }

    private static boolean pIsDisplayed(WebDriver driver, WebElement element) {
        if (driver == null || element == null) {
            throw new IllegalArgumentException(
                    "Parameters must not be null. Driver: " + driver + " WebElement: " + element);
        }

        return element.isDisplayed() && isInViewPort(driver, element);
    }

    public static boolean isInViewPort(WebDriver driver, WebElement element) {
        return pIsInViewPort(driver, element);
    }

    private static boolean pIsInViewPort(WebDriver driver, WebElement element) {
        Dimension screenSize = driver.manage().window().getSize();
        Locatable item = (Locatable) element;

        int x;
        int y;
        try {
            y = item.getCoordinates().inViewPort().getY();
            x = item.getCoordinates().inViewPort().getX();
        } catch (WebDriverException e) {
            LOGGER.trace("Error getting location of element.", e);
            return false;
        }

        // Philosophical Question: Is a WebElement displayed, if only one Pixel of it is displayed? Currently, yes.
        Dimension size = element.getSize();
        boolean inViewportHorizontally = x + size.width >= 0 && x <= screenSize.getWidth();
        boolean inViewportVertically = y + size.height >= 0 && y <= screenSize.getHeight();
        if (inViewportHorizontally && inViewportVertically) {
            return true;
        } else {
            LOGGER.debug("Element not in view: " + x + "," + y + " " + element);
            return false;
        }
    }

    /**
     * Checks if an element is displayed in current viewport by requesting the scroll offset via JS prior to getting
     * the element display offset.
     * !! Scrolls element into view by selenium, does not affect the check itself.
     *
     * @param driver .
     * @param element .
     * @return boolean value.
     */
    public static boolean isInViewPortScrollJS(WebDriver driver, WebElement element) {
        return pIsInViewPortScrollJS(driver, element);
    }

    private static boolean pIsInViewPortScrollJS(WebDriver driver, WebElement element) {
        long scrollY = 0;
        long scrollX = 0;
        try {
            scrollY = 0;
            scrollX = 0;
            Object yOffset = JSUtils.executeScript(driver, "return window.scrollY;");
            if (yOffset != null) {
                scrollY = (Long) yOffset;
            }
            Object xOffset = JSUtils.executeScript(driver, "return window.scrollX;");
            if (xOffset != null) {
                scrollX = (Long) xOffset;
            }
        } catch (Exception e) {
            throw new FennecSystemException("Error: " + e);
        }

        Locatable item = (Locatable) element;

        int x;
        int y;
        try {
            y = item.getCoordinates().inViewPort().getY();
            x = item.getCoordinates().inViewPort().getX();
        } catch (WebDriverException e) {
            LOGGER.trace("Error getting location of element.", e);
            return false;
        }

        boolean inViewportHorizontally = x >= scrollX;
        boolean inViewportVertically = y >= scrollY;
        if (inViewportHorizontally && inViewportVertically) {
            return true;
        } else {
            LOGGER.debug("Element not in view: " + x + "," + y + " " + element);
            return false;
        }
    }
}
