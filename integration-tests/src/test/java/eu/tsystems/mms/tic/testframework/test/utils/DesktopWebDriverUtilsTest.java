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

import eu.tsystems.mms.tic.testframework.AbstractExclusiveTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import org.testng.annotations.Test;

public class DesktopWebDriverUtilsTest extends AbstractExclusiveTestSitesTest<WebTestPage> {

    @Override
    public Class<WebTestPage> getPageClass() {
        return WebTestPage.class;
    }

    /**
     * Test the clickJS method of DesktopWebDriverUtils
     */
    @Test
    public void testT01_DesktopWebDriverUtils_clickJS() {
        WebTestPage page = getPage();

        page.submitButton.hover();
        desktopWebDriverUtils.clickJS(page.submitButton);
        page.textOutputField.expect().text().isContaining("Form 16 submit");
    }

    /**
     * Test the doubleClickJS method of DesktopWebDriverUtils
     */
    @Test
    public void testT02_DesktopWebDriverUtils_doubleClickJS() {
        WebTestPage page = getPage();

        desktopWebDriverUtils.doubleClickJS(page.checkbox);
        page.textOutputField.expect().text().isContaining("Input 3 Double clicked");
    }

    /**
     * Test the rightClickJS method of DesktopWebDriverUtils
     */
    @Test
    public void testT03_DesktopWebDriverUtils_rightClickJS() {
        WebTestPage page = getPage();

        desktopWebDriverUtils.rightClickJS(page.submitButton);    // How can we assert a right click?
    }

    /**
     * Test the mouseOverJS method of DesktopWebDriverUtils
     */
    @Test
    public void testT04_DesktopWebDriverUtils_mouseOverJS() {
        WebTestPage page = getPage();

        desktopWebDriverUtils.mouseOverJS(page.textbox);
        page.textOutputField.expect().text().isContaining("Input 5 Mouse over");
    }

    /**
     * Test the clickAbsolute method of DesktopWebDriverUtils
     */
    @Test
    public void testT05_DesktopWebDriverUtils_clickAbsolute() {
        WebTestPage page = getPage();

        page.submitButton.hover();
        desktopWebDriverUtils.clickAbsolute(page.submitButton);
        page.textOutputField.expect().text().isContaining("Form 16 submit");
    }

    /**
     * Test the mouseOverAbsolute2Axis method of DesktopWebDriverUtils
     */
    @Test
    public void testT06_DesktopWebDriverUtils_mouseOverAbsolute2Axis() {
        WebTestPage page = getPage();

        desktopWebDriverUtils.mouseOverAbsolute2Axis(page.textbox);      // How can we assert this special mouse over?
    }

    /**
     * Test the mouseOverByImage method of DesktopWebDriverUtils
     */
    @Test
    public void testT07_mouseOverByImage() {
        WebTestPage page = getPage();

        String fileName = "byImage/input_field.png";
        desktopWebDriverUtils.mouseOverByImage(page.getWebDriver(), fileName);
        page.textOutputField.expect().text().isContaining("Input 5 Mouse over");
    }

    /**
     * Test the clickOverByImage method of DesktopWebDriverUtils
     */
    @Test
    public void testT08_clickByImage() {
        WebTestPage page = getPage();

        String fileName = "byImage/submit_button.png";
        desktopWebDriverUtils.clickByImage(page.getWebDriver(), fileName);
        page.textOutputField.expect().text().isContaining("Form 16 submit");
    }

    /**
     * Test the clickOverByImage method of DesktopWebDriverUtils for an element that is not inside the current viewport
     */
    @Test
    public void testT09_clickByImageWithScrolling() {
        WebTestPage page = getPage();

        String fileName = "byImage/click_me_button.png";
        desktopWebDriverUtils.clickByImage(page.getWebDriver(), fileName);
        page.clickPositionInfo.expect().text().isContaining("center center");
    }
}
