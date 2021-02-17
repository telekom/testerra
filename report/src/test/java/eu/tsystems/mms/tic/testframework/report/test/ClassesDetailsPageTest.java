/*
 * Testerra
 *
 * (C) 2020, Alex Rockstroh, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.report.test;

import eu.tsystems.mms.tic.testframework.annotations.TestClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.general.AbstractAnnotationMarkerTest;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.ReportAnnotationType;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ClassesDetailsPage;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestAnnotations;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestCorridorMid;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestExecutionFilter;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestFailed;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestPassed;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestRetry;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestSkipped;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.openqa.selenium.Dimension;
import org.testng.annotations.Test;

@TestClassContext(name = "View-ClassesDetails")
public class ClassesDetailsPageTest extends AbstractAnnotationMarkerTest {

    /**
     * checkTestResultsColorsAreDisplayed
     * Checks whether the color of test results are correct
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    // Test case #831
    public void testT01_checkTestResultsColorsAreDisplayedAndTestResultCategoryIsCorrect() {
        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
                WEB_DRIVER_MANAGER.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.getReportDirectory()),
                "My_Context");
        classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.FAILEDMINOR);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_FailedMinor1", TestResultHelper.TestResult.FAILEDMINOR);

        classesDetailsPage = classesDetailsPage.changeTestUnderTestClass(ReportTestUnderTestCorridorMid.class.getSimpleName());
        classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.FAILED);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_testMidCorridorFailed1", TestResultHelper.TestResult.FAILED);

        classesDetailsPage = classesDetailsPage.changeTestUnderTestClass(ReportTestUnderTestPassed.class.getSimpleName());
        classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.PASSED);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_TestStatePassed1", TestResultHelper.TestResult.PASSED);
        classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.PASSEDMINOR);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_PassedMinor1", TestResultHelper.TestResult.PASSEDMINOR);

        classesDetailsPage = classesDetailsPage.changeTestUnderTestClass(ReportTestUnderTestSkipped.class.getSimpleName());
        classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.SKIPPED);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_TestStateSkipped1", TestResultHelper.TestResult.SKIPPED);

        classesDetailsPage = classesDetailsPage.changeTestUnderTestClass(ReportTestUnderTestExecutionFilter.class.getSimpleName());
        classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.FAILEDEXPECTED);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_FailedMinorAnnotatedWithFail_Run3", TestResultHelper.TestResult.FAILEDEXPECTED);

        classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.PASSEDRETRY);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_FilterFailedMinorWithPassedRetry-" + TestResultHelper.TestResult.PASSEDRETRY.getTestState(), TestResultHelper.TestResult.PASSEDRETRY);

        classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.RETRIED); //retry of method which will fail next retry
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_FilterFailedMinorWithFailedRetry-" + TestStatusController.Status.FAILED_RETRIED.name(), TestResultHelper.TestResult.RETRIED);

        classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.RETRIED); //retry of method which will pass next retry
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_FilterFailedMinorWithPassedRetry-" + TestStatusController.Status.FAILED_RETRIED.name(), TestResultHelper.TestResult.RETRIED);
    }

    /**
     * Checks the displayed method information for passed tests. Concerns method name and Details Button.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    // Test case #832
    public void testT02_checkTestMethodInformationForPassedTests() {

        Class<ReportTestUnderTestPassed> classWithPassedMethods = ReportTestUnderTestPassed.class;
        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
                WEB_DRIVER_MANAGER.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.getReportDirectory()),
                classWithPassedMethods.getSimpleName());

        String testmethodName = classWithPassedMethods.getMethods()[0].getName();
        classesDetailsPage.assertMethodNameIsDisplayedForTestMethod(testmethodName);
        classesDetailsPage.assertDetailsLinkIsDisplayedAndWorks(testmethodName);
    }

    /**
     * Checks the displayed method information for failed tests. Concerns method name, stack trace and Details Button.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    // Test case #833
    public void testT03_checkTestMethodInformationForFailedTests() {

        Class<ReportTestUnderTestFailed> classWithFailedMethods = ReportTestUnderTestFailed.class;
        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
                WEB_DRIVER_MANAGER.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.getReportDirectory()),
                classWithFailedMethods.getAnnotation(TestClassContext.class).name());

        final String testmethodName = "test_FailedMinor1";
        classesDetailsPage.assertMethodNameIsDisplayedForTestMethod(testmethodName,
                "Report- TestsUnderTest", "Failed Creator");
        classesDetailsPage.assertErrorMessageIsDisplayedForTestMethod(testmethodName);
        classesDetailsPage.assertStackTraceLinkIsDisplayedForTestMethod(testmethodName);
        classesDetailsPage.assertDetailsLinkIsDisplayedAndWorks(testmethodName);
    }

    /**
     * checkScreenShotSymbol
     * Checks whether the SCREENSHOT symbol is not displayed
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    // Test case #834
    public void testT04_checkScreenShotSymbolIsNotDisplayed() {
        final String testundertestMethodName = "test_FailedInheritedMinor2";
        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
                WEB_DRIVER_MANAGER.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.getReportDirectory()),
                "My_Context");
        classesDetailsPage.assertScreenshotIsNotDisplayedForMethod(testundertestMethodName);
    }

    /**
     * Checks whether the annotation marks are displayed correctly on Classes Details Page
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #835
    public void testT05_checkAnnotationsAreDisplayed() {
        //TODO use DataProvider for this, enhanced to Minor Errors
        HashMap<String, List<ReportAnnotationType>> methodsTestObjects = new HashMap<>();
        methodsTestObjects.put("testAllMarkers",
                Arrays.asList(ReportAnnotationType.NEW, ReportAnnotationType.READY_FOR_APPROVAL, ReportAnnotationType.SUPPORT_METHOD, ReportAnnotationType.IN_DEVELOPMENT,
                        ReportAnnotationType.NO_RETRY));
        methodsTestObjects.put("testNewMarkerFailure", Collections.singletonList(ReportAnnotationType.NEW));
        methodsTestObjects.put("testNewMarkerSuccess", Collections.singletonList(ReportAnnotationType.NEW));
        methodsTestObjects.put("testReadyForApprovalMarker", Collections.singletonList(ReportAnnotationType.READY_FOR_APPROVAL));
        methodsTestObjects.put("testSupportMethodMarker", Collections.singletonList(ReportAnnotationType.SUPPORT_METHOD));
        methodsTestObjects.put("testNoRetryMarker", Collections.singletonList(ReportAnnotationType.NO_RETRY));
        methodsTestObjects.put("testInDevelopmentMarker", Collections.singletonList(ReportAnnotationType.IN_DEVELOPMENT));

        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
                WEB_DRIVER_MANAGER.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()),
                ReportTestUnderTestAnnotations.class.getSimpleName());
        checkAnnotationsAreDisplayed(classesDetailsPage, methodsTestObjects);
    }

    /**
     * checkTestMethodRetrySymbol
     * Checks whether the RETRY symbol is displayed
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    // Test case #836
    public void testT06_checkAnnotationsForRetry() {
        TestStep.begin("Check Retried Annotation");
        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
                WEB_DRIVER_MANAGER.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.getReportDirectory()),
                ReportTestUnderTestRetry.class.getSimpleName());
        WEB_DRIVER_MANAGER.getWebDriver().manage().window().setSize(new Dimension(1920, 1080));
        HashMap<String, List<ReportAnnotationType>> methodsTestObjects = new HashMap<>();
        methodsTestObjects.put("test_TestRetryExceptionTrigger", Collections.singletonList(ReportAnnotationType.RETRIED));
        checkAnnotationsAreDisplayed(classesDetailsPage, methodsTestObjects);
    }

}
