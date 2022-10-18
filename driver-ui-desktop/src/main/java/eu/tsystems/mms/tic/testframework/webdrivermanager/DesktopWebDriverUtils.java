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

import com.google.inject.Inject;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;

public final class DesktopWebDriverUtils implements Loggable {
    final JSUtils utils;

    @Inject
    public DesktopWebDriverUtils(final JSUtils jsUtils) {
        this.utils = jsUtils;
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
