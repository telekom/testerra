package eu.tsystems.mms.tic.testframework.report.test.dashboard;

import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractTestReportNumbers;
import eu.tsystems.mms.tic.testframework.report.general.AbstractTestDashboard;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.*;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
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
    public void testT02_checkTestDuration() throws Exception {
        DashboardPage dashboardPage = getDashboardPage(ReportDirectory.REPORT_DIRECTORY_1);
        String duration = dashboardPage.dashboardModuleTestResultNumberBreakdown.testDurationString.getText();
        Assert.assertTrue(duration.contains("s") && duration.contains("ms"), "Time Format of test duration is correct");
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
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT06_testReportPercentages() throws Exception {
        TestReportOneNumbers testReportOneNumbers = new TestReportOneNumbers();
        assertReportPercentages(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), testReportOneNumbers);
    }

    /**
     * This test tests the passed percentages of the current and the last report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    public void testT07_testReportPercentages() throws Exception {
        TestReportTwoNumbers testReportTwoNumbers = new TestReportTwoNumbers();
        assertReportPercentages(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.getReportDirectory()), testReportTwoNumbers);
    }

    /**
     * This test tests the passed percentages of the current and the last report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER3})
    public void testT08_testReportPercentages() throws Exception {
        TestReportThreeNumbers testReportThreeNumbers = new TestReportThreeNumbers();
        assertReportPercentages(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_3.getReportDirectory()), testReportThreeNumbers);
    }

    /**
     * This test tests the passed percentages of the current and the last report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER4})
    public void testT09_testReportPercentages() throws Exception {
        TestReportFourNumbers testReportFourNumbers = new TestReportFourNumbers();
        assertReportPercentages(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_4.getReportDirectory()), testReportFourNumbers);
    }

    /**
     * This test tests the passed percentages of the current and the last reportFilter.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER5})
    public void testT10_testReportPercentages() throws Exception {
        TestReportFiveNumbers testReportFiveNumbers = new TestReportFiveNumbers();
        assertReportPercentages(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_5.getReportDirectory()), testReportFiveNumbers);
    }

    /**
     * This  test tests the passed percentages of the current and the last reportFilter.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    public void testT11_testReportPercentages() throws Exception {
        TestReportSixNumbers testReportSixNumbers = new TestReportSixNumbers();
        assertReportPercentages(PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.getReportDirectory()), testReportSixNumbers);
    }

    /**
     * This test tests the numbers and deltas of the different status.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1},dataProvider = "testResultNumbers")
    public void testT12_checkTestNumbers(String report, AbstractTestReportNumbers numbers) {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(report));
        dashboardPage.dashboardModuleTestResultNumberBreakdown.assertTestNumbers(numbers);
    }

    @DataProvider(parallel = true)
    public Object[][] testResultNumbers(){
        Object[][] result = new Object[][]{
                new Object[]{ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory(),new TestReportOneNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_2.getReportDirectory(),new TestReportTwoNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_3.getReportDirectory(),new TestReportThreeNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_4.getReportDirectory(),new TestReportFourNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_5.getReportDirectory(),new TestReportFiveNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_6.getReportDirectory(),new TestReportSixNumbers()}
        };
        return result;
    }


    private void assertReportPercentages(String reportDirectory, TestNumberHelper testNumberHelper) throws Exception {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), reportDirectory);
        final String expectedPercentage = testNumberHelper.getPercentage();
        final String actualPercentage = dashboardPage.dashboardModuleTestResultNumberBreakdown.testPercentageString.getText();
        NonFunctionalAssert.assertTrue(actualPercentage.contains(expectedPercentage), "reportFilter percentage is correct. expected: " + expectedPercentage + " but found " + actualPercentage);
    }
}
