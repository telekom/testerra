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

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithExistingElement;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import io.testerra.report.test.AbstractTestSitesTest;
import io.testerra.report.test.pages.TestPage;
import io.testerra.report.test.pages.pretest.CheckPages.checkRuleCheckPages.CheckRule_IS_DISPLAYED_CheckPage;
import io.testerra.report.test.pages.pretest.UniversalPage;

public class GenerateScreenshotsInTesterraReportTest extends AbstractTestSitesTest implements AssertProvider {

    private String exclusiveSessionId2;

    @Test
    public void test_Failed_WithScreenShot() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, TestPage.INPUT_TEST_PAGE);
        Assert.fail("'Failed' on reached Page.");
    }

    @Test()
    @Fails()
    public void test_takeScreenshotViaCollectedAssertion_fails() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, TestPage.INPUT_TEST_PAGE);

        CONTROL.collectAssertions(() -> {
            ASSERT.assertTrue(false, "assertion passed");
        });
    }

//     one screenshot manually shot on normal session
    @Test
    public void test_GenerateScreenshotManually() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        visitTestPage(driver, TestPage.INPUT_TEST_PAGE);

        final PageWithExistingElement pageWithExistingElement = PAGE_FACTORY.createPage(PageWithExistingElement.class, driver);
        pageWithExistingElement.screenshotToReport();
    }

//     one screenshot on error on exclusive session
    @Test()
    @Fails()
    public void test_takeScreenshotOnExclusiveSession_fails() {
        WebDriver driver = getExclusiveWebDriver();
        visitTestPage(driver, TestPage.INPUT_TEST_PAGE);
        Assert.fail("'Failed' on reached Page.");
    }

//    two screenshots success on different session types
    @Test
    public void test_takeScreenshotsWithMultipleActiveSessions(){
        WebDriver driver1 = getExclusiveWebDriver();
        WebDriver driver2 = WEB_DRIVER_MANAGER.getWebDriver();

        visitTestPage(driver1, TestPage.DUMMY_TEST_PAGE);
        visitTestPage(driver2, TestPage.DUMMY_TEST_PAGE_2);

        UITestUtils.takeScreenshots();
    }

//    two screenshot on success exclusive sessions only
    @Test
    public void test_takeScreenshotWithMultipleExclusiveSessions(){
        WebDriver driver1 = getExclusiveWebDriver();
        WebDriver driver2 = WEB_DRIVER_MANAGER.getWebDriver();
        exclusiveSessionId2 = WEB_DRIVER_MANAGER.makeExclusive(driver2);

        visitTestPage(driver1, TestPage.DUMMY_TEST_PAGE);
        visitTestPage(driver2, TestPage.DUMMY_TEST_PAGE_2);

        UITestUtils.takeScreenshots();
    }

//    one screenshot manually shot on exclusive session shown
    @Test
    public void test_takeExclusiveSessionScreenshotWithMultipleActiveSessions(){
        WebDriver driver1 = getExclusiveWebDriver();
        WebDriver driver2 = WEB_DRIVER_MANAGER.getWebDriver();

        visitTestPage(driver2, TestPage.DUMMY_TEST_PAGE_2);
        PAGE_FACTORY.createPage(UniversalPage.class, driver2);

        visitTestPage(driver1, TestPage.DUMMY_TEST_PAGE);
        final CheckRule_IS_DISPLAYED_CheckPage pageWithExistingElement = PAGE_FACTORY.createPage(CheckRule_IS_DISPLAYED_CheckPage.class, driver1);
        pageWithExistingElement.screenshotToReport();
    }

// two screenshots on error different session types
    @Test
    public void test_takeScreenshotOnErrorWithMultipleActiveSessionsError(){
        WebDriver driver1 = getExclusiveWebDriver();
        WebDriver driver2 = WEB_DRIVER_MANAGER.getWebDriver();

        visitTestPage(driver1, TestPage.DUMMY_TEST_PAGE);
        visitTestPage(driver2, TestPage.DUMMY_TEST_PAGE_2);

        PAGE_FACTORY.createPage(PageWithExistingElement.class, driver2);
    }

//    two screenshot on error exclusive sessions only
    @Test
    public void test_takeScreenshotOnErrorWithMultipleExclusiveSessions(){
        WebDriver driver1 = getExclusiveWebDriver();
        WebDriver driver2 = WEB_DRIVER_MANAGER.getWebDriver();
        exclusiveSessionId2 = WEB_DRIVER_MANAGER.makeExclusive(driver2);

        visitTestPage(driver1, TestPage.DUMMY_TEST_PAGE);
        visitTestPage(driver2, TestPage.DUMMY_TEST_PAGE_2);

        PAGE_FACTORY.createPage(PageWithExistingElement.class, driver2);
    }

    @AfterClass
    public void closeExclusiveSessionOfTestClass() {
        if (exclusiveSessionId2 != null) {
            WEB_DRIVER_MANAGER.shutdownSession(this.exclusiveSessionId2);
            exclusiveSessionId2 = null;
        }
    }
}
