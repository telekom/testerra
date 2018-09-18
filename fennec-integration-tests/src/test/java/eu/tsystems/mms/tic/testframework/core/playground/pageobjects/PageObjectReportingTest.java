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
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.core.playground.pageobjects;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.pageobjects.Wrong1WebTestPage;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testframework.utils.TestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by pele on 03.03.2016.
 */
public class PageObjectReportingTest extends AbstractTest {

    static {
//        WebDriverManager.config().browser = Browsers.firefox;
        WebDriverManager.config().browser = Browsers.chrome;
        WebDriverManager.config().webDriverMode = WebDriverMode.remote;
        WebDriverManager.setBaseURL("http://www.google.de");

//        WebDriverManager.config().setFirefoxVersion("52-esr");
        WebDriverManager.config().browserVersion = "57";
//        WebDriverManager.config().browserVersion = "63";
//        WebDriverManager.config().browserVersion = "15";
        DesiredCapabilities caps = new DesiredCapabilities();

        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setLegacy(true);
        caps.setCapability(FirefoxOptions.FIREFOX_OPTIONS, firefoxOptions);
        WebDriverManager.setGlobalExtraCapabilities(caps);
    }

    @Test
    public void testPassingMethod() throws Exception {
    }

    @Test
    public void testT01Fa_checkpage_StackedThrowable() {
        errorTest();
    }

//    @Test
//    public void testT01Fb_checkpage_StackedThrowable() {
//        errorTest();
//    }

    @Test
    public void testError1() {
        Assert.assertTrue(false);
    }

    @Test
    public void testError2() {
        Assert.assertTrue(false);
    }

    private void errorTest() {
        final WebDriver driver = WebDriverManager.getWebDriver();
//        driver.get(WebTestPage.URL);
        Wrong1WebTestPage fennecWebTestPage = new Wrong1WebTestPage(driver);
    }

    @Test
    public void test2Sessions() throws Exception {
        WebDriver driver = WebDriverManager.getWebDriver();
        WebDriver session1 = WebDriverManager.getWebDriver("session1");
        WebDriver session2 = WebDriverManager.getWebDriver("session2");

        session1.get("https://tapbs01.t-systems-mms.eu/");
        session2.get("http://aerokube.com/ggr/latest/#_getting_host_by_session_id/");

        System.out.println(driver.getTitle());
        System.out.println(session1.getTitle());
        System.out.println(session2.getTitle());

        TestUtils.sleep(10000);
    }
}
