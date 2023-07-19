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
package io.testerra.report.test.report_test.common;

import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.methodReport.ReportStepsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CheckRuleTest extends AbstractReportTest {

    @DataProvider(parallel = true)
    public Object[][] dataProvider_methods() {
        return new Object[][]{
                {"preTest05_checkRule_IS_DISPLAYED_check"},
                {"preTest06_checkRule_IS_NOT_DISPLAYED_check"},
                {"preTest07_checkRule_IS_PRESENT_check"},
                {"preTest08_checkRule_IS_NOT_PRESENT_check"}

        };
    }

    @Test(dataProvider = "dataProvider_methods")
    public void testT01_checkRuleErrorsAreCorrectReported(String method) {
        String className = "GenerateFailedCheckTestsTTReportTest";
        String[] failureAspects = new String[]{
                "PageFactoryException",
                "UiElementAssertionError"
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport(WEB_DRIVER_MANAGER.getWebDriver());
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(method);

        TestStep.begin("Check details tab contains correct failure aspects");
        reportDetailsTab.assertStacktraceContainsExpectedFailureAspects(failureAspects);
    }

    @Test
    public void testT02_prioritizedErrorMessage_isDisplayedCorrect() {
        String className = "GenerateFailedCheckTestsTTReportTest";
        String methodName = "preTest03_prioritizedErrorMessageCheck";
        String[] failureAspects = new String[]{
                "AssertionError: Custom error message - pretest"
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport(WEB_DRIVER_MANAGER.getWebDriver());
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Check details tab contains correct failure aspects");
        reportDetailsTab.assertStacktraceContainsExpectedFailureAspects(failureAspects);
    }

    @Test
    public void testT03_timeoutCheckPassed_DurationIsValid() {
        String methodName = "preTest03_timeoutCheck_passed";
        String className = "GeneratePassedCheckTestsTTReportTest";
        int validDurationThreshold_lowerBound = 10;         // pageDelay
        int validDurationThreshold_upperBound = 20;         // timeout time

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport(WEB_DRIVER_MANAGER.getWebDriver());
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportStepsTab reportStepsTab = reportTestsPage.navigateToStepsTab(methodName);

        TestStep.begin("Check duration card contains valid value");
        reportStepsTab.assertDurationIsValid(validDurationThreshold_lowerBound, validDurationThreshold_upperBound);
    }

    @Test
    public void testT04_timeoutCheckFailed_DurationIsNotValid() {
        String methodName = "preTest04_timeoutCheck";
        String className = "GenerateFailedCheckTestsTTReportTest";
        int validDurationThreshold_lowerBound = 10;         // pageDelay
        //int validDurationThreshold_upperBound = 20;         // timeout time

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport(WEB_DRIVER_MANAGER.getWebDriver());
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Check duration card contains valid value");
        reportDetailsTab.assertDurationIsNotValid(validDurationThreshold_lowerBound);
    }

    @Test
    public void testT05_collectCheckDisplaysFailureAspectsCorrect() {
        String methodName = "preTest01_collectCheck";
        String className = "GenerateFailedCheckTestsTTReportTest";
        String[] failureAspects = new String[]{
                "UiElementAssertionError: Expected that CollectCheckPage -> collectCheckedUiElement_01 displayed is true",
                "UiElementAssertionError: Expected that CollectCheckPage -> collectCheckedUiElement_02 displayed is true"
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport(WEB_DRIVER_MANAGER.getWebDriver());
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Check details tab contains correct failure aspects");
        reportDetailsTab.assertStacktraceContainsExpectedFailureAspects(failureAspects);
    }

    @Test
    public void testT06_optionalCheckPassesWithFailureAspect() {
        String methodName = "preTest02_optionalCheck_passed";
        String className = "GeneratePassedCheckTestsTTReportTest";
        Status status = Status.PASSED;
        String[] failureAspects = new String[]{
                "UiElementAssertionError: Expected that OptionalCheckPage -> optionalCheckedUiElement displayed is true"
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport(WEB_DRIVER_MANAGER.getWebDriver());
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Check details tab contains correct failure aspects");
        reportDetailsTab.assertStacktraceContainsExpectedFailureAspects(failureAspects);

        TestStep.begin("Check test is passed");
        reportDetailsTab.assertMethodOverviewContainsCorrectContent(className, status.title, methodName);
    }

}


