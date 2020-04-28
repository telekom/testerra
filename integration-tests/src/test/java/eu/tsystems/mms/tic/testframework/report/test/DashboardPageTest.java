package eu.tsystems.mms.tic.testframework.report.test;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.general.AbstractTestDashboard;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.XrayTest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static eu.tsystems.mms.tic.testframework.report.model.TestResultHelper.TestResult;

@TestContext(name = "View-Dashboard-General")
public class DashboardPageTest extends AbstractTestDashboard {

    /**
     * Clicks the desired pie segment and tests the provided bars of the bar chart for correct colors.
     * It runs once for every test status in report 3. 8 times in total.
     */
    @Fails(ticketString = "TAP2DEV-667")
    @Test(dataProviderClass = TestResultHelper.class, dataProvider = "getAllTestResults", groups = {SystemTestsGroup.SYSTEMTESTSFILTER3})
    @XrayTest(key = "TAP2DEV-846")
    public void testT01_clickActualPieAndCheckBarColors(TestResult testResult) throws Exception {
        //TODO try out other locator or using java script
        try {
            DashboardPage dashboardPage = getDashboardPage(ReportDirectory.REPORT_DIRECTORY_3);
            dashboardPage = dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(testResult);
            List<GuiElement> bars = dashboardPage.dashboardModuleClassBarChart.getCurrentBars();
            for (GuiElement bar : bars) {
                String color = bar.getAttribute("fill");
                AssertCollector.assertEquals(color, testResult.getColor(), "All bars in the second report " + testResult.toString() + " bar chart for actual run are correct.");
            }
        } catch (WebDriverException e) {                 // PieSliece does not exist
            Assert.fail("The Pie-Slice in the actual Pie-Chart does not exist: " + e.getMessage());
        }
    }

    /**
     * Clicks the desired 'number' and tests the provided bars of the bar chart for correct colors.
     * It runs once for every test status in report 4. 8 times in total.
     */
    @Test(dataProviderClass = TestResultHelper.class, dataProvider = "getAllTestResults", groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    @Fails(ticketString = "TAP2DEV-667")
    @XrayTest(key = "TAP2DEV-847")
    public void testT02_clickTestNumberAndCheckBarColors(TestResult testResults) throws Exception {
        DashboardPage dashboardPage = getDashboardPage(ReportDirectory.REPORT_DIRECTORY_1);
        if (dashboardPage.dashboardModuleTestResultNumberBreakdown.isNumberDisplayed(testResults)) {
            dashboardPage = dashboardPage.dashboardModuleTestResultNumberBreakdown.clickNumberForTestResult(testResults);
            //TODO sagu try to read out canvas
            //List<GuiElement> bars = dashboardPage.dashboardModuleClassBarChart.getCurrentBars();
            //for (GuiElement bar : bars) {
             //   String color = bar.getAttribute("fill");
               // AssertCollector.assertEquals(color, testResults.getColor(), "The " + testResults.toString() + " bar chart in the fourth report has the correct color.");
            //}
        }

    }

    /**
     * Tests the desired pie chart segment and its displayed bars for the correct test method names.
     * It runs once for every test status in the second report.
     */
    @Fails(ticketString = "TAP2DEV-667")
    @Test(dataProviderClass = TestResultHelper.class, dataProvider = "getAllTestResults", groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    @XrayTest(key="TAP2DEV-848")
    public void testT03_checkListedMethodsAfterClickingPieAndBar(TestResult testResults) throws Exception {
        //TODO try out other locator or using java script
        DashboardPage dashboardPage = getDashboardPage(ReportDirectory.REPORT_DIRECTORY_2);
        dashboardPage = dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(testResults);
        List<GuiElement> bars = dashboardPage.dashboardModuleClassBarChart.getCurrentBars();
        for (GuiElement bar : bars) {
            dashboardPage = dashboardPage.click(bar);

            String label = null;
            for (GuiElement methoddetail : dashboardPage.getMethodChartModule().getCurrentMethods()) {
                label = methoddetail.getText();
                label = label.toUpperCase();
                if (testResults == TestResult.FAILEDMINOR) {
                    AssertCollector.assertTrue(label.contains("FAILED") && label.contains("MINOR"), "All methods listed in the " + testResults.toString() + "'s are correct");
                } else {
                    AssertCollector.assertTrue(label.contains(testResults.toString()), "All methods listed in the " + testResults.toString() + "'s are correct");
                }
            }
        }
    }

    /**
     * Tests if the tt. logo is displayed.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    @Fails(ticketString = "TAP2DEV-623", description = "https://jira.t-systems-mms.eu/browse/TAP2DEV-623: Testerra Logo Decision outstanding.")
    @XrayTest(key = "TAP2DEV-849")
    public void testT04_checksIfTesterraLogoIsDisplayed() {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        Assert.assertTrue(dashboardPage.testerraLogo.isDisplayed(), "Testerra logo is displayed on dashboard page.");
    }

    /**
     * Tests a failed method for the 'info' symbol which indicates that there is a screenshot for the test.
     * It runs once in the 1st report.
     */
    @Fails(ticketString = "TAP2DEV-667")
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    @XrayTest(key = "TAP2DEV-850")
    public void testT05_checksScreenshotForFailedMethod() throws Exception {
        //TODO try out other locator or using java script
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(TestResult.FAILEDMINOR);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(0));
        GuiElement method1 = null;
        for (GuiElement method : dashboardPage.getMethodChartModule().getCurrentMethods()) {
            if (method.getText().contains("test_FailedInheritedMinor2"))
                method1 = method;
        }
        if (method1 != null)
            Assert.assertEquals(method1.getSubElement(By.xpath("./../../..//a[contains(@href, 'showFotorama')]")).isDisplayed(), true, "Button for method to look at Screenshot is displayed.");

    }

    /**
     * Tests whether a passing under test that is annotated with @Fails causes the dashboard page to indicate it
     */
    @Fails(ticketString = "TAP2DEV-667")
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER4})
    @XrayTest(key="TAP2DEV-851")
    public void testT06_checkDashboardIndicationThatPassedTestIsAnnotatedWithFails() throws Exception {
        //TODO try out other locator or using java script
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_4.getReportDirectory()));
        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(TestResult.PASSED);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(0));
        AssertCollector.assertTrue(dashboardPage.dashboardModuleInformationCorridor.repairedFailsIndicationButton.isDisplayed(), "The dashboard page does not show the indicator, that there is a redundant @Fails annotation.");
        AssertCollector.assertTrue(dashboardPage.getMethodChartModule().methodChartRepairedFailsIndication.isDisplayed(), "The test_TestStatePassed2 method does not indicate in the method chart, that it is annotated with a redundant @Fails.");
    }
}
