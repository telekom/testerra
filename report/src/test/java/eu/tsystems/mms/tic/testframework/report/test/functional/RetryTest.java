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
package eu.tsystems.mms.tic.testframework.report.test.functional;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.annotations.TestClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.AbstractReportTest;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ClassesDetailsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodDetailsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.DashboardModuleMethodChart;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.testng.annotations.Test;

@TestClassContext(name = "Functional-Retry")
public class RetryTest extends AbstractReportTest {

    /**
     * Tests whether the data provider produced the expected number of test methods
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    @Fails(ticketString = "668")
    // Test case #791
    public void testT01_checkRetryDataProviderRunsTheExpectedNumberOfTestMethods() {
        final String retryClassName = "ReportTestUnderTestRetry";

        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.getReportDirectory()));
        GuiElement retryBarChartMethodLabel = dashboardPage.dashboardModuleClassBarChart.getBarChartElementByClassName(retryClassName);
        retryBarChartMethodLabel.click();
        DashboardModuleMethodChart dashboardModuleMethodChart = dashboardPage.getMethodChartModule();
        AssertCollector.assertEquals(dashboardModuleMethodChart.getNumberMethodsInMethodChartForTestResult(TestResultHelper.TestResult.FAILED), 7, "Number of failed methods in Class " + retryClassName + " is correct:");
        AssertCollector.assertEquals(dashboardModuleMethodChart.getNumberMethodsInMethodChartForTestResult(TestResultHelper.TestResult.PASSED), 1, "Number of passed methods in Class " + retryClassName + " is correct:");

    }

    /**
     * Tests whether only the second test run is displayed if there was a retry
     *
     * @throws Exception
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    @Fails(ticketString = "667")
    // Test case #792
    public void testT02_checkFailedRetriedTestsOnlySecondRetryIsDisplayed() throws Exception {

        List<String> testNames = new ArrayList<>();
        testNames.add("test_TestNoRetryTriggerInASubcause");
        testNames.add("test_TestRetryMessageTrigger (2/2)");
        testNames.add("test_TestRetryTriggerInASubcause (2/2)");
        testNames.add("test_TestRetryExceptionTrigger (2/2)");
        testNames.add("test_DataProviderTest (2/2)");

        List<String> tagNames = new ArrayList<>();
        tagNames.add("(1) Retry1");
        tagNames.add("(1) Retry2");
        tagNames.add("(1) Retry3");


        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.getReportDirectory()));
        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(TestResultHelper.TestResult.FAILED);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(0));

        List<GuiElement> currentMethods = dashboardPage.getMethodChartModule().getCurrentMethods();

        for (GuiElement currentMethod : currentMethods) {

            String testMethodName = currentMethod.getText();
            testMethodName = testMethodName.substring(0, testMethodName.indexOf(" Suite: "));
            AssertCollector.assertTrue(testNames.contains(testMethodName), "The test method '" + testMethodName + "' is an expected retry test method.");

            GuiElement methodRetryTag = currentMethod.getSubElement(By.xpath("((./../font)[2])"));

            if (methodRetryTag.isDisplayed()) {

                String testMethodTagName = methodRetryTag.getText();
                AssertCollector.assertTrue(tagNames.contains(testMethodTagName), "The test method '" + testMethodName + "' has an invalid tag.");
            }
        }
    }

    /**
     * Tests whether a successful retried test is treated as passed test
     *
     * @throws Exception
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    @Fails(ticketString = "667")
    // Test case #793
    public void testT03_checkSuccessfulRetriedTestsAreDisplayed() throws Exception {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.getReportDirectory()));
        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(TestResultHelper.TestResult.PASSED);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(1));
        Assert.assertTrue(dashboardPage.getMethodChartModule().methodChartSuccessfulRetried.getText().contains("test_ExceptionRetryTest"), "The retried test 'test_ExceptionRetryTest' does not exist in the passed method chart.");
    }

    /**
     * Tests the number of retries of a retried test method
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    @Fails(ticketString = "668")
    // Test case #794
    public void testT04_checkRetriedTestsWithDataProviderHaveTheExpectedNumberOfRetries() {
        final int numberOfFailedMethods = 7;
        final int numberOfPassedMethods = 1;
        final int numberOfRetryMethods = 3;

        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.getReportDirectory()));
        GuiElement reportTestUnderTestRetry = dashboardPage.dashboardModuleClassBarChart.getBarChartElementByClassName("ReportTestUnderTestRetry");
        reportTestUnderTestRetry.click();
        DashboardModuleMethodChart dashboardModuleMethodChart = dashboardPage.getMethodChartModule();
        AssertCollector.assertEquals(dashboardModuleMethodChart.getNumberMethodsInMethodChartForTestResult(TestResultHelper.TestResult.FAILED), numberOfFailedMethods, "The number of failed tests using DataProvider and Retry is correct.");
        AssertCollector.assertEquals(dashboardModuleMethodChart.getNumberMethodsInMethodChartForTestResult(TestResultHelper.TestResult.PASSED), numberOfPassedMethods, "The number of passed tests using DataProvider and Retry is correct.");

        int retryOccurrenceCounter = 0;
        List<GuiElement> currentMethods = dashboardPage.getMethodChartModule().getCurrentMethods();

        for (GuiElement currentMethod : currentMethods) {

            GuiElement methodRetryTag = currentMethod.getSubElement(By.xpath("((./../font)[2])"));

            if (currentMethod.getText().contains("test_DataProviderTest (2/2)") && methodRetryTag.isDisplayed()) {

                String testMethodTagName = methodRetryTag.getText();
                int counter = retryOccurrenceCounter + 1;

                if (testMethodTagName.equals("(1) Retry" + counter)) {
                    retryOccurrenceCounter++;
                }

            }
        }

        AssertCollector.assertEquals(retryOccurrenceCounter, numberOfRetryMethods, "The number of runs using DataProvider and Retry is correct.");
    }

    /**
     * Tests whether the name of retried test with data provider has the expected structure
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    @Fails(ticketString = "668")
    // Test case #796
    public void testT05_checkRetriedTestsWithDataProviderTestMethodsHaveTheCorrectName() {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.getReportDirectory()));
        final int numberOfRetryMethods = 3;

        int retryOccurrenceCounter = 1;
        for (; retryOccurrenceCounter < numberOfRetryMethods + 1; retryOccurrenceCounter++) {
            GuiElement reportTestUnderTestRetry = dashboardPage.dashboardModuleClassBarChart.getBarChartElementByClassName("ReportTestUnderTestRetry");
            TimerUtils.sleep(1000);
            reportTestUnderTestRetry.asserts().assertIsDisplayed();
            reportTestUnderTestRetry.click();
            String expectedMethodName = "test_DataProviderTest [Retry" + retryOccurrenceCounter + "] (2/2)";
            for (GuiElement indexMethod : dashboardPage.getMethodChartModule().getCurrentMethods()) {
                if (indexMethod.getText().contains(expectedMethodName)) {
                    MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, indexMethod);
                    AssertCollector.assertTrue(methodDetailsPage.getMethodNameString().contains(expectedMethodName), "The name of retry method should be correct on Method Details Page. Expected " + expectedMethodName + " but found " + methodDetailsPage.getMethodNameString());
                    dashboardPage = methodDetailsPage.gotoDashboardPageByClickingBackButton();
                    break;
                }
            }
        }
        AssertCollector.assertEquals(retryOccurrenceCounter - 1, numberOfRetryMethods, "All Retry Methods are correctly displayed in Method Details Page");
    }

    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    // Test case #797
    public void testT06_checkMethodThatDependsOnPassedRetryIsSuccessful() {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.getReportDirectory()));
        WebDriverManager.getWebDriver().manage().window().setSize(new Dimension(1920, 1080));
        ClassesDetailsPage reportTestUnderTestRetry = dashboardPage.goToClasses().gotoClassesDetailsPageForClass("ReportTestUnderTestRetry");
        reportTestUnderTestRetry.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_MethodDependsOnMethodThatPassesInRetry", TestResultHelper.TestResult.PASSED);
    }

}
