/*
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
import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import eu.tsystems.mms.tic.testframework.utils.WebDriverKeepAliveSequence;
import eu.tsystems.mms.tic.testframework.utils.WebDriverUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

@TestContext(name = "WebDriverUtilsTest")
public class WebDriverUtilsTest extends AbstractTestSitesTest {

    private final String msgSwitchSuccessfully = "Find and switch to window successful";

    //    private WebDriver createWebDriver(boolean extraSession) {
    //        WebDriver driver;
    //
    //        if (extraSession) {
    //            driver = WebDriverManager.getWebDriver("test");
    //            visitTestPage(driver);
    //        } else {
    //            driver = WebDriverManager.getWebDriver();
    //        }
    //
    //        return driver;
    //    }

    private void openPopUpWindow(WebDriver driver) {
        GuiElement guiElement = new GuiElement(driver, By.linkText("Open pop up"));
        guiElement.click();
    }

    @Test
    public void testT01_WebDriverUtils_findWindowAndSwitchTo() throws Exception {
        WebDriver driver = WebDriverManager.getWebDriver();

        openPopUpWindow(driver);

        WebDriverUtils.findWindowAndSwitchTo("List", false);

        Assert.assertEquals(driver.getTitle(), "List", msgSwitchSuccessfully);
    }

    @Test
    public void testT02_WebDriverUtils_findWindowAndSwitchTo_WrongTitle() throws Exception {
        WebDriver driver = WebDriverManager.getWebDriver();

        openPopUpWindow(driver);

        boolean out = WebDriverUtils.findWindowAndSwitchTo("abc", false);

        Assert.assertFalse(out, msgSwitchSuccessfully);
    }

    @Test(enabled = false)
    @Deprecated
    public void testT03_WebDriverUtils_findWindowAndSwitchTo_Fast() throws Exception {
        WebDriver driver = WebDriverManager.getWebDriver();

        openPopUpWindow(driver);

        long timeMillisBegin = System.currentTimeMillis();

        boolean out = WebDriverUtils.findWindowAndSwitchTo("List", true);

        long timeMillisDuration = System.currentTimeMillis() - timeMillisBegin;

        this.log().info("Window switch took about " + timeMillisDuration + " ms.");
        Assert.assertTrue(out, msgSwitchSuccessfully);
        Assert.assertTrue(timeMillisDuration < 300, "Find and switch to Window need less than 1000 ms");
    }

    @Test(enabled = false)
    @Deprecated
    public void testT04_WebDriverUtils_findWindowAndSwitchTo_FastWrongTitle() throws Exception {
        WebDriver driver = WebDriverManager.getWebDriver();

        openPopUpWindow(driver);

        boolean out = WebDriverUtils.findWindowAndSwitchTo("abc", true);

        Assert.assertFalse(out, msgSwitchSuccessfully);
    }

    @Test
    public void testT05_WebDriverUtils_findWindowAndSwitchTo_Driver() throws Exception {
        WebDriver driver = WebDriverManager.getWebDriver();

        openPopUpWindow(driver);

        boolean out = WebDriverUtils.findWindowAndSwitchTo("List", true, driver);

        Assert.assertTrue(out, msgSwitchSuccessfully);
    }

    @Test
    public void testT06_WebDriverUtils_findWindowAndSwitchTo_DriverWrongTitle() throws Exception {
        WebDriver driver = WebDriverManager.getWebDriver();

        openPopUpWindow(driver);

        boolean out = WebDriverUtils.findWindowAndSwitchTo("abc", true, driver);

        Assert.assertFalse(out, msgSwitchSuccessfully);
    }

    @Test
    public void testT07_WebDriverUtils_findWindowAndSwitchTo_ContainsURL() throws Exception {
        WebDriver driver = WebDriverManager.getWebDriver();

        openPopUpWindow(driver);

        boolean out = WebDriverUtils.findWindowAndSwitchTo("List", "Input", true, driver);

        Assert.assertTrue(out, msgSwitchSuccessfully);
    }

    @Test
    public void testT08_WebDriverUtils_findWindowAndSwitchTo_ContainsWrongURL() throws Exception {
        WebDriver driver = WebDriverManager.getWebDriver();

        openPopUpWindow(driver);

        boolean out = WebDriverUtils.findWindowAndSwitchTo("List", "Output", true, driver);

        Assert.assertFalse(out, msgSwitchSuccessfully);
    }

    @Test
    public void testT10_linkChecker() throws Exception {
        WebDriver driver = WebDriverManager.getWebDriver();
        WebDriverUtils.linkChecker("Test", driver);
    }


    @Test
    public void testT11_WebDriverKeepAliveTimedOut() {

        WebDriver driver = WebDriverManager.getWebDriver();
        final WebDriverKeepAliveSequence webDriverKeepAliveSequence = WebDriverUtils.keepWebDriverAlive(driver, 1, 10);

        TimerUtils.sleep(15_000);
        final WebDriverKeepAliveSequence.KeepAliveState returningObject = webDriverKeepAliveSequence.getReturningObject();
        Assert.assertEquals(returningObject, WebDriverKeepAliveSequence.KeepAliveState.REMOVED_BY_TIMEOUT);
    }

    @Test
    public void testT12_WebDriverKeepAliveRemovedByUser() {
        WebDriver driver = WebDriverManager.getWebDriver();
        final WebDriverKeepAliveSequence webDriverKeepAliveSequence = WebDriverUtils.keepWebDriverAlive(driver, 1, 10);

        TimerUtils.sleep(5_000);
        WebDriverUtils.removeKeepAliveForWebDriver(driver);

        TimerUtils.sleep(10_000);
        final WebDriverKeepAliveSequence.KeepAliveState returningObject = webDriverKeepAliveSequence.getReturningObject();
        Assert.assertEquals(returningObject, WebDriverKeepAliveSequence.KeepAliveState.REMOVED_BY_USER);
    }

    @Test
    public void testT13_WebDriverKeepAliveRemovedByDriverShutdown() {
        WebDriver driver = WebDriverManager.getWebDriver();
        final WebDriverKeepAliveSequence webDriverKeepAliveSequence = WebDriverUtils.keepWebDriverAlive(driver, 1, 10);
        TimerUtils.sleep(3_000);

        WebDriverManager.shutdown();
        TimerUtils.sleep(10_000);
        final WebDriverKeepAliveSequence.KeepAliveState returningObject = webDriverKeepAliveSequence.getReturningObject();
        Assert.assertEquals(returningObject, WebDriverKeepAliveSequence.KeepAliveState.REMOVED_BY_DRIVER_SHUTDOWN);
    }

}
