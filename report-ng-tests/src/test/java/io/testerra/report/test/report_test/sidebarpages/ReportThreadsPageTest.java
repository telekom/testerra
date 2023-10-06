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

package io.testerra.report.test.report_test.sidebarpages;

import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;

import org.testng.annotations.Test;

import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.TestDataProvider;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportThreadsPage;

public class ReportThreadsPageTest extends AbstractReportTest {

    // Deactivated testcase due to renewed threads view (echarts) since it is not that easy possible
    // to access elements such as the specific testcases inside the canvas element of echarts
    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "dataProviderForDifferentTestMethodForEachStatus", enabled = false)
    public void testT01_checkSearchForMethodsSelectionWorksCorrectly(String method) {
        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnGeneralReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to threads page.");
        ReportThreadsPage reportThreadsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.THREADS, ReportThreadsPage.class);

        TestStep.begin("Check whether thread report contains all methods");

        reportThreadsPage = reportThreadsPage.clickSearchBar();
        reportThreadsPage = reportThreadsPage.selectMethod(method);
        reportThreadsPage.assertMethodBoxIsSelected(method);
    }

}
