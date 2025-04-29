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
import io.testerra.report.test.pages.report.historyPages.ReportRunComparisonTab;
import io.testerra.report.test.pages.report.historyPages.ReportTestClassesTab;
import io.testerra.report.test.pages.report.historyPages.ReportTestRunTab;
import io.testerra.report.test.pages.report.methodReport.ReportMethodHistoryTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import org.testng.annotations.Test;

public class ReportHistoryRunComparisonTabTest extends AbstractReportTest {

    @Test
    public void testT01_checkHistoryIndex() {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnHistoryReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to run comparison of the history page.");
        ReportTestRunTab reportHistoryTestRunTab = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.HISTORY, ReportTestRunTab.class);
        ReportRunComparisonTab reportRunComparisonTab = reportHistoryTestRunTab.navigateToRunComparisonTab();

        TestStep.begin("Check if the correct executions are displayed.");
        int selectedHistoryIndex = reportRunComparisonTab.getSelectedHistoryIndex();

        reportRunComparisonTab.checkStatisticsOfPreviousRun("Execution index", String.valueOf(selectedHistoryIndex));
        reportRunComparisonTab.assertPreviousStatusColumnHeadlineContainsCorrectHistoryIndex(selectedHistoryIndex);

        reportRunComparisonTab.checkStatisticsOfCurrentRun("Execution index", String.valueOf(selectedHistoryIndex + 1));
        reportRunComparisonTab.assertCurrentStatusColumnHeadlineContainsCorrectHistoryIndex(selectedHistoryIndex + 1);
    }

    @Test
    public void testT02_checkNavigationToMethodHistory() {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnHistoryReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to history page.");
        ReportTestRunTab reportHistoryTestRunTab = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.HISTORY, ReportTestRunTab.class);
        ReportRunComparisonTab reportRunComparisonTab = reportHistoryTestRunTab.navigateToRunComparisonTab();

        TestStep.begin("Navigate to the method.");
        String methodName = "test_highFlakiness";
        ReportMethodHistoryTab reportMethodHistoryTab = reportRunComparisonTab.clickOnMethodInTable(methodName);
        reportMethodHistoryTab.assertMethodNamesAreCorrect(methodName);
    }

    @Test
    public void testT03_checkNavigationToClassHistory() {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnHistoryReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to history page.");
        ReportTestRunTab reportHistoryTestRunTab = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.HISTORY, ReportTestRunTab.class);
        ReportRunComparisonTab reportRunComparisonTab = reportHistoryTestRunTab.navigateToRunComparisonTab();

        TestStep.begin("Navigate to the class.");
        String className = "DynamicHistoryTest";
        ReportTestClassesTab reportTestClassTab = reportRunComparisonTab.clickOnClassInTable(className);
        reportTestClassTab.assertSelectedClass(className);
    }
}
