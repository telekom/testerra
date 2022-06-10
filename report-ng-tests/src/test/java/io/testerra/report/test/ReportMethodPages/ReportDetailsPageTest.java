package io.testerra.report.test.ReportMethodPages;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportPageType;
import io.testerra.report.test.pages.report.ReportConcreteMethodPage;
import io.testerra.report.test.pages.report.ReportDetailsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ReportDetailsPageTest extends AbstractReportTest {

    @DataProvider
    public Object[][] statusDataProviderForTestsWithFailureAspect() {
        return new Object[][]{{Status.FAILED}, {Status.FAILED_EXPECTED}, {Status.SKIPPED}, {Status.RETRIED}};
    }

    @Test(dataProvider = "statusDataProviderForTestsWithFailureAspect")
    public void testT01_checkFailureAspectContainsCorrectStatus(Status status) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check whether the displayed test state corresponds to each method");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), status.title);
        for (int i = 0; i < reportTestsPage.getAmountOfTableRows(); i++) {
            ReportConcreteMethodPage reportConcreteMethodPage = reportTestsPage.navigateToMethodReport(i);
            ReportDetailsPage reportDetailsPage = reportConcreteMethodPage.navigateBetweenTabs(ReportPageType.DETAILS, ReportDetailsPage.class);
            reportDetailsPage.assertFailureAspectsCorrespondsToCorrectStatus(status.title);
            reportDetailsPage.gotoToReportPage(ReportPageType.TESTS, ReportTestsPage.class);
            reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), status.title);
        }
    }

    @Test(dataProvider = "statusDataProviderForTestsWithFailureAspect")
    public void testT02_checkStacktraceContainsInformation(Status status) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Select non-passed status and check all methods for error message");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), status.title);
        for (int i = 0; i < reportTestsPage.getAmountOfTableRows(); i++) {
            ReportConcreteMethodPage reportConcreteMethodPage = reportTestsPage.navigateToMethodReport(i);
            ReportDetailsPage reportDetailsPage = reportConcreteMethodPage.navigateBetweenTabs(ReportPageType.DETAILS, ReportDetailsPage.class);

            reportDetailsPage = reportDetailsPage.clickStacktraceDropdown();
            reportDetailsPage.assertStacktraceIsDisplayed();

            reportDetailsPage.gotoToReportPage(ReportPageType.TESTS, ReportTestsPage.class);
            reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), status.title);
        }
    }

    @Test(dataProvider = "statusDataProviderForTestsWithFailureAspect")
    public void testT03_checkOriginCardForCodeLinesAndMarkedFailureLine(Status status) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Select non-passed status and check all methods for error message");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), status.title);
        for (int i = 0; i < reportTestsPage.getAmountOfTableRows(); i++) {
            ReportConcreteMethodPage reportConcreteMethodPage = reportTestsPage.navigateToMethodReport(i);
            ReportDetailsPage reportDetailsPage = reportConcreteMethodPage.navigateBetweenTabs(ReportPageType.DETAILS, ReportDetailsPage.class);

            reportDetailsPage.assertContainsCodeLines();
            reportDetailsPage.assertFailLureLineIsMarked();

            reportDetailsPage.gotoToReportPage(ReportPageType.TESTS, ReportTestsPage.class);
            reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), status.title);
        }
    }
}