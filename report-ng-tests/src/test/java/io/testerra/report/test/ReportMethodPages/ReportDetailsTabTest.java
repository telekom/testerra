package io.testerra.report.test.ReportMethodPages;

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

    @Test(dataProvider = "testStatesDataProvider")
    public void testT01_checkFailureAspectContainsCorrectStatus(Status status) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check whether the displayed test state corresponds to each method");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), status.title);
        for (int i = 0; i < reportTestsPage.getAmountOfTableRows(); i++) {

            if (!reportTestsPage.methodGotFailureAspect(i)) continue;

            ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(i);
            reportMethodPage.assertPageIsValid(ReportMethodPageType.DETAILS);
            reportMethodPage.detailPageAssertsFailureAspectsCorrespondsToCorrectStatus(status.title);

            reportTestsPage = reportMethodPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
            reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), status.title);
        }
    }

    @Test
    public void testT02_passedTestsWithFailureAspectsContainOptionalAssert() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String expectedFailureAspect = "OptionalAssert";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Select passed status and check target tab for non-failure-aspects methods");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.PASSED.title);
        for (int i = 0; i < reportTestsPage.getAmountOfTableRows(); i++) {

            if (!reportTestsPage.methodGotFailureAspect(i)) continue;

            ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(i);
            reportMethodPage.assertPageIsValid(ReportMethodPageType.DETAILS);
            reportMethodPage.detailsPageAssertsTestMethodContainsCorrectFailureAspect(expectedFailureAspect);

            reportMethodPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
            reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.PASSED.title);
        }
    }

    @Test
    public void testT03_failedTestsContainCorrespondingFailureAspect() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String[] expectedFailureAspects = {"AssertCollector.fail", "PageNotFoundException", "Assert.fail"};

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check for each failed test method whether it contains a valid failure aspect");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.FAILED.title);
        for (int i = 0; i < reportTestsPage.getAmountOfTableRows(); i++) {

            if (!reportTestsPage.methodGotFailureAspect(i)) continue;

            ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(i);
            reportMethodPage.assertPageIsValid(ReportMethodPageType.DETAILS);
            reportMethodPage.detailsPageAssertsTestMethodContainsCorrectFailureAspect(expectedFailureAspects);

            reportTestsPage = reportMethodPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
            reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.FAILED.title);
        }
    }

    @Test
    public void testT04_expectedFailedTestsContainCorrespondingFailureAspectAndFailsAnnotation() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String[] expectedFailureAspects = {"AssertCollector.fail", "PageNotFoundException", "Assert.fail"};

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check for each failed test method whether it contains a valid failure aspect");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.FAILED_EXPECTED.title);
        for (int i = 0; i < reportTestsPage.getAmountOfTableRows(); i++) {

            if (!reportTestsPage.methodGotFailureAspect(i)) continue;

            ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(i);
            reportMethodPage.assertPageIsValid(ReportMethodPageType.DETAILS);
            reportMethodPage.detailsPageAssertsTestMethodContainsCorrectFailureAspect(expectedFailureAspects);
            reportMethodPage.assertTestMethodeReportContainsFailsAnnotation();

            reportTestsPage = reportMethodPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
            reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.FAILED_EXPECTED.title);
        }
    }

    @Test
    public void testT05_repairedTestsArePassedButContainFailsAnnotation() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check for each failed test method whether it contains a valid failure aspect");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.REPAIRED.title);
        for (int i = 0; i < reportTestsPage.getAmountOfTableRows(); i++) {

            ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(i);
            reportMethodPage.assertPageIsValid(ReportMethodPageType.STEPS);
            reportMethodPage.assertTestMethodeReportContainsFailsAnnotation();

            reportTestsPage = reportMethodPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
            reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.REPAIRED.title);
        }
    }

    @Test
    public void testT06_skippedTestsContainCorrectFailureAspects() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check for each failed test method whether it contains a valid failure aspect");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.SKIPPED.title);
        for (int i = 0; i < reportTestsPage.getAmountOfTableRows(); i++) {

            ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(i);
            reportMethodPage.assertPageIsValid(ReportMethodPageType.DETAILS);
            reportMethodPage.detailsPageAssertSkippedTestContainsCorrespondingFailureAspect();

            reportTestsPage = reportMethodPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
            reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.SKIPPED.title);
        }
    }

}