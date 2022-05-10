package io.testerra.report.test;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
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

        TestStep.begin("Check whether to corresponding test-state-part of the pieChart refreshes the shown test classes");
        reportDashBoardPage.clickPieChartPart(testState);
        reportDashBoardPage.assertCorrectBarChartsAreDisplayed(testState);
    }

    @Test(dataProvider = "dataProviderForDifferentTestStates")
    public void testT02_showCorrectTestClassesWhenClickingOnNumbersChart(TestState testState) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        final ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));
        reportDashBoardPage.assertPageIsShown();

        TestStep.begin("Check whether each test status displays the correct test classes.");
        reportDashBoardPage.assertNumbersChartContainsTestState(testState);

        TestStep.begin("Check whether to corresponding test-state-part of the pieChart refreshes the shown test classes");
        reportDashBoardPage.clickNumberChartPart(testState);
        reportDashBoardPage.assertCorrectBarChartsAreDisplayed(testState);
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
    public void testT07_headlineOfReport() {
    }

    @Test
    public void testT08_reportTestDate() {
    }

    @Test
    public void testT09_reportTestNumbers() {
    }


}
