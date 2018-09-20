package eu.tsystems.mms.tic.testframework.report.test.dashboard;

import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.general.AbstractTestDashboard;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.*;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.Assert;
import org.testng.annotations.Test;

@FennecClassContext("View-Dashboard-FailureCorridor")
public class DashboardModuleFailureCorridorTest extends AbstractTestDashboard {

    /**
     * This test checks the color of the label that indicates whether the failure corridor is matched or not
     * in report1
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT01_checkFailureCorridorMatchingForColor() {
        assertFailureCorridorMatchingForColor(ReportDirectory.REPORT_DIRECTORY_1, "red");
    }

    /**
     * This test checks the color of the label that indicates whether the failure corridor is matched or not
     * in report2
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    public void testT02_checkFailureCorridorMatchingForColor() {
        assertFailureCorridorMatchingForColor(ReportDirectory.REPORT_DIRECTORY_2, "red");
    }

    /**
     * This test checks the color of the label that indicates whether the failure corridor is matched or not
     * in report3
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER3})
    public void testT03_checkFailureCorridorMatchingForColor() {
        assertFailureCorridorMatchingForColor(ReportDirectory.REPORT_DIRECTORY_3, "red");
    }

    /**
     * This test checks the color of the label that indicates whether the failure corridor is matched or not
     * in report4
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER4})
    public void testT04_checkFailureCorridorMatchingForColor() {
        assertFailureCorridorMatchingForColor(ReportDirectory.REPORT_DIRECTORY_4, "red");
    }

    /**
     * This test checks the color of the label that indicates whether the failure corridor is matched or not
     * in report5
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER5})
    public void testT05_checkFailureCorridorMatchingForColor() {
        assertFailureCorridorMatchingForColor(ReportDirectory.REPORT_DIRECTORY_5, "red");
    }

    /**
     * This test checks the color of the label that indicates whether the failure corridor is matched or not
     * in report6
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    public void testT06_checkFailureCorridorMatchingForColor() {
        assertFailureCorridorMatchingForColor(ReportDirectory.REPORT_DIRECTORY_6, "green");
    }

    /**
     * This test checks the numbers of the failure corridor in report1
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT07_checkFailureCorridorNumbers() {
        TestReportOneNumbers testReportOneNumbers = new TestReportOneNumbers();
        checkFailureCorridorNumbers(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()), testReportOneNumbers);
    }

    /**
     * This test checks the numbers of the failure corridor in report2
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    public void testT08_checkFailureCorridorNumbers() {
        TestReportTwoNumbers testReportTwoNumbers = new TestReportTwoNumbers();
        checkFailureCorridorNumbers(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.toString()), testReportTwoNumbers);
    }

    /**
     * This test checks the numbers of the failure corridor in report3
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER3})
    public void testT09_checkFailureCorridorNumbers() {
        TestReportThreeNumbers testReportThreeNumbers = new TestReportThreeNumbers();
        checkFailureCorridorNumbers(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_3.toString()), testReportThreeNumbers);
    }

    /**
     * This test checks the numbers of the failure corridor in report4
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER4})
    public void testT10_checkFailureCorridorNumbers() {
        TestReportFourNumbers testReportFourNumbers = new TestReportFourNumbers();
        checkFailureCorridorNumbers(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_4.toString()), testReportFourNumbers);
    }

    /**
     * This test checks the numbers of the failure corridor in report5
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER5})
    public void testT11_checkFailureCorridorNumbers() {
        TestReportFiveNumbers testReportFiveNumbers = new TestReportFiveNumbers();
        checkFailureCorridorNumbers(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_5.toString()), testReportFiveNumbers);
    }

    /**
     * This test checks the numbers of the failure corridor in report6
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    public void testT12_checkFailureCorridorNumbers() {
        TestReportSixNumbers testReportSixNumbers = new TestReportSixNumbers();
        checkFailureCorridorNumbers(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.toString()), testReportSixNumbers);
    }


    private void assertFailureCorridorMatchingForColor(ReportDirectory reportDirectory, String style) {
        DashboardPage dashboardPage = getDashboardPage(reportDirectory);
        GuiElement corridorMatch = dashboardPage.dashboardModuleFailureCorridor.failureCorridorDescription;
        Assert.assertTrue(corridorMatch.getAttribute("style").contains(style), "The failure corridor matches. The style in the reportFilter " + reportDirectory + " is " + style + ".");
    }

    private void checkFailureCorridorNumbers(String reportDirectory, TestNumberHelper testNumberHelper) {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), reportDirectory);

        String highActualClass = dashboardPage.dashboardModuleFailureCorridor.highCorridorActualButton.getAttribute("class");
        String midActualClass = dashboardPage.dashboardModuleFailureCorridor.midCorridorActualButton.getAttribute("class");
        String lowActualClass = dashboardPage.dashboardModuleFailureCorridor.lowCorridorActualButton.getAttribute("class");
        int highActual = Integer.parseInt(dashboardPage.dashboardModuleFailureCorridor.highCorridorActualButton.getText().replaceAll(" ", ""));
        int midActual = Integer.parseInt(dashboardPage.dashboardModuleFailureCorridor.midCorridorActualButton.getText().replaceAll(" ", ""));
        int lowActual = Integer.parseInt(dashboardPage.dashboardModuleFailureCorridor.lowCorridorActualButton.getText().replaceAll(" ", ""));
        int highLimitActual = Integer.parseInt(dashboardPage.dashboardModuleFailureCorridor.highCorridorLimitButton.getText().replaceAll(" ", ""));
        int midLimitActual = Integer.parseInt(dashboardPage.dashboardModuleFailureCorridor.midCorridorLimitButton.getText().replaceAll(" ", ""));
        int lowLimitActual = Integer.parseInt(dashboardPage.dashboardModuleFailureCorridor.lowCorridorLimitButton.getText().replaceAll(" ", ""));

        String actualMessage = "The reached number of tests with %s priority is correct.";
        String colorMessage = "The color of the number of tests with %s priority is correct.";
        String limitMessage = "The limit of tests with %s priority is correct.";
        String high = "high";
        String mid = "mid";
        String low = "low";

        AssertCollector.assertEquals(highActual, testNumberHelper.getHighCorridorActual(), String.format(actualMessage, high));
        AssertCollector.assertEquals(midActual, testNumberHelper.getMidCorridorActual(), String.format(actualMessage, mid));
        AssertCollector.assertEquals(lowActual, testNumberHelper.getLowCorridorActual(), String.format(actualMessage, low));
        AssertCollector.assertEquals(highActualClass, testNumberHelper.getHighMatched(), String.format(colorMessage, high));
        AssertCollector.assertEquals(midActualClass, testNumberHelper.getMidMatched(), String.format(colorMessage, mid));
        AssertCollector.assertEquals(lowActualClass, testNumberHelper.getLowMatched(), String.format(colorMessage, low));
        AssertCollector.assertEquals(highLimitActual, testNumberHelper.getHighCorridorLimit(), String.format(limitMessage, high));
        AssertCollector.assertEquals(midLimitActual, testNumberHelper.getMidCorridorLimit(), String.format(limitMessage, mid));
        AssertCollector.assertEquals(lowLimitActual, testNumberHelper.getLowCorridorLimit(), String.format(limitMessage, low));
    }

}
