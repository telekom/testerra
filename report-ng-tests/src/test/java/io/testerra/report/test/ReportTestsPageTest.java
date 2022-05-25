package io.testerra.report.test;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportPageType;
import io.testerra.report.test.pages.report.ReportDashBoardPage;
import io.testerra.report.test.pages.report.ReportTestsPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ReportTestsPageTest extends AbstractReportTest {

    @DataProvider
    public static Object[][] dataProviderForDifferentTestStatesForFiltering() {
        return new Object[][]{
                {Status.FAILED},
                {Status.FAILED_EXPECTED},
                {Status.SKIPPED},
                {Status.PASSED},
                {Status.REPAIRED},
        };
    }

    @Test
    public void testT01_checkInitialTable() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.assertPageIsShown();

        TestStep.begin("Check whether the table on tests page is displayed correctly");
        reportTestsPage.assertTableIsDisplayedCorrect();
    }

    @Test(dataProvider = "dataProviderForDifferentTestStatesForFiltering")
    public void testT02_filterForTestStates(Status status) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.assertPageIsShown();

        TestStep.begin("Filter for test state");
        reportTestsPage.selectTestStateFilter(status);

        TestStep.begin("Check whether the table on tests page is displayed correctly");
        reportTestsPage.assertTableIsDisplayedCorrect();
    }

    @Test
    public void testT03_filterForClasses() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.assertPageIsShown();

        TestStep.begin("Loop through all available classes and check whether the table is displayed correctly.");
        reportTestsPage.assertCorrectTableWhenLoopingThroughClasses();
    }

    @Test
    public void testT04_SearchForTestMethods() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.assertPageIsShown();

        TestStep.begin("Loop through all available methods and check whether the table is displayed correctly.");
        reportTestsPage.assertCorrectTableWhenLoopingThroughMethods();
    }

    @Test
    public void testT05_showConfigurationMethods() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.assertPageIsShown();

        TestStep.begin("Enable 'Show configuration methods and check whether more methods are displayed");
        reportTestsPage.assertShowConfigurationMethodsButtonDisplaysMoreMethods();

        TestStep.begin("Enable 'Show configuration methods' and check whether more methods are displayed");
        reportTestsPage.assertConfigurationMethodsAreDisplayed();

        TestStep.begin("Check whether the table is displayed correctly");
        reportTestsPage.assertTableIsDisplayedCorrect();
    }

}
