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
 package eu.tsystems.mms.tic.testframework.test.utils;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.annotations.TestClassContext;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import eu.tsystems.mms.tic.testframework.utils.WebDriverKeepAliveSequence;
import eu.tsystems.mms.tic.testframework.utils.WebDriverUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

@TestClassContext(name = "WebDriverUtilsTest")
public class WebDriverUtilsTest extends AbstractTestSitesTest {

    private final String msgSwitchSuccessfully = "Find and switch to window successful";

    private void openPopUpWindow(WebDriver driver) {
        GuiElement guiElement = new GuiElement(driver, By.linkText("Open pop up"));
        guiElement.click();
    }

    @Test
    public void testT01_WebDriverUtils_findWindowAndSwitchTo() throws Exception {
        WebDriver driver = getWebDriver();

        openPopUpWindow(driver);

        WebDriverUtils.findWindowAndSwitchTo("List");

        Assert.assertEquals(driver.getTitle(), "List", msgSwitchSuccessfully);
    }

    @Test
    public void testT02_WebDriverUtils_findWindowAndSwitchTo_WrongTitle() throws Exception {
        WebDriver driver = getWebDriver();

        openPopUpWindow(driver);

        boolean out = WebDriverUtils.findWindowAndSwitchTo("abc");

        Assert.assertFalse(out, msgSwitchSuccessfully);
    }

    @Test
    public void testT05_WebDriverUtils_findWindowAndSwitchTo_Driver() throws Exception {
        WebDriver driver = getWebDriver();

        openPopUpWindow(driver);

        boolean out = WebDriverUtils.findWindowAndSwitchTo("List", driver);

        Assert.assertTrue(out, msgSwitchSuccessfully);
    }

    @Test
    public void testT06_WebDriverUtils_findWindowAndSwitchTo_DriverWrongTitle() throws Exception {
        WebDriver driver = getWebDriver();

        openPopUpWindow(driver);

        boolean out = WebDriverUtils.findWindowAndSwitchTo("abc", driver);

        Assert.assertFalse(out, msgSwitchSuccessfully);
    }

    @Test
    public void testT07_WebDriverUtils_findWindowAndSwitchTo_ContainsURL() throws Exception {
        WebDriver driver = getWebDriver();

        openPopUpWindow(driver);

        boolean out = WebDriverUtils.findWindowAndSwitchTo("List", "Input", driver);

        Assert.assertTrue(out, msgSwitchSuccessfully);
    }

    @Test
    public void testT08_WebDriverUtils_findWindowAndSwitchTo_ContainsWrongURL() throws Exception {
        WebDriver driver = getWebDriver();

        openPopUpWindow(driver);

        boolean out = WebDriverUtils.findWindowAndSwitchTo("List", "Output", driver);

        Assert.assertFalse(out, msgSwitchSuccessfully);
    }

    @Test
    public void testT11_WebDriverKeepAliveTimedOut() {

        WebDriver driver = getWebDriver();
        final WebDriverKeepAliveSequence webDriverKeepAliveSequence = WebDriverUtils.keepWebDriverAlive(driver, 1, 10);

        TimerUtils.sleep(15_000);
        final WebDriverKeepAliveSequence.KeepAliveState returningObject = webDriverKeepAliveSequence.getReturningObject();
        Assert.assertEquals(returningObject, WebDriverKeepAliveSequence.KeepAliveState.REMOVED_BY_TIMEOUT);
    }

    @Test
    public void testT12_WebDriverKeepAliveRemovedByUser() {
        WebDriver driver = getWebDriver();
        final WebDriverKeepAliveSequence webDriverKeepAliveSequence = WebDriverUtils.keepWebDriverAlive(driver, 1, 10);

        TimerUtils.sleep(5_000);
        WebDriverUtils.removeKeepAliveForWebDriver(driver);

        TimerUtils.sleep(10_000);
        final WebDriverKeepAliveSequence.KeepAliveState returningObject = webDriverKeepAliveSequence.getReturningObject();
        Assert.assertEquals(returningObject, WebDriverKeepAliveSequence.KeepAliveState.REMOVED_BY_USER);
    }

    @Test
    public void testT13_WebDriverKeepAliveRemovedByDriverShutdown() {
//        WEB_DRIVER_MANAGER.getConfig().reset();
//        WebDriver driver = getWebDriver();
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        final WebDriverKeepAliveSequence webDriverKeepAliveSequence = WebDriverUtils.keepWebDriverAlive(driver, 1, 10);
        TimerUtils.sleep(3_000);

        WEB_DRIVER_MANAGER.shutdownAllThreadSessions();
        TimerUtils.sleep(10_000);
        final WebDriverKeepAliveSequence.KeepAliveState returningObject = webDriverKeepAliveSequence.getReturningObject();
        Assert.assertEquals(returningObject, WebDriverKeepAliveSequence.KeepAliveState.REMOVED_BY_DRIVER_SHUTDOWN);
    }

    @Test
    public void WebDriverUtils_findWindowByTitle() {
        WebDriver driver = getWebDriver();

        openPopUpWindow(driver);

        boolean switched = WebDriverUtils.switchToWindow(driver, webDriver -> webDriver.getTitle().equals("List"));

        Assert.assertTrue(switched);
        Assert.assertEquals(driver.getTitle(), "List", msgSwitchSuccessfully);

        // Close popup
        driver.close();

        // And switch back
        switched = WebDriverUtils.switchToWindow(driver, webDriver -> webDriver.getTitle().contains("Input"));
        Assert.assertTrue(switched);
    }

    @Test
    public void WebDriverUtils_findWindowByTitle_no_context_switch() {
        WebDriver driver = getWebDriver();

        openPopUpWindow(driver);

        String titleBeforeSwitch = driver.getTitle();

        Assert.assertFalse(WebDriverUtils.switchToWindow(driver, webDriver -> false));
        Assert.assertEquals(driver.getTitle(), titleBeforeSwitch);
    }

}
