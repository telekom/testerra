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
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.testng.annotations.Test;

public class DetailsTabExtensionsTest extends AbstractReportTest{

    @Test
    public void testT01_navigateToPreviousAndNextFailedMethods(){
        String methodName = "test_failedWithInterceptedClick";
        String className = "GenerateFailedStatusInTesterraReportTest";

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport();
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Navigate to previous failed method");
        reportDetailsTab = reportDetailsTab.navigateToPreviousFailedMethod();
        reportDetailsTab.assertPageIsValid();

        TestStep.begin("Navigate to starting failed method");
        reportDetailsTab = reportDetailsTab.navigateToNextFailedMethod();
        reportDetailsTab.assertPageIsValid();

        TestStep.begin("Navigate to next failed method");
        reportDetailsTab = reportDetailsTab.navigateToNextFailedMethod();
        reportDetailsTab.assertPageIsValid();
    }

}
