package eu.tsystems.mms.tic.testframework.report.test.dashboard;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.general.AbstractTestDashboard;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjetcs.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestAnnotations;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestExecutionFilter;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.XrayTest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.Test;

@TestContext(name = "View-Dashboard-Module BarChart")
public class DashboardModuleClassBarChartTest extends AbstractTestDashboard {

    /**
     * Checks whether all test results are covered by the execution filter
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    @Fails(ticketString = "TAP2DEV-668")
    @XrayTest(key = "TAP2DEV-853")
    public void testT01_checkExecutionFilter() {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.getReportDirectory()));
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
    @Fails(ticketString = "TAP2DEV-668")
    @XrayTest(key = "TAP2DEV-854")
    public void testT02_checkReportAnnotations() {
        final String annotationMethod = "testAllMarkers";
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        dashboardPage.dashboardModuleClassBarChart.getBarChartElementByClassName(ReportTestUnderTestAnnotations.class.getSimpleName()).click();
        dashboardPage.assertAllAnnotationMarksAreDisplayed(annotationMethod);
    }
}
