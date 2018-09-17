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

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class LoggingTests.
 *
 * @author pele
 */
@Listeners(FennecListener.class)
public class ManySimpleGuiTests {

    static {
        WebDriverManager.config().executeCloseWindows = false;
    }

    @BeforeMethod
    public void beforeMethod() {
        WebDriver driver = WebDriverManager.getWebDriver();
        driver.manage().window().maximize();
//        TestUtils.takeScreenshot(driver, null, true, "3");
    }

    @AfterMethod
    public void afterMethod() {
        WebDriverManager.forceShutdown();
    }

    @Test
    public void test1() { WebDriverManager.getWebDriver().getCurrentUrl(); }
    @Test
    public void test2() { WebDriverManager.getWebDriver().getCurrentUrl(); }
    @Test
    public void test3() { WebDriverManager.getWebDriver().getCurrentUrl(); }
    @Test
    public void test4() { WebDriverManager.getWebDriver().getCurrentUrl(); }
    @Test
    public void test5() { WebDriverManager.getWebDriver().getCurrentUrl(); }
    @Test
    public void test6() { WebDriverManager.getWebDriver().getCurrentUrl(); }
    @Test
    public void test7() { WebDriverManager.getWebDriver().getCurrentUrl(); }
    @Test
    public void test8() { WebDriverManager.getWebDriver().getCurrentUrl(); }
    @Test
    public void test9() { WebDriverManager.getWebDriver().getCurrentUrl(); }
    @Test
    public void test10() { WebDriverManager.getWebDriver().getCurrentUrl(); }

    @Test
    public void testF11() { new GuiElement(WebDriverManager.getWebDriver(), By.id("huhu")).asserts().assertIsDisplayed(); }
    @Test
    public void testF12() { new GuiElement(WebDriverManager.getWebDriver(), By.id("huhu")).asserts().assertIsDisplayed(); }

}
