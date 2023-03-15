/*
 * Testerra
 *
 * (C) 2022, Marc Dietrich, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package io.testerra.report.test.report_test.sidebarpages;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;

import org.testng.annotations.Test;

import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.TestDataProvider;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportLogsPage;
import io.testerra.report.test.pages.utils.LogLevel;
import io.testerra.report.test.pages.utils.TestData;

public class ReportLogsPageTest extends AbstractReportTest {

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderForLogLevel")
    public void testT01_checkLogLevelFilter(LogLevel logLevel) {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport();

        TestStep.begin("Navigate to logs page.");
        ReportLogsPage reportLogsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.LOGS, ReportLogsPage.class);

        TestStep.begin("Check whether the logLevel-select works correctly");
        reportLogsPage = reportLogsPage.selectLogLevel(logLevel);
        reportLogsPage.assertLogReportContainsCorrectLogLevel(logLevel);
    }

    @Test(enabled = false)
    @Fails(description = "filter does not work correct (issues with brackets, spaces and not even applied to whole log report)")
    public void testT02_filterForWholeLogLines() {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport();

        TestStep.begin("Navigate to logs page.");
        ReportLogsPage reportLogsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.LOGS, ReportLogsPage.class);

        TestStep.begin("Check whether the search filter works correctly");
        //fails because filter does not work correct
        // -> filter does not work when there are leading or trailing spaces
        // -> filter does not work when there are brackets in the filter request
        // (-> filter is only applied on the right side of the logReport behind the "-"- sign in each line

        //TODO: search constraints: bug or feature?
        // --> set issue on github (as bug)

        // reportLogsPage.assertLogReportIsCorrectWhenSearchingForDifferentLogLines();
    }

    /**
     * Basic LogPageTest doing one search and check marked line without any scrolling
     * can be replaced when scrolling is robust
     */
    @Test()
    public void testT03_filterForMethodContentBasic() {
        final String methodName = "test_Passed";
        final String methodClass = "GeneratePassedStatusInTesterraReportTest";
        final Status methodStatus = Status.PASSED;

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport();

        TestStep.begin("Navigate to logs page.");
        ReportLogsPage reportLogsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.LOGS, ReportLogsPage.class);

        TestStep.begin("Check method name is contained in log report");
        reportLogsPage = reportLogsPage.search(methodName);
        reportLogsPage.assertMarkedLogLinesContainTextNoScroll(methodName);

        TestStep.begin("Check method class is contained in log report");
        reportLogsPage = reportLogsPage.search(methodClass);
        reportLogsPage.assertMarkedLogLinesContainTextNoScroll(methodClass);

        TestStep.begin("Check method name is contained in log report");
        reportLogsPage = reportLogsPage.search(methodStatus.title);
        reportLogsPage.assertMarkedLogLinesContainTextNoScroll(methodStatus.title);
    }

    /**
     * Basic LogPageTest doing filter and one search and check marked line without any scrolling
     * can be replaced when scrolling is robust
     */
    @Test()
    public void testT04_filterForMethodContentWithLogLevelBasic() {
        final String methodeName = "testAssertCollector";
        final String methodeClass = "GeneratePassedStatusInTesterraReportTest";
        final Status methodStatus = Status.FAILED;

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport();

        TestStep.begin("Navigate to logs page.");
        ReportLogsPage reportLogsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.LOGS, ReportLogsPage.class);

        for (final LogLevel logLevel : LogLevel.values()) {

            TestStep.begin("Check whether the logLevel-select works correctly");
            reportLogsPage = reportLogsPage.selectLogLevel(logLevel);
            reportLogsPage.assertLogReportContainsCorrectLogLevel(logLevel);

            TestStep.begin("Check methode name is contained in log report");
            reportLogsPage = reportLogsPage.search(methodeName);
            reportLogsPage.assertMarkedLogLinesContainTextNoScroll(methodeName);

            TestStep.begin("Check methode class is contained in log report");
            reportLogsPage = reportLogsPage.search(methodeClass);
            reportLogsPage.assertMarkedLogLinesContainTextNoScroll(methodeClass);

            TestStep.begin("Check methode name is contained in log report");
            reportLogsPage = reportLogsPage.search(methodStatus.title);
            reportLogsPage.assertMarkedLogLinesContainTextNoScroll(methodStatus.title);
        }
    }

    /**
     * LogView in report is buggy and scrolling the page in headless mode via WebDriver Action
     * produces StaleElement Exceptions due to flickering of Page/constant DOM reloading
     * @param data
     */
    @Test(enabled = false, dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderForPreTestMethods_Classes_States")
    public void testT05_filterForMethodContent(TestData data) {
        String methodeName = data.getMethod();
        String methodeClass = data.getMethodClass();
        Status methodStatus = data.getStatus1();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport();

        TestStep.begin("Navigate to logs page.");
        ReportLogsPage reportLogsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.LOGS, ReportLogsPage.class);

        TestStep.begin("Check methode name is contained in log report");
        reportLogsPage = reportLogsPage.search(methodeName);
        reportLogsPage.assertMarkedLogLinesContainText(methodeName);

        TestStep.begin("Check methode class is contained in log report");
        reportLogsPage = reportLogsPage.search(methodeClass);
        reportLogsPage.assertMarkedLogLinesContainText(methodeClass);

        TestStep.begin("Check methode name is contained in log report");
        reportLogsPage = reportLogsPage.search(methodStatus.title);
        reportLogsPage.assertMarkedLogLinesContainText(methodStatus.title);
    }

    /**
     * LogView in report is buggy and scrolling the page in headless mode via WebDriver Action
     * produces StaleElement Exceptions due to flickering of Page/constant DOM reloading
     * @param data
     */
    @Test(enabled = false, dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderForPreTestMethods_Classes_States")
    public void testT06_filterForMethodContentWithLogLevel(TestData data) {
        String methodeName = data.getMethod();
        String methodeClass = data.getMethodClass();
        Status methodStatus = data.getStatus1();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport();

        TestStep.begin("Navigate to logs page.");
        ReportLogsPage reportLogsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.LOGS, ReportLogsPage.class);

        for (LogLevel logLevel : LogLevel.values()) {

            TestStep.begin("Check whether the logLevel-select works correctly");
            reportLogsPage = reportLogsPage.selectLogLevel(logLevel);
            reportLogsPage.assertLogReportContainsCorrectLogLevel(logLevel);

            TestStep.begin("Check methode name is contained in log report");
            reportLogsPage = reportLogsPage.search(methodeName);
            reportLogsPage.assertMarkedLogLinesContainText(methodeName);

            TestStep.begin("Check methode class is contained in log report");
            reportLogsPage = reportLogsPage.search(methodeClass);
            reportLogsPage.assertMarkedLogLinesContainText(methodeClass);

            TestStep.begin("Check methode name is contained in log report");
            reportLogsPage = reportLogsPage.search(methodStatus.title);
            reportLogsPage.assertMarkedLogLinesContainText(methodStatus.title);
        }
    }

}
