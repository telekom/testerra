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

public class LayoutTest extends AbstractReportTest {

    @Test
    public void testT01_checkFailedLayoutTestReport() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String methodName = "layoutTest01_layoutTestFailing";
        String className = "GenerateLayoutTestsTTReportTest";
        String[] failureAspects = new String[]{
                "AssertionError",
                "pixel distance",
                "is lower than"
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Failure Aspect contains actual-expected comparison");
        reportDetailsTab.assertFailureAspectCardContainsImageComparison();

        TestStep.begin("Check details tab contains correct failure aspects");
        reportDetailsTab.assertStacktraceContainsExpectedFailureAspects(failureAspects);
    }

    @Test
    public void testT02_checkLayoutTestPassesWithMinor() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String methodName = "layoutTest03_layoutTestPassingWithMinor";
        String className = "GenerateLayoutTestsTTReportTest";
        String[] failureAspects = new String[]{
                "AssertionError",
                "The actual image",
                "has a different size than the reference image"
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Check details tab contains correct failure aspects");
        reportDetailsTab.assertStacktraceContainsExpectedFailureAspects(failureAspects);

        TestStep.begin("Check test is passed");
        reportDetailsTab.assertMethodOverviewContainsCorrectContent(className, Status.PASSED.title, methodName);
    }

}
