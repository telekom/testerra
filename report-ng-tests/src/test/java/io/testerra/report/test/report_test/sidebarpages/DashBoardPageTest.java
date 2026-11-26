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

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.tsystems.mms.tic.testframework.pageobjects.TestableUiElement;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.TestDataProvider;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportFailureAspectsPage;
import io.testerra.report.test.pages.utils.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class DashBoardPageTest extends AbstractReportTest {

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderForDifferentTestStatesWithAmounts")
    public void testT01_reportTestResult(Status status, int amount, String subtext) {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Check whether the count test results are correct");
        String result = reportDashBoardPage.getTestResultText(status);
        ASSERT.assertContains(result, String.format("%s %s", amount, status.title));
        if (subtext != null) {
            ASSERT.assertContains(result, subtext);
        }
    }

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderStatusClasses")
    public void testT02_showCorrectTestClassesWhenFilterTestResults(Status status, List<Map<String, Object>> testClasses) throws JsonProcessingException {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Check whether the corresponding test-state-part of the numbersChart refreshes the shown test classes");
        reportDashBoardPage = reportDashBoardPage.clickTestResult(status);
        List<Map<String, Object>> filteredClasses = reportDashBoardPage.getFilteredTestclasses();
        ASSERT.assertTrue(CollectionUtils.isEqualCollection(testClasses, filteredClasses),
                String.format("Testclasses chart for %s: expected %s, actual %s", status.title, testClasses, filteredClasses));
    }

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderForDashBoardTestStates")
    public void testT03_showSelectedStatusInBreakdown(Status status) {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Filter report to status.");
        reportDashBoardPage = reportDashBoardPage.clickTestResult(status);

        TestStep.begin("Check whether the displayed percentages are correct");
        String selectedStatus = reportDashBoardPage.getBreakpointSelection();
        ASSERT.assertContains(selectedStatus, status.title);
    }

    @Test
    public void testT04_reportDurationDisplayed() {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Check whether the start and end is displayed");
        reportDashBoardPage.assertDurationCard();

        TestStep.begin("Check whether the duration is displayed and correct");
        String testDuration = reportDashBoardPage.getTestDuration();
        ASSERT.assertTrue(
                DateTimeUtils.verifyDateTimeString(testDuration),
                "Test duration format");
        String testStarted = reportDashBoardPage.getTestStarted();
        ASSERT.assertTrue(
                DateTimeUtils.verifyDateTimeString(testStarted),
                "Started test time format");
        String testEnded = reportDashBoardPage.getTestEnded();
        ASSERT.assertTrue(
                DateTimeUtils.verifyDateTimeString(testEnded),
                "Ended test time format");
    }

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderFailureCorridorBounds")
    public void testT05_failureCorridorCorrectness(String failureCorridorType, long bound, int currentValue) {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Check displayed and compare failure corridor values to allowed bounds");
        TestableUiElement element = reportDashBoardPage.getFailureCorridorElement(failureCorridorType);
        String label = element.waitFor().text().getActual();
        ASSERT.assertContains(label, String.format("%s %s", currentValue, failureCorridorType), String.format("%s chip", failureCorridorType));
        ASSERT.assertContains(label, String.format("of %s", bound), "Allowed bounds");
    }

    @Test
    public void testT06_topFailureAspectsMajorLink() {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Check top failure aspects are displayed");
        reportDashBoardPage.assertTopFailureAspectsAreDisplayed();

        TestStep.begin("Check Major failure aspects link works correct");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.clickMajorFailureAspectsLink();
        final boolean hasFailedState = reportFailureAspectsPage.checkFailureStatusInRows(Status.FAILED);
        Assert.assertTrue(hasFailedState, "FailureAspectsPage contains State 'Failed'");
    }

    @Test
    public void testT07_topFailureAspectsMinorLink() {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Check top failure aspects are displayed");
        reportDashBoardPage.assertTopFailureAspectsAreDisplayed();

        TestStep.begin("Check Minor failure aspects link works correct");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.clickMinorFailureAspectsLink();
        final boolean hasFailedState = reportFailureAspectsPage.checkFailureStatusInRows(Status.FAILED);
        Assert.assertFalse(hasFailedState, "FailureAspectsPage contains State 'Failed'");

    }

    @Test
    public void testT08_topFailureAspectsOrderedList() {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Check top failure aspects are displayed");
        reportDashBoardPage.assertTopFailureAspectsAreDisplayed();

        TestStep.begin("Check order of listed failure aspects is correct");
        final List<String> dashboardList = reportDashBoardPage.getListOfFailureAspects();
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class);

        final List<String> failureAspectsList = reportFailureAspectsPage.getOrderListOfTopFailureAspects();
        Assert.assertEquals(dashboardList, failureAspectsList.subList(0, 3), "Shown Top failure aspects on 'ReportDashBoardPage' and 'FailureAspectsPage' are the same");
    }


    /***********/


//    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderForDashBoardTestStates")
//    public void testT08_barChartFilterHovering(Status status) {
//        TestStep.begin("Navigate to dashboard page.");
//        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());
//
//        TestStep.begin("Check whether each test status displays the correct test classes.");
//        reportDashBoardPage.assertNumbersChartContainsTestState(status);
//
//        TestStep.begin("Show corresponding bars to test state");
//        reportDashBoardPage = reportDashBoardPage.clickTestResult(status);
//
//        TestStep.begin("Check whether hovering above a bar in barchart let a popup appear with correct content");
//        reportDashBoardPage.assertPopupWhileHoveringWithCorrectContent(status);
//    }


    @Test
    public void testT13_historyChartLayout() {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnHistoryReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Check if the layout of the history chart is correct");
        reportDashBoardPage.assertHistoryChartMatchesScreenshot(3.0);
    }
}
