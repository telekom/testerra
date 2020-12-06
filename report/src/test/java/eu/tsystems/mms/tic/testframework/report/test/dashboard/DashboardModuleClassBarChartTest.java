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

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.annotations.TestClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.general.AbstractTestDashboard;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestAnnotations;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestExecutionFilter;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.Test;

@TestClassContext(name = "View-Dashboard-Module BarChart")
public class DashboardModuleClassBarChartTest extends AbstractTestDashboard {

    /**
     * Checks whether all test results are covered by the execution filter
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    @Fails(ticketString = "668")
    // Test case #853
    public void testT01_checkExecutionFilter() {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.getReportDirectory()));
        GuiElement testundertestExecutionFilterElement = dashboardPage.dashboardModuleClassBarChart.getBarChartElementByClassName(ReportTestUnderTestExecutionFilter.class.getSimpleName());

        testundertestExecutionFilterElement.click();

        for (TestResultHelper.TestResult testResult: TestResultHelper.TestResult.values()){
            dashboardPage.getResultTableHeaderForTestResult(testResult).assertCollector().assertIsDisplayed();
        }
    }

    /**
     * Checks whether the annotation marks are displayed correctly
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    @Fails(ticketString = "668")
    // Test case #854
    public void testT02_checkReportAnnotations() {
        final String annotationMethod = "testAllMarkers";
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        dashboardPage.dashboardModuleClassBarChart.getBarChartElementByClassName(ReportTestUnderTestAnnotations.class.getSimpleName()).click();
        dashboardPage.assertAllAnnotationMarksAreDisplayed(annotationMethod);
    }
}
