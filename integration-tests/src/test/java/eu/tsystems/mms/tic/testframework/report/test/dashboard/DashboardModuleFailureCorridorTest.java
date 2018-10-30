package eu.tsystems.mms.tic.testframework.report.test.dashboard;

import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractTestReportNumbers;
import eu.tsystems.mms.tic.testframework.report.general.AbstractTestDashboard;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.*;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@FennecClassContext("View-Dashboard-FailureCorridor")
public class DashboardModuleFailureCorridorTest extends AbstractTestDashboard {

    @DataProvider(parallel = true)
    public Object[][] testResultNumbers(){
        Object[][] result = new Object[][]{
                new Object[]{ReportDirectory.REPORT_DIRECTORY_1,new TestReportOneNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_2,new TestReportTwoNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_3,new TestReportThreeNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_4,new TestReportFourNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_5,new TestReportFiveNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_6,new TestReportSixNumbers()}
        };
        return result;
    }

    /**
     * This test checks the color of the label that indicates whether the failure corridor is matched or not.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, dataProvider = "testResultNumbers")
    public void testT01_checkFailureCorridorMatchingForColor(ReportDirectory report, AbstractTestReportNumbers numbers) {
        DashboardPage dashboardPage = getDashboardPage(report);
        GuiElement corridorMatch = dashboardPage.dashboardModuleFailureCorridor.failureCorridorDescription;
        String style = numbers.getFailureCorridorMatched();
        Assert.assertTrue(corridorMatch.getAttribute("style").contains(style), "The failure corridor matches. The style in the reportFilter " + report + " is " + style + ".");
    }


    /**
     * This test checks the numbers of the failure corridor.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1},dataProvider = "testResultNumbers")
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
