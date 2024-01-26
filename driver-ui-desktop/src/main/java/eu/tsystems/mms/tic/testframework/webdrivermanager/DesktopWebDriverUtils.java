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
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.constants.JSMouseAction;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementHighlighter;
import eu.tsystems.mms.tic.testframework.sikuli.WebDriverScreen;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.sikuli.api.ColorImageTarget;
import org.sikuli.api.DefaultScreenRegion;
import org.sikuli.api.ScreenRegion;

import java.awt.Rectangle;
import java.awt.Color;
import java.io.File;
import java.util.Optional;

public final class DesktopWebDriverUtils implements Loggable {

    public DesktopWebDriverUtils() {

    }

    public void clickAbsolute(UiElement uiElement) {
        log().debug("Absolute navigation and click on: " + uiElement.toString());
        WebDriver driver = uiElement.getWebDriver();
        uiElement.findWebElement(webElement -> {
            Point point = webElement.getLocation();

            Actions action = new Actions(driver);

            // goto 0,0
            action.moveToElement(webElement, 1 + -point.getX(), 1 + -point.getY());

            // move y, then x
            action.moveByOffset(0, point.getY()).moveByOffset(point.getX(), 0);

            // move to webElement
            action.moveToElement(webElement);
            action.moveByOffset(1, 1);
            action.click().perform();
        });
    }

    public void mouseOverAbsolute2Axis(UiElement uiElement) {
        WebDriver driver = uiElement.getWebDriver();
        uiElement.findWebElement(webElement -> {
            demoMouseOver(driver, webElement);
            Actions action = new Actions(driver);

            Point point = webElement.getLocation();

            // goto 0,0
            action.moveToElement(webElement, 1 + -point.getX(), 1 + -point.getY()).perform();

            // move y, then x
            action.moveByOffset(0, point.getY()).moveByOffset(point.getX(), 0).perform();

            // move to webElement
            action.moveToElement(webElement).perform();
        });
    }

    public void mouseOverJS(UiElement uiElement) {
        WebDriver driver = uiElement.getWebDriver();
        uiElement.findWebElement(webElement -> {
            demoMouseOver(driver, webElement);

            final String script = "var fireOnThis = arguments[0];"
                    + "var evObj = document.createEvent('MouseEvents');"
                    + "evObj.initEvent( 'mouseover', true, true );"
                    + "fireOnThis.dispatchEvent(evObj);";
            JSUtils.executeScriptWOCatch(driver, script, webElement);
        });
    }

    public void clickJS(UiElement uiElement) {
        WebDriver driver = uiElement.getWebDriver();
        uiElement.findWebElement(webElement -> {
            JSUtils.executeScriptWOCatch(driver, "arguments[0].click();", webElement);
        });
    }

    public void rightClickJS(UiElement uiElement) {
        WebDriver driver = uiElement.getWebDriver();
        uiElement.findWebElement(webElement -> {
            final String script = "var element = arguments[0];" +
                    "var e = element.ownerDocument.createEvent('MouseEvents');" +
                    "e.initMouseEvent('contextmenu', true, true, element.ownerDocument.defaultView, 1, 0, 0, 0, 0, false,false, false, false,2, null);" +
                    "return !element.dispatchEvent(e);";
            JSUtils.executeScriptWOCatch(driver, script, webElement);
        });
    }

    public void doubleClickJS(UiElement uiElement) {
        uiElement.findWebElement(webElement -> {
            Point location = webElement.getLocation();
            JSUtils.executeJavaScriptMouseAction(uiElement.getWebDriver(), webElement, JSMouseAction.DOUBLE_CLICK, location.getX(), location.getY());
        });
    }

    private void demoMouseOver(final WebDriver webDriver, final WebElement webElement) {
        if (Testerra.Properties.DEMO_MODE.asBool()) {
            UiElementHighlighter elementHighlighter = Testerra.getInjector().getInstance(UiElementHighlighter.class);
            elementHighlighter.highlight(webDriver, webElement, new Color(255, 255, 0));
        }
    }

    public void clickByImage(final WebDriver driver, String fileName) {
        final Optional<ScreenRegion> imageRegion = getElementPositionByImage(driver, fileName);
        if (imageRegion.isEmpty()) {
            throw new RuntimeException("Could not find element similar to image: " + fileName);
        }
        Actions action = new Actions(driver);
        action
                .moveByOffset(imageRegion.get().getCenter().getX(), imageRegion.get().getCenter().getY())
                .click()
                .build()
                .perform();
    }

    public void mouseOverByImage(final WebDriver driver, String fileName) {
        final Optional<ScreenRegion> imageRegion = getElementPositionByImage(driver, fileName);
        if (imageRegion.isEmpty()) {
            throw new RuntimeException("Could not find element similar to image: " + fileName);
        }
        Actions action = new Actions(driver);
        action
                .moveByOffset(imageRegion.get().getCenter().getX(), imageRegion.get().getCenter().getY())
                .build()
                .perform();
    }

    private Optional<ScreenRegion> getElementPositionByImage(final WebDriver driver, String fileName) {
        File imageFile = FileUtils.getResourceFile(fileName);

        WebDriverScreen webDriverScreen;
        webDriverScreen = new WebDriverScreen(driver);
        ScreenRegion webdriverRegion = new DefaultScreenRegion(webDriverScreen);
        boolean elementFound = false;

        // The pattern to search for on the web page
        ColorImageTarget target = new ColorImageTarget(imageFile);
        // The resulting region in the browser
        ScreenRegion imageRegion;

        JSUtils utils = new JSUtils();
        // Search the element beginning at the top of the page
        utils.scrollToTop(driver);
        // Calculate the scroll height by halving the viewport height to ensure the searched image is not skipped
        int scrollHeight = (int) (driver.manage().window().getSize().getHeight() * 0.5);
        // Get the height of the page
        int documentHeight = utils.getDocumentHeight(driver);
        // The current scroll height in the browser
        int currentScrollHeight;

        do {
            currentScrollHeight = utils.getCurrentScrollHeight(driver);
            imageRegion = webdriverRegion.find(target);

            // Check if the element is visible in the current viewport
            if (imageRegion != null) {
                // Element is visible, break out of the loop
                Rectangle r = imageRegion.getBounds();
                log().info("Image \"{}\" found at ({},{}) with dimension {}x{}", imageFile.getAbsolutePath(), r.x, r.y, r.width, r.height);
                elementFound = true;
            } else {
                // Scroll down to further search for the element
                utils.scrollByOffset(driver, new Point(0, scrollHeight));
            }
        } while (currentScrollHeight < documentHeight && !elementFound); // End loop if element was found or bottom of page is reached

        if (elementFound) {
            return Optional.of(imageRegion);
        } else {
            return Optional.empty();
        }
    }
}
