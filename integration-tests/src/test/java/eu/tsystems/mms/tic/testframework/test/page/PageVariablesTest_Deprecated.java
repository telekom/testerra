/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.test.page;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.test.PageFactoryTest;
import eu.tsystems.mms.tic.testframework.test.core.pageobjects.testdata.MyVariables;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

/**
 * Created by rnhb on 19.06.2015.
 *
 * Tests for correct execution of checkpage().
 * To test that checkpage() is executed, a not existing, check-annotated element is used.
 *
 */
public class PageVariablesTest_Deprecated extends AbstractTestSitesTest implements PageFactoryTest {
    @Override
    public WebTestPage getPage() {
        return PageFactory.create(WebTestPage.class, WebDriverManager.getWebDriver(), new MyVariables(1));
    }

    @Test
    public void testT25F_assertIsNotTextDisplayed_butPresent() {
        WebTestPage page = getPage();

        GuiElement input = new GuiElement(page.getWebDriver(), By.xpath("//label[@for='inputMillis']"));
        WebElement webElement = input.getWebElement();

        page.assertIsTextDisplayed("in Millis");
        JSUtils.executeScript(page.getWebDriver(), "arguments[0].style.visibility='hidden';", webElement);
        page.assertIsNotTextDisplayed("in Millis");
    }
}
