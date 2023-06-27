package io.testerra.report.test.report_test.extensions;

import eu.tsystems.mms.tic.testframework.common.DefaultPropertyManager;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider.WEB_DRIVER_MANAGER;

public class TicketStringsTest extends AbstractReportTest {

    @Test
    public void testT01_checkFailsAnnotationMessageContainsTicketString() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String methodName = "test_expectedFailedWithTicketString";
        String className = "GenerateExpectedFailedStatusInTesterraReportTest";
        String expectedTicketString = "placeholder ticket string";

        TestStep.begin("Navigate to steps page");
        ReportDashBoardPage reportDashBoardPage = super.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        reportDetailsTab.assertTestMethodeReportContainsFailsAnnotation();
        reportDetailsTab.assertFailsAnnotationMessage(expectedTicketString);
    }

    @Test
    public void testT02_checkFailsTicketStringOnTestsPage() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String methodName = "test_expectedFailedWithTicketString";
        String expectedTicketString = "placeholder ticket string";

        TestStep.begin("Navigate to steps page");
        ReportDashBoardPage reportDashBoardPage = super.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        reportTestsPage.assertTicketString(methodName, expectedTicketString);
    }

}
