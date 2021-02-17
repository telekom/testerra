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
package eu.tsystems.mms.tic.testframework.report.test;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.annotations.TestClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.general.AbstractTestDashboard;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.Test;
import static eu.tsystems.mms.tic.testframework.report.model.TestResultHelper.TestResult;

@TestClassContext(name = "View-Dashboard-General")
public class DashboardPageTest extends AbstractTestDashboard {

    /**
     * Clicks the desired pie segment and tests the provided bars of the bar chart for correct colors.
     * It runs once for every test status in report 3. 8 times in total.
     */
    @Fails(ticketString = "667")
    @Test(dataProviderClass = TestResultHelper.class, dataProvider = "getAllTestResults", groups = {SystemTestsGroup.SYSTEMTESTSFILTER3})
    // Test case #846
    public void testT01_clickActualPieAndCheckBarColors(TestResult testResult) throws Exception {
        //TODO try out other locator or using java script
        try {
            DashboardPage dashboardPage = getDashboardPage(ReportDirectory.REPORT_DIRECTORY_3);
            dashboardPage = dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(testResult);
            List<GuiElement> bars = dashboardPage.dashboardModuleClassBarChart.getCurrentBars();
            for (GuiElement bar : bars) {
                String color = bar.getAttribute("fill");
                AssertCollector.assertEquals(color, testResult.getColor(), "All bars in the second report " + testResult.toString() + " bar chart for actual run are correct.");
            }
        } catch (WebDriverException e) {                 // PieSliece does not exist
            Assert.fail("The Pie-Slice in the actual Pie-Chart does not exist: " + e.getMessage());
        }
    }

    /**
     * Clicks the desired 'number' and tests the provided bars of the bar chart for correct colors.
     * It runs once for every test status in report 4. 8 times in total.
     */
    @Test(dataProviderClass = TestResultHelper.class, dataProvider = "getAllTestResults", groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    @Fails(ticketString = "667")
    // Test case #847
    public void testT02_clickTestNumberAndCheckBarColors(TestResult testResults) throws Exception {
        DashboardPage dashboardPage = getDashboardPage(ReportDirectory.REPORT_DIRECTORY_1);
        if (dashboardPage.dashboardModuleTestResultNumberBreakdown.isNumberDisplayed(testResults)) {
            dashboardPage = dashboardPage.dashboardModuleTestResultNumberBreakdown.clickNumberForTestResult(testResults);
            //TODO sagu try to read out canvas
            List<GuiElement> bars = dashboardPage.dashboardModuleClassBarChart.getCurrentBars();
            for (GuiElement bar : bars) {
                String color = bar.getAttribute("fill");
                AssertCollector.assertEquals(color, testResults.getColor(), "The " + testResults.toString() + " bar chart in the fourth report has the correct color.");
            }
        }
    }

    /**
     * Tests the desired pie chart.
     * It runs once for every test status in the second report.
     */
    @Fails(ticketString = "667")
    @Test(dataProviderClass = TestResultHelper.class, dataProvider = "getAllTestResults", groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    // Test case #848
    public void testT03_checkListedMethodsAfterClickingPieAndBar(TestResult testResults) throws Exception {
        //TODO try out other locator or using java script
        DashboardPage dashboardPage = getDashboardPage(ReportDirectory.REPORT_DIRECTORY_2);
        dashboardPage = dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(testResults);
        List<GuiElement> bars = dashboardPage.dashboardModuleClassBarChart.getCurrentBars();
        for (GuiElement bar : bars) {
            dashboardPage = dashboardPage.click(bar);

            String label = null;
            for (GuiElement methoddetail : dashboardPage.getMethodChartModule().getCurrentMethods()) {
                label = methoddetail.getText();
                label = label.toUpperCase();
                if (testResults == TestResult.FAILEDMINOR) {
                    AssertCollector.assertTrue(label.contains("FAILED") && label.contains("MINOR"), "All methods listed in the " + testResults.toString() + "'s are correct");
                } else {
                    AssertCollector.assertTrue(label.contains(testResults.toString()), "All methods listed in the " + testResults.toString() + "'s are correct");
                }
            }
        }
    }

    /**
     * Tests if the tt. logo is displayed.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #849
    public void testT04_checksIfTesterraLogoIsDisplayed() {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WEB_DRIVER_MANAGER.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        Assert.assertTrue(dashboardPage.testerraLogo.isDisplayed(), "Testerra logo is displayed on dashboard page.");
    }

    /**
     * Tests a failed method for the 'info' symbol which indicates that there is a screenshot for the test.
     * It runs once in the 1st report.
     */
    @Fails(ticketString = "667")
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #850
    public void testT05_checksScreenshotForFailedMethod() throws Exception {
        //TODO try out other locator or using java script
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WEB_DRIVER_MANAGER.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(TestResult.FAILEDMINOR);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(0));
        GuiElement method1 = null;
        for (GuiElement method : dashboardPage.getMethodChartModule().getCurrentMethods()) {
            if (method.getText().contains("test_FailedInheritedMinor2")) {
                method1 = method;
            }
        }
        if (method1 != null) {
            Assert.assertEquals(method1.getSubElement(By.xpath("./../../..//a[contains(@href, 'showFotorama')]")).isDisplayed(), true, "Button for method to look at Screenshot is displayed.");
        }

    }

    /**
     * Tests whether a passing under test that is annotated with @Fails causes the dashboard page to indicate it
     */
    @Fails(ticketString = "667")
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER4})
    // Test case #851
    public void testT06_checkDashboardIndicationThatPassedTestIsAnnotatedWithFails() throws Exception {
        //TODO try out other locator or using java script
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WEB_DRIVER_MANAGER.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_4.getReportDirectory()));
        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(TestResult.PASSED);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(0));
        AssertCollector.assertTrue(dashboardPage.dashboardModuleInformationCorridor.repairedFailsIndicationButton.isDisplayed(), "The dashboard page does not show the indicator, that there is a redundant @Fails annotation.");
        AssertCollector.assertTrue(dashboardPage.getMethodChartModule().methodChartRepairedFailsIndication.isDisplayed(), "The test_TestStatePassed2 method does not indicate in the method chart, that it is annotated with a redundant @Fails.");
    }
}
