package io.testerra.report.test.report_test.TT2_extensions;

import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.TestDataProvider;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.AbstractReportMethodPage;
import io.testerra.report.test.pages.report.methodReport.LastScreenshotOverlay;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ExclusiveSessionsTest extends AbstractReportTest {

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderMultipleScreenShotTests")
    public void test_multipleScreenshotsShown(final String methodName, final String className, final Class<AbstractReportMethodPage> detailsPage){
        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport();
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        AbstractReportMethodPage reportDetailsPage = reportTestsPage.navigateToMethodDetails(detailsPage, methodName);

        TestStep.begin("Open last screenshot and check for multiple entries");
        LastScreenshotOverlay lastScreenshotOverlay = reportDetailsPage.openLastScreenshot();
        changeScreenshotAndAssertChange(lastScreenshotOverlay);
    }

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderSingleScreenShotTests")
    public void test_singleScreenshotShown(final String methodName, final String className, final Class<AbstractReportMethodPage> detailsPage){
        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport();
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        AbstractReportMethodPage reportDetailsPage = reportTestsPage.navigateToMethodDetails(detailsPage, methodName);

        TestStep.begin("Open last screenshot and check for multiple entries");
        final LastScreenshotOverlay lastScreenshotOverlay = reportDetailsPage.openLastScreenshot();
        lastScreenshotOverlay.assertSingleScreenshot();
    }

    private void changeScreenshotAndAssertChange(LastScreenshotOverlay lastScreenshotOverlay) {
        final String screenShotPathOld = lastScreenshotOverlay.getPathToScreenshot();
        lastScreenshotOverlay = lastScreenshotOverlay.swipeToNextScreenshot();
        final String screenShotPathNew = lastScreenshotOverlay.getPathToScreenshot();
        Assert.assertNotEquals(screenShotPathOld, screenShotPathNew, "Page sources should differ, since screenshots should differ!");
    }
}
