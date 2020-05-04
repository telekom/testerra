package eu.tsystems.mms.tic.testframework.report.test.dashboard;

import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.report.pageobjetcs.abstracts.AbstractTestReportNumbers;
import eu.tsystems.mms.tic.testframework.report.general.AbstractTestDashboard;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.pageobjetcs.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.XrayTest;
import eu.tsystems.mms.tic.testframework.report.model.TestNumberHelper;
import eu.tsystems.mms.tic.testframework.report.model.TestReportFiveNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestReportFourNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestReportOneNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestReportSixNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestReportThreeNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestReportTwoNumbers;
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

@TestContext(name = "View-Dashboard-ResultNumbers")
public class DashboardModuleResultNumberBreakdownTest extends AbstractTestDashboard {

    /**
     * This test tests the duration of the test report for the correct format.
     * It runs once in the 1st report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    @XrayTest(key = "TAP2DEV-855")
    public void testT01_checkTestDuration(){
        DashboardPage dashboardPage = getDashboardPage(ReportDirectory.REPORT_DIRECTORY_1);
        String duration = dashboardPage.dashboardModuleTestResultNumberBreakdown.testDurationString.getText();
        Assert.assertTrue(duration.contains("s") && duration.contains("ms"), "Time Format of test duration is correct");
    }

    /**
     * This test tests the start date/time of the report for the correct format.
     * It runs once in the 1st report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    @XrayTest(key = "TAP2DEV-856")
    public void testT02_checkTestStartDateAndTime() throws Exception {
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
    @XrayTest(key="TAP2DEV-857")
    public void testT03_checkTestFinishDateAndTime() throws Exception {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
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
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, dataProvider = "testResultNumbers")
    @XrayTest(key = "TAP2DEV-858")
    public void testT04_testReportPercentages(ReportDirectory report, AbstractTestReportNumbers numbers) throws Exception {
        DashboardPage dashboardPage = getDashboardPage(report);
        final String expectedPercentage = numbers.getPercentage();
        final String actualPercentage = dashboardPage.dashboardModuleTestResultNumberBreakdown.testPercentageString.getText();
        NonFunctionalAssert.assertTrue(actualPercentage.contains(expectedPercentage), "reportFilter percentage is correct. expected: " + expectedPercentage + " but found " + actualPercentage);
    }

    /**
     * This test tests the numbers and deltas of the different status.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, dataProvider = "testResultNumbers")
    @XrayTest(key = "TAP2DEV-859")
    public void testT05_checkTestNumbers(ReportDirectory report, AbstractTestReportNumbers numbers) {
        DashboardPage dashboardPage = getDashboardPage(report);
        dashboardPage.dashboardModuleTestResultNumberBreakdown.assertTestNumbers(numbers);
    }
}