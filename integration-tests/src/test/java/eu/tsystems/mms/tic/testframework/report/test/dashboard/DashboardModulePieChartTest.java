package eu.tsystems.mms.tic.testframework.report.test.dashboard;

import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.general.AbstractTest;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriverException;
import org.testng.annotations.Test;

@FennecClassContext("View-Dashboard-PieChart")
public class DashboardModulePieChartTest extends AbstractTest{

    /**
     * This test tests whether the 'last run pie chart' displays the correct pie segment.
     * It runs once for every test status in report 3. 8 times in total.
     */
    @Test(dataProviderClass = TestResultHelper.class, dataProvider = "getAllTestResults", groups = {SystemTestsGroup.SYSTEMTESTSFILTER3}, enabled=false)
    public void testT01_testLastRunPieChart(TestResultHelper.TestResult testResult) throws Exception {
        try {
            DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_3.toString()));
            dashboardPage.dashboardModuleTestResultPieChart.clickLastRunPieSegmentForTestResult(testResult);  //on Click nothing happens with the pie
        } catch (WebDriverException e) {
            throw new AssertionError(testResult.toString() + " pie segment does not exist in third reportFilter last run pie, but it should.");
        }
    }
}
