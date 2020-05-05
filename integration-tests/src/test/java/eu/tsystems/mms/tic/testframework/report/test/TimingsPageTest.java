package eu.tsystems.mms.tic.testframework.report.test;

import eu.tsystems.mms.tic.testframework.AbstractReportTest;
import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.pageobjects.TimingsPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.Test;


@TestContext(name = "View-Timings")
public class TimingsPageTest extends AbstractReportTest {

    /**
     * Checks whether the timingsPage will be displayed correctly
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT01_checkCorrectDisplayOfTimingsPage() {
        TimingsPage timingsPage = GeneralWorkflow.doOpenBrowserAndReportTimingsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        timingsPage.assertPageIsDisplayedCorrectly();
    }
}