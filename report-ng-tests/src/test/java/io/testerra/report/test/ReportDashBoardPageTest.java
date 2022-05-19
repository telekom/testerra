package io.testerra.report.test;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.helper.TestState;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.ReportPageType;
import io.testerra.report.test.pages.report.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ReportDashBoardPageTest extends AbstractReportTest {

    @DataProvider
    public Object[][] dataProviderForDifferentTestStates() {
        return new Object[][]{{TestState.Passed}, {TestState.Failed}, {TestState.ExpectedFailed}, {TestState.Skipped}};
    }

    @DataProvider
    public static Object[][] dataProviderForDifferentTestStatesWithAmounts() {
        return new Object[][]{
                {6, TestState.Failed},
                {3, TestState.ExpectedFailed},
                {4, TestState.Skipped},
                {4, TestState.Passed},
        };
    }

    @DataProvider
    public Object[][] dataProviderForNavigationBetweenDifferentPages() {
        return new Object[][]
                {{ReportPageType.TESTS, ReportTestsPage.class},
                        {ReportPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class},
                        {ReportPageType.LOGS, ReportLogsPage.class},
                        {ReportPageType.THREADS, ReportThreadsPage.class}};
    }

    @Test(dataProvider = "dataProviderForDifferentTestStates")
    public void testT01_showCorrectTestClassesWhenClickingOnPieChart(TestState testState) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        final ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));
        reportDashBoardPage.assertPageIsShown();

        TestStep.begin("Check whether each test status displays the correct test classes.");
        reportDashBoardPage.assertPieChartContainsTestState(testState);

        TestStep.begin("Check whether the corresponding test-state-part of the pieChart refreshes the shown test classes");
        reportDashBoardPage.clickPieChartPart(testState);
        reportDashBoardPage.assertCorrectBarChartsAreDisplayed();
    }

    @Test(dataProvider = "dataProviderForDifferentTestStates")
    public void testT02_showCorrectTestClassesWhenClickingOnNumbersChart(TestState testState) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        final ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));
        reportDashBoardPage.assertPageIsShown();

        TestStep.begin("Check whether each test status displays the correct test classes.");
        reportDashBoardPage.assertNumbersChartContainsTestState(testState);

        TestStep.begin("Check whether the corresponding test-state-part of the numbersChart refreshes the shown test classes");
        reportDashBoardPage.clickNumberChartPart(testState);
        reportDashBoardPage.assertCorrectBarChartsAreDisplayed();
    }

    @Test(dataProvider = "dataProviderForNavigationBetweenDifferentPages")
    public void testT03_navigationTowardsDifferentPagesDashBoardPage(ReportPageType type, Class<AbstractReportPage> pageClass) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));
        reportDashBoardPage.assertPageIsShown();

        TestStep.begin("Navigate to '" + type.name() + "' page, check whether page was reached and navigate back to 'Dashboard' page.");
        reportDashBoardPage.gotoToReportPage(type, pageClass);
        reportDashBoardPage.verifyReportPage(type);
    }

    @Test
    public void testT04_reportDurationDisplayed() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));
        reportDashBoardPage.assertPageIsShown();

        TestStep.begin("Check whether the start is displayed");
        reportDashBoardPage.assertStartTimeIsDisplayed();

        TestStep.begin("Check whether the end is displayed");
        reportDashBoardPage.assertEndedTimeIsDisplayed();

        TestStep.begin("Check whether the duration is displayed and correct");
        reportDashBoardPage.assertDurationIsDisplayedCorrect();
    }

    @Test(dataProvider = "dataProviderForDifferentTestStates")
    public void testT05_barChartLinksToFilteredTestsPage(TestState testState){
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        final ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));
        reportDashBoardPage.assertPageIsShown();

        TestStep.begin("Check whether each test status displays the correct test classes.");
        reportDashBoardPage.assertNumbersChartContainsTestState(testState);

        TestStep.begin("Check whether clicking on barchart bars navigates to tests page with correct filter.");
        reportDashBoardPage.clickNumberChartPart(testState);
        ReportTestsPage reportTestsPage = reportDashBoardPage.navigateToFilteredTestPageByClickingBarChartBar();
        reportTestsPage.assertPageIsShown();
        reportTestsPage.assertCorrectTestStatus(testState);
    }

    @Test
    public void testT06_barChartLength() {
        WebDriver driver = WebDriverManager.getWebDriver();
        final double threshold = 0.01;  //1% (random chosen value, but some threshold is needed when asserting with double)

        TestStep.begin("Navigate to dashboard page.");
        final ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));
        reportDashBoardPage.assertPageIsShown();

        TestStep.begin("Check barchart is shown and iterate through all bars to check correct length");
        reportDashBoardPage.assertBarChartIsDisplayed();
        reportDashBoardPage.assertCorrectBarsLength(threshold);
    }

    @Test(dataProvider = "dataProviderForDifferentTestStatesWithAmounts")
    public void testT07_reportPercentages(int amount, TestState testState){
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        final ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));
        reportDashBoardPage.assertPageIsShown();

        TestStep.begin("Check whether each test status displays the correct test classes.");
        reportDashBoardPage.assertPieChartContainsTestState(testState);

        TestStep.begin("Check whether the displayed percentages are correct");
        reportDashBoardPage.assertPieChartPercentages(amount, testState);
    }

    @Test(dataProvider = "dataProviderForDifferentTestStates")
    public void testT08_barChartFilterHovering(TestState testState){
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        final ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));
        reportDashBoardPage.assertPageIsShown();

        TestStep.begin("Check whether each test status displays the correct test classes.");
        reportDashBoardPage.assertNumbersChartContainsTestState(testState);

        TestStep.begin("Show corresponding bars to test state");
        reportDashBoardPage.clickNumberChartPart(testState);

        TestStep.begin("Check whether hovering above a bar in barchart let a popup appear with correct content");
        reportDashBoardPage.assertPopupWhileHoveringWithCorrectContent(testState);
    }

    @Test(enabled = false)
    public void testT09_failureCorridorCorrectness(FailureCorridor failureCorridor){
        //TODO:
    }


}
