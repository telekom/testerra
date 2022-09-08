/*
 * Testerra
 *
 * (C) 2022, Clemens Große, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package io.testerra.report.test.pretest_status.simple;

import static eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager.getWebDriver;

import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithExistingElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.testerra.report.test.AbstractTestSitesTest;
import io.testerra.report.test.pages.TestPage;

public class GenerateScreenshotsInTesterraReportTest extends AbstractTestSitesTest {

    @Test
    public void test_Failed_WithScreenShot() {
        WebDriver driver = getWebDriver();
        visitTestPage(driver, TestPage.INPUT_TEST_PAGE);
        Assert.fail("'Failed' on reached Page.");
    }

    @Test
    public void test_GenerateScreenshotManually() {
        WebDriver driver = getWebDriver();
        visitTestPage(driver, TestPage.INPUT_TEST_PAGE);

        final PageWithExistingElement pageWithExistingElement = PageFactory.create(PageWithExistingElement.class, driver);
        pageWithExistingElement.takeScreenshot();
    }
}
