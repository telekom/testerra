package io.testerra.report.test.report_test.sidebarpages;

import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportPrintPreviewDialog;
import org.testng.annotations.Test;

public class ReportPrintPreviewTest extends AbstractReportTest {
    @Test()
    public void testT01_openPreviewTest() {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Click 'Print report' button");
        ReportPrintPreviewDialog reportPrintPreviewDialog = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.PRINT_REPORT, ReportPrintPreviewDialog.class);

        reportPrintPreviewDialog.checkIFrameTitle();
    }
}