package io.testerra.report.test.report_test.TT2_extensions;

import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.testng.annotations.Test;

public class DetailsTabExtensionsTest extends AbstractReportTest{

    @Test
    public void testT01_navigateToPreviousAndNextFailedMethods(){
        String methodName = "test_failedWithInterceptedClick";
        String className = "GenerateFailedStatusInTesterraReportTest";

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport();
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Navigate to previous failed method");
        reportDetailsTab = reportDetailsTab.navigateToPreviousFailedMethod();
        reportDetailsTab.assertPageIsValid();

        TestStep.begin("Navigate to starting failed method");
        reportDetailsTab = reportDetailsTab.navigateToNextFailedMethod();
        reportDetailsTab.assertPageIsValid();

        TestStep.begin("Navigate to next failed method");
        reportDetailsTab = reportDetailsTab.navigateToNextFailedMethod();
        reportDetailsTab.assertPageIsValid();
    }

}