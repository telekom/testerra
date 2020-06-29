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
import org.json.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

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
        pClickAbsolute(guiElement.getWebDriver(), guiElement.getWebElement());
    }

    private void demoMouseOver(GuiElement guiElement) {
        if (POConfig.isDemoMode()) {
            guiElement.highlight(new Color(255, 255, 0));
        }
    }

    public void mouseOverAbsolute2Axis(GuiElement guiElement) {
        demoMouseOver(guiElement);
        mouseOverAbsolute2Axis(guiElement.getWebDriver(), guiElement.getWebElement());
    }

    public void mouseOverJS(GuiElement guiElement) {
        demoMouseOver(guiElement);
        pMouseOverJS(guiElement);
    }

    private void pMouseOverJS(GuiElement guiElement) {
        final String code = "var fireOnThis = arguments[0];"
                + "var evObj = document.createEvent('MouseEvents');"
                + "evObj.initEvent( 'mouseover', true, true );"
                + "fireOnThis.dispatchEvent(evObj);";

        ((JavascriptExecutor) guiElement.getWebDriver()).executeScript(code, guiElement.getWebElement());
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

    private void mouseOverAbsolute2Axis(WebDriver driver, WebElement webElement) {
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
        JSUtils.executeScript(guiElement.getWebDriver(), "arguments[0].click();", guiElement.getWebElement());
    }

    public void rightClickJS(GuiElement guiElement) {
        String script = "var element = arguments[0];" +
                "var e = element.ownerDocument.createEvent('MouseEvents');" +
                "e.initMouseEvent('contextmenu', true, true,element.ownerDocument.defaultView, 1, 0, 0, 0, 0, false,false, false, false,2, null);" +
                "return !element.dispatchEvent(e);";

        JSUtils.executeScript(guiElement.getWebDriver(), script, guiElement.getWebElement());
    }

    public void doubleClickJS(GuiElement guiElement) {
        WebElement webElement = guiElement.getWebElement();
        Point location = webElement.getLocation();
        JSUtils.executeJavaScriptMouseAction(guiElement.getWebDriver(), webElement, JSMouseAction.DOUBLE_CLICK, location.getX(), location.getY());
    }
}
