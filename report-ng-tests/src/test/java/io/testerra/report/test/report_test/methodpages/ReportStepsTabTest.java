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

import eu.tsystems.mms.tic.testframework.common.DefaultPropertyManager;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.methodReport.ReportStepsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import io.testerra.report.test.pages.utils.TestData;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider.WEB_DRIVER_MANAGER;

public class ReportStepsTabTest extends AbstractReportTest {

    @Test(dataProvider = "dataProviderForTestsWithoutFailureAspect")
    public void testT01_passedTestsWithoutFailureAspectsLinkToStepsTab(String method) {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Select passed status and check target tab for non-failure-aspects methods");
        reportTestsPage.selectTestStatus(Status.PASSED);

        ReportStepsTab reportStepsTab = reportTestsPage.navigateToStepsTab(method);
        reportStepsTab.assertSeveralTestStepsAreListed();
        reportStepsTab.assertThat().displayed();
    }

    @Test(dataProvider = "dataProviderForPreTestMethodsWithFailureAspects")
    public void testT02_checkTestStepsContainFailureAspectMessage(TestData data) {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String method = data.getMethod();
        String failureAspect = data.getFailureAspect();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check whether steps page contains correct failure aspects.");
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(method);
        ReportStepsTab reportStepsTab = reportDetailsTab.navigateToStepsTab();
        reportStepsTab.assertsTestStepsContainFailureAspectMessage(failureAspect);
    }

    @Test
    public void testT03_assertCollectorsAreListedInTestSteps(){
        String preTestCollectorMethod = "testAssertCollector"; //testAssertCollector //index on Page: 3 but offset 1 (Starts counting with 1)
        String expectedStatement = "AssertCollector";
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Navigate to report method page of testAssertCollector()");
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(preTestCollectorMethod);

        TestStep.begin("Navigate to test steps tab");
        ReportStepsTab reportStepsTab = reportDetailsTab.navigateToStepsTab();

        TestStep.begin("Check that each failure aspect contains an 'AssertCollector' statement.");
        reportStepsTab.assertEachFailureAspectContainsExpectedStatement(expectedStatement);
    }

    @Test
    public void testT04_repairedTestsArePassedButContainFailsAnnotation() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String repairedTest = "test_expectedFailedPassed";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check for each failed test method whether it contains a valid failure aspect");
        reportTestsPage = reportTestsPage.selectTestStatus(Status.REPAIRED);

        ReportStepsTab reportStepsTab = reportTestsPage.navigateToStepsTab(repairedTest);
        reportStepsTab.assertTestMethodeReportContainsFailsAnnotation();
    }
}
