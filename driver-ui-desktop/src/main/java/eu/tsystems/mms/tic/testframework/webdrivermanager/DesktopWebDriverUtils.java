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

import eu.tsystems.mms.tic.testframework.constants.JSMouseAction;
import eu.tsystems.mms.tic.testframework.internal.StopWatch;
import eu.tsystems.mms.tic.testframework.model.NodeInfo;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.POConfig;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import eu.tsystems.mms.tic.testframework.utils.RESTUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import java.awt.Color;
import org.json.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DesktopWebDriverUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DesktopWebDriverUtils.class);

    public DesktopWebDriverUtils() {

    }

    public static NodeInfo getNodeInfo(DesktopWebDriverRequest desktopWebDriverRequest) {
        if (desktopWebDriverRequest.webDriverMode == WebDriverMode.local) {
            return new NodeInfo("local", 0);
        }

        final String host = desktopWebDriverRequest.seleniumServerHost;
        final String port = desktopWebDriverRequest.seleniumServerPort;
        String url = desktopWebDriverRequest.seleniumServerURL;

        final String sessionId = desktopWebDriverRequest.storedSessionId;

        url = url.replace("/wd/hub", "");

        /*
        grid3 mode
         */
        try {
            String nodeResponse = RESTUtils.requestGET(url + "/host/" + sessionId, 30 * 1000, String.class);
            JSONObject out = new JSONObject(nodeResponse);
            NodeInfo nodeInfo = new NodeInfo(out.getString("Name"), out.getInt("Port"));
            return nodeInfo;
        } catch (Exception e) {
            LOGGER.info("Could not get node info", e);
            return new NodeInfo(host, Integer.valueOf(port));
        }
    }

    public void clickAbsolute(GuiElement guiElement) {
        LOGGER.trace("Absolute navigation and click on: " + guiElement.toString());
        clickAbsolute(guiElement.getWebDriver(), guiElement.getWebElement());
    }

    /**
     * @deprecated Please use: {@link #clickAbsolute(GuiElement)}
     */
    @Deprecated
    public void clickAbsolute(final WebDriver webDriver, final WebElement webElement) {
        pClickAbsolute(webDriver, webElement);
    }

    private void demoMouseOver(GuiElement guiElement) {
        if (POConfig.isDemoMode()) {
            guiElement.highlight(new Color(255, 255, 0));
        }
    }

    /**
     * @deprecated Please use: {@link #demoMouseOver(GuiElement)}
     */
    @Deprecated
    private void demoMouseOver(final WebDriver webDriver, final WebElement webElement) {
        if (POConfig.isDemoMode()) {
            highlightWebElement(webDriver, webElement, new Color(255, 255, 0));
        }
    }

    public void highlightWebElement(final WebDriver webDriver, final WebElement webElement, final Color color) {
        JSUtils.highlightWebElement(webDriver, webElement, color);
    }

    public void mouseOverAbsolute2Axis(GuiElement guiElement) {
        mouseOverAbsolute2Axis(guiElement.getWebDriver(), guiElement.getWebElement());
    }

    /**
     * @deprecated Please use: {@link #mouseOverAbsolute2Axis(GuiElement)}
     */
    @Deprecated
    public void mouseOverAbsolute2Axis(final WebDriver webDriver, final WebElement webElement) {
        demoMouseOver(webDriver, webElement);
        pMouseOverAbsolute2Axis(webDriver, webElement);
    }

    public void mouseOverJS(GuiElement guiElement) {
        demoMouseOver(guiElement);
        pMouseOverJS(guiElement.getWebDriver(), guiElement.getWebElement());
    }

    /**
     * @deprecated Please use: {@link #mouseOverJS(GuiElement)}
     */
    @Deprecated
    public void mouseOverJS(final WebDriver webDriver, final WebElement webElement) {
        demoMouseOver(webDriver, webElement);
        pMouseOverJS(webDriver, webElement);
    }

    private void pMouseOverJS(final WebDriver webDriver, final WebElement webElement) {
        final String code = "var fireOnThis = arguments[0];"
                + "var evObj = document.createEvent('MouseEvents');"
                + "evObj.initEvent( 'mouseover', true, true );"
                + "fireOnThis.dispatchEvent(evObj);";

        ((JavascriptExecutor) webDriver).executeScript(code, webElement);
    }

    private void pClickAbsolute(WebDriver driver, WebElement webElement) {
        // Start the StopWatch for measuring the loading time of a Page
        StopWatch.startPageLoad(driver);

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
    }

    private void pMouseOverAbsolute2Axis(WebDriver driver, WebElement webElement) {
        Actions action = new Actions(driver);

        Point point = webElement.getLocation();

        // goto 0,0
        action.moveToElement(webElement, 1 + -point.getX(), 1 + -point.getY()).perform();

        // move y, then x
        action.moveByOffset(0, point.getY()).moveByOffset(point.getX(), 0).perform();

        // move to webElement
        action.moveToElement(webElement).perform();
    }

    public void clickJS(GuiElement guiElement) {
        clickJS(guiElement.getWebDriver(), guiElement.getWebElement());
    }

    /**
     * @deprecated Please use: {@link #clickJS(GuiElement)}
     */
    @Deprecated
    public void clickJS(final WebDriver webDriver, final WebElement webElement) {
        JSUtils.executeScript(webDriver, "arguments[0].click();", webElement);
    }

    public void rightClickJS(GuiElement guiElement) {
        rightClickJS(guiElement.getWebDriver(), guiElement.getWebElement());
    }

    /**
     * @deprecated Please use: {@link #rightClickJS(GuiElement)}
     */
    @Deprecated
    public void rightClickJS(final WebDriver webDriver, final WebElement webElement) {
        String script = "var element = arguments[0];" +
                "var e = element.ownerDocument.createEvent('MouseEvents');" +
                "e.initMouseEvent('contextmenu', true, true,element.ownerDocument.defaultView, 1, 0, 0, 0, 0, false,false, false, false,2, null);" +
                "return !element.dispatchEvent(e);";

        JSUtils.executeScript(webDriver, script, webElement);
    }

    public void doubleClickJS(GuiElement guiElement) {
        doubleClickJS(guiElement.getWebDriver(), guiElement.getWebElement());
    }

    /**
     * @deprecated Please use: {@link #doubleClickJS(GuiElement)}
     */
    @Deprecated
    public void doubleClickJS(final WebDriver webDriver, final WebElement webElement) {
        Point location = webElement.getLocation();
        JSUtils.executeJavaScriptMouseAction(webDriver, webElement, JSMouseAction.DOUBLE_CLICK, location.getX(), location.getY());
    }
}
