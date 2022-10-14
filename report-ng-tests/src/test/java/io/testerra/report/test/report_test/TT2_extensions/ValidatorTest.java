package io.testerra.report.test.report_test.TT2_extensions;

import eu.tsystems.mms.tic.testframework.common.DefaultPropertyManager;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider.WEB_DRIVER_MANAGER;

public class ValidatorTest extends AbstractReportTest {

    @Test
    public void testT01_checkTrueValidatorCausesExpectedFail() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String methodName = "test_expectedFailedWithValidator_isValid";
        String className = "GenerateExpectedFailedStatusInTesterraReportTest";
        String[] failureAspects = new String[]{
                "AssertionError: Expected Fail - validator is: " + true,
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));
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
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String methodName = "test_expectedFailedWithValidator_isNotValid";
        String className = "GenerateExpectedFailedStatusInTesterraReportTest";
        String[] failureAspects = new String[]{
                "AssertionError: Expected Fail - validator is: " + false,
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));
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
