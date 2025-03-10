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

package io.testerra.report.test.report_test.historypages;

import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.historyPages.ReportTestRunTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import org.testng.annotations.Test;

public class ReportHistoryTestRunTabTest extends AbstractReportTest {

    @Test
    public void testT01_testHistoryChartLayout() {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnHistoryReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to history page.");
        ReportTestRunTab reportHistoryTestRunTab = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.HISTORY, ReportTestRunTab.class);

        TestStep.begin("Check if the layout of the history chart is correct.");
        reportHistoryTestRunTab.assertHistoryChartMatchesScreenshot(1.0);
    }

    @Test
    public void testT02_topFlakyTests() {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnHistoryReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to history page.");
        ReportTestRunTab reportHistoryTestRunTab = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.HISTORY, ReportTestRunTab.class);

        TestStep.begin("Check the top flaky test.");
        ASSERT.assertTrue(reportHistoryTestRunTab.getOrderListOfTopFlakyTests().get(0).contains("test_highFlakiness"));
    }

    @Test
    public void testT03_historyStatistics() {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnHistoryReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to history page.");
        ReportTestRunTab reportHistoryTestRunTab = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.HISTORY, ReportTestRunTab.class);

        TestStep.begin("Check history statistics.");
        reportHistoryTestRunTab.checkStatistics("Total runs", "10");
    }
}
