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

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.model.NodeInfo;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import eu.tsystems.mms.tic.testframework.utils.RESTUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import org.json.JSONObject;

public final class DesktopWebDriverUtils implements Loggable {

    JSUtils utils = new JSUtils();

    public DesktopWebDriverUtils() {

    }

    public NodeInfo getNodeInfo(DesktopWebDriverRequest desktopWebDriverRequest) {
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
            log().debug("Could not get node info: " + e.getMessage());
            return new NodeInfo(host, Integer.valueOf(port));
        }
    }

    public void clickAbsolute(UiElement guiElement) {
        log().debug("Absolute navigation and click on: " + guiElement.toString());
        guiElement.findWebElement(webElement -> utils.clickAbsolute(guiElement.getWebDriver(), webElement));
    }

    public void mouseOverAbsolute2Axis(UiElement guiElement) {
        guiElement.findWebElement(webElement -> utils.mouseOverAbsolute2Axis(guiElement.getWebDriver(), webElement));
    }

    public void mouseOverJS(UiElement guiElement) {
        guiElement.findWebElement(webElement -> utils.mouseOver(guiElement.getWebDriver(), webElement));
    }

    public void clickJS(UiElement guiElement) {
        guiElement.findWebElement(webElement -> utils.click(guiElement.getWebDriver(), webElement));
    }

    public void rightClickJS(UiElement guiElement) {
        guiElement.findWebElement(webElement -> utils.rightClick(guiElement.getWebDriver(), webElement));
    }

    public void doubleClickJS(UiElement guiElement) {
        guiElement.findWebElement(webElement -> utils.doubleClick(guiElement.getWebDriver(), webElement));
    }
}
