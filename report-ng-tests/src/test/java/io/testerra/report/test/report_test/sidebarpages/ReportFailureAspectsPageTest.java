package io.testerra.report.test.report_test.sidebarpages;

import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportFailureAspectsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import io.testerra.report.test.pages.utils.FailureAspectType;
import io.testerra.report.test.pages.utils.TestData;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class ReportFailureAspectsPageTest extends AbstractReportTest {

    @Test
    public void testT01_checkFailureAspectIndicesDescendingCorrectly() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver);

        TestStep.begin("Navigate to failure aspects page.");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class);

        TestStep.begin("Check whether the content table is displayed correct");
        reportFailureAspectsPage.assertRankColumnDescends();
    }

    @Test(dataProvider = "dataProviderForFailureAspectsTypes")
    public void testT02_checkTypeFilter(final FailureAspectType failureAspectType) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver);

        TestStep.begin("Navigate to failure aspects page.");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class);

        TestStep.begin("Check whether the table become adjusted correctly when type is selected");
        reportFailureAspectsPage = reportFailureAspectsPage.selectFailureAspect(failureAspectType);
        reportFailureAspectsPage.assertFailureAspectTypeIsFilteredCorrectly(failureAspectType);
    }

    @Test(dataProvider = "dataProviderForFailureAspects")
    public void testT03_checkSearchFilter(String failureAspect) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver);

        TestStep.begin("Navigate to failure aspects page.");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class);

        TestStep.begin("Check whether the table become adjusted correctly when different search requests are queried");
        reportFailureAspectsPage = reportFailureAspectsPage.search(failureAspect);
        reportFailureAspectsPage.assertFailureAspectsColumnContainsCorrectAspects(failureAspect);
    }

    @Test
    public void testT04_checkShowExpectedFailedButton() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver);

        TestStep.begin("Navigate to failure aspects page.");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class);

        TestStep.begin("Check whether the table become adjusted correctly when 'show expected failed' button is enabled!");
        int amountOfAspectsButtonEnabled = reportFailureAspectsPage.getColumns(ReportFailureAspectsPage.FailureAspectsTableEntry.STATUS).size();
        // TODO: why assert?
        reportFailureAspectsPage.assertShowExpectedFailedButtonIsDisabled();
        reportFailureAspectsPage = reportFailureAspectsPage.clickShowExpectedFailedButton();
        int amountOfAspectsButtonDisabled = reportFailureAspectsPage.getColumns(ReportFailureAspectsPage.FailureAspectsTableEntry.STATUS).size();
        Assert.assertTrue(amountOfAspectsButtonDisabled < amountOfAspectsButtonEnabled,
                "There should be more aspects listed, when expected fails button is enabled!");
    }

    @Test(dataProvider = "failureAspectsWithMultipleStatus")
    public void testT05_checkStatesRedirectCorrectForFailureAspectsWithDifferentStates(TestData data) {
        WebDriver driver = WebDriverManager.getWebDriver();
        String failureAspect = data.getFailureAspect();
        Status status1 = data.getStates().get(0);
        Status status2 = data.getStates().get(1);

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver);

        TestStep.begin("Navigate to failure aspects page.");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class);

        // TODO: verify methodname?
        TestStep.begin("Check every status for failure aspect links to tests-page with content");
        ReportTestsPage reportTestsPage = reportFailureAspectsPage.clickStateLink(failureAspect, status1);
        reportTestsPage.clickConfigurationMethodsSwitch();
        reportTestsPage.assertCorrectTestStatus(status1);

        reportFailureAspectsPage = reportTestsPage.gotoToReportPage(ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class);

        reportTestsPage = reportFailureAspectsPage.clickStateLink(failureAspect, status2);
        reportTestsPage.clickConfigurationMethodsSwitch();
        reportTestsPage.assertCorrectTestStatus(status2);
    }

    @Test(dataProvider = "dataProviderForFailureAspectsWithCorrespondingStates")
    public void testT06_checkNavigationWithFailureAspect(TestData data) {
        WebDriver driver = WebDriverManager.getWebDriver();
        String failureAspect = data.getFailureAspect();
        List<Status> statusList = data.getStates();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver);

        TestStep.begin("Navigate to failure aspects page.");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class);

        TestStep.begin("Check every failure aspect link for correct directing");
        ReportTestsPage reportTestsPage = reportFailureAspectsPage.clickFailureAspectLink(failureAspect);
        reportTestsPage = reportTestsPage.clickConfigurationMethodsSwitch();
        reportTestsPage.assertCorrectTestStates(statusList);
    }

}
