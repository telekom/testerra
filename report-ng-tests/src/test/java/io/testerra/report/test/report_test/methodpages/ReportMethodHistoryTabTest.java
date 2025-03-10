/*
 * Testerra
 *
 * (C) 2025, Tobias Adler, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
 */

package io.testerra.report.test.report_test.methodpages;

import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.methodReport.ReportMethodHistoryTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.testng.annotations.Test;

public class ReportMethodHistoryTabTest extends AbstractReportTest {

    @Test
    public void testT01_methodHistoryChartLayout() {
        String method = "test_highFlakiness";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnHistoryReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        ReportDetailsTab reportStepsTab = reportTestsPage.navigateToDetailsTab(method);
        ReportMethodHistoryTab methodHistoryTab = reportStepsTab.navigateToMethodHistoryTab();

        TestStep.begin("Check if the layout of the method history chart is correct.");
        methodHistoryTab.assertMethodHistoryChartMatchesScreenshot(1.0);
    }

    @Test
    public void testT02_failureAspectsChartLayout() {
        String method = "test_multipleFailureAspects";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnHistoryReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        ReportDetailsTab reportStepsTab = reportTestsPage.navigateToDetailsTab(method);
        ReportMethodHistoryTab methodHistoryTab = reportStepsTab.navigateToMethodHistoryTab();

        TestStep.begin("Check if the layout of the failure aspects chart is correct.");
        methodHistoryTab.assertFailureAspectsChartMatchesScreenshot(1.0);
    }

    @Test
    public void testT03_statusShareChartLayout() {
        String method = "test_highFlakiness";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnHistoryReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        ReportDetailsTab reportStepsTab = reportTestsPage.navigateToDetailsTab(method);
        ReportMethodHistoryTab methodHistoryTab = reportStepsTab.navigateToMethodHistoryTab();

        TestStep.begin("Check if the layout of the status share chart is correct.");
        methodHistoryTab.assertStatusShareChartMatchesScreenshot(1.0);
    }

    @Test
    public void testT04_checkMethodHistoryStatistics() {
        String method = "test_highFlakiness";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnHistoryReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        ReportDetailsTab reportStepsTab = reportTestsPage.navigateToDetailsTab(method);
        ReportMethodHistoryTab methodHistoryTab = reportStepsTab.navigateToMethodHistoryTab();

        TestStep.begin("Check history statistics.");
        methodHistoryTab.checkStatistics("Total runs", "10");
        methodHistoryTab.checkStatistics("Flakiness", "100.0%");
    }
}
