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
package eu.tsystems.mms.tic.testframework.core.playground.guielement;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.filter.WebElementFilter;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by pele on 30.10.2015.
 */
public class GuiElementAssertIsDisplayedTest extends AbstractTest {

    String browser;

    @BeforeClass
    public void setUp() throws Exception {
        browser = WebDriverManager.config().browser;
        WebDriverManager.config().browser = Browsers.htmlunit;
    }

    @AfterClass
    public void tearDown() throws Exception {
        WebDriverManager.config().browser = browser;
    }

    @Test
    public void test_NotDisplayed() throws Exception {
        final WebDriver driver = start();
        GuiElement guiElement = new GuiElement(driver, By.xpath("//*")).withWebElementFilter(WebElementFilter.DISPLAYED.is(false));
        guiElement.assertIsDisplayed();
    }

    @Test
    public void test_NotPresent() throws Exception {
        final WebDriver driver = start();
        GuiElement guiElement = new GuiElement(driver, By.id("12121212"));
        guiElement.assertIsDisplayed();
    }

    @Test
    public void test_Parent_NotPresent() throws Exception {
        final WebDriver driver = start();
        GuiElement parent = new GuiElement(driver, By.id("12121212"));
        GuiElement guiElement = parent.getSubElement(By.xpath("//*"));
        guiElement.assertIsDisplayed();
    }

    @Test
    public void test_Frame_NotPresent() throws Exception {
        final WebDriver driver = start();
        GuiElement frame = new GuiElement(driver, By.id("12121212"));
        GuiElement guiElement = new GuiElement(driver, By.xpath("//*"), frame);
        guiElement.assertIsDisplayed();
    }

    private WebDriver start() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        driver.get(TestPage.INPUT_TEST_PAGE.getUrl());
        return driver;
    }


}
