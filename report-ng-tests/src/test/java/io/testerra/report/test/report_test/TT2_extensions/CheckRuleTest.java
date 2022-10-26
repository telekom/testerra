package io.testerra.report.test.report_test.TT2_extensions;

import eu.tsystems.mms.tic.testframework.common.DefaultPropertyManager;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.methodReport.ReportStepsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider.WEB_DRIVER_MANAGER;

public class CheckRuleTest extends AbstractReportTest {

    @DataProvider(parallel = true)
    public Object[][] dataProvider_methods() {
        return new Object[][]{
                {"preTest05_checkRule_IS_DISPLAYED_check"},
                {"preTest06_checkRule_IS_NOT_DISPLAYED_check"},
                {"preTest07_checkRule_IS_PRESENT_check"},
                {"preTest08_checkRule_IS_NOT_PRESENT_check"}

        };
    }


    @Test(dataProvider = "dataProvider_methods")
    public void testT01_checkRuleErrorsAreCorrectReported(String method) {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String className = "GenerateFailedCheckTestsTTReportTest";
        String[] failureAspects = new String[]{
                "PageFactoryException",
                "UiElementAssertionError"
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(method);

        TestStep.begin("Check details tab contains correct failure aspects");
        reportDetailsTab.assertStacktraceContainsExpectedFailureAspects(failureAspects);
    }

    @Test
    public void testT02_prioritizedErrorMessage_isDisplayedCorrect() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String className = "GenerateFailedCheckTestsTTReportTest";
        String methodName = "preTest03_prioritizedErrorMessageCheck";
        String[] failureAspects = new String[]{
                "AssertionError: Custom error message - pretest"
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Check details tab contains correct failure aspects");
        reportDetailsTab.assertStacktraceContainsExpectedFailureAspects(failureAspects);
    }

    @Test
    public void testT03_timeoutCheckPassed_DurationIsValid() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String methodName = "preTest03_timeoutCheck_passed";
        String className = "GeneratePassedCheckTestsTTReportTest";
        int validDurationThreshold_lowerBound = 10;         // pageDelay
        int validDurationThreshold_upperBound = 20;         // timeout time

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportStepsTab reportStepsTab = reportTestsPage.navigateToStepsTab(methodName);

        TestStep.begin("Check duration card contains valid value");
        reportStepsTab.assertDurationIsValid(validDurationThreshold_lowerBound, validDurationThreshold_upperBound);
    }

    @Test
    public void testT04_timeoutCheckFailed_DurationIsNotValid() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String methodName = "preTest04_timeoutCheck";
        String className = "GenerateFailedCheckTestsTTReportTest";
        int validDurationThreshold_lowerBound = 10;         // pageDelay
        int validDurationThreshold_upperBound = 20;         // timeout time

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Check duration card contains valid value");
        reportDetailsTab.assertDurationIsNotValid(validDurationThreshold_lowerBound, validDurationThreshold_upperBound);
    }


    //TODO: failing -> frage: sollten nicht alle failure-aspects die collected wurden auch angezeigt werden?
    @Test
    public void testT05_collectCheckDisplaysFailureAspectsCorrect() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String methodName = "preTest01_collectCheck";
        String className = "GenerateFailedCheckTestsTTReportTest";
        String[] failureAspects = new String[]{
                "UiElementAssertionError: Expected that CollectCheckPage -> collectCheckedUiElement_01 displayed is true",
                "UiElementAssertionError: Expected that CollectCheckPage -> collectCheckedUiElement_02 displayed is true"
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Check details tab contains correct failure aspects");
        reportDetailsTab.assertStacktraceContainsExpectedFailureAspects(failureAspects);
    }

    @Test
    public void testT06_optionalCheckPassesWithFailureAspect() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String methodName = "preTest02_optionalCheck_passed";
        String className = "GeneratePassedCheckTestsTTReportTest";
        Status status = Status.PASSED;
        String[] failureAspects = new String[]{
                "UiElementAssertionError: Expected that OptionalCheckPage -> optionalCheckedUiElement displayed is true"
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Check details tab contains correct failure aspects");
        reportDetailsTab.assertStacktraceContainsExpectedFailureAspects(failureAspects);

        TestStep.begin("Check test is passed");
        reportDetailsTab.assertMethodOverviewContainsCorrectContent(className, status.title, methodName);
    }

}


