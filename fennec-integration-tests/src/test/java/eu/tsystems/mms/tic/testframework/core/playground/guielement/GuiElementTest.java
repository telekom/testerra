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
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.TestPage;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by pele on 17.08.2015.
 */
public class GuiElementTest extends AbstractTest {

    @Test
    public void testNonFunctionalAssert() throws Exception {
//        WebDriverManager.config().browser = Browsers.htmlunit;
//        WebDriverManager.getWebDriver();
//        GuiElement bla = new GuiElement(driver, By.className("bla"));
//        bla.nonFunctionalAssert.assertIsPresentFast();
        NonFunctionalAssert.assertTrue(false);
    }

    @Test
    public void testPosition() throws Exception {
        final WebDriver driver = WebDriverManager.getWebDriver();
        driver.get(TestPage.INPUT_TEST_PAGE.getUrl());
        GuiElement guiElement = new GuiElement(driver, By.id("11"));
        Long left = JSUtils.getElementInnerBorders(guiElement).get("left");
        Assert.assertNotNull(left, "Position is not null");
        Assert.assertEquals((long) left, 452, "Position");
    }

    @Test
    public void testError() throws Exception {
        final WebDriver driver = WebDriverManager.getWebDriver();
        driver.get(TestPage.INPUT_TEST_PAGE.getUrl());
        GuiElement guiElement = new GuiElement(driver, By.id("1fwfw1"));
        guiElement.click();
    }
}
