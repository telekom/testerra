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

import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.historyPages.ReportTestClassesTab;
import io.testerra.report.test.pages.report.historyPages.ReportTestRunTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import org.testng.annotations.Test;

public class ReportHistoryTestClassesTabTest extends AbstractReportTest {

    @Test
    public void testT01_classesHistoryChartLayout() {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnHistoryReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to test classes tab of history page.");
        ReportTestRunTab reportHistoryTestRunTab = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.HISTORY, ReportTestRunTab.class);
        ReportTestClassesTab reportTestClassesTab = reportHistoryTestRunTab.navigateToTestClassesTab();

        TestStep.begin("Check if the layout of the history chart is correct.");
        reportTestClassesTab.assertClassesHistoryChartMatchesScreenshot(1.0, "classes_history_chart");
    }

    @Test
    public void testT02_selectedClassHistoryChartLayout() {
        String className = "DynamicHistoryTest";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnHistoryReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to test classes tab of history page.");
        ReportTestRunTab reportHistoryTestRunTab = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.HISTORY, ReportTestRunTab.class);
        ReportTestClassesTab reportTestClassesTab = reportHistoryTestRunTab.navigateToTestClassesTab();

        TestStep.begin("Select a class.");
        reportTestClassesTab = reportTestClassesTab.selectClassName(className);

        TestStep.begin("Check if the layout of the history chart is correct.");
        reportTestClassesTab.assertClassesHistoryChartMatchesScreenshot(1.0, "selected_class_history_chart");
    }

    @Test
    public void testT03_filteredClassHistoryChartLayout() {
        String className = "DynamicHistoryTest";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnHistoryReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to test classes tab of history page.");
        ReportTestRunTab reportHistoryTestRunTab = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.HISTORY, ReportTestRunTab.class);
        ReportTestClassesTab reportTestClassesTab = reportHistoryTestRunTab.navigateToTestClassesTab();

        TestStep.begin("Select a status.");
        reportTestClassesTab = reportTestClassesTab.selectTestStatus(Status.FAILED);

        TestStep.begin("Check if the layout of the history chart is correct.");
        reportTestClassesTab.assertClassesHistoryChartMatchesScreenshot(1.0, "filtered_classes_history_chart");
    }

    @Test
    public void testT04_filteredSelectedClassHistoryChartLayout() {
        String className = "DynamicHistoryTest";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnHistoryReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to test classes tab of history page.");
        ReportTestRunTab reportHistoryTestRunTab = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.HISTORY, ReportTestRunTab.class);
        ReportTestClassesTab reportTestClassesTab = reportHistoryTestRunTab.navigateToTestClassesTab();

        TestStep.begin("Select a class and a status.");
        reportTestClassesTab = reportTestClassesTab.selectClassName(className);
        reportTestClassesTab = reportTestClassesTab.selectTestStatus(Status.FAILED);

        TestStep.begin("Check if the layout of the history chart is correct.");
        reportTestClassesTab.assertClassesHistoryChartMatchesScreenshot(1.0, "filtered_selected_class_history_chart");
    }
}
