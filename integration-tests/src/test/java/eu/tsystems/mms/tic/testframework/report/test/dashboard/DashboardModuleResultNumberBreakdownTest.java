/*
 * Testerra
 *
 * (C) 2020, Alex Rockstroh, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package eu.tsystems.mms.tic.testframework.report.test.dashboard;

import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts.AbstractTestReportNumbers;
import eu.tsystems.mms.tic.testframework.report.general.AbstractTestDashboard;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
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

@TestContext(name = "View-Dashboard-ResultNumbers")
public class DashboardModuleResultNumberBreakdownTest extends AbstractTestDashboard {

    /**
     * This test tests the duration of the test report for the correct format.
     * It runs once in the 1st report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #855
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
    // Test case #856
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
    // Test case #857
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
    // Test case #858
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
    // Test case #859
    public void testT05_checkTestNumbers(ReportDirectory report, AbstractTestReportNumbers numbers) {
        DashboardPage dashboardPage = getDashboardPage(report);
        dashboardPage.dashboardModuleTestResultNumberBreakdown.assertTestNumbers(numbers);
    }
}