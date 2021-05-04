/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package eu.tsystems.mms.tic.testframework.test.guielement;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.utils.LogAssertUtils;
import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import java.io.File;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GuiElementAdditionalTests extends AbstractTestSitesTest {

    @Test
    public void testT01_Upload() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        final File resourceFile = FileUtils.getResourceFile("testfiles/Test.txt");
        final String absoluteFilePath = resourceFile.getAbsolutePath();


        GuiElement input = new GuiElement(driver, By.id("2"));
        input.sendKeys(absoluteFilePath);
    }

    @Test
    public void testT02_SensibleData() {

        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement input = new GuiElement(driver, By.id("1")).sensibleData();

        Assert.assertTrue(input.isDisplayed());
        Assert.assertTrue(input.hasSensibleData());

        input.type("testT02_SensibleData");
        // pageobjects.GuiElement - type "*****************" on By.id: 1
        LogAssertUtils.assertEntryInLogFile("pageobjects.GuiElement - type \"*****************\" on By.id: 1");
        LogAssertUtils.assertEntryNotInLogFile("pageobjects.GuiElement - type \"testT02_SensibleData\" on By.id: 1");
    }

    @Test
    public void testT03_GetElementsWithParenthesisInXpath() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement box = new GuiElement(driver, By.xpath("//div[@class='box'][1]"));
        GuiElement subElement = box.getSubElement(By.xpath("(//input[@id='2'])"));

        subElement.asserts().assertIsPresent();

    }

    @Test()
    public void test04_LocateSubElementWithUniqueConfigurator_fails() {
        Locate.setConfigurator(Locate::unique);
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement body = new GuiElement(driver, By.xpath("//body"));
        GuiElement div = body.getSubElement(By.xpath("//div"));

        Throwable notFoundException = null;
        try {
            div.click();
        } catch (TimeoutException e) {
            notFoundException = e.getCause();
        } catch (ElementNotFoundException e) {
            notFoundException = e;
        }

        Assert.assertTrue(notFoundException.getCause().getMessage().contains("To many WebElements found"), "Expect to fail because of to many WebElements found");
    }
}
