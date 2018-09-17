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
/*
 * Created on 22.02.2013
 *
 * Copyright(c) 2013 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.SkipException;
import org.testng.annotations.*;

/**
 * Class LoggingTests.
 *
 * @author pele
 */
@Listeners(FennecListener.class)
public class ReportingScreenshotsTests {

    static {
        WebDriverManager.config().executeCloseWindows = false;
    }

    @BeforeMethod
    public void beforeMethod() {
        WebDriver driver = WebDriverManager.getWebDriver();
//        TestUtils.takeScreenshot(driver, null, true, "3");
//        errorExecution();
    }

    @AfterMethod
    public void afterMethod() {
        WebDriverManager.forceShutdown();
    }

    @Test
    public void testRerunTest() {
        throw new WebDriverException("Error communicating with the remote browser. It may have died.");
    }

    @Test
    public void testSkip() throws Exception {
        throw new SkipException("yeah skipped");
    }

    @Test
    public void testScreenshot1() {
        errorExecution();
    }

    @Test
    public void testScreenshot2() {
        WebDriver driver = WebDriverManager.getWebDriver();
        UITestUtils.takeScreenshot(driver, true);
        UITestUtils.takeScreenshot(driver, true);
        UITestUtils.takeScreenshot(driver, true);
        errorExecution();
    }

    @Test
    public void testNonFunctionalAndPassed() {
        WebDriver driver = WebDriverManager.getWebDriver();
        NonFunctionalAssert.assertEquals(true, false, "must fail");
        UITestUtils.takeScreenshot(driver, true);
    }

    @Test
    public void testNonFunctionalAndFailed() {
        WebDriver driver = WebDriverManager.getWebDriver();
        NonFunctionalAssert.assertEquals(true, false, "must fail");
        UITestUtils.takeScreenshot(driver, true);
        errorExecution();
    }

    @Test
    public void testAssertCollectorFailed() {
        WebDriver driver = WebDriverManager.getWebDriver();
        AssertCollector.assertEquals(true, false, "must fail");
        AssertCollector.assertEquals(true, false, "must fail");
        AssertCollector.assertEquals(true, false, "must fail");
    }

    @Test
    public void testAssertCollectorFailedWithMethodError() {
        WebDriverManager.getWebDriver("session1");
        WebDriverManager.getWebDriver("session2");
        AssertCollector.assertEquals(true, false, "must fail");
        AssertCollector.assertEquals(true, false, "must fail");
        AssertCollector.assertEquals(true, false, "must fail");
        errorExecution();
    }

    private void errorExecution() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement notHere = new GuiElement(driver, By.id("nothere"));
        notHere.setTimeoutInSeconds(1);
        notHere.assertIsPresent();
    }

    @BeforeClass
    public void setup() {
        String url = "http://www.google.de";
        WebDriverManager.setBaseURL(url);
    }
}
