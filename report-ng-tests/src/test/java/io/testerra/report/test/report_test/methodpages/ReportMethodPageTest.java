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

package io.testerra.report.test.report_test.methodpages;

import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.TestDataProvider;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.methodReport.ReportStepsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportThreadsPage;
import io.testerra.report.test.pages.utils.DateTimeUtils;
import io.testerra.report.test.pages.utils.TestData;

public class ReportMethodPageTest extends AbstractReportTest {


    // Deactivated testcase due to renewed threads view (echarts) since it is not that easy possible
    // to access elements such as the specific testcases inside the canvas element of echarts
    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderForPreTestMethods_Classes_States_ForStepsType", enabled = false)
    public void testT01_methodOverviewIsCorrectForStepsType(TestData data) {
        String method = data.getMethod();
        String methodClass = data.getMethodClass();
        Status status = data.getStatus1();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Navigate to method detail page and check for correct content");
        reportTestsPage = reportTestsPage.selectTestStatus(status);
        ReportStepsTab reportStepsTab = reportTestsPage.navigateToStepsTab(method);
        reportStepsTab.assertMethodOverviewContainsCorrectContent(methodClass, status.title, method);
        ReportThreadsPage reportThreadsPage = reportStepsTab.clickThreadLink();
        reportThreadsPage.assertMethodBoxIsSelected(method);
    }

    // Deactivated testcase due to renewed threads view (echarts) since it is not that easy possible
    // to access elements such as the specific testcases inside the canvas element of echarts
    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderForPreTestMethods_Classes_States_ForDetailsType", enabled = false)
    public void testT01_methodOverviewIsCorrectForDetailsType(TestData data) {
        String method = data.getMethod();
        String methodClass = data.getMethodClass();
        Status status = data.getStatus1();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Navigate to method detail page and check for correct content");
        reportTestsPage = reportTestsPage.selectTestStatus(status);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(method);
        reportDetailsTab.assertMethodOverviewContainsCorrectContent(methodClass, status.title, method);
        ReportThreadsPage reportThreadsPage = reportDetailsTab.clickThreadLink();
        reportThreadsPage.assertMethodBoxIsSelected(method);
    }

    @Test
    public void testT02_checkDurationFormat() {
        String exampleMethod = "test_Passed";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to method page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        ReportStepsTab reportStepsTab = reportTestsPage.navigateToStepsTab(exampleMethod);

        TestStep.begin("Check whether the duration is displayed and correct");
        final String testDuration = reportStepsTab.getTestDuration();
        final boolean dateFormatIsCorrect = DateTimeUtils.verifyDateTimeString(testDuration);
        Assert.assertTrue(dateFormatIsCorrect, String.format("Test Duration '%s' has correct format", testDuration));
    }

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderForPreTestMethodsWithScreenshot")
    public void testT03_checkScreenshot(final String methodName) {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to method page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        reportDetailsTab.assertScreenshotIsDisplayed();
    }

    @Test
    public void testT04_checkScreenshotManuallyCreated() {
        final String methodName = "test_GenerateScreenshotManually";
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to method page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        ReportStepsTab reportStepsTab = reportTestsPage.navigateToStepsTab(methodName);

        reportStepsTab.assertScreenshotIsDisplayed();
    }

    @Test
    public void testT05_checkScreenshotFailedTest() {
        final String methodName = "test_Failed_WithScreenShot";
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to method page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        reportDetailsTab.assertScreenshotIsDisplayed();
    }

}
