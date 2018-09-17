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
 * Created on 07.01.2013
 *
 * Copyright(c) 2013 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.pageobjects;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.pageobjects.*;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.WebTestPage;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA. User: pele Date: 07.01.13 Time: 14:14 To change this template use File | Settings | File
 * Templates.
 */
public class PageObjectsTest extends AbstractTest {

    static {
        Flags.REPORT_SCREENSHOTS_PREVIEW = false;
        Flags.XETA_WEB_TAKE_ACTION_SCREENSHOTS = true;
    }

    WebTestPage.MyVariables pageVariables = new WebTestPage.MyVariables(99);

    @Test
    public void testT01_SimplePageTest() {
        POConfig.setDemoMode(true);
        final WebDriver driver = WebDriverManager.getWebDriver();
        driver.get(WebTestPage.URL);

        //WebTestPage fennecWebTestPage = new WebTestPage(driver);

        WebTestPage fennecWebTestPage = PageFactory.create(WebTestPage.class, driver, pageVariables);

        fennecWebTestPage.assertFunctionalityOfButton1();
        fennecWebTestPage = fennecWebTestPage.reloadPage();
        fennecWebTestPage.assertFunctionalityOfButton1();
    }

    @Test
    public void testT02F_checkpage_StackedThrowable() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        driver.get(WebTestPage.URL);
        Wrong1WebTestPage fennecWebTestPage = new Wrong1WebTestPage(driver);
    }

    @Test
    public void testT03F_checkpage_NoStackedThrowable() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        driver.get(WebTestPage.URL);
        Wrong2FennecWebTestPage fennecWebTestPage = new Wrong2FennecWebTestPage(driver);
    }

    @Test
    public void testT04F_checkpage_NonFunctional() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        driver.get(WebTestPage.URL);
        GuiElement guiElement = new GuiElement(driver, By.xpath("//huhu"));
        guiElement.nonFunctionalAssert.assertIsPresent();
        Wrong2FennecWebTestPage fennecWebTestPage = new Wrong2FennecWebTestPage(driver);
    }

    @Test
    public void testT05_checkpage_NonFunctional() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        driver.get(WebTestPage.URL);
        GuiElement guiElement = new GuiElement(driver, By.xpath("//huhu"));
        guiElement.nonFunctionalAssert.assertIsPresent();
    }

    @Test
    public void testCallCheckPageOnlyFromTopClass() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        driver.get(WebTestPage.URL);
        new WebTestPage2(driver);
    }

    @Test
    public void testT11_CheckPageNotShown() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        driver.get(WebTestPage.URL);
        PageFactory.checkNot(WebTestPage.class, driver, pageVariables);
    }
}
