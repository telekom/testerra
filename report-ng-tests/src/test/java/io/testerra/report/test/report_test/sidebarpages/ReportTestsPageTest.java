package io.testerra.report.test.report_test.sidebarpages;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ReportTestsPageTest extends AbstractReportTest {

    @DataProvider
    public Object[][] dataProviderForDifferentTestStates() {
        return new Object[][]{
                {Status.PASSED}, {Status.SKIPPED}, {Status.FAILED}, {Status.FAILED_EXPECTED}, {Status.RETRIED}, {Status.RECOVERED}, {Status.REPAIRED}
        };
    }

    @DataProvider
    public Object[][] dataProviderForDifferentTestClasses() {
        return new Object[][]{
                {"GeneratePassedStatusInTesterraReportTest"},
                {"GenerateFailedStatusInTesterraReportTest"},
                {"GenerateSkippedStatusInTesterraReportTest"},
                {"GenerateSkippedStatusViaBeforeMethodInTesterraReportTest"},
                {"GenerateExpectedFailedStatusInTesterraReportTest"},
                {"GenerateScreenshotsInTesterraReportTest"}
        };
    }

    @DataProvider
    public Object[][] dataProviderForFailureAspects() {
        return new Object[][]{
                {"AssertionError"},
                {"PageNotFoundException"},
                {"SkipException"},
                {"RuntimeException"},
                {"Throwable"}
        };
    }

    @Test(dataProvider = "dataProviderForDifferentTestStates")
    public void testT01_filterForTestStates(Status status) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check whether the tests page table is correct for " + status.title + " state");
        reportTestsPage = reportTestsPage.selectDropBoxElement(reportTestsPage.getTestStatusSelect(), status.title);
        reportTestsPage.assertCorrectTestStatus(status);
        reportTestsPage.assertStatusColumnHeadlineContainsCorrectText();
    }

    @Test(dataProvider = "dataProviderForDifferentTestClasses")
    public void testT02_filterForClasses(String className) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check whether class-column contains correct classes");
        reportTestsPage = reportTestsPage.selectDropBoxElement(reportTestsPage.getTestClassSelect(), className);
        reportTestsPage.assertClassColumnContainsCorrectClasses(className);
        reportTestsPage.assertClassColumnHeadlineContainsCorrectText();
    }

    @Test(dataProvider = "dataProviderForDifferentTestMethodForEachStatus")
    public void testT03_SearchForTestMethods(String testMethod) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check whether method-column contains correct methods");
        reportTestsPage.search(testMethod);
        reportTestsPage.assertMethodColumnContainsCorrectMethods(testMethod);
        reportTestsPage.assertMethodeColumnHeadlineContainsCorrectText();
    }

    @Test(dataProvider = "dataProviderForFailureAspects")
    public void testT04_SearchForFailureAspect(String failureAspect) {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check whether method-column contains methods with correct failure aspects");
        reportTestsPage = reportTestsPage.search(failureAspect);
        reportTestsPage.assertMethodColumnContainsCorrectMethods(failureAspect);
        reportTestsPage.assertMethodeColumnHeadlineContainsCorrectText();
    }

    @Test
    public void testT05_showConfigurationMethods() {
        WebDriver driver = WebDriverManager.getWebDriver();

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.visitTestPage(ReportDashBoardPage.class, driver, PropertyManager.getProperty("file.path.content.root"));

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Enable 'Show configuration methods and check whether more methods are displayed");
        int amountOfMethodsBeforeSwitch = reportTestsPage.getAmountOfEntries();
        reportTestsPage.assertMethodeColumnHeadlineContainsCorrectText();
        reportTestsPage.clickConfigurationMethodsSwitch();
        int amountOfMethodsAfterSwitch = reportTestsPage.getAmountOfEntries();
        Assert.assertTrue(amountOfMethodsBeforeSwitch < amountOfMethodsAfterSwitch,
                "'Show configuration methods' switch should display some more (configuration) methods.");
        reportTestsPage.assertConfigurationMethodsAreDisplayed();
        reportTestsPage.assertMethodeColumnHeadlineContainsCorrectText();
        reportTestsPage.assertTestMethodIndicDoesNotAppearTwice();
    }

}
