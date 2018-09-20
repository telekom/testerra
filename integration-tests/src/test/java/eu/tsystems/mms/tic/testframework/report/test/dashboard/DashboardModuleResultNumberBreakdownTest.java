package eu.tsystems.mms.tic.testframework.report.test.dashboard;

import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@FennecClassContext("View-Dashboard-ResultNumbers")
public class DashboardModuleResultNumberBreakdownTest extends AbstractTestDashboard {

    /**
     * This test tests whether the label 'No history data' is displayed, if no inherited methods are there. It does this
     * in the first report because it is the only one that does not provide inherited tests.
     * It runs once in the 1st report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT01_checksNoHistoryLabelIfHistoryNonExistant() throws Exception {
        final String expectedHistoryDataText = "No history data";
        DashboardPage dashboardPage = getDashboardPage(ReportDirectory.REPORT_DIRECTORY_1);
        GuiElement noHistoryData = dashboardPage.dashboardModuleTestResultNumberBreakdown.lastRunTestPercentageString;
        NonFunctionalAssert.assertEquals(noHistoryData.getText(), expectedHistoryDataText, "'" + expectedHistoryDataText + "' label is displayed, because there is no history in the first reportFilter.");
    }

    /**
     * This test tests the duration of the test report for the correct format.
     * It runs once in the 1st report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT02_checkTestDuration() throws Exception {
        DashboardPage dashboardPage = getDashboardPage(ReportDirectory.REPORT_DIRECTORY_1);
        String duration = dashboardPage.dashboardModuleTestResultNumberBreakdown.testDurationString.getText();
        Assert.assertTrue(duration.contains("s") && duration.contains("ms"), "Time Format of test duration is correct");
    }

    /**
     * This test tests the delta of the duration of the test report for the correct format.
     * It runs once in the 2nd report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2}, enabled=false)
    public void testT03_checkTestDurationDelta() throws Exception {
        DashboardPage dashboardPage = getDashboardPage(ReportDirectory.REPORT_DIRECTORY_2);
        String durationDelta = dashboardPage.dashboardModuleTestResultNumberBreakdown.testDurationDeltaString.getText();
        Assert.assertTrue(durationDelta.contains("s") && durationDelta.contains("ms"), "Time Format of test duration delta is correct");
    }

    /**
     * This test tests the start date/time of the report for the correct format.
     * It runs once in the 1st report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT04_checkTestStartDateAndTime() throws Exception {
        DashboardPage dashboardPage = getDashboardPage(ReportDirectory.REPORT_DIRECTORY_1);
        String elementString = dashboardPage.dashboardModuleTestResultNumberBreakdown.testStartTimeString.getText();
        String dateString;
        try {
            dateString = elementString.substring(6);
        } catch (Exception e) {
            throw new Exception("The start date and time are not displayed correctly");
        }

        Date date;
        Date now;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            now = calendar.getTime();
            dateFormat.format(now);
            dateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("CET")));
            date = dateFormat.parse(dateString);
        } catch (Exception e) {
            throw new Exception("The start date and time are not displayed in the correct date format");
        }

        Assert.assertTrue(date.before(now), "The start date is in the past. Now: " + now + ". Start date: " + date);

    }

    /**
     * This test tests the finish date/time of the test report for the correct format.
     * It runs once in the 1st report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT05_checkTestFinishDateAndTime() throws Exception {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()));
        String elementString = dashboardPage.dashboardModuleTestResultNumberBreakdown.testEndTimeString.getText();
        String dateString;
        try {
            dateString = elementString.substring(4);
        } catch (Exception e) {
            throw new Exception("The finish date and time are not displayed correctly");
        }

        Date date;
        Date now;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            now = calendar.getTime();
            dateFormat.format(now);
            dateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("CET")));
            date = dateFormat.parse(dateString);
        } catch (Exception e) {
            throw new Exception("The finish date and time are not displayed in the correct date format");
        }

        Assert.assertTrue(date.before(now), "The finish date is in the past: Now: " + now + ". Finish date: " + date);

    }


    /**
     * This test tests the passed percentages of the current and the last report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT06_testReportPercentages() throws Exception {
        TestReportOneNumbers testReportOneNumbers = new TestReportOneNumbers();
        assertReportPercentages(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()), testReportOneNumbers);
    }

    /**
     * This test tests the passed percentages of the current and the last report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    public void testT07_testReportPercentages() throws Exception {
        TestReportTwoNumbers testReportTwoNumbers = new TestReportTwoNumbers();
        assertReportPercentages(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.toString()), testReportTwoNumbers);
    }

    /**
     * This test tests the passed percentages of the current and the last report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER3})
    public void testT08_testReportPercentages() throws Exception {
        TestReportThreeNumbers testReportThreeNumbers = new TestReportThreeNumbers();
        assertReportPercentages(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_3.toString()), testReportThreeNumbers);
    }

    /**
     * This test tests the passed percentages of the current and the last report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER4})
    public void testT09_testReportPercentages() throws Exception {
        TestReportFourNumbers testReportFourNumbers = new TestReportFourNumbers();
        assertReportPercentages(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_4.toString()), testReportFourNumbers);
    }

    /**
     * This test tests the passed percentages of the current and the last reportFilter.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER5})
    public void testT10_testReportPercentages() throws Exception {
        TestReportFiveNumbers testReportFiveNumbers = new TestReportFiveNumbers();
        assertReportPercentages(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_5.toString()), testReportFiveNumbers);
    }

    /**
     * This  test tests the passed percentages of the current and the last reportFilter.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    public void testT11_testReportPercentages() throws Exception {
        TestReportSixNumbers testReportSixNumbers = new TestReportSixNumbers();
        assertReportPercentages(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.toString()), testReportSixNumbers);
    }

    /**
     * This test tests the numbers and deltas of the different status.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT12_checkTestNumbers() {
        TestReportOneNumbers testReportOneNumbers = new TestReportOneNumbers();
        assertTestNumbers(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()), false, false, true, false, testReportOneNumbers);
    }

    /**
     * This test tests the numbers and deltas of the different status.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    public void testT13_checkTestNumbers() {
        TestReportTwoNumbers testReportTwoNumbers = new TestReportTwoNumbers();
        assertTestNumbers(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.toString()), false, false, true, true, testReportTwoNumbers);
    }

    /**
     * This test tests the numbers and deltas of the different status.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER3})
    public void testT14_checkTestNumbers() {
        TestReportThreeNumbers testReportThreeNumbers = new TestReportThreeNumbers();
        assertTestNumbers(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_3.toString()), false, false, true, true, testReportThreeNumbers);
    }

    /**
     * This test tests the numbers and deltas of the different status.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER4})
    public void testT15_checkTestNumbers() {
        TestReportFourNumbers testReportFourNumbers = new TestReportFourNumbers();
        assertTestNumbers(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_4.toString()), false, false, true, false, testReportFourNumbers);
    }

    /**
     * This test tests the numbers and deltas of the different status.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER5})
    public void testT16_checkTestNumbers() {
        TestReportFiveNumbers testReportFiveNumbers = new TestReportFiveNumbers();
        assertTestNumbers(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_5.toString()), false, false, true, false, testReportFiveNumbers);
    }

    /**
     * This test tests the numbers and deltas of the different status.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    public void testT17_checkTestNumbers() {
        TestReportSixNumbers testReportSixNumbers = new TestReportSixNumbers();
        assertTestNumbers(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.toString()), false, false, false, false, testReportSixNumbers);
    }


    private void assertReportPercentages(String reportDirectory, TestNumberHelper testNumberHelper) throws Exception {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), reportDirectory);
        final String expectedPercentage = testNumberHelper.getPercentage();
        final String actualPercentage = dashboardPage.dashboardModuleTestResultNumberBreakdown.testPercentageString.getText();
        NonFunctionalAssert.assertTrue(actualPercentage.contains(expectedPercentage), "reportFilter percentage is correct. expected: " + expectedPercentage + " but found " + actualPercentage);

        if (PropertyManager.getBooleanProperty("isPlatform")) {

            final String expectedPercentageDelta = testNumberHelper.getPercentageDelta();
            final String actualPercentageDelta = dashboardPage.dashboardModuleTestResultNumberBreakdown.testPercentageDeltaString.getText();
            NonFunctionalAssert.assertTrue(actualPercentageDelta.contains(expectedPercentageDelta), "reportFilter percentage delta is correct. expected: " + expectedPercentageDelta + " but found " + actualPercentageDelta);
        }

    }

    private void assertTestNumbers(String report, boolean isDelta, boolean isInherited, boolean isSkipped, boolean isExpectedToFail, TestNumberHelper testNumberHelper) {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), report);
        dashboardPage.dashboardModuleTestResultNumberBreakdown.assertTestNumbers(isDelta, isInherited, isSkipped, isExpectedToFail, testNumberHelper);
    }

}
