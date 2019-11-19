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
package eu.tsystems.mms.tic.testframework.core.test.pageobjects.page;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.IPageFactoryTest;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.MyVariables;
import eu.tsystems.mms.tic.testframework.pageobjects.WebTestFramedPage;
import eu.tsystems.mms.tic.testframework.pageobjects.WebTestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

/**
 * Created by rnhb on 19.06.2015.
 *
 * Tests for correct execution of checkpage().
 * To test that checkpage() is executed, a not existing, check-annotated element is used.
 *
 */
public class AssertTextPageTest_NewApi extends AbstractTestSitesTest implements IPageFactoryTest {

    @Override
    public WebTestPage getPage() {
        return pageFactory.createPage(WebTestPage.class);
    }

    public WebTestFramedPage getFramePage() {
        return pageFactory.createPage(WebTestFramedPage.class);
    }

    @Test
    public void testT11_assertIsTextPresent() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.FRAME_TEST_PAGE.getUrl();
        driver.get(url);

        Control.withElementTimeout(0, () -> {
            WebTestFramedPage page = getFramePage();
            page.anyElementContainsText("Frame1234").present().isTrue();
        });
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_assertIsTextPresent_fails() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.FRAME_TEST_PAGE.getUrl();
        driver.get(url);

        Control.withElementTimeout(0, () -> {
            WebTestFramedPage page = getFramePage();
            page.anyElementContainsText("Bifi").present().isTrue();
        });
    }

    @Test
    public void testT13_assertIsNotTextPresent() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.FRAME_TEST_PAGE.getUrl();
        driver.get(url);
        Control.withElementTimeout(0, () -> {
            WebTestFramedPage page = getFramePage();
            page.anyElementContainsText("Bifi").present().isFalse();
        });
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_assertIsNotTextPresent_fails() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.FRAME_TEST_PAGE.getUrl();
        driver.get(url);

        Control.withElementTimeout(0, () -> {
            WebTestFramedPage page = getFramePage();
            page.anyElementContainsText("Frame1234").present().isFalse();
        });
    }

    @Test
    public void testT21_assertIsTextDisplayed() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.FRAME_TEST_PAGE.getUrl();
        driver.get(url);

        Control.withElementTimeout(0, () -> {
            WebTestFramedPage page = getFramePage();
            page.anyElementContainsText("Frame1234").displayed().isTrue();
        });
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_assertIsTextDisplayed_fails() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.FRAME_TEST_PAGE.getUrl();
        driver.get(url);

        Control.withElementTimeout(0, () -> {
            WebTestFramedPage page = getFramePage();
            page.anyElementContainsText("Bifi").displayed().isTrue();
        });
    }

    @Test
    public void testT23_assertIsNotTextDisplayed() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.FRAME_TEST_PAGE.getUrl();
        driver.get(url);

        Control.withElementTimeout(0, () -> {
            WebTestFramedPage page = getFramePage();
            page.anyElementContainsText("Bifi").displayed().isFalse();
        });
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_assertIsNotTextDisplayed_fails() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.FRAME_TEST_PAGE.getUrl();
        driver.get(url);

        Control.withElementTimeout(0, () -> {
            WebTestFramedPage page = getFramePage();
            page.anyElementContainsText("Frame1234").displayed().isFalse();
        });
    }

    @Test
    public void testT25F_assertIsNotTextDisplayed_butPresent() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String url = TestPage.INPUT_TEST_PAGE.getUrl();
        driver.get(url);

        WebTestPage page = getPage();
        IGuiElement input = page.getGuiElementBy(By.xpath("//label[@for='inputMillis']"));
        input.displayed().isTrue();
        WebElement webElement = input.getWebElement();

        page.anyElementContainsText("in Millis").displayed().isTrue();
        JSUtils.executeScript(driver, "arguments[0].style.visibility='hidden';", webElement);
        page.anyElementContainsText("in Millis").displayed().isFalse();
    }

}
