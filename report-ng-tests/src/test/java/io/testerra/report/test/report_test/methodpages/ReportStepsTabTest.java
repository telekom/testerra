package io.testerra.report.test.report_test.methodpages;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportMethodPageType;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportMethodPage;
import io.testerra.report.test.pages.report.methodReport.ReportStepsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.graalvm.compiler.hotspot.phases.OnStackReplacementPhase;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ReportStepsTabTest extends AbstractReportTest {

    @DataProvider
    public Object[][] dataProviderForTestsWithoutFailureAspect() {
        return new Object[][]{
                {"test_Passed"},
                {"test_expectedFailedPassed"},
                {"test_GenerateScreenshotManually"}
        };
    }

    @DataProvider
    public Object[][] dataProviderForPreTestMethodsWithFailureAspects(){
        return new Object[][]{
                {"test_SkippedNoStatus","SkipException: Test Skipped."},
                {"test_Optional_Assert","AssertionError: minor fail"},
                {"test_failedPageNotFound","PageNotFoundException: Test page not reached."},
                {"test_expectedFailedPageNotFound","PageNotFoundException: Test page not reached."},
        };
    }



    @Test(dataProvider = "dataProviderForTestsWithoutFailureAspect")
    public void testT01_passedTestsWithoutFailureAspectsLinkToStepsTab(String method) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Select passed status and check target tab for non-failure-aspects methods");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.PASSED.title);

        ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(method);
        reportMethodPage.assertPageIsValid(ReportMethodPageType.STEPS);
    }

    @Test(dataProvider = "dataProviderForPreTestMethodsWithFailureAspects")
    public void testT02_checkTestStepsContainFailureAspectMessage(String method, String failureAspect) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check whether steps page contains correct failure aspects.");
        ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(method);
        reportMethodPage.navigateBetweenTabs(ReportMethodPageType.STEPS, ReportStepsTab.class);
        reportMethodPage.assertPageIsValid(ReportMethodPageType.STEPS);
        reportMethodPage.stepsPageAssertsTestStepsContainFailureAspectMessage(failureAspect);
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
        ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(preTestCollectorMethod);

        TestStep.begin("Navigate to test steps tab");
        reportMethodPage.navigateBetweenTabs(ReportMethodPageType.STEPS, ReportStepsTab.class);

        TestStep.begin("Check that each failure aspect contains an 'AssertCollector' statement.");
        reportMethodPage.stepsPageAssertsEachFailureAspectContainsExpectedStatement(expectedStatement);
    }
}
