package io.testerra.report.test.report_test.TT2_extensions;

import eu.tsystems.mms.tic.testframework.common.DefaultPropertyManager;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.methodReport.ReportStepsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider.WEB_DRIVER_MANAGER;

public class TestStepsTest extends AbstractReportTest {

    @Test
    public void testT01_checkStepsTabContainsCertainTestSteps() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String methodName = "layoutTest01_layoutTestFailing";
        String className = "GenerateLayoutTestsTTReportTest";
        String[] certainTestSteps = new String[]{
                "get web driver",
                "visit test page",
                "create page object and take screenshot"
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = super.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);
        ReportStepsTab reportStepsTab = reportDetailsTab.navigateToStepsTab();

        TestStep.begin("Check for test steps");
        reportStepsTab.assertCertainTestStepsAreListed(certainTestSteps);
    }
}
