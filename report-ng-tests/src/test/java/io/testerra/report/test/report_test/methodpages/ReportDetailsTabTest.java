package io.testerra.report.test.report_test.methodpages;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import io.testerra.report.test.pages.utils.TestData;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class ReportDetailsTabTest extends AbstractReportTest {

    @Test(dataProvider = "dataProviderForPreTestMethodsWithFailureAspect")
    public void testT01_checkFailureAspectContainsCorrectStatus(TestData data) {
        WebDriver driver = WebDriverManager.getWebDriver();
        String method = data.getMethod();
        Status status = data.getStatus1();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check whether the displayed test state corresponds to each method");
        reportTestsPage = reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), status.title);

        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(method, status);
        reportDetailsTab.assertPageIsValid();
        reportDetailsTab.assertFailureAspectsCorrespondsToCorrectStatus(status.title);
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
        reportTestsPage = reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.PASSED.title);

        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);
        reportDetailsTab.assertTestMethodContainsCorrectFailureAspect(expectedFailureAspect);
    }

    @Test(dataProvider = "dataProviderForPreTestMethodsWithStatusFailed")
    public void testT03_failedTestsContainCorrespondingFailureAspect(TestData data) {
        WebDriver driver = WebDriverManager.getWebDriver();
        String method = data.getMethod();
        String expectedFailureAspect = data.getFailureAspect();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check for each failed test method whether it contains a valid failure aspect");
        reportTestsPage = reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.FAILED.title);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(method);
        reportDetailsTab.assertTestMethodContainsCorrectFailureAspect(expectedFailureAspect);
    }

    @Test(dataProvider = "dataProviderForPreTestMethodsWithStatusExpectedFailed")
    public void testT04_expectedFailedTestsContainCorrespondingFailureAspectAndFailsAnnotation(TestData data) {
        WebDriver driver = WebDriverManager.getWebDriver();
        String method = data.getMethod();
        String expectedFailureAspect = data.getFailureAspect();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check for each failed test method whether it contains a valid failure aspect");
        reportTestsPage = reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.FAILED_EXPECTED.title);

        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(method);
        reportDetailsTab.assertTestMethodContainsCorrectFailureAspect(expectedFailureAspect);
        reportDetailsTab.assertTestMethodeReportContainsFailsAnnotation();
    }

    @Test(dataProvider = "dataProviderForPreTestMethodsWithStatusSkipped")
    public void testT05_skippedTestsContainCorrectFailureAspects(String method) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check for each failed test method whether it contains a valid failure aspect");
        reportTestsPage = reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.SKIPPED.title);

        ReportDetailsTab reportMethodPage = reportTestsPage.navigateToDetailsTab(method);
        reportMethodPage.assertSkippedTestContainsCorrespondingFailureAspect();
    }

}