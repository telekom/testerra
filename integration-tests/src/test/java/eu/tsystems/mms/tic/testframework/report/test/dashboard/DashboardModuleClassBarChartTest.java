package eu.tsystems.mms.tic.testframework.report.test.dashboard;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.general.AbstractTestDashboard;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestAnnotations;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestExecutionFilter;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.Test;

@FennecClassContext("View-Dashboard-Module BarChart")
public class DashboardModuleClassBarChartTest extends AbstractTestDashboard {

    /**
     * Checks whether the inherited test results are displayed
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER3}, enabled=false)
    public void testT01_checkExecutionFilterInherited() {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_3.toString()));
        GuiElement testundertestExecutionFilterElement = dashboardPage.dashboardModuleClassBarChart.getBarChartElementByClassName(ReportTestUnderTestExecutionFilter.class.getSimpleName());
        testundertestExecutionFilterElement.click();
        dashboardPage.getResultTableHeaderForTestResult(TestResultHelper.TestResult.PASSEDINHERITED).asserts().assertIsDisplayed();
        dashboardPage.getResultTableHeaderForTestResult(TestResultHelper.TestResult.FAILEDINHERITED).asserts().assertIsDisplayed();
        dashboardPage.getResultTableHeaderForTestResult(TestResultHelper.TestResult.SKIPPEDINHERITED).asserts().assertIsDisplayed();

        dashboardPage.getResultTableHeaderForTestResult(TestResultHelper.TestResult.PASSED).asserts().assertIsNotDisplayed();
        dashboardPage.getResultTableHeaderForTestResult(TestResultHelper.TestResult.FAILED).asserts().assertIsNotDisplayed();
        dashboardPage.getResultTableHeaderForTestResult(TestResultHelper.TestResult.SKIPPED).asserts().assertIsNotDisplayed();
    }

    /**
     * Checks whether all test results are covered by the execution filter
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    @Fails(ticketString = "XETA-679")
    public void testT02_checkExecutionFilter() {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.toString()));
        GuiElement testundertestExecutionFilterElement = dashboardPage.dashboardModuleClassBarChart.getBarChartElementByClassName(ReportTestUnderTestExecutionFilter.class.getSimpleName());

        testundertestExecutionFilterElement.click();

        for (TestResultHelper.TestResult testResult: TestResultHelper.TestResult.values()){
            dashboardPage.getResultTableHeaderForTestResult(testResult).assertCollector().assertIsDisplayed();
        }
    }

    /**
     * Checks whether the annotation marks are displayed correctly
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    @Fails(ticketString = "XETA-679")
    public void testT03_checkReportAnnotations() {
        final String annotationMethod = "testAllMarkers";
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()));
        dashboardPage.dashboardModuleClassBarChart.getBarChartElementByClassName(ReportTestUnderTestAnnotations.class.getSimpleName()).click();
        dashboardPage.assertAllAnnotationMarksAreDisplayed(annotationMethod);
    }
}
