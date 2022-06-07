package io.testerra.report.test.sideBarPagesTests;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.ReportPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportFailureAspectsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportLogsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportThreadsPage;
import io.testerra.report.test.pages.utils.DateTimeUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

public class ReportDashBoardPageTest extends AbstractReportTest {

    @DataProvider
    public Object[][] dataProviderForDifferentTestStates() {
        return new Object[][]{{Status.PASSED}, {Status.FAILED}, {Status.FAILED_EXPECTED}, {Status.SKIPPED}};
    }

    @DataProvider
    public static Object[][] dataProviderForDifferentTestStatesWithAmounts() {
        return new Object[][]{
                {5, Status.FAILED},
                {3, Status.FAILED_EXPECTED},
                {4, Status.SKIPPED},
                {5, Status.PASSED}
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

    @DataProvider
    public Object[][] dataProviderFailureCorridorBounds() {
        PropertyManager.loadProperties("report-ng-tests/src/test/resources/test.properties");
        return new Object[][]
                {{"High", PropertyManager.getIntProperty("tt.failure.corridor.allowed.failed.tests.high")},
                        {"Mid", PropertyManager.getIntProperty("tt.failure.corridor.allowed.failed.tests.mid")},
                        {"Low", PropertyManager.getIntProperty("tt.failure.corridor.allowed.failed.tests.low")}};
    }

    @Test(dataProvider = "dataProviderForDifferentTestStates")
    public void testT01_showCorrectTestClassesWhenClickingOnPieChart(Status status) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Check whether each test status displays the correct test classes.");
        reportDashBoardPage.assertPieChartContainsTestState(status);

        TestStep.begin("Check whether the corresponding test-state-part of the pieChart refreshes the shown test classes");
        reportDashBoardPage = reportDashBoardPage.clickPieChartPart(status);
        reportDashBoardPage.assertCorrectBarChartsAreDisplayed();
    }

    @Test(dataProvider = "dataProviderForDifferentTestStates")
    public void testT02_showCorrectTestClassesWhenClickingOnNumbersChart(Status status) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Check whether each test status displays the correct test classes.");
        reportDashBoardPage.assertNumbersChartContainsTestState(status);

        TestStep.begin("Check whether the corresponding test-state-part of the numbersChart refreshes the shown test classes");
        reportDashBoardPage = reportDashBoardPage.clickNumberChartPart(status);
        reportDashBoardPage.assertCorrectBarChartsAreDisplayed();
    }

    @Test(dataProvider = "dataProviderForNavigationBetweenDifferentPages")
    public void testT03_navigationTowardsDifferentPagesDashBoardPage(ReportPageType type, Class<AbstractReportPage> pageClass) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to '" + type.name() + "' page, check whether page was reached and navigate back to 'Dashboard' page.");
        reportDashBoardPage.gotoToReportPage(type, pageClass);
        reportDashBoardPage.verifyReportPage(type);
    }

    @Test
    public void testT04_reportDurationDisplayed() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Check whether the start is displayed");
        reportDashBoardPage.assertStartTimeIsDisplayed();

        TestStep.begin("Check whether the end is displayed");
        reportDashBoardPage.assertEndedTimeIsDisplayed();

        TestStep.begin("Check whether the duration is displayed and correct");
        final String testDuration = reportDashBoardPage.getTestDuration();
        final boolean dateFormatIsCorrect = DateTimeUtils.verifyDateTimeString(testDuration);
        Assert.assertTrue(dateFormatIsCorrect, String.format("Test Duration '%s' has correct format", testDuration));
    }

    @Test(dataProvider = "dataProviderForDifferentTestStates")
    public void testT05_barChartLinksToFilteredTestsPage(Status status) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Check whether each test status displays the correct test classes.");
        reportDashBoardPage.assertNumbersChartContainsTestState(status);

        TestStep.begin("Check whether clicking on barchart bars navigates to tests page with correct filter.");
        reportDashBoardPage = reportDashBoardPage.clickNumberChartPart(status);
        ReportTestsPage reportTestsPage = reportDashBoardPage.navigateToFilteredTestPageByClickingBarChartBar();
        reportTestsPage.assertPageIsShown();
        reportTestsPage.assertCorrectTestStatus(status);
    }

    @Test
    public void testT06_barChartLength() {
        WebDriver driver = WebDriverManager.getWebDriver();
        final double threshold = 0.01;  //1% (random chosen value, but some threshold is needed when asserting with double)

        TestStep.begin("Navigate to dashboard page.");
        final ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Check barchart is shown and iterate through all bars to check correct length");
        reportDashBoardPage.assertBarChartIsDisplayed();
        reportDashBoardPage.assertCorrectBarsLength(threshold);
    }

    @Test(dataProvider = "dataProviderForDifferentTestStatesWithAmounts")
    public void testT07_reportPercentages(int amount, Status status) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        final ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Check whether each test status displays the correct test classes.");
        reportDashBoardPage.assertPieChartContainsTestState(status);

        TestStep.begin("Check whether the displayed percentages are correct");
        reportDashBoardPage.assertPieChartPercentages(amount, status);
    }

    @Test(dataProvider = "dataProviderForDifferentTestStates")
    public void testT08_barChartFilterHovering(Status status) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Check whether each test status displays the correct test classes.");
        reportDashBoardPage.assertNumbersChartContainsTestState(status);

        TestStep.begin("Show corresponding bars to test state");
        reportDashBoardPage = reportDashBoardPage.clickNumberChartPart(status);

        TestStep.begin("Check whether hovering above a bar in barchart let a popup appear with correct content");
        reportDashBoardPage.assertPopupWhileHoveringWithCorrectContent(status);
    }

    @Test(dataProvider = "dataProviderFailureCorridorBounds")
    public void testT09_failureCorridorCorrectness(String failureCorridorType, int bound) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        final ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Check displayed and compare failure corridor values to allowed bounds");
        reportDashBoardPage.assertFailureCorridorIsDisplayed(failureCorridorType);
        reportDashBoardPage.assertFailureCorridorValuesAreCorrectClassified(failureCorridorType, bound);
    }

    @Test
    public void testT10_topFailureAspectsMajorLink() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        final ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver,
                PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Check top failure aspects are displayed");
        reportDashBoardPage.assertTopFailureAspectsAreDisplayed();

        TestStep.begin("Check Major failure aspects link works correct");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.clickMajorFailureAspectsLink();
        final boolean hasFailedState = reportFailureAspectsPage.getFailedStateExistence();
        Assert.assertTrue(hasFailedState, "FailureAspectsPage contains State 'Failed'");
    }

    @Test
    public void testT11_topFailureAspectsMinorLink() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        final ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Check top failure aspects are displayed");
        reportDashBoardPage.assertTopFailureAspectsAreDisplayed();

        TestStep.begin("Check Minor failure aspects link works correct");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.clickMinorFailureAspectsLink();
        final boolean hasFailedState = reportFailureAspectsPage.getFailedStateExistence();
        Assert.assertFalse(hasFailedState, "FailureAspectsPage contains State 'Failed'");

    }

    @Test
    public void testT12_topFailureAspectsOrderedList() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        final ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Check top failure aspects are displayed");
        reportDashBoardPage.assertTopFailureAspectsAreDisplayed();

        TestStep.begin("Check order of listed failure aspects is correct");
        List<String> topFailureAspectsReportDashboardPage = reportDashBoardPage.getOrderListOfTopFailureAspects();
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.gotoToReportPage(ReportPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class);
        List<String> topFailureAspectsReportFailureAspectsPage = reportFailureAspectsPage.getOrderListOfTopFailureAspects();
        Assert.assertEquals(topFailureAspectsReportDashboardPage, topFailureAspectsReportFailureAspectsPage.subList(0, 3));
    }


}
