package io.testerra.report.test.report_test.TT2_extensions;

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

public class ExclusiveSessionsTest extends AbstractReportTest {

    @Test
    public void test_multipleSessionsScreenshot(){
        String methodName = "test_takeScreenshotWithMultipleActiveSessions";
        String className = "GenerateScreenshotsInTesterraReportTest";

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport();
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Open last screenshot and check for multiple entries");
        reportDetailsTab.openLastScreenshot();
        reportDetailsTab.swipeToNextScreenshot();
    }

    @Test
    public void test_ExclusiveSessionsScreenshot(){
        String methodName = "test_takeExclusiveSessionScreenshotWithMultipleActiveSessions";
        String className = "GenerateScreenshotsInTesterraReportTest";

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport();
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Open last screenshot and check for multiple entries");
        reportDetailsTab.openLastScreenshot();
        reportDetailsTab.swipeToNextScreenshot();
    }
}
