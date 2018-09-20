package eu.tsystems.mms.tic.testframework.report.test.dashboard;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.*;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.modules.DashboardModuleHistoryAreaChart;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.Test;

import java.util.List;

@FennecClassContext("View-Dashboard-HistoryChart")
public class DashboardModuleHistoryAreaChartTest {

    /**
     * This test tests whether hovering the points in time of the history chart displays the corrects numbers for inherited
     * reports.
     * It runs once in the 5th reportFilter.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER5})
    @Fails(description = "Hover over an chart element and display tooltip does not work with phantomjs/ghostdriver", intoReport = false)
    public void testT01_hoverHistoryChartEntriesAndCheckForHistoricTestNumbers() throws Exception {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_5.toString()));
        DashboardModuleHistoryAreaChart xetaReportHistoryAreaChartModule = dashboardPage.getHistoryAreaChartModule();
        List<GuiElement> segments = xetaReportHistoryAreaChartModule.getHistoryChartSegments();

        for (int i = 0; i < segments.size(); i++) {
            GuiElement segment = segments.get(i);
            xetaReportHistoryAreaChartModule.hoverHistorySegment(segment);

            int passed = Integer.parseInt(xetaReportHistoryAreaChartModule.historyHoverElementPassed.getText().substring(0, xetaReportHistoryAreaChartModule.historyHoverElementPassed.getText().indexOf(" ")));
            int passedInherited = Integer.parseInt(xetaReportHistoryAreaChartModule.historyHoverElementPassedInherited.getText().substring(0, xetaReportHistoryAreaChartModule.historyHoverElementPassedInherited.getText().indexOf(" ")));
            int passedMinor = Integer.parseInt(xetaReportHistoryAreaChartModule.historyHoverElementPassedMinor.getText().substring(0, xetaReportHistoryAreaChartModule.historyHoverElementPassedMinor.getText().indexOf(" ")));
            int failed = Integer.parseInt(xetaReportHistoryAreaChartModule.historyHoverElementFailed.getText().substring(0, xetaReportHistoryAreaChartModule.historyHoverElementFailed.getText().indexOf(" ")));
            int failedInherited = Integer.parseInt(xetaReportHistoryAreaChartModule.historyHoverElementFailedInherited.getText().substring(0, xetaReportHistoryAreaChartModule.historyHoverElementFailedInherited.getText().indexOf(" ")));
            int failedMinor = Integer.parseInt(xetaReportHistoryAreaChartModule.historyHoverElementFailedMinor.getText().substring(0, xetaReportHistoryAreaChartModule.historyHoverElementFailedMinor.getText().indexOf(" ")));
            int skipped = Integer.parseInt(xetaReportHistoryAreaChartModule.historyHoverElementSkipped.getText().substring(0, xetaReportHistoryAreaChartModule.historyHoverElementSkipped.getText().indexOf(" ")));
            int skippedInherited = Integer.parseInt(xetaReportHistoryAreaChartModule.historyHoverElementSkippedInherited.getText().substring(0, xetaReportHistoryAreaChartModule.historyHoverElementSkippedInherited.getText().indexOf(" ")));

            //Output Strings for the Asserts
            String StrOutPassed = "Passed number for %s report history hover element is correct.";
            String StrOutPassedInherited = "Passed inherited number for %s report history hover element is correct.";
            String StrOutPassedMinor = "Passed with minor number for %s report history hover element is correct.";
            String StrOutFailed = "Failed number for %s report history hover element is correct.";
            String StrOutFailedInherited = "Failed inherited number for %s report history hover element is correct.";
            String StrOutFailedMinor = "Failed with minor number for %s report history hover element is correct.";
            String StrOutSkipped = "Skipped number for %s report history hover element is correct.";
            String StrOutSkippedInherited = "Skipped inherited number for %s report history hover element is correct.";

            switch (i) {
                case 0:
                    TestReportFiveNumbers testReportFiveNumbers = new TestReportFiveNumbers();
                    AssertCollector.assertEquals(passed, testReportFiveNumbers.getPassed(), String.format(StrOutPassed, "fifth"));
                    AssertCollector.assertEquals(passedInherited, testReportFiveNumbers.getPassedInherited(), String.format(StrOutPassedInherited, "fifth"));
                    AssertCollector.assertEquals(passedMinor, testReportFiveNumbers.getPassedMinor(), String.format(StrOutPassedMinor, "fifth"));
                    AssertCollector.assertEquals(failed, testReportFiveNumbers.getFailed(), String.format(StrOutFailed, "fifth"));
                    AssertCollector.assertEquals(failedInherited, testReportFiveNumbers.getFailedInherited(), String.format(StrOutFailedInherited, "first"));
                    AssertCollector.assertEquals(failedMinor, testReportFiveNumbers.getFailedMinor(), String.format(StrOutFailedMinor, "fifth"));
                    AssertCollector.assertEquals(skipped, testReportFiveNumbers.getSkipped(), String.format(StrOutSkipped, "fifth"));
                    AssertCollector.assertEquals(skippedInherited, testReportFiveNumbers.getSkippedInherited(), String.format(StrOutSkippedInherited, "fifth"));
                    break;
                case 1:
                    TestReportFourNumbers testReportFourNumbers = new TestReportFourNumbers();
                    AssertCollector.assertEquals(passed, testReportFourNumbers.getPassed(), String.format(StrOutPassed, "fourth"));
                    AssertCollector.assertEquals(passedInherited, testReportFourNumbers.getPassedInherited(), String.format(StrOutPassedInherited, "fourth"));
                    AssertCollector.assertEquals(passedMinor, testReportFourNumbers.getPassedMinor(), String.format(StrOutPassedMinor, "fourth"));
                    AssertCollector.assertEquals(failed, testReportFourNumbers.getFailed(), String.format(StrOutFailed, "fourth"));
                    AssertCollector.assertEquals(failedInherited, testReportFourNumbers.getFailedInherited(), String.format(StrOutFailedInherited, "fourth"));
                    AssertCollector.assertEquals(failedMinor, testReportFourNumbers.getFailedMinor(), String.format(StrOutFailedMinor, "fourth"));
                    AssertCollector.assertEquals(skipped, testReportFourNumbers.getSkipped(), String.format(StrOutSkipped, "fourth"));
                    AssertCollector.assertEquals(skippedInherited, testReportFourNumbers.getSkippedInherited(), String.format(StrOutSkippedInherited, "fourth"));
                    break;
                case 2:
                    TestReportThreeNumbers testReportThreeNumbers = new TestReportThreeNumbers();
                    AssertCollector.assertEquals(passed, testReportThreeNumbers.getPassed(), String.format(StrOutPassed, "third"));
                    AssertCollector.assertEquals(passedInherited, testReportThreeNumbers.getPassedInherited(), String.format(StrOutPassedInherited, "third"));
                    AssertCollector.assertEquals(passedMinor, testReportThreeNumbers.getPassedMinor(), String.format(StrOutPassedMinor, "third"));
                    AssertCollector.assertEquals(failed, testReportThreeNumbers.getFailed(), String.format(StrOutFailed, "third"));
                    AssertCollector.assertEquals(failedInherited, testReportThreeNumbers.getFailedInherited(), String.format(StrOutFailedInherited, "third"));
                    AssertCollector.assertEquals(failedMinor, testReportThreeNumbers.getFailedMinor(), String.format(StrOutFailedMinor, "third"));
                    AssertCollector.assertEquals(skipped, testReportThreeNumbers.getSkipped(), String.format(StrOutSkipped, "third"));
                    AssertCollector.assertEquals(skippedInherited, testReportThreeNumbers.getSkippedInherited(), String.format(StrOutSkippedInherited, "third"));
                    break;
                case 3:
                    TestReportTwoNumbers testReportTwoNumbers = new TestReportTwoNumbers();
                    AssertCollector.assertEquals(passed, testReportTwoNumbers.getPassed(), String.format(StrOutPassed, "second"));
                    AssertCollector.assertEquals(passedInherited, testReportTwoNumbers.getPassedInherited(), String.format(StrOutPassedInherited, "second"));
                    AssertCollector.assertEquals(passedMinor, testReportTwoNumbers.getPassedMinor(), String.format(StrOutPassedMinor, "second"));
                    AssertCollector.assertEquals(failed, testReportTwoNumbers.getFailed(), String.format(StrOutFailed, "second"));
                    AssertCollector.assertEquals(failedInherited, testReportTwoNumbers.getFailedInherited(), String.format(StrOutFailedInherited, "fourth"));
                    AssertCollector.assertEquals(failedMinor, testReportTwoNumbers.getFailedMinor(), String.format(StrOutFailedMinor, "second"));
                    AssertCollector.assertEquals(skipped, testReportTwoNumbers.getSkipped(), String.format(StrOutSkipped, "second"));
                    AssertCollector.assertEquals(skippedInherited, testReportTwoNumbers.getSkippedInherited(), String.format(StrOutSkippedInherited, "fourth"));
                    break;
                case 4:
                    TestReportOneNumbers testReportOneNumbers = new TestReportOneNumbers();
                    AssertCollector.assertEquals(passed, testReportOneNumbers.getPassed(), String.format(StrOutPassed, "first"));
                    AssertCollector.assertEquals(passedInherited, 0, "Passed inherited number for first report history hover element is correct.");
                    AssertCollector.assertEquals(passedMinor, testReportOneNumbers.getPassedMinor(), String.format(StrOutPassedMinor, "first"));
                    AssertCollector.assertEquals(failed, testReportOneNumbers.getFailed(), String.format(StrOutFailed, "first"));
                    AssertCollector.assertEquals(failedInherited, 0, "Failed inherited number for first report history hover element is correct.");
                    AssertCollector.assertEquals(failedMinor, testReportOneNumbers.getFailedMinor(), String.format(StrOutFailedMinor, "first"));
                    AssertCollector.assertEquals(skipped, testReportOneNumbers.getSkipped(), String.format(StrOutSkipped, "first"));
                    AssertCollector.assertEquals(skippedInherited, 0, "Skipped inherited number for first report history hover element is correct.");
                    break;
                default:
            }
        }
    }
}
