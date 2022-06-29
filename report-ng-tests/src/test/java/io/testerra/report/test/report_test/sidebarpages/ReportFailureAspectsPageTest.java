package io.testerra.report.test.report_test.sidebarpages;

import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportFailureAspectsPage;

public class ReportFailureAspectsPageTest extends AbstractReportTest {

    @Test
    public void testT01_checkInitialTable() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver);

        TestStep.begin("Navigate to failure aspects page.");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class);

//      TODO:  call only needed asserts
        reportFailureAspectsPage.assertFailureAspectsTableIsDisplayedCorrect();
    }

    @Test
    public void testT02_checkTypeFilter() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver);

        TestStep.begin("Navigate to failure aspects page.");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class);

        // TODO: separate action and assert: make possible selection, assert result per selection, all separate methods
        TestStep.begin("Check whether the table become adjusted correctly when different types are selected");
        reportFailureAspectsPage.assertFailureAspectTableIsCorrectDisplayedWhenIteratingThroughSelectableTypes();
    }

    @Test
    public void testT03_checkSearchFilter() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver);

        TestStep.begin("Navigate to failure aspects page.");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class);

        // TODO: separate action and assert: get possible search terms, invoke search per termn, assert result per search, all separate methods
        //  make test fail in case of no existing table rows/failure aspects
        TestStep.begin("Check whether the table become adjusted correctly when different search requests are queried");
        reportFailureAspectsPage.assertFailureAspectTableIsCorrectDisplayedWhenSearchingForDifferentAspects();
    }

    @Test
    public void testT04_checkButtonFilter() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver);

        TestStep.begin("Navigate to failure aspects page.");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class);

        TestStep.begin("Check whether the table become adjusted correctly when 'show expected failed' button is enabled!");
        reportFailureAspectsPage.assertShowExpectedFailedButtonWorksCorrectly();
        reportFailureAspectsPage.assertFailureAspectsTableIsDisplayedCorrect();
    }

}
