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

import eu.tsystems.mms.tic.testframework.common.DefaultPropertyManager;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ReportStepsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider.WEB_DRIVER_MANAGER;

public class PriorityMessagesTest extends AbstractReportTest {

    @Test
    public void testT01_checkPriorityMessages(){
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String methodName = "preTest08_priorityMessagesTest";
        String className = "GeneratePassedCheckTestsTTReportTest";
        String[] priorityMessages = new String[]{
                "GeneratePassedCheckTestsTTReportTest: It is gonna be ok.",
                "GeneratePassedCheckTestsTTReportTest: Warn me!",
                "GeneratePassedCheckTestsTTReportTest: Tell me more!"
        };
        String[] states= new String[]{
                "status-skipped",
                "status-failed"
        };

        TestStep.begin("Navigate to steps page");
        ReportDashBoardPage reportDashBoardPage = super.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportStepsTab reportStepsTab = reportTestsPage.navigateToStepsTab(methodName);

        reportStepsTab.assertPriorityMessages(priorityMessages);
        reportStepsTab.assertStatesOfPriorityMessages(states);
    }


    @Test
    public void testT02_checkGlobalPriorityMessages(){
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String[] priorityMessages = new String[]{
                "GeneratePassedCheckTestsTTReportTest: It is gonna be ok.",
                "GeneratePassedCheckTestsTTReportTest: Warn me!",
                "GeneratePassedCheckTestsTTReportTest: Tell me more!"
        };
        String[] states= new String[]{
                "status-skipped",
                "status-failed"
        };

        TestStep.begin("Navigate to steps page");
        ReportDashBoardPage reportDashBoardPage = super.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));

        reportDashBoardPage.assertPriorityMessages(priorityMessages);
        reportDashBoardPage.assertStatesOfPriorityMessages(states);
    }

    @Test
    public void testT03_checkPriorityMessagesOnTestsPage(){
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        String methodName = "preTest08_priorityMessagesTest";
        String[] priorityMessages = new String[]{
                "It is gonna be ok.",
                "Warn me!",
                "Tell me more!"
        };

        TestStep.begin("Navigate to tests page");
        ReportDashBoardPage reportDashBoardPage = super.visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        TestStep.begin("Check priority message-preview for test");
        reportTestsPage.checkPriorityMessagesPreviewForTest(methodName, priorityMessages);
    }

}
