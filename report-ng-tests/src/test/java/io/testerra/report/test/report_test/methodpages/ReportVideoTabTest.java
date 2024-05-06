/*
 * Testerra
 *
 * (C) 2024, Selina Natschke, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package io.testerra.report.test.report_test.methodpages;

import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportStepsTab;
import io.testerra.report.test.pages.report.methodReport.ReportVideoTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.testng.annotations.Test;

public class ReportVideoTabTest extends AbstractReportTest {

    @Test
    public void testT01_checkDisplayedVideoGotContent() {
        String preTestWithVideoTab = "preTest10_videoTest_passed";
        String usedBrowser = "Chrome";

        TestStep.begin("Navigate to dashboard page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport(WEB_DRIVER_MANAGER.getWebDriver());

        TestStep.begin("Navigate to tests page.");
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Navigate to method browser info page and check for correct content");
        reportTestsPage = reportTestsPage.clickConfigurationMethodsSwitch();
        ReportStepsTab reportStepsTab = reportTestsPage.navigateToStepsTab(preTestWithVideoTab);
        ReportVideoTab reportVideoTab = reportStepsTab.navigateToVideoTab();

        reportVideoTab.validateBrowser(usedBrowser);
        reportVideoTab.checkSessionId();
        reportVideoTab.checkVideo();
    }
}
