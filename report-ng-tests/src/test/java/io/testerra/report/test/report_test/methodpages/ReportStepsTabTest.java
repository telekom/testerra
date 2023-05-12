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

import org.testng.annotations.Test;

import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.TestDataProvider;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.methodReport.ReportStepsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import io.testerra.report.test.pages.utils.TestData;

public class ReportStepsTabTest extends AbstractReportTest {

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderForTestsWithoutFailureAspect")
    public void testT01_passedTestsWithoutFailureAspectsLinkToStepsTab(String method) {

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport();

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Select passed status and check target tab for non-failure-aspects methods");
        reportTestsPage.selectTestStatus(Status.PASSED);

        ReportStepsTab reportStepsTab = reportTestsPage.navigateToStepsTab(method);
        reportStepsTab.assertSeveralTestStepsAreListed();
        reportStepsTab.assertThat().displayed();
    }

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderForPreTestMethodsWithFailureAspects")
    public void testT02_checkTestStepsContainFailureAspectMessage(TestData data) {
        final String method = data.getMethod();
        final String failureAspect = data.getFailureAspects()[0];

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport();

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check whether steps page contains correct failure aspects.");
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(method);
        ReportStepsTab reportStepsTab = reportDetailsTab.navigateToStepsTab();
        reportStepsTab.assertsTestStepsContainFailureAspectMessage(failureAspect);
    }

    @Test
    public void testT03_assertCollectorsAreListedInTestSteps(){
        String preTestCollectorMethod = "test_AssertCollector"; //test_AssertCollector //index on Page: 3 but offset 1 (Starts counting with 1)
        String expectedStatement = "AssertCollector";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport();

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Navigate to report method page of test_AssertCollector()");
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(preTestCollectorMethod);

        TestStep.begin("Navigate to test steps tab");
        ReportStepsTab reportStepsTab = reportDetailsTab.navigateToStepsTab();

        TestStep.begin("Check that each failure aspect contains an 'AssertCollector' statement.");
        reportStepsTab.assertEachFailureAspectContainsExpectedStatement(expectedStatement);
    }

    @Test
    public void testT04_repairedTestsArePassedButContainFailsAnnotation() {
        String repairedTest = "test_expectedFailedPassed";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport();

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check for each failed test method whether it contains a valid failure aspect");
        reportTestsPage = reportTestsPage.selectTestStatus(Status.REPAIRED);

        ReportStepsTab reportStepsTab = reportTestsPage.navigateToStepsTab(repairedTest);
        reportStepsTab.assertTestMethodeReportContainsFailsAnnotation();
    }
}
