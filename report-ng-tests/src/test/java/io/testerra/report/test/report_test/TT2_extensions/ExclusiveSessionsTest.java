package io.testerra.report.test.report_test.TT2_extensions;

import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.TestDataProvider;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.LastScreenshotOverlay;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.methodReport.ReportStepsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ExclusiveSessionsTest extends AbstractReportTest {

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderMultipleScreenShotsTestFailed")
    public void test_multipleScreenshots(final String methodName, final String className){
        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport();
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Open last screenshot and check for multiple entries");
        LastScreenshotOverlay lastScreenshotOverlay = reportDetailsTab.openLastScreenshot();
        changeScreenshotAndAssertChange(lastScreenshotOverlay);

    }

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderScreenShotTestPassed")
    public void test_multipleScreenshotsPassed(final String methodName, final String className){
        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport();
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportStepsTab reportStepsTab = reportTestsPage.navigateToStepsTab(methodName);

        TestStep.begin("Open last screenshot and check for multiple entries");
        LastScreenshotOverlay lastScreenshotOverlay = reportStepsTab.openLastScreenshot();
        changeScreenshotAndAssertChange(lastScreenshotOverlay);
    }

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderSingleScreenShotTestFailed")
    public void test_singleScreenshotsFailed(final String methodName, final String className){
        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport();
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Open last screenshot and check for multiple entries");
        final LastScreenshotOverlay lastScreenshotOverlay = reportDetailsTab.openLastScreenshot();
        lastScreenshotOverlay.assertSingleScreenshot();
    }

    private void changeScreenshotAndAssertChange(LastScreenshotOverlay lastScreenshotOverlay) {
        final String screenShotPathOld = lastScreenshotOverlay.getPathToScreenshot();
        lastScreenshotOverlay = lastScreenshotOverlay.swipeToNextScreenshot();
        final String screenShotPathNew = lastScreenshotOverlay.getPathToScreenshot();
        Assert.assertNotEquals(screenShotPathOld, screenShotPathNew, "Page sources should differ, since screenshots should differ!");
    }
}
