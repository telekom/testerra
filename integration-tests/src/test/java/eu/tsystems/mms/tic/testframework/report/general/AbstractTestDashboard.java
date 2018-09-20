package eu.tsystems.mms.tic.testframework.report.general;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;

public class AbstractTestDashboard extends AbstractAnnotationMarkerTest {

    /**
     * Wrapper method for doOpenBrowserAndReportDashboardPage
     *
     * @param reportDirectory
     * @return
     */
    public DashboardPage getDashboardPage(ReportDirectory reportDirectory) {
        return GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(reportDirectory.toString()));
    }
}
