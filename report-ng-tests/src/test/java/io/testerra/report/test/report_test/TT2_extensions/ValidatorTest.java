package io.testerra.report.test.report_test.TT2_extensions;

import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.testng.annotations.Test;

public class ValidatorTest extends AbstractReportTest {

//    TODO: consolidate test logic into dedicated method, use Dataprovider to write only one test?

    // answer for TODO: I think data-provider is not suitable here, because tests do actually check different behavior?

    @Test
    public void testT01_checkTrueValidatorCausesExpectedFail() {
        String methodName = "test_expectedFailedWithValidator_isValid";
        String className = "GenerateExpectedFailedStatusInTesterraReportTest";
        String[] failureAspects = new String[]{
                "AssertionError: Expected Fail - validator is: " + true,
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport();
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Check details tab contains correct failure aspects");
        reportDetailsTab.assertStacktraceContainsExpectedFailureAspects(failureAspects);

        TestStep.begin("Check test is expected failed");
        reportDetailsTab.assertMethodOverviewContainsCorrectContent(className, Status.FAILED_EXPECTED.title, methodName);
        reportDetailsTab.assertTestMethodeReportContainsFailsAnnotation();
    }

    @Test
    public void testT02_checkFalseValidatorCausesFail() {
        String methodName = "test_expectedFailedWithValidator_isNotValid";
        String className = "GenerateExpectedFailedStatusInTesterraReportTest";
        String[] failureAspects = new String[]{
                "AssertionError: Expected Fail - validator is: " + false,
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport();
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Check details tab contains correct failure aspects");
        reportDetailsTab.assertStacktraceContainsExpectedFailureAspects(failureAspects);

        TestStep.begin("Check test is expected failed");
        reportDetailsTab.assertMethodOverviewContainsCorrectContent(className, Status.FAILED.title, methodName);
        reportDetailsTab.assertTestMethodeReportContainsFailsAnnotation();
    }
}
