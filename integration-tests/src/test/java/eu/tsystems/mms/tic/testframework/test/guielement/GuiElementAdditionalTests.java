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
import eu.tsystems.mms.tic.testframework.core.utils.Log4jFileReader;
import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.Locator;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import io.testerra.test.pretest_guielement.GuiElementAdditional2Tests;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

public class GuiElementAdditionalTests extends AbstractTestSitesTest implements AssertProvider {

    private final Log4jFileReader LOG_4_J_FILE_READER = new Log4jFileReader("logs/pre-test-log4j.log");

    @Test
    public void testT01_Upload() {
        final WebDriver driver = getWebDriver();
        final File resourceFile = FileUtils.getResourceFile("testfiles/Test.txt");
        final String absoluteFilePath = resourceFile.getAbsolutePath();

        GuiElement input = new GuiElement(driver, By.id("2"));
        input.sendKeys(absoluteFilePath);
    }

    /** Needs pretest {@link GuiElementAdditional2Tests#testGuiElement_Create_SensibleData}. */
    @Test
    public void testT02_SensibleData() {
        final String searchStringSensibleData = "facade.GuiElementFacadeLoggingDecorator - type \"*****************\" on By.id: 1";
        final String searchStringNotExisits = "type \"testT02_SensibleData\" on By.id: 1";

        List<String> foundEntries = LOG_4_J_FILE_READER.filterLogForString(searchStringSensibleData);
        Assert.assertEquals(foundEntries.size(), 1, String.format("Log should contains string '%s'", searchStringSensibleData));

        foundEntries = LOG_4_J_FILE_READER.filterLogForString(searchStringNotExisits);
        Assert.assertEquals(foundEntries.size(), 0, String.format("Log should not contain string '%s'", searchStringNotExisits));
    }

    @Test
    public void testT03_GetElementsWithParenthesisInXpath() {
        final WebDriver driver = getWebDriver();
        GuiElement box = new GuiElement(driver, By.xpath("//div[@class='box'][1]"));
        GuiElement subElement = box.getSubElement(By.xpath("(//input[@id='1'])"));

        subElement.asserts().assertIsPresent();
    }

    @Test()
    public void test04_LocateSubElementWithUniqueConfigurator_fails() {
        Locate.setThreadLocalConfigurator(Locator::unique);
        final WebDriver driver = getWebDriver();
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

        ASSERT.assertEndsWith(notFoundException.getCause().getMessage(), "equals [1]");
        Locate.setThreadLocalConfigurator(null);
    }
}
