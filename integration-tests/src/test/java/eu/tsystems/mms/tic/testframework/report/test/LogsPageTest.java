package eu.tsystems.mms.tic.testframework.report.test;

import eu.tsystems.mms.tic.testframework.AbstractReportTest;
import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.pageobjects.LogsPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.Test;

@TestContext(name = "View-Logs")
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