/*
 * Testerra
 *
 * (C) 2022, Marc Dietrich, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package io.testerra.report.test.report_test.common;

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
