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
package eu.tsystems.mms.tic.testframework.core.test.pageobjects.page;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.IPageFactoryTest;
import eu.tsystems.mms.tic.testframework.pageobjects.FluentWebFramedTestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.FluentWebTestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IPageFactory;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by rnhb on 19.06.2015.
 *
 * Tests for correct execution of checkpage().
 * To test that checkpage() is executed, a not existing, check-annotated element is used.
 *
 */
public class AssertTextPageTest_NewApi extends AbstractTestSitesTest implements IPageFactoryTest {

    private static final IPageFactory pageFactory = Testerra.ioc().getInstance(IPageFactory.class);

    @BeforeClass
    public void setupTestableBrowser() {
        System.getProperties().setProperty(TesterraProperties.BROWSER, Browsers.chrome);
    }

    @Override
    public FluentWebTestPage getPage() {
        return pageFactory.createPage(FluentWebTestPage.class);
    }

    public FluentWebFramedTestPage getFramePage() {
        return pageFactory.createPage(FluentWebFramedTestPage.class);
    }

    @Test
    public void testT11_assertIsTextPresent() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.FRAME_TEST_PAGE.getUrl();
        driver.get(url);

        FluentWebFramedTestPage page = getFramePage();
        page.assertIsTextPresent("Frame1234");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT12F_assertIsTextPresent() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.FRAME_TEST_PAGE.getUrl();
        driver.get(url);

        FluentWebFramedTestPage page = getFramePage();
        page.assertIsTextPresent("Bifi");
    }

    @Test
    public void testT13_assertIsNotTextPresent() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.FRAME_TEST_PAGE.getUrl();
        driver.get(url);

        FluentWebFramedTestPage page = getFramePage();
        page.assertIsNotTextPresent("Bifi");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT14F_assertIsNotTextPresent() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.FRAME_TEST_PAGE.getUrl();
        driver.get(url);

        FluentWebFramedTestPage page = getFramePage();
        page.assertIsNotTextPresent("Frame1234");
    }

    @Test
    public void testT21_assertIsTextDisplayed() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.FRAME_TEST_PAGE.getUrl();
        driver.get(url);

        FluentWebFramedTestPage page = getFramePage();
        page.assertIsTextDisplayed("Frame1234");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT22F_assertIsTextDisplayed() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.FRAME_TEST_PAGE.getUrl();
        driver.get(url);

        FluentWebFramedTestPage page = getFramePage();
        page.assertIsTextDisplayed("Bifi");
    }

    @Test
    public void testT23_assertIsNotTextDisplayed() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.FRAME_TEST_PAGE.getUrl();
        driver.get(url);

        FluentWebFramedTestPage page = getFramePage();
        page.assertIsNotTextDisplayed("Bifi");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT24F_assertIsNotTextDisplayed() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.FRAME_TEST_PAGE.getUrl();
        driver.get(url);

        FluentWebFramedTestPage page = getFramePage();
        page.assertIsNotTextDisplayed("Frame1234");
    }

    @Test
    public void testT25F_assertIsNotTextDisplayed_butPresent() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.INPUT_TEST_PAGE.getUrl();
        driver.get(url);

        FluentWebTestPage page = getPage();

        GuiElement input = new GuiElement(driver, By.xpath("//label[@for='inputMillis']"));
        WebElement webElement = input.getWebElement();

        page.assertIsTextDisplayed("in Millis");
        JSUtils.executeScript(driver, "arguments[0].style.visibility='hidden';", webElement);
        page.assertIsNotTextDisplayed("in Millis");
    }

}
