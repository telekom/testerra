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
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.FennecWebTestPage3;
import eu.tsystems.mms.tic.testframework.pageobjects.FennecWebTestPage4;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by pele on 05.08.2015.
 */
public class GuiElementTest extends AbstractTest {

    @Test
    public void testGuiElementList() throws Exception {

            WebDriverManager.setBaseURL(TestPage.INPUT_TEST_PAGE.getUrl());
            final WebDriver driver = WebDriverManager.getWebDriver();
            final GuiElement guiElement = new GuiElement(driver, By.xpath(".//*"));
            List<GuiElement> list = guiElement.getList();
            for (GuiElement element : list) {
                System.out.println(element.getName());
                System.out.println(element.getWebElement().getTagName());
            }
    }

    @Test
    public void testGuiElementsWithSensibleData() throws Exception {
        WebDriverManager.setBaseURL(TestPage.INPUT_TEST_PAGE.getUrl());
        final WebDriver driver = WebDriverManager.getWebDriver();
        final GuiElement guiElement = new GuiElement(driver, By.id("1")).sensibleData();
        guiElement.type("pwd");
    }

    @Test
    public void testGuiElementsLoggingWithoutDefinedDescription() throws Exception {
//        WebDriverManager.config().webDriverMode = WebDriverMode.local;
        WebDriverManager.config().browser = Browsers.htmlunit;
        WebDriverManager.setBaseURL(FennecWebTestPage3.URL);

        final WebDriver driver = WebDriverManager.getWebDriver();
        FennecWebTestPage3 fennecWebTestPage3 = new FennecWebTestPage3(driver);
        fennecWebTestPage3.clickOnField();
        fennecWebTestPage3.subElement();
    }

    @Test
    public void testFailureScreenshot() throws Exception {
        WebDriverManager.setBaseURL(FennecWebTestPage3.URL);

        final WebDriver driver = WebDriverManager.getWebDriver();
        FennecWebTestPage3 fennecWebTestPage3 = new FennecWebTestPage3(driver);
        fennecWebTestPage3.clickOnField();
    }

    @Test
    public void testFailingPageCheckOfMandatoryElements() throws Exception {
        WebDriverManager.config().browser = Browsers.htmlunit;
        WebDriverManager.setBaseURL(FennecWebTestPage4.URL);

        final WebDriver driver = WebDriverManager.getWebDriver();
        new FennecWebTestPage4(driver);
    }

}
