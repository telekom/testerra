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

import eu.tsystems.mms.tic.testframework.AbstractReportTest;
import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodDetailsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ThreadsPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@TestContext(name = "View-Threads")
public class ThreadsPageTest extends AbstractReportTest {

    @DataProvider(parallel = true)
    public Object[][] testMethods(){
        Object[][] result = new Object[][]{
                new Object[]{"test_FilterPassedNoMinor", TestResultHelper.TestResult.PASSED},
                new Object[]{"test_FilterFailedNoMinor", TestResultHelper.TestResult.FAILED},
                new Object[]{"test_FilterSkipped", TestResultHelper.TestResult.SKIPPED},
                new Object[]{"test_FilterFailedMinor", TestResultHelper.TestResult.FAILEDMINOR},
                new Object[]{"test_FilterPassedMinor", TestResultHelper.TestResult.PASSEDMINOR},
                new Object[]{"test_FilterExpectedFailed", TestResultHelper.TestResult.FAILEDEXPECTED},
                new Object[]{"test_FilterPassedRetry", TestResultHelper.TestResult.PASSEDRETRY}
        };
        return result;
    }

    /**
     * Checks whether the threadsPage will be displayed correctly
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT01_checkCorrectDisplayOfThreadsPage() {
        // Test case #334
        ThreadsPage threadsPage = GeneralWorkflow.doOpenBrowserAndReportThreadsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        threadsPage.assertPageIsDisplayedCorrectly();
    }

    /**
     * This test checks for every test status whether the link from the threads page corresponds to the correct methodDetailsPage.
     * It runs once for each possible test status.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER7}, dataProvider = "testMethods")
    public void testT02_checkCorrectLinksFromThreadsPageToMethodDetailsPage(String methodName, TestResultHelper.TestResult methodResult) {
        // Test case #913
        ThreadsPage threadsPage = GeneralWorkflow.doOpenBrowserAndReportThreadsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_7.getReportDirectory()));
        MethodDetailsPage methodDetailsPage = threadsPage.clickMethodAndOpenMethodDetailsPage(methodName);
        methodDetailsPage.assertCorrectTestMethodIsDisplayed(methodName, methodResult);
    }
}