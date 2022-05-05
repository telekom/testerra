package io.testerra.report.test;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.helper.TestState;
import io.testerra.report.test.pages.ReportDashBoardPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ReportDashBoardPageTest extends AbstractReportTest {

    @DataProvider
    public Object[][] dataProviderForDifferentTestStates() {
        return new Object[][]{{TestState.Passed}, {TestState.Failed}, {TestState.ExpectedFailed}, {TestState.Skipped}};
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

}
