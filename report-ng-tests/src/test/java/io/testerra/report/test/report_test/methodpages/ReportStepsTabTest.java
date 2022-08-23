package io.testerra.report.test.report_test.methodpages;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.methodReport.ReportStepsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import io.testerra.report.test.pages.utils.TestData;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class ReportStepsTabTest extends AbstractReportTest {

    @Test(dataProvider = "dataProviderForTestsWithoutFailureAspect")
    public void testT01_passedTestsWithoutFailureAspectsLinkToStepsTab(String method) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Select passed status and check target tab for non-failure-aspects methods");
        reportTestsPage = reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.PASSED.title);

        ReportStepsTab reportStepsTab = reportTestsPage.navigateToStepsTab(method);
        reportStepsTab.assertSeveralTestStepsAreListed();
        reportStepsTab.assertPageIsShown();
    }

    @Test(dataProvider = "dataProviderForPreTestMethodsWithFailureAspects")
    public void testT02_checkTestStepsContainFailureAspectMessage(TestData data) {
        WebDriver driver = WebDriverManager.getWebDriver();
        String method = data.getMethod();
        String failureAspect = data.getFailureAspect();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

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
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

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
        WebDriver driver = WebDriverManager.getWebDriver();
        String repairedTest = "test_expectedFailedPassed";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check for each failed test method whether it contains a valid failure aspect");
        reportTestsPage = reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.REPAIRED.title);

        ReportStepsTab reportStepsTab = reportTestsPage.navigateToStepsTab(repairedTest);
        reportStepsTab.assertTestMethodeReportContainsFailsAnnotation();
    }
}
