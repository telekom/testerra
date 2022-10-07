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

import eu.tsystems.mms.tic.testframework.common.DefaultPropertyManager;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportFailureAspectsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import io.testerra.report.test.pages.utils.DateTimeUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider.WEB_DRIVER_MANAGER;

public class ReportDashBoardPageTest extends AbstractReportTest {

    @Test(dataProvider = "dataProviderForDashBoardTestStates")
    public void testT01_showCorrectTestClassesWhenClickingOnPieChart(Status status) {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Check whether each test status displays the correct test classes.");
        reportDashBoardPage.assertPieChartContainsTestState(status);

        TestStep.begin("Check whether the corresponding test-state-part of the pieChart refreshes the shown test classes");
        reportDashBoardPage = reportDashBoardPage.clickPieChartPart(status);
        reportDashBoardPage.assertCorrectBarChartsAreDisplayed();
    }

    @Test(dataProvider = "dataProviderForDashBoardTestStates")
    public void testT02_showCorrectTestClassesWhenClickingOnNumbersChart(Status status) {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Check whether each test status displays the correct test classes.");
        reportDashBoardPage.assertNumbersChartContainsTestState(status);

        TestStep.begin("Check whether the corresponding test-state-part of the numbersChart refreshes the shown test classes");
        reportDashBoardPage = reportDashBoardPage.clickNumberChartPart(status);
        reportDashBoardPage.assertCorrectBarChartsAreDisplayed();
    }

    @Test(dataProvider = "dataProviderForNavigationBetweenDifferentPages")
    public void testT03_navigationTowardsDifferentPagesDashBoardPage(ReportSidebarPageType type, Class<AbstractReportPage> pageClass) {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Navigate to '" + type.name() + "' page, check whether page was reached and navigate back to 'Dashboard' page.");
        reportDashBoardPage.gotoToReportPage(type, pageClass);
        reportDashBoardPage.verifyReportPage(type);
    }

    @Test
    public void testT04_reportDurationDisplayed() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Check whether the start is displayed");
        reportDashBoardPage.assertStartTimeIsDisplayed();

        TestStep.begin("Check whether the end is displayed");
        reportDashBoardPage.assertEndedTimeIsDisplayed();

        TestStep.begin("Check whether the duration is displayed and correct");
        final String testDuration = reportDashBoardPage.getTestDuration();
        final boolean dateFormatIsCorrect = DateTimeUtils.verifyDateTimeString(testDuration);
        Assert.assertTrue(dateFormatIsCorrect, String.format("Test Duration '%s' has correct format", testDuration));
    }

    @Test(dataProvider = "dataProviderForDashBoardTestStates")
    public void testT05_barChartLinksToFilteredTestsPage(Status status) {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Check whether each test status displays the correct test classes.");
        reportDashBoardPage.assertNumbersChartContainsTestState(status);

        TestStep.begin("Check whether clicking on barchart bars navigates to tests page with correct filter.");
        reportDashBoardPage = reportDashBoardPage.clickNumberChartPart(status);
        ReportTestsPage reportTestsPage = reportDashBoardPage.navigateToFilteredTestPageByClickingBarChartBar();
        reportTestsPage.assertCorrectTestStatus(status);
    }

    @Test
    public void testT06_barChartLength() {
        final double threshold = 0.01;  //1% (random chosen value, but some threshold is needed when asserting with double)
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Check barchart is shown and iterate through all bars to check correct length");
        reportDashBoardPage.assertBarChartIsDisplayed();
        reportDashBoardPage.assertCorrectBarsLength(threshold);
    }

    @Test(dataProvider = "dataProviderForDifferentTestStatesWithAmounts")
    public void testT07_reportPercentages(int amount, Status status) {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Check whether each test status displays the correct test classes.");
        reportDashBoardPage.assertPieChartContainsTestState(status);

        TestStep.begin("Check whether the displayed percentages are correct");
        reportDashBoardPage.assertPieChartPercentages(amount, status);
    }

    @Test(dataProvider = "dataProviderForDashBoardTestStates")
    public void testT08_barChartFilterHovering(Status status) {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Check whether each test status displays the correct test classes.");
        reportDashBoardPage.assertNumbersChartContainsTestState(status);

        TestStep.begin("Show corresponding bars to test state");
        reportDashBoardPage = reportDashBoardPage.clickNumberChartPart(status);

        TestStep.begin("Check whether hovering above a bar in barchart let a popup appear with correct content");
        reportDashBoardPage.assertPopupWhileHoveringWithCorrectContent(status);
    }

    @Test(dataProvider = "dataProviderFailureCorridorBounds")
    public void testT09_failureCorridorCorrectness(String failureCorridorType, long bound, int currentValue) {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Check displayed and compare failure corridor values to allowed bounds");
        reportDashBoardPage.assertFailureCorridorValue(failureCorridorType, currentValue);
        reportDashBoardPage.assertFailureCorridorValuesAreCorrectClassified(failureCorridorType, bound);
    }

    @Test
    public void testT10_topFailureAspectsMajorLink() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Check top failure aspects are displayed");
        reportDashBoardPage.assertTopFailureAspectsAreDisplayed();

        TestStep.begin("Check Major failure aspects link works correct");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.clickMajorFailureAspectsLink();
        final boolean hasFailedState = reportFailureAspectsPage.getContainsFailedStateExistenceInEachRow();
        Assert.assertTrue(hasFailedState, "FailureAspectsPage contains State 'Failed'");
    }

    @Test
    public void testT11_topFailureAspectsMinorLink() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Check top failure aspects are displayed");
        reportDashBoardPage.assertTopFailureAspectsAreDisplayed();

        TestStep.begin("Check Minor failure aspects link works correct");
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.clickMinorFailureAspectsLink();
        final boolean hasFailedState = reportFailureAspectsPage.getContainsFailedStateExistenceInEachRow();
        Assert.assertFalse(hasFailedState, "FailureAspectsPage contains State 'Failed'");

    }

    @Test
    public void testT12_topFailureAspectsOrderedList() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));

        TestStep.begin("Check top failure aspects are displayed");
        reportDashBoardPage.assertTopFailureAspectsAreDisplayed();

        TestStep.begin("Check order of listed failure aspects is correct");
        final List<String> topFailureAspectsOnReportDashboardPage = reportDashBoardPage.getOrderListOfTopFailureAspects();
        ReportFailureAspectsPage reportFailureAspectsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class);

        final List<String> topFailureAspectsOnReportFailureAspectsPage = reportFailureAspectsPage.getOrderListOfTopFailureAspects();
        Assert.assertEquals(topFailureAspectsOnReportDashboardPage, topFailureAspectsOnReportFailureAspectsPage.subList(0, 3), "Shown Top failure aspects on 'ReportDashBoardPage' and 'FailureAspectsPage' are the same");
    }


}
