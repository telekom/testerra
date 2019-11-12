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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.core.test.utils;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.utils.WebDriverUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by nigr on 07.09.2015.
 */
public class WebDriverUtilsTest extends AbstractTestSitesTest {

    private final String msgSwitchSuccessfully = "Find and switch to window successful";

    private WebDriver createWebDriver(boolean extraSession) {
        WebDriver driver;

        if (extraSession) {
            driver = WebDriverManager.getWebDriver("test");
        } else {
            driver = WebDriverManager.getWebDriver();
        }

        String url = TestPage.INPUT_TEST_PAGE.getUrl();
        driver.get(url);

        return driver;
    }

    private void openPopUpWindow(WebDriver driver) {
        GuiElement guiElement = new GuiElement(driver, By.linkText("Open pop up"));
        guiElement.click();
    }

    @Test
    public void testT01_WebDriverUtils_findWindowAndSwitchTo() throws Exception {
        WebDriver driver = createWebDriver(false);

        openPopUpWindow(driver);

        WebDriverUtils.findWindowAndSwitchTo("List", false);

        Assert.assertEquals(driver.getTitle(), "List", msgSwitchSuccessfully);
    }

    @Test
    public void testT02_WebDriverUtils_findWindowAndSwitchTo_WrongTitle() throws Exception {
        WebDriver driver = createWebDriver(false);

        openPopUpWindow(driver);

        boolean out = WebDriverUtils.findWindowAndSwitchTo("abc", false);

        Assert.assertFalse(out, msgSwitchSuccessfully);
    }

    @Test
    public void testT03_WebDriverUtils_findWindowAndSwitchTo_Fast() throws Exception {
        WebDriver driver = createWebDriver(false);

        openPopUpWindow(driver);

        long timeMillisBegin = System.currentTimeMillis();

        boolean out = WebDriverUtils.findWindowAndSwitchTo("List", true);

        long timeMillisDuration = System.currentTimeMillis() - timeMillisBegin;

        Assert.assertTrue(out, msgSwitchSuccessfully);
        Assert.assertTrue(timeMillisDuration < 300, "Find and switch to Window need less than 1000 ms");
    }

    @Test
    public void testT04_WebDriverUtils_findWindowAndSwitchTo_FastWrongTitle() throws Exception {
        WebDriver driver = createWebDriver(false);

        openPopUpWindow(driver);

        boolean out = WebDriverUtils.findWindowAndSwitchTo("abc", true);

        Assert.assertFalse(out, msgSwitchSuccessfully);
    }

    @Test
    public void testT05_WebDriverUtils_findWindowAndSwitchTo_Driver() throws Exception {
        WebDriver driver = createWebDriver(true);

        openPopUpWindow(driver);

        boolean out = WebDriverUtils.findWindowAndSwitchTo("List", true, driver);

        Assert.assertTrue(out, msgSwitchSuccessfully);
    }

    @Test
    public void testT06_WebDriverUtils_findWindowAndSwitchTo_DriverWrongTitle() throws Exception {
        WebDriver driver = createWebDriver(true);

        openPopUpWindow(driver);

        boolean out = WebDriverUtils.findWindowAndSwitchTo("abc", true, driver);

        Assert.assertFalse(out, msgSwitchSuccessfully);
    }

    @Test
    public void testT07_WebDriverUtils_findWindowAndSwitchTo_ContainsURL() throws Exception {
        WebDriver driver = createWebDriver(true);

        openPopUpWindow(driver);

        boolean out = WebDriverUtils.findWindowAndSwitchTo("List", "Input", true, driver);

        Assert.assertTrue(out, msgSwitchSuccessfully);
    }

    @Test
    public void testT08_WebDriverUtils_findWindowAndSwitchTo_ContainsWrongURL() throws Exception {
        WebDriver driver = createWebDriver(true);

        openPopUpWindow(driver);

        boolean out = WebDriverUtils.findWindowAndSwitchTo("List", "Output", true, driver);

        Assert.assertFalse(out, msgSwitchSuccessfully);
    }

    @Test
    public void testT10_linkChecker() throws Exception {
        final WebDriver driver = createWebDriver(false);
        WebDriverUtils.linkChecker("Test", driver);
    }

}
