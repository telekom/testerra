package eu.tsystems.mms.tic.testframework.report.test;

import eu.tsystems.mms.tic.testframework.AbstractReportTest;
import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MonitorPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.XrayTest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.Test;

@TestContext(name = "View-Monitor")
public class MonitorPageTest extends AbstractReportTest {

    /**
     * Checks whether the monitorPage will be displayed correctly
     */
    @XrayTest(key = "TAP2DEV-425")
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT01_checkCorrectDisplayOfMonitorPage() {
        MonitorPage monitorPage = GeneralWorkflow.doOpenBrowserAndReportMonitorPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        monitorPage.assertPageIsDisplayedCorrectly();
    }
}