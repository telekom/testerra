package io.testerra.report.test.report_test.methodpages;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportMethodPageType;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportMethodPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ReportDetailsTabTest extends AbstractReportTest {

    @DataProvider
    public Object[][] testStatesDataProvider() {
        return new Object[][]{
                {Status.PASSED},
                {Status.SKIPPED},
                {Status.FAILED_EXPECTED},
                {Status.FAILED},
                {Status.REPAIRED},
                {Status.RECOVERED},
                {Status.RETRIED}};
    }

    @DataProvider
    public Object[][] dataProviderForPreTestMethodsWithFailureAspect() {
        return new Object[][]{
                // skipped
                {"test_SkippedNoStatus", Status.SKIPPED},
                // Failed
                {"testAssertCollector", Status.FAILED},
                // expected Failed
                {"test_expectedFailedAssertCollector", Status.FAILED_EXPECTED},
                // retried
                {"test_PassedAfterRetry", Status.RETRIED}
        };
    }

    @DataProvider
    public Object[][] dataProviderForPreTestMethodsWithStatusFailed() {
        return new Object[][]{
                {"testAssertCollector"},
                {"test_failedPageNotFound"},
                {"test_Failed"},
                {"test_Failed_WithScreenShot"}
        };
    }

    @DataProvider
    public Object[][] dataProviderForPreTestMethodsWithStatusExpectedFailed() {
        return new Object[][]{
                {"test_expectedFailedAssertCollector"},
                {"test_expectedFailedPageNotFound"},
                {"test_expectedFailed"}
        };
    }

    @DataProvider
    public Object[][] dataProviderForPreTestMethodsWithStatusSkipped() {
        return new Object[][]{
                {"test_SkippedNoStatus"},
                {"test_Skipped_AfterErrorInDataProvider"},
                {"test_Skipped_DependingOnFailed"},
                {"test_Skipped_AfterErrorInBeforeMethod"}
        };
    }

    @Test(dataProvider = "dataProviderForPreTestMethodsWithFailureAspect")
    public void testT01_checkFailureAspectContainsCorrectStatus(String method, Status status) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check whether the displayed test state corresponds to each method");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), status.title);

        ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(method, ReportMethodPageType.DETAILS);
        reportMethodPage.assertPageIsValid(ReportMethodPageType.DETAILS);
        reportMethodPage.detailPageAsserts_FailureAspectsCorrespondsToCorrectStatus(status.title);
    }

    @Test
    public void testT02_passedTestsWithFailureAspectsContainOptionalAssert() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String methodName = "test_Optional_Assert";
        String expectedFailureAspect = "OptionalAssert";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Select passed status and check target tab for non-failure-aspects methods");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.PASSED.title);

        ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(methodName, ReportMethodPageType.DETAILS);
        reportMethodPage.assertPageIsValid(ReportMethodPageType.DETAILS);
        reportMethodPage.detailsPageAssertsTestMethodContainsCorrectFailureAspect(expectedFailureAspect);
    }

    @Test(dataProvider = "dataProviderForPreTestMethodsWithStatusFailed")
    public void testT03_failedTestsContainCorrespondingFailureAspect(String method) {
        WebDriver driver = WebDriverManager.getWebDriver();
        String[] expectedFailureAspects = {"AssertCollector.fail", "PageNotFoundException", "Assert.fail"};

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check for each failed test method whether it contains a valid failure aspect");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.FAILED.title);
        ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(method, ReportMethodPageType.DETAILS);
        reportMethodPage.assertPageIsValid(ReportMethodPageType.DETAILS);
        reportMethodPage.detailsPageAssertsTestMethodContainsCorrectFailureAspect(expectedFailureAspects);
    }

    @Test(dataProvider = "dataProviderForPreTestMethodsWithStatusExpectedFailed")
    public void testT04_expectedFailedTestsContainCorrespondingFailureAspectAndFailsAnnotation(String method) {
        WebDriver driver = WebDriverManager.getWebDriver();
        String[] expectedFailureAspects = {"AssertCollector.fail", "PageNotFoundException", "Assert.fail"};

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check for each failed test method whether it contains a valid failure aspect");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.FAILED_EXPECTED.title);

        ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(method, ReportMethodPageType.DETAILS);
        reportMethodPage.assertPageIsValid(ReportMethodPageType.DETAILS);
        reportMethodPage.detailsPageAssertsTestMethodContainsCorrectFailureAspect(expectedFailureAspects);
        reportMethodPage.assertTestMethodeReportContainsFailsAnnotation();
    }

    @Test
    public void testT05_repairedTestsArePassedButContainFailsAnnotation() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String repairedTest = "test_expectedFailedPassed";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check for each failed test method whether it contains a valid failure aspect");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.REPAIRED.title);

        ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(repairedTest, ReportMethodPageType.STEPS);
        reportMethodPage.assertPageIsValid(ReportMethodPageType.STEPS);
        reportMethodPage.assertTestMethodeReportContainsFailsAnnotation();
    }

    @Test(dataProvider = "dataProviderForPreTestMethodsWithStatusSkipped")
    public void testT06_skippedTestsContainCorrectFailureAspects(String method) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check for each failed test method whether it contains a valid failure aspect");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.SKIPPED.title);

        ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(method, ReportMethodPageType.DETAILS);
        reportMethodPage.assertPageIsValid(ReportMethodPageType.DETAILS);
        reportMethodPage.detailsPageAssertSkippedTestContainsCorrespondingFailureAspect();
    }

}