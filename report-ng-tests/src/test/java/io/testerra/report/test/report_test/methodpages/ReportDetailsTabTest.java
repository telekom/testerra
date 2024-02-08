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
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import io.testerra.report.test.pages.utils.TestData;

public class ReportDetailsTabTest extends AbstractReportTest {

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderForPreTestMethodsWithFailureAspect")
    public void testT01_checkFailureAspectContainsCorrectStatus(TestData data) {
        String method = data.getMethod();
        Status status = data.getStatus1();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check whether the displayed test state corresponds to each method");
        reportTestsPage = reportTestsPage.selectTestStatus(status);

        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(method, status);
        reportDetailsTab.assertPageIsValid();
        reportDetailsTab.assertFailureAspectsCorrespondsToCorrectStatus(status.title);
    }

    @Test
    public void testT02_passedTestsWithFailureAspectsContainOptionalAssert() {
        String methodName = "test_Optional_Assert";
        String expectedFailureAspect = "OptionalAssert";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Select passed status and check target tab for non-failure-aspects methods");
        reportTestsPage = reportTestsPage.selectTestStatus(Status.PASSED);

        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);
        reportDetailsTab.assertTestMethodContainsCorrectFailureAspect(expectedFailureAspect);
    }

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderForPreTestMethodsWithStatusFailed")
    public void testT03_failedTestsContainCorrespondingFailureAspect(TestData data) {
        final String method = data.getMethod();
        final String [] expectedFailureAspect = data.getFailureAspects();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check for each failed test method whether it contains a valid failure aspect");
        reportTestsPage = reportTestsPage.selectTestStatus(Status.FAILED);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(method);
        reportDetailsTab.assertTestMethodContainsCorrectFailureAspect(expectedFailureAspect);
    }

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderForPreTestMethodsWithStatusExpectedFailed")
    public void testT04_expectedFailedTestsContainCorrespondingFailureAspectAndFailsAnnotation(TestData data) {
        final String method = data.getMethod();
        final String[] expectedFailureAspect = data.getFailureAspects();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check for each failed test method whether it contains a valid failure aspect");
        reportTestsPage = reportTestsPage.selectTestStatus(Status.FAILED_EXPECTED);

        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(method);
        reportDetailsTab.assertTestMethodContainsCorrectFailureAspect(expectedFailureAspect);
        reportDetailsTab.assertTestMethodeReportContainsFailsAnnotation();
    }

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderForPreTestMethodsWithStatusSkipped")
    public void testT05_skippedTestsContainCorrectFailureAspects(String method) {

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check for each failed test method whether it contains a valid failure aspect");
        reportTestsPage.selectTestStatus(Status.SKIPPED);

        ReportDetailsTab reportMethodPage = reportTestsPage.navigateToDetailsTab(method);
        reportMethodPage.assertSkippedTestContainsCorrespondingFailureAspect();
    }

    @Test
    public void testT06_failedTestWithHtmlInFailureAspect() {
        final String method = "test_failedWithInterceptedClick";
        final String expectedFailureAspect = "ElementClickInterceptedException: element click intercepted: Element <button id=\"btn\">...</button> "
                + "is not clickable at point";

        TestStep.begin("navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("verify html text is not rendered");
        reportTestsPage = reportTestsPage.selectTestStatus(Status.FAILED);
        final ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(method);

        final String failureAspect = reportDetailsTab.getFailureAspect().waitFor().text().getActual();
        Assert.assertTrue(failureAspect.contains(expectedFailureAspect), "html shown as text in failure aspect");

        TestStep.begin("verify status of failure aspect");
        reportDetailsTab.assertFailureAspectsCorrespondsToCorrectStatus(Status.FAILED.title);

        TestStep.begin("verify stack trace");
        reportDetailsTab.assertStacktraceContainsExpectedFailureAspects(expectedFailureAspect);
    }

    @Test
    public void testT07_failedTestInInnerClass() {
        final String method = "test_failInInnerClass";

        TestStep.begin("navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("verify page is complete");
        reportTestsPage = reportTestsPage.selectTestStatus(Status.FAILED);
        final ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(method);

        reportDetailsTab.assertPageIsValid();
    }
}
