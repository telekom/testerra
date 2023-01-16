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

public class PriorityMessagesTest extends AbstractReportTest {

    @Test
    public void testT01_checkPriorityMessages(){
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String methodName = "preTest08_priorityMessagesTest";
        String className = "GeneratePassedCheckTestsTTReportTest";
        String[] priorityMessages = new String[]{
                "GeneratePassedCheckTestsTTReportTest: It's gonna be ok.",
                "GeneratePassedCheckTestsTTReportTest: Warn me!",
                "GeneratePassedCheckTestsTTReportTest: Tell me more!"
        };
        String[] states= new String[]{
                "status-skipped",
                "status-failed"
        };

        TestStep.begin("Navigate to steps page");
        ReportDashBoardPage reportDashBoardPage = super.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportStepsTab reportStepsTab = reportTestsPage.navigateToStepsTab(methodName);

        reportStepsTab.assertPriorityMessages(priorityMessages);
        reportStepsTab.assertStatesOfPriorityMessages(states);
    }

}
