package io.testerra.report.test.report_test.methodpages;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportMethodPageType;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportMethodPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportThreadsPage;
import io.testerra.report.test.pages.utils.DateTimeUtils;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ReportMethodPageTest extends AbstractReportTest {


    @Test(dataProvider = "dataProviderForPreTestMethods")
    public void testT01_methodOverviewIsCorrect(String method, String methodClass, Status status, String failureAspect) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Navigate to method detail page and check for correct content");
        reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), status.title);
        ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(method, ReportMethodPageType.DETAILS);
        reportMethodPage.assertMethodOverviewContainsCorrectContent(methodClass, status.title, method);
        ReportThreadsPage reportThreadsPage = reportMethodPage.clickThreadLink();
        reportThreadsPage.assertMethodBoxIsSelected(method);
    }

    @Test
    public void testT02_checkDurationFormat() {
        WebDriver driver = WebDriverManager.getWebDriver();
        String exampleMethod = "test_Passed";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to method page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        ReportMethodPage reportMethodPage = reportTestsPage.navigateToMethodReport(exampleMethod, ReportMethodPageType.STEPS);

        TestStep.begin("Check whether the duration is displayed and correct");
        final String testDuration = reportMethodPage.getTestDuration();
        final boolean dateFormatIsCorrect = DateTimeUtils.verifyDateTimeString(testDuration);
        Assert.assertTrue(dateFormatIsCorrect, String.format("Test Duration '%s' has correct format", testDuration));
    }

    @Test
    public void testT03_checkLastScreenShotCard() {
        //TODO: should every method-report (for failed methods) contain a screenshot?
    }

}
