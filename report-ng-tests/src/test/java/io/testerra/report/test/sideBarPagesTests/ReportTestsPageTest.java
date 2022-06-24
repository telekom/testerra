package io.testerra.report.test.sideBarPagesTests;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

public class ReportTestsPageTest extends AbstractReportTest {

    @Test
    public void testT01_checkInitialTable() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check whether the table on tests page is displayed correctly");
        reportTestsPage.assertTableIsDisplayedCorrect();
    }

    @Test
    public void testT02_filterForTestStates() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Get a list of all possible test states, which can be selected on tests page");
        List<String> testStates = reportTestsPage.getListOfAllSelectableStates();

        TestStep.begin("Check whether the tests page table is correct for every test state");
        reportTestsPage.LoopThroughPossibleTestStateListAndAssertTableIsDisplayedCorrect(testStates);
    }

    @Test
    public void testT03_filterForClasses() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Loop through all available classes and check whether the table is displayed correctly.");
        reportTestsPage.assertCorrectTableWhenLoopingThroughClasses();
    }

    @Test
    public void testT04_SearchForTestMethods() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Loop through all available methods and check whether the table is displayed correctly.");
        reportTestsPage.assertCorrectTableWhenLoopingThroughMethods();
    }

    @Test
    public void testT05_SearchForFailureAspect() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Loop through all available assertions and check whether the table is displayed correctly.");
        reportTestsPage.assertCorrectTableWhenLoopingThroughFailureAspect();
    }

    @Test
    public void testT06_showConfigurationMethods() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Enable 'Show configuration methods and check whether more methods are displayed");
        reportTestsPage.assertShowConfigurationMethodsButtonDisplaysMoreMethods();

        TestStep.begin("Enable 'Show configuration methods' and check whether more methods are displayed");
        reportTestsPage.assertConfigurationMethodsAreDisplayed();

        TestStep.begin("Check whether the table is displayed correctly");
        reportTestsPage.assertTableIsDisplayedCorrect();
    }

}
