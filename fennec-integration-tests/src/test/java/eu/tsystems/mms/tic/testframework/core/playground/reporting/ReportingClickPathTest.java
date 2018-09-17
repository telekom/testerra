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
 * Created on 02.04.2014
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.WebTestPage;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public class ReportingClickPathTest extends AbstractTest {

    /*
    testable: http://jsbin.com/vatefekimu/1/edit?html,js,output
     */

    Logger logger = LoggerFactory.getLogger(ReportingClickPathTest.class);

    @Test
    public void testT01_ClickPath() {
        WebDriverManager.config().browser = Browsers.htmlunit;
        final WebDriver driver = WebDriverManager.getWebDriver();
        final String url = TestPage.INPUT_TEST_PAGE.getUrl();
        driver.get(url);
        final WebTestPage fennecWebTestPage = new WebTestPage(driver);
        fennecWebTestPage.reloadPage();
        fennecWebTestPage.reloadPage();
        fennecWebTestPage.assertFunctionalityOfButton1();
    }

    @Test
    public void testT02_ClickPath_Pass() {
        WebDriverManager.config().browser = Browsers.htmlunit;
//        WebDriverManager.config().browser = Browsers.firefox;
        final WebDriver driver = WebDriverManager.getWebDriver();
        final String url = TestPage.INPUT_TEST_PAGE.getUrl();
        driver.get(url);
        WebTestPage fennecWebTestPage = new WebTestPage(driver);
        fennecWebTestPage.reloadPage();
        GuiElement input1 = new GuiElement(driver, By.id("1"));
        input1.type("huhu");

        GuiElement button2 = new GuiElement(driver, By.id("6"));
        button2.click();

        GuiElement input2 = new GuiElement(driver, By.xpath("//input[@id='1']"));
        input2.type("buff");

        UITestUtils.takeScreenshot(driver, true);

        GuiElement button1 = new GuiElement(driver, By.xpath("//*[@id='4']"));
        button1.click();
        fennecWebTestPage = new WebTestPage(driver);

        fennecWebTestPage.assertFunctionalityOfButton1();
    }

    @Test
    public void testT03_ClickPath_Fail() {
        testT02_ClickPath_Pass();
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement button1 = new GuiElement(driver, By.xpath("//*[@id='989898']"));
        button1.assertIsPresent();
    }

    @BeforeMethod
    public void setUp(Method method) throws Exception {
        logger.info("Something");
    }

}
