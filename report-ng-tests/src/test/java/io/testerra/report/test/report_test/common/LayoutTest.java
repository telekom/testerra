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

import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.methodReport.ComparisonDialogOverlay;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.testng.annotations.Test;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab.ScreenshotType;

public class LayoutTest extends AbstractReportTest {

    @Test
    public void testT01_checkFailedLayoutTestReport() {
        String methodName = "layoutTest01_layoutTestFailing";
        String className = "GenerateLayoutTestsTTReportTest";
        String errorMessage = "Expected that UniversalPage pixel distance";

        String[] failureAspects = new String[]{
                "AssertionError",
                "pixel distance",
                "is lower than"
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport(WEB_DRIVER_MANAGER.getWebDriver());
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Failure Aspect contains actual-expected comparison");
        reportDetailsTab.getComparisonImgElement(errorMessage, "Actual").assertThat().displayed(true);
        reportDetailsTab.getComparisonImgElement(errorMessage, "Difference").assertThat().displayed(true);
        reportDetailsTab.getComparisonImgElement(errorMessage, "Expected").assertThat().displayed(true);

        TestStep.begin("Check details tab contains correct failure aspects");
        reportDetailsTab.assertStacktraceContainsExpectedFailureAspects(failureAspects);
    }

    @Test
    public void testT02_checkLayoutTestPassesWithMinor() {
        String methodName = "layoutTest03_layoutTestPassingWithMinor";
        String className = "GenerateLayoutTestsTTReportTest";
        String[] failureAspects = new String[]{
                "AssertionError",
                "The actual image",
                "has a different size than the reference image"
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport(WEB_DRIVER_MANAGER.getWebDriver());
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Check details tab contains correct failure aspects");
        reportDetailsTab.assertStacktraceContainsExpectedFailureAspects(failureAspects);

        TestStep.begin("Check test is passed");
        reportDetailsTab.assertMethodOverviewContainsCorrectContent(className, Status.PASSED.title, methodName);
    }

    @Test
    public void testT03_checkMultiCheckLayoutTest() {
        String methodName = "layoutTest04_layoutTestFailing_MultiChecks";
        String className = "GenerateLayoutTestsTTReportTest";
        String[] failureAspects = new String[]{
                "pixelDistance(\"inputHtml_box1\")",
                "pixelDistance(\"inputHtml_box2\")",
                "Just a simple error message"
        };

        String[] errorMessages = new String[]{
                "Expected that UniversalPage -> UiElement(By.id: box1)",
                "Expected that UniversalPage -> UiElement(By.id: box2)",
                "Just a simple error message"
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport(WEB_DRIVER_MANAGER.getWebDriver());
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);
        reportDetailsTab.assertTestMethodContainsCorrectFailureAspect(failureAspects);
        ASSERT.assertTrue(reportDetailsTab.hasFailureAspectAScreenshotComparison(errorMessages[0]), "Visibility of screenshot comparison in Failure aspect box");
        ASSERT.assertTrue(reportDetailsTab.hasFailureAspectAScreenshotComparison(errorMessages[1]), "Visibility of screenshot comparison in Failure aspect box");
        ASSERT.assertFalse(reportDetailsTab.hasFailureAspectAScreenshotComparison(errorMessages[2]), "Visibility of screenshot comparison in Failure aspect box");

        reportDetailsTab.getComparisonImgElement(errorMessages[0], "Actual").assertThat().attribute("src").isContaining("box1");
        reportDetailsTab.getComparisonImgElement(errorMessages[0], "Expected").assertThat().attribute("src").isContaining("box1");
        reportDetailsTab.getComparisonImgElement(errorMessages[1], "Actual").assertThat().attribute("src").isContaining("box2");
        reportDetailsTab.getComparisonImgElement(errorMessages[1], "Expected").assertThat().attribute("src").isContaining("box2");
    }

    @Test
    public void testT04_checkFailedLayoutTestAndFailedAssertion() {
        String methodName = "layoutTest05_layoutTestFailing_AssertionFailing";
        String className = "GenerateLayoutTestsTTReportTest";

        String[] errorMessages = new String[]{
                "Expected that pixel distance (%) of WebDriver screenshot to image ",
                "Just a simple error message"
        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport(WEB_DRIVER_MANAGER.getWebDriver());
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);
        ASSERT.assertTrue(reportDetailsTab.hasFailureAspectAScreenshotComparison(errorMessages[0]), "Visibility of screenshot comparison in Failure aspect box");
        ASSERT.assertFalse(reportDetailsTab.hasFailureAspectAScreenshotComparison(errorMessages[1]), "Visibility of screenshot comparison in Failure aspect box");

        reportDetailsTab.getComparisonImgElement(errorMessages[0], "Actual").assertThat().attribute("src").isContaining("inputHtml_image2");
        reportDetailsTab.getComparisonImgElement(errorMessages[0], "Expected").assertThat().attribute("src").isContaining("inputHtml_image2");
    }

    @Test
    public void testT05_checkPassedLayoutTestAndFailedAssertion() {
        String methodName = "layoutTest06_layoutTestPassing_AssertionFailing";
        String className = "GenerateLayoutTestsTTReportTest";

        String errorMessage = "Just a simple error message";

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport(WEB_DRIVER_MANAGER.getWebDriver());
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);
        ASSERT.assertFalse(reportDetailsTab.hasFailureAspectAScreenshotComparison(errorMessage), "Visibility of screenshot comparison in Failure aspect box");
    }

    @Test
    public void testT06_checkLayoutDialog() {
        String methodName = "layoutTest01_layoutTestFailing";
        String className = "GenerateLayoutTestsTTReportTest";

//        String[] imageTitles = new String[]{
//                "Actual",
//                "Difference",
//                "Expected"
//        };

        TestStep.begin("Navigate to details page");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport(WEB_DRIVER_MANAGER.getWebDriver());
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);
        reportTestsPage.selectClassName(className);
        ReportDetailsTab reportDetailsTab = reportTestsPage.navigateToDetailsTab(methodName);

        TestStep.begin("Open Layout Dialog from actual image");
//        for(String title: imageTitles){
//            ComparisonDialogOverlay comparisonDialogOverlay = reportDetailsTab.openComparisonDialogByClickingOnScreenShot(title);
        ComparisonDialogOverlay comparisonDialogOverlay = reportDetailsTab.openComparisonDialogByClickingOnScreenShot(ScreenshotType.Actual);
        comparisonDialogOverlay.checkContent(ScreenshotType.Actual, ScreenshotType.Difference, "layout_compare_dialog");
//            comparisonDialogOverlay.checkSelectedAndContentFromStartingMatched(title);
        reportDetailsTab = comparisonDialogOverlay.closeDialog();
//        }
    }

}
