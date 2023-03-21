package io.testerra.report.test.report_test.extensions;

import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;

import org.testng.annotations.Test;

import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.TestDataProvider;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;

public class ValidatorTest extends AbstractReportTest {

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderValidatorTest")
    public void testT01_checkTrueValidatorCausesCorrectStatus(final String methodName,
                                                              final String className,
                                                              final String[] failureAspects,
                                                              final Status status) {
        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport();
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Check details tab contains correct failure aspects");
        reportDetailsTab.assertStacktraceContainsExpectedFailureAspects(failureAspects);

        TestStep.begin(String.format("Check test has status '%s'", status));
        reportDetailsTab.assertMethodOverviewContainsCorrectContent(className, status.title, methodName);
        reportDetailsTab.assertTestMethodeReportContainsFailsAnnotation();
    }
}
