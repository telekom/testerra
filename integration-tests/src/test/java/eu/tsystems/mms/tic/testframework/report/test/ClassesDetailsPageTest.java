package eu.tsystems.mms.tic.testframework.report.test;

import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.general.AbstractAnnotationMarkerTest;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.ReportAnnotationType;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ClassesDetailsPage;
import eu.tsystems.mms.tic.testframework.report.testundertest.*;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fakr on 19.06.2017.
 */
@FennecClassContext("View-ClassesDetails")
public class ClassesDetailsPageTest extends AbstractAnnotationMarkerTest {

    /**
     * checkTestResultsColorsAreDisplayed
     * Checks whether the color of test results are correct
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    public void testT01_checkTestResultsColorsAreDisplayedAndTestResultCategoryIsCorrect() {
        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
                WebDriverManager.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.toString()),
                "My_Context");
        classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.FAILEDMINOR);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_FailedInheritedMinor1", TestResultHelper.TestResult.FAILEDMINOR);
        classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.FAILEDMINOR);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_FailedMinor1", TestResultHelper.TestResult.FAILEDMINOR);

        classesDetailsPage = classesDetailsPage.changeTestUnderTestClass(ReportTestUnderTestCorridorMid.class.getSimpleName());
        classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.FAILED);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_testMidCorridorFailed1", TestResultHelper.TestResult.FAILED);

        classesDetailsPage = classesDetailsPage.changeTestUnderTestClass(ReportTestUnderTestPassed.class.getSimpleName());
        classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.PASSED);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_TestStatePassed1", TestResultHelper.TestResult.PASSED);
        classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.PASSEDMINOR);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_PassedInheritedMinor1", TestResultHelper.TestResult.PASSEDMINOR);
        classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.PASSEDMINOR);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_PassedMinor1", TestResultHelper.TestResult.PASSEDMINOR);

        classesDetailsPage = classesDetailsPage.changeTestUnderTestClass(ReportTestUnderTestSkipped.class.getSimpleName());
        classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.SKIPPED);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_TestStateSkipped1", TestResultHelper.TestResult.SKIPPED);
        classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.SKIPPED);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_TestStateSkippedInherited1", TestResultHelper.TestResult.SKIPPED);

    }

    /**
     * Checks the displayed method information for passed tests. Concerns method name and Details Button.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    public void testT02_checkTestMethodInformationForPassedTests() {

        Class<ReportTestUnderTestPassed> classWithPassedMethods = ReportTestUnderTestPassed.class;
        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
                WebDriverManager.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.toString()),
                classWithPassedMethods.getSimpleName());

        String testmethodName = classWithPassedMethods.getMethods()[0].getName();
        classesDetailsPage.assertMethodNameIsDisplayedForTestMethod(testmethodName);
        classesDetailsPage.assertDetailsLinkIsDisplayedAndWorks(testmethodName);
    }

    /**
     * Checks the displayed method information for failed tests. Concerns method name, stack trace and Details Button.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    public void testT03_checkTestMethodInformationForFailedTests() {

        Class<ReportTestUnderTestFailed> classWithFailedMethods = ReportTestUnderTestFailed.class;
        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
                WebDriverManager.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.toString()),
                classWithFailedMethods.getAnnotation(FennecClassContext.class).value());

        final String testmethodName = "test_FailedMinor1";
        classesDetailsPage.assertMethodNameIsDisplayedForTestMethod(testmethodName,
                "Report- TestsUnderTest", "Failed Creator");
        classesDetailsPage.assertErrorMessageIsDisplayedForTestMethod(testmethodName);
        classesDetailsPage.assertStackTraceLinkIsDisplayedForTestMethod(testmethodName);
        classesDetailsPage.assertDetailsLinkIsDisplayedAndWorks(testmethodName);
    }

    /**
     * checkScreenShotSymbol
     * Checks whether the SCREENSHOT symbol is displayed
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    public void testT04_checkScreenShotSymbol() {
        final String testundertestMethodName = "test_FailedInheritedMinor2";
        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
                WebDriverManager.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.toString()),
                "My_Context");
        classesDetailsPage.assertScreenshotIsNotDisplayedForMethod(testundertestMethodName);
    }

    /**
     * Checks whether the annotation marks are displayed correctly on Classes Details Page
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT05_checkAnnotationsAreDisplayed() {
        //TODO use DataProvider for this, enhanced to Minor Errors
        HashMap<String,List<ReportAnnotationType>> methodsTestObjects = new HashMap<>();
        methodsTestObjects.put("testAllMarkers", Arrays.asList(ReportAnnotationType.NEW, ReportAnnotationType.READY_FOR_APPROVAL, ReportAnnotationType.SUPPORT_METHOD));
        methodsTestObjects.put("testNewMarkerFailure", Collections.singletonList(ReportAnnotationType.NEW));
        methodsTestObjects.put("testNewMarkerSuccess", Collections.singletonList(ReportAnnotationType.NEW));
        methodsTestObjects.put("testReadyForApprovalMarker", Collections.singletonList(ReportAnnotationType.READY_FOR_APPROVAL));
        methodsTestObjects.put("testSupportMethodMarker", Collections.singletonList(ReportAnnotationType.SUPPORT_METHOD));

        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
            WebDriverManager.getWebDriver(),
            PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()),
            ReportTestUnderTestAnnotations.class.getSimpleName());
        checkAnnotationsAreDisplayed(classesDetailsPage, methodsTestObjects);
    }

    /**
     * checkTestMethodRetrySymbol
     * Checks whether the RETRY symbol is displayed
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    public void testT06_checkAnnotationsForRetry() {
        TestStep.begin("Check Retried Annotation");
        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
                WebDriverManager.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.toString()),
                ReportTestUnderTestRetry.class.getSimpleName());

        HashMap<String,List<ReportAnnotationType>> methodsTestObjects = new HashMap<>();
        methodsTestObjects.put("test_TestRetryExceptionTrigger (1/2)", Collections.singletonList(ReportAnnotationType.RETRIED));
        checkAnnotationsAreDisplayed(classesDetailsPage, methodsTestObjects);
    }

}
