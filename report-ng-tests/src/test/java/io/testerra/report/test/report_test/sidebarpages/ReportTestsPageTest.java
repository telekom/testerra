/*
 * Testerra
 *
 * (C) 2022, Marc Dietrich, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package io.testerra.report.test.report_test.sidebarpages;

import eu.tsystems.mms.tic.testframework.common.DefaultPropertyManager;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import static eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider.WEB_DRIVER_MANAGER;

public class ReportTestsPageTest extends AbstractReportTest {

    @Test(dataProvider = "dataProviderForTestStates")
    public void testT01_filterForTestStates(Status status) {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check whether the tests page table is correct for " + status.title + " state");
        reportTestsPage = reportTestsPage.selectTestStatus(status);
        reportTestsPage.assertCorrectTestStatus(status);
        reportTestsPage.assertStatusColumnHeadlineContainsCorrectText();
    }

    @Test(dataProvider = "dataProviderForDifferentTestClasses")
    public void testT02_filterForClasses(String className) {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check whether class-column contains correct classes");
        reportTestsPage = reportTestsPage.selectClassName(className);
        reportTestsPage.assertClassColumnContainsCorrectClasses(className);
        reportTestsPage.assertClassColumnHeadlineContainsCorrectText();
    }

    @Test(dataProvider = "dataProviderForDifferentTestMethodForEachStatus")
    public void testT03_SearchForTestMethods(String testMethod) {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check whether method-column contains correct methods");
        reportTestsPage = reportTestsPage.search(testMethod);
        reportTestsPage.assertMethodColumnContainsCorrectMethods(testMethod);
        reportTestsPage.assertMethodeColumnHeadlineContainsCorrectText();
    }

    @Test(dataProvider = "dataProviderForFailureAspects")
    public void testT04_SearchForFailureAspect(String failureAspect) {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check whether method-column contains methods with correct failure aspects");
        reportTestsPage = reportTestsPage.search(failureAspect);
        reportTestsPage.assertMethodColumnContainsCorrectMethods(failureAspect);
        reportTestsPage.assertMethodeColumnHeadlineContainsCorrectText();
    }

    @Test
    public void testT05_showConfigurationMethods() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Enable 'Show configuration methods and check whether more methods are displayed");
        int amountOfMethodsBeforeSwitch = reportTestsPage.getAmountOfEntries();
        reportTestsPage.assertMethodeColumnHeadlineContainsCorrectText();
        reportTestsPage.clickConfigurationMethodsSwitch();
        int amountOfMethodsAfterSwitch = reportTestsPage.getAmountOfEntries();
        Assert.assertTrue(amountOfMethodsBeforeSwitch < amountOfMethodsAfterSwitch,
                "'Show configuration methods' switch should display some more (configuration) methods.");
        reportTestsPage.assertConfigurationMethodsAreDisplayed();
        reportTestsPage.assertMethodeColumnHeadlineContainsCorrectText();
        reportTestsPage.assertTestMethodIndicDoesNotAppearTwice();
    }

}
