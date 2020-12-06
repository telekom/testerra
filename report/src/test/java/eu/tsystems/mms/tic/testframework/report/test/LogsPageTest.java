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

import eu.tsystems.mms.tic.testframework.annotations.TestClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.AbstractReportTest;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.pageobjects.LogsPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.Test;

@TestClassContext(name = "View-Logs")
public class LogsPageTest extends AbstractReportTest {

    /**
     * Checks whether the logsPage will be displayed correctly
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #428
    public void testT01_checkCorrectDisplayOfLogsPage() {
        LogsPage logsPage = GeneralWorkflow.doOpenBrowserAndReportLogsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        logsPage.assertPageIsDisplayedCorrectly();
    }

    /**
     * Checks whether a specific log can be found in the table and has the correct values
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #427
    public void testT02_checkForCorrectDisplayOfLogs() {
        LogsPage logsPage = GeneralWorkflow.doOpenBrowserAndReportLogsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        logsPage.insertSearchTermInInSearchBar(
                "WARN",
                "Not retrying this method, because test is @Fails annotated.",
                "eu.tsystems.mms.tic.testframework.execution.testng.RetryAnalyzer",
                "TestNG-test=Execution Filter Creator-4"
        );

        logsPage.assertLogMessageIsDisplayed();
    }
}
