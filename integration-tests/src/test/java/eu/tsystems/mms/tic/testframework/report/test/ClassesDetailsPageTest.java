package eu.tsystems.mms.tic.testframework.report.test;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.general.AbstractAnnotationMarkerTest;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.ReportAnnotationType;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ClassesDetailsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodDetailsPage;
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


        if (PropertyManager.getBooleanProperty("isPlatform")) {

            classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.FAILEDINHERITED);
            classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_FailedInheritedMinor1", TestResultHelper.TestResult.FAILEDINHERITED);
            classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.FAILEDMINOR);
            classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_FailedMinor1", TestResultHelper.TestResult.FAILEDMINOR);

            classesDetailsPage = classesDetailsPage.changeTestUnderTestClass(ReportTestUnderTestCorridorMid.class.getSimpleName());
            classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.FAILED);
            classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_testMidCorridorFailed1", TestResultHelper.TestResult.FAILED);

            classesDetailsPage = classesDetailsPage.changeTestUnderTestClass(ReportTestUnderTestPassed.class.getSimpleName());
            classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.PASSED);
            classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_TestStatePassed1", TestResultHelper.TestResult.PASSED);
            classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.PASSEDINHERITED);
            classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_PassedInheritedMinor1", TestResultHelper.TestResult.PASSEDINHERITED);
            classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.PASSEDMINOR);
            classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_PassedMinor1", TestResultHelper.TestResult.PASSEDMINOR);

            classesDetailsPage = classesDetailsPage.changeTestUnderTestClass(ReportTestUnderTestSkipped.class.getSimpleName());
            classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.SKIPPED);
            classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_TestStateSkipped1", TestResultHelper.TestResult.SKIPPED);
            classesDetailsPage.assertColorIsDisplayedForTestResult(TestResultHelper.TestResult.SKIPPEDINHERITED);
            classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory("test_TestStateSkippedInherited1", TestResultHelper.TestResult.SKIPPEDINHERITED);

        } else {

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
    }

    /**
     * checkTestMethodInformationInMethodInfoBody
     * Checks whether the method information are displayed. Concerns method name, starttime, endtime, duration and thread information
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    @Fails(description = "sagu: There are no method information for starttime, endtime, etc. (currently) available in the free edition")
    public void testT02_checkTestMethodInformationInMethodInfoBody() {

        final String testundertestMethodName = "test_FailedInheritedMinor1";

        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
                WebDriverManager.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.toString()),
                "My_Context");
        classesDetailsPage.assertMethodNameIsDisplayedForTestMethod(testundertestMethodName);
        classesDetailsPage.assertTimeAndThreadInformationAreDisplayedForTestMethod(testundertestMethodName);

    }

    /**
     * checkTestMethodInformationInMethodResultBody
     * Checks whether error message, tacktrace link and details link are displayed in method test results
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    public void testT03_checkTestMethodInformationInMethodResultBody() {

        final String testundertestMethodName = "test_FailedInheritedMinor1";

        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
                WebDriverManager.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.toString()),
                "My_Context");
        classesDetailsPage.assertErrorMessageIsDisplayedForTestMethod(testundertestMethodName);
        classesDetailsPage.assertStackTraceLinkIsDisplayedForTestMethod(testundertestMethodName);
        classesDetailsPage.assertDetailsLinkIsDisplayedAndWorks(testundertestMethodName);
    }

    /**
     * checkScreenShotSymbol
     * Checks whether the SCREENSHOT symbol is displayed
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    @Fails(description="sagu: Screenshot symbol is missing")
    public void testT04_checkScreenShotSymbol() {

        final String testundertestMethodName = "test_FailedInheritedMinor2";

        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
                WebDriverManager.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.toString()),
                "My_Context");
        classesDetailsPage.assertScreenshotIsDisplayedForMethod(testundertestMethodName);
    }

    /**
     * checkTestMethodRetrySymbol
     * Checks whether the RETRY symbol is displayed
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    public void testT05_checkTestMethodRetrySymbol() {

        final String testundertestMethodName = "test_TestRetryExceptionTrigger (1/2)";

        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(
                WebDriverManager.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.toString()),
                ReportTestUnderTestRetry.class,
                testundertestMethodName);

        checkRetryAnnotationIsDisplayed(methodDetailsPage, testundertestMethodName);
    }

    /**
     * checkConfigMethodShowingAndHiding
     * Checks whether its possible to show/hide config methods
     */
    @Fails(description = "sagu: Show config methods - button not available in XETA 10 free")
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    public void testT06_checkConfigMethodShowingAndHiding() {

        ClassesDetailsPage classesDetailsPage = GeneralWorkflow.doOpenBrowserAndReportClassesDetailsPage(
                WebDriverManager.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.toString()),
                "ReportTestUnderTestRetry");
        classesDetailsPage.assertHidingAndShowingOfConfigMethodSection();
    }

    /**
     * Checks whether the annotation marks are displayed correctly on Classes Details Page
     */
    @Fails(description = "sagu: Annotations not anymore on the classesDetailPage, rather on the MethodDetailsPage")
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT07_checkAnnotationAreDisplayed() {
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
        classesDetailsPage.changeTestUnderTestClass(ReportTestUnderTestRetryHistory.class.getSimpleName());
        checkRetryAnnotationIsDisplayed(classesDetailsPage, "test_FailedToPassedHistoryWithRetry");
    }





}
