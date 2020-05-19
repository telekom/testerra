package eu.tsystems.mms.tic.testframework.report.test.dashboard;

import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.pageobjetcs.abstracts.AbstractTestReportNumbers;
import eu.tsystems.mms.tic.testframework.report.general.AbstractTestDashboard;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.XrayTest;
import eu.tsystems.mms.tic.testframework.report.pageobjetcs.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.model.TestReportFiveNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestReportFourNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestReportOneNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestReportSixNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestReportThreeNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestReportTwoNumbers;
import org.testng.Assert;
import org.testng.annotations.Test;

@TestContext(name = "View-Dashboard-FailureCorridor")
public class DashboardModuleFailureCorridorTest extends AbstractTestDashboard {

    /**
     * This test checks the color of the label that indicates whether the failure corridor is matched or not.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, dataProvider = "testResultNumbers")
    // Test case #852
    public void testT01_checkFailureCorridorMatchingForColor(ReportDirectory report, AbstractTestReportNumbers numbers) {
        DashboardPage dashboardPage = getDashboardPage(report);
        GuiElement corridorMatch = dashboardPage.dashboardModuleFailureCorridor.failureCorridorDescription;
        String style = numbers.getFailureCorridorMatched();
        Assert.assertTrue(corridorMatch.getAttribute("style").contains(style), "The failure corridor matches. The style in the reportFilter " + report + " is " + style + ".");
    }


    /**
     * This test checks the numbers of the failure corridor.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, dataProvider = "testResultNumbers")
    // Test case #424
    public void testT02_checkFailureCorridorNumbers(ReportDirectory report, AbstractTestReportNumbers numbers) {
        DashboardPage dashboardPage = getDashboardPage(report);

        String highActualClass = dashboardPage.dashboardModuleFailureCorridor.highCorridorActualButton.getAttribute("class");
        int highActual = Integer.parseInt(dashboardPage.dashboardModuleFailureCorridor.highCorridorActualButton.getText().replaceAll(" ", ""));
        int highLimitActual = Integer.parseInt(dashboardPage.dashboardModuleFailureCorridor.highCorridorLimitButton.getText().replaceAll(" ", ""));
        AssertCollector.assertEquals(highActual, numbers.getHighCorridorActual(), "Expected another number for current high corridor.");
        AssertCollector.assertEquals(highLimitActual, numbers.getHighCorridorLimit(), "Expected another number for the high corridor limit.");
        AssertCollector.assertEquals(highActualClass, numbers.getHighMatched(), "Expecteted another style for the high corridor.");

        String midActualClass = dashboardPage.dashboardModuleFailureCorridor.midCorridorActualButton.getAttribute("class");
        int midActual = Integer.parseInt(dashboardPage.dashboardModuleFailureCorridor.midCorridorActualButton.getText().replaceAll(" ", ""));
        int midLimitActual = Integer.parseInt(dashboardPage.dashboardModuleFailureCorridor.midCorridorLimitButton.getText().replaceAll(" ", ""));
        AssertCollector.assertEquals(midActual, numbers.getMidCorridorActual(), "Expected another number for current mid corridor.");
        AssertCollector.assertEquals(midLimitActual, numbers.getMidCorridorLimit(), "Expected another number for the mid corridor limit.");
        AssertCollector.assertEquals(midActualClass, numbers.getMidMatched(), "Expecteted another style for the mid corridor.");


        String lowActualClass = dashboardPage.dashboardModuleFailureCorridor.lowCorridorActualButton.getAttribute("class");
        int lowActual = Integer.parseInt(dashboardPage.dashboardModuleFailureCorridor.lowCorridorActualButton.getText().replaceAll(" ", ""));
        int lowLimitActual = Integer.parseInt(dashboardPage.dashboardModuleFailureCorridor.lowCorridorLimitButton.getText().replaceAll(" ", ""));
        AssertCollector.assertEquals(lowActual, numbers.getLowCorridorActual(), "Expected another number for current low corridor");
        AssertCollector.assertEquals(lowLimitActual, numbers.getLowCorridorLimit(), "Expected another number for the low corridor limit.");
        AssertCollector.assertEquals(lowActualClass, numbers.getLowMatched(), "Expecteted another style for the low corridor.");
    }

}