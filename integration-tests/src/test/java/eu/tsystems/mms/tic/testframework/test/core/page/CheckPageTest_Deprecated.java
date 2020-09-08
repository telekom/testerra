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
package eu.tsystems.mms.tic.testframework.test.core.page;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PageWithExistingElement;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PageWithExistingStaticElement;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PageWithNonCheckableCheck;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PageWithNotExistingElement;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.PageWithNullElement;
import eu.tsystems.mms.tic.testframework.exceptions.PageNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

/**
 * Created by rnhb on 29.12.2015.
 */
public class CheckPageTest_Deprecated extends AbstractTestSitesTest {

    @Test
    public void testT01_checkExistingElement() throws Exception {
        new PageWithExistingElement(WebDriverManager.getWebDriver());
    }

    @Test(expectedExceptions = PageNotFoundException.class)
    public void testT02_checkNotExistingElement() throws Exception {
        new PageWithNotExistingElement(WebDriverManager.getWebDriver());
    }

    @Test(expectedExceptions = TesterraRuntimeException.class)
    public void testT03_checkNullElement() throws Exception {
        new PageWithNullElement(WebDriverManager.getWebDriver());
    }

    @Test(expectedExceptions = TesterraRuntimeException.class)
    public void testT04_checkStaticElement() throws Exception {
        new PageWithExistingStaticElement(WebDriverManager.getWebDriver());
    }

    @Test(expectedExceptions = TesterraRuntimeException.class)
    public void testT05_checkNonCheckableElement() throws Exception {
        new PageWithNonCheckableCheck(WebDriverManager.getWebDriver());
    }

    @Test
    public void testT06_CheckPage_ExecutionFromDifferentClass() throws Exception {
        WebDriver webDriver = WebDriverManager.getWebDriver();

        Page page = new Page(webDriver) {
            @Check
            GuiElement testElement = new GuiElement(getWebDriver(), By.xpath("not existing"));
        };

        boolean exceptionWasThrown = false;
        try {
            page.checkPage();
        } catch (PageNotFoundException e) {
            exceptionWasThrown = true;
            String message = e.getMessage();
            Assert.assertTrue(message.contains("testElement"), "Error message is properly named.");
        }
        Assert.assertTrue(exceptionWasThrown, "PageNotFoundException was thrown because element was not found.");
    }

    @Test
    public void testT07_CheckPage_BlockedExecutionFromParentPage() throws Exception {
        WebDriver webDriver = WebDriverManager.getWebDriver();

        class ParentPage extends Page {
            @Check
            GuiElement testElement = new GuiElement(getWebDriver(), By.xpath("not existing"));

            public ParentPage(WebDriver driver) {
                super(driver);
                checkPage();
            }
        }

        try {
            new ParentPage(webDriver) {
            };
        } catch (PageNotFoundException e) {
            Assert.fail("No PageNotFoundException should be thrown because checkPage should not be executed from parent page.");
        }
    }
}
