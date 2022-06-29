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
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class ReportStepsTabTest extends AbstractReportTest {

    @Test
    public void testT01_passedTestsWithoutFailureAspectsLinkToStepsTab() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Select passed status and check target tab for non-failure-aspects methods");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.PASSED.title);
        for (int i = 0; i < reportTestsPage.getAmountOfTableRows(); i++) {

            if (reportTestsPage.methodGotFailureAspect(i)) continue;

            ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(i);
            reportMethodPage.assertPageIsValid(ReportMethodPageType.STEPS);

            reportTestsPage = reportMethodPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
            reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), Status.PASSED.title);
        }
    }

    @Test
    public void testT02_checkTestStepsContainFailureAspectMessage() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Select passed status and check target tab for non-failure-aspects methods");
        for (int i = 0; i < reportTestsPage.getAmountOfTableRows(); i++) {

            if (!reportTestsPage.methodGotFailureAspect(i)) continue;

            ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(i);
            reportMethodPage.assertPageIsValid(ReportMethodPageType.DETAILS);
            String failureAspect = reportMethodPage.getFailureAspectFromDetailsPage();
            reportMethodPage.navigateBetweenTabs(ReportMethodPageType.STEPS, ReportStepsTab.class);
            reportMethodPage.assertPageIsValid(ReportMethodPageType.STEPS);
            reportMethodPage.stepsPageAssertsTestStepsContainFailureAspectMessage(failureAspect);

            reportTestsPage = reportMethodPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        }
    }
}
