package io.testerra.report.test.report_test.sidebarpages;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;

import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportFailureAspectsPage;

import java.util.List;
import java.util.Optional;

public class ReportFailureAspectsPageTest extends AbstractReportTest {

    @DataProvider
    public static Object[][] dataProviderForFailureAspects() {
        return new Object[][]{
                {"AssertionError: Creating TestStatus 'Failed'", Status.FAILED, Status.FAILED},
                {"AssertionError: failed1", Status.FAILED, Status.FAILED_EXPECTED},
                {"AssertionError: failed2", Status.FAILED, Status.FAILED_EXPECTED},
                {"PageNotFoundException: Test page not reached.", Status.FAILED, Status.FAILED_EXPECTED},
                {"AssertionError: Error in @BeforeMethod", Status.SKIPPED, Status.FAILED},
                {"AssertionError: 'Failed' on reached Page.", Status.FAILED, null},
                {"AssertionError: minor fail", Status.PASSED, null},
                {"SkipException: Test Skipped.", Status.SKIPPED, null},
                {"RuntimeException: Error in DataProvider.",  Status.SKIPPED, null},
                {/*[...]*/"depends on not successfully finished methods",  Status.SKIPPED, null},
                {"AssertionError: test_FailedToPassedHistoryWithRetry", Status.RETRIED, null},
                {"AssertionError: No Oil.", Status.FAILED_EXPECTED, null}
        };
    }

    @DataProvider
    public Object[][] failureAspectsWithMultipleStatus(){
        return new Object[][]{
                {"AssertionError: Creating TestStatus 'Failed'", Status.FAILED, Status.FAILED},
                {"AssertionError: failed1",  Status.FAILED, Status.FAILED_EXPECTED},
                {"AssertionError: failed2",  Status.FAILED, Status.FAILED_EXPECTED},
                {"PageNotFoundException: Test page not reached.",  Status.FAILED, Status.FAILED_EXPECTED},
                {"AssertionError: Error in @BeforeMethod", Status.SKIPPED,  Status.FAILED}
        };
    }

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

    // TODO: test with explicitly provided filter options via Dataprovider
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

    // TODO: test with explicitly provided failure aspects via Dataprovider
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

    @Test(dataProvider = "failureAspectsWithMultipleStatus")
    public void testT05_checkStatesRedirectCorrectForFailureAspectsWithDifferentStates(String failureAspect, Status state1, Status state2){
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver);

        TestStep.begin("Navigate to failure aspects page.");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class);

        TestStep.begin("Check every status for failure aspect links to tests-page with content");
        ReportTestsPage reportTestsPage = reportFailureAspectsPage.clickStateLink(failureAspect, state1);
        reportTestsPage.clickConfigurationMethodsSwitch();
        reportTestsPage.assertCorrectTestStatus(state1);

        reportFailureAspectsPage = reportTestsPage.gotoToReportPage(ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class);

        reportTestsPage = reportFailureAspectsPage.clickStateLink(failureAspect, state2);
        reportTestsPage.clickConfigurationMethodsSwitch();
        reportTestsPage.assertCorrectTestStatus(state2);
    }

    @Test(dataProvider = "dataProviderForFailureAspects")
    public void testT06_checkNavigationWithFailureAspect(String failureAspect, Status status1, Status status2){
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver);

        TestStep.begin("Navigate to failure aspects page.");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class);

        TestStep.begin("Check every failure aspect link for correct directing");
        Optional<GuiElement> failureAspectLink = reportFailureAspectsPage.getColumns(1).stream().filter(i -> i.getText().contains(failureAspect)).findFirst();
        Assert.assertTrue(failureAspectLink.isPresent(), "Provided failure Aspect should be contained in failure-aspects-page");
        ReportTestsPage reportTestsPage = reportFailureAspectsPage.clickFailureAspectLink(failureAspectLink.get());
        reportTestsPage.assertCorrectTestStates(status1, status2);
    }

}
