/*
 * Testerra
 *
 * (C) 2020, Martin Großmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.test.utils;

import com.google.inject.Inject;
import eu.tsystems.mms.tic.testframework.ioc.DriverUi_Desktop;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locator;
import eu.tsystems.mms.tic.testframework.test.guielement.AbstractGuiElementTest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverUtils;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

@Guice(modules = DriverUi_Desktop.class)
public class DesktopWebDriverUtilsTest extends AbstractGuiElementTest {
    @Inject
    public DesktopWebDriverUtilsTest(final DesktopWebDriverUtils desktopWebDriverUtils) {
        super(desktopWebDriverUtils);
    }

    /**
     * Test the clickJS method of DesktopWebDriverUtils
     */
    @Test
    public void testT01_DesktopWebDriverUtils_clickJS() {
        GuiElement element = getClickableElement();
        GuiElement out = getLoggerTableElement();

        element.mouseOver();
        super.desktopWebDriverUtils.clickJS(element);
        out.asserts().assertTextContains("Form 16 submit");
    }

    /**
     * Test the doubleClickJS method of DesktopWebDriverUtils
     */
    @Test
    public void testT02_DesktopWebDriverUtils_doubleClickJS() {
        final GuiElement element = getSelectableElement();

        super.desktopWebDriverUtils.doubleClickJS(element);
        getLoggerTableElement().asserts().assertTextContains("Input 3 Double clicked");
    }

    /**
     * Test the rightClickJS method of DesktopWebDriverUtils
     */
    @Test
    public void testT03_DesktopWebDriverUtils_rightClickJS() {
        GuiElement element = getClickableElement();

        super.desktopWebDriverUtils.rightClickJS(element);    // How can we assert a right click?
    }

    /**
     * Test the mouseOverJS method of DesktopWebDriverUtils
     */
    @Test
    public void testT04_DesktopWebDriverUtils_mouseOverJS() {
        GuiElement element = getTextBoxElement();
        GuiElement out = getLoggerTableElement();

        super.desktopWebDriverUtils.mouseOverJS(element);
        out.asserts().assertTextContains("Input 5 Mouse over");
    }

    /**
     * Test the clickAbsolute method of DesktopWebDriverUtils
     */
    @Test
    public void testT05_DesktopWebDriverUtils_clickAbsolute() {
        GuiElement element = getClickableElement();
        GuiElement out = getLoggerTableElement();

        element.mouseOver();
        super.desktopWebDriverUtils.clickAbsolute(element);
        out.asserts().assertTextContains("Form 16 submit");
    }

    /**
     * Test the mouseOverAbsolute2Axis method of DesktopWebDriverUtils
     */
    @Test
    public void testT06_DesktopWebDriverUtils_mouseOverAbsolute2Axis() {
        GuiElement element = getTextBoxElement();
        GuiElement out = getLoggerTableElement();

        super.desktopWebDriverUtils.mouseOverAbsolute2Axis(element);      // How can we assert this special mouse over?
    }

    public GuiElement getGuiElementBy(Locator locator) {
        WebDriver driver = getWebDriver();
        return new GuiElement(driver, locator);
    }

}
