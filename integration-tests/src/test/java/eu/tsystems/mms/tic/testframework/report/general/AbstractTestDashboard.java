package eu.tsystems.mms.tic.testframework.report.general;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.model.*;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.DataProvider;

public class AbstractTestDashboard extends AbstractAnnotationMarkerTest {

    /**
     * Wrapper method for doOpenBrowserAndReportDashboardPage
     *
     * @param reportDirectory
     * @return
     */
    public DashboardPage getDashboardPage(ReportDirectory reportDirectory) {
        return GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(reportDirectory.getReportDirectory()));
    }

    @DataProvider(parallel = true)
    public Object[][] testResultNumbers(){
        Object[][] result = new Object[][]{
                new Object[]{ReportDirectory.REPORT_DIRECTORY_1,new TestReportOneNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_2,new TestReportTwoNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_3,new TestReportThreeNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_4,new TestReportFourNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_5,new TestReportFiveNumbers()},
                new Object[]{ReportDirectory.REPORT_DIRECTORY_6,new TestReportSixNumbers()}
        };
        return result;
    }
}