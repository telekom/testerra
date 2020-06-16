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

import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.report.general.AbstractAnnotationMarkerTest;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.ReportAnnotationType;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodDependenciesPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodDetailsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodMinorErrorsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodScreenshotPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodStackPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodStepsPage;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestCorridorLow;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestExecutionFilter;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestExpectedtoFail;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestPassed;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@TestContext(name = "View-MethodDetails")
public class MethodDetailsPageTest extends AbstractAnnotationMarkerTest {

    /**
     * Checks the method details page for passed tests.
     * It runs once for a test that passed in the 1st report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #805
    public void testT01_checkMethodDetailsForPassedTest() {
        String methodName = "test_FilterPassedNoMinor";
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestExecutionFilter.class.getSimpleName(), methodName);

        //Method Details
        AssertCollector.assertEquals(methodDetailsPage.getMethodNameString(), methodName, "The method name is displayed correctly for " + methodName);
        AssertCollector.assertTrue(methodDetailsPage.getMethodResultString().contains(TestResultHelper.TestResult.PASSED.getXpathClassesDetailsHeader()), "The method status is displayed correctly for " + methodName);

        //shown tabs: Back, Steps and dependencies
        AssertCollector.assertTrue(methodDetailsPage.getBackTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getStepsTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getDependenciesTab().isDisplayed());
    }

    /**
     * Checks the method details page for passed tests.
     * It runs once for a test that passed with minor in the 1st report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #806
    public void testT02_checkMethodDetailsForPassedMinorTest() {
        String methodName = "test_FilterPassedMinor";
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestExecutionFilter.class.getSimpleName(), methodName);

        //Method Details
        AssertCollector.assertTrue(methodDetailsPage.getMethodNameString().contains(methodName), "The method name is displayed correctly for " + methodName);
        AssertCollector.assertTrue(methodDetailsPage.getMethodResultString().contains(TestResultHelper.TestResult.PASSEDMINOR.getXpathClassesDetailsHeader()), "The method status is displayed correctly for " + methodName);

        //Annotation
        HashMap<String, List<ReportAnnotationType>> methodsTestObjects = new HashMap<>();
        methodsTestObjects.put(methodName, Collections.singletonList(ReportAnnotationType.MINOR));
        checkAnnotationsAreDisplayed(methodDetailsPage, methodsTestObjects);

        //checks the number of minor error hint
        AssertCollector.assertTrue(methodDetailsPage.getMinorCount().isDisplayed(), "The button that indicates how many minor errors the test contains is displayed on minor details page.");
        AssertCollector.assertTrue(methodDetailsPage.getMinorCount().getText().contains("1"), "The button that indicates how many errors the test contains indicates correct numbers on minor details page.");

        //shown tabs: Back, Steps, Minor Errors and dependencies
        AssertCollector.assertTrue(methodDetailsPage.getBackTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getStepsTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getMinorErrors().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getDependenciesTab().isDisplayed());
    }


    /**
     * Checks the method details page for passed tests.
     * It runs once for a test that failed in the 1st report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #807
    public void testT03_checkMethodDetailsForFailedTest() {
        String methodName = "test_FailedInheritedFilter";
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestExecutionFilter.class.getSimpleName(), methodName);

        ///Method Details
        AssertCollector.assertEquals(methodDetailsPage.getMethodNameString(), methodName, "The method name is displayed correctly for " + methodName);
        AssertCollector.assertTrue(methodDetailsPage.getMethodResultString().contains(TestResultHelper.TestResult.FAILED.getXpathClassesDetailsHeader()), "The method status is displayed correctly for " + methodName);

        //shown tabs: Back, Steps, stack and dependencies
        AssertCollector.assertTrue(methodDetailsPage.getBackTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getStepsTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getStackTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getDependenciesTab().isDisplayed());
    }

    /**
     * Checks the method details page for passed tests.
     * It runs once for a test that failed with minor in the 1st report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #808
    public void testT04_checkMethodDetailsForFailedMinorTest() {
        String methodName = "test_FilterFailedMinor";
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestExecutionFilter.class.getSimpleName(), methodName);

        //General Details
        AssertCollector.assertTrue(methodDetailsPage.getMethodNameString().contains(methodName), "The method name is displayed correctly for " + methodName);
        AssertCollector.assertTrue(methodDetailsPage.getMethodResultString().contains(TestResultHelper.TestResult.FAILEDMINOR.getXpathClassesDetailsHeader()), "The method status is displayed correctly for " + methodName);

        //Annotation
        HashMap<String, List<ReportAnnotationType>> methodsTestObjects = new HashMap<>();
        methodsTestObjects.put(methodName, Collections.singletonList(ReportAnnotationType.MINOR));
        checkAnnotationsAreDisplayed(methodDetailsPage, methodsTestObjects);

        //shown tabs: Back, Steps, stack, minor errors and dependencies
        AssertCollector.assertTrue(methodDetailsPage.getBackTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getStepsTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getMinorErrors().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getStackTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getDependenciesTab().isDisplayed());
    }

    /**
     * Checks the method details page for passed tests.
     * It runs once for a test that skipped in the 1st report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #809
    public void testT05_checkMethodDetailsForSkippedTest() {
        String methodName = "test_FilterSkipped";
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestExecutionFilter.class.getSimpleName(), methodName);

        //General Details
        AssertCollector.assertEquals(methodDetailsPage.getMethodNameString(), methodName, "The method name is displayed correctly for " + methodName);
        AssertCollector.assertTrue(methodDetailsPage.getMethodResultString().contains(TestResultHelper.TestResult.SKIPPED.getXpathClassesDetailsHeader()), "The method status is displayed correctly for " + methodName);

        //shown tabs: Back, Steps and dependencies
        AssertCollector.assertTrue(methodDetailsPage.getBackTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getStepsTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getDependenciesTab().isDisplayed());
    }


    /**
     * Checks the method details page for passed tests.
     * It runs once for a test that retried in the 1st report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #810
    public void testT06_checkMethodDetailsForRetriedTest() {
        String methodName = "test_FilterFailedNoMinorWithFailedRetry (1/2)";
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestExecutionFilter.class.getSimpleName(), methodName);

        //General Details
        AssertCollector.assertTrue(methodDetailsPage.getMethodNameString().contains(methodName), "The method name is displayed correctly for " + methodName);
        AssertCollector.assertTrue(methodDetailsPage.getMethodResultString().contains(TestResultHelper.TestResult.RETRIED.getXpathClassesDetailsHeader()), "The method status is displayed correctly for " + methodName);

        //Annotation
        HashMap<String, List<ReportAnnotationType>> methodsTestObjects = new HashMap<>();
        methodsTestObjects.put(methodName, Collections.singletonList(ReportAnnotationType.RETRIED));
        checkAnnotationsAreDisplayed(methodDetailsPage, methodsTestObjects);

        //shown tabs: Back, Steps and dependencies
        AssertCollector.assertTrue(methodDetailsPage.getBackTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getStepsTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getDependenciesTab().isDisplayed());

        //check retry hints
        //TODO add check for retry information
    }

    /**
     * Checks the method details page for passed tests.
     * It runs once for a test that expected failed in the 1st report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #811
    public void testT07_checkMethodDetailsForExpectedFailedTest() {
        String methodName = "test_FailedMinorAnnotatedWithFail_Run1";
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestExecutionFilter.class.getSimpleName(), methodName);

        //General Details
        AssertCollector.assertTrue(methodDetailsPage.getMethodNameString().contains(methodName), "The method name is displayed correctly for " + methodName);
        AssertCollector.assertTrue(methodDetailsPage.getMethodResultString().contains(TestResultHelper.TestResult.FAILEDEXPECTED.getXpathClassesDetailsHeader()), "The method status is displayed correctly for " + methodName);

        //shown tabs: Back, Steps, Stack and dependencies
        AssertCollector.assertTrue(methodDetailsPage.getBackTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getStepsTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getStackTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getDependenciesTab().isDisplayed());

        //check Failing expected hint
        //TODO add a check for failing expected hint

    }

    /**
     * Checks the method details page for passed tests.
     * It runs once for a test that passed retried in the 1st report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT08_checkMethodDetailsForPassedRetriedTest() {
        String methodName = "test_FilterFailedNoMinorWithPassedRetry";
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestExecutionFilter.class.getSimpleName(), methodName);

        //General Details
        AssertCollector.assertTrue(methodDetailsPage.getMethodNameString().contains(methodName), "The method name is displayed correctly for " + methodName);
        AssertCollector.assertTrue(methodDetailsPage.getMethodResultString().contains(TestResultHelper.TestResult.PASSEDRETRY.getXpathClassesDetailsHeader()), "The method status is displayed correctly for " + methodName);

        //shown tabs: Back, Steps and dependencies
        AssertCollector.assertTrue(methodDetailsPage.getBackTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getStepsTab().isDisplayed());
        AssertCollector.assertTrue(methodDetailsPage.getDependenciesTab().isDisplayed());
    }

    /**
     * Checks the information provided for tests that are marked with @Fails but passed.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #812
    public void testT09_checkRepairedFailsIndication() {
        String methodName = "test_TestStatePassed2";
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestPassed.class.getSimpleName(), methodName);

        AssertCollector.assertTrue(methodDetailsPage.getRepairedFailsIndication().isDisplayed(), "The method details page indicates that the shown method is repaired.");
        AssertCollector.assertTrue(methodDetailsPage.getRepairedFailsIndication().getText().contains("ticketId=1"), "The indicator for the repaired fails annotation contains the correct ticketId.");
        AssertCollector.assertTrue(methodDetailsPage.getRepairedFailsIndication().getText().contains("description=Does not actually fail."), "The indicator for the repaired fails annotation contains the correct description.");
    }

    /**
     * Tests the shown method details on a failed method with screenshots.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #814
    public void testT10_checkScreenshotTab() {
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), "My_Context", "test_FailedInheritedMinor2");
        MethodScreenshotPage screenshotPage = GeneralWorkflow.doOpenReportMethodScreenshotPage(methodDetailsPage);
        Assert.assertTrue(screenshotPage.getScreenShot().isDisplayed(), "There is no screenshot in the methodDetailsPage of the first report.");
    }

    /**
     * Clicks itself from the dashboard page to the 'stack' tab of the method details. It tests whether
     * the provided stackstrace is correct.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #816
    public void testT11_checkStackTraceTab() {
        String methodName = "test_FailedInheritedFilter";
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestExecutionFilter.class.getSimpleName(), methodName);

        MethodStackPage stackPage = GeneralWorkflow.doOpenReportStracktracePage(methodDetailsPage);
        AssertCollector.assertTrue(stackPage.getStackTrace().contains("java.lang.AssertionError: expected [true] but found [false]"));
        AssertCollector.assertTrue(stackPage.getStackTrace().contains("eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestExecutionFilter.test_FailedInheritedFilter(ReportTestUnderTestExecutionFilter.java:92"));
    }

    /**
     * Checks the number of minor errors in the method details page and the minor errors tab in the method
     * details in report 1
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #817
    public void testT12_checkMinorErrorsTab() {
        String methodName = "test_FilterPassedMinor";
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestExecutionFilter.class.getSimpleName(), methodName);

        MethodMinorErrorsPage minorErrorsPage = methodDetailsPage.clickMinorErrorsTab();

        AssertCollector.assertTrue(minorErrorsPage.getAssertion().isDisplayed(), "The assertion message button is displayed in a with minor details page.");
        AssertCollector.assertTrue(minorErrorsPage.getAssertion().getText().equals("Assert: expected [true] but found [false]"), "The assertion message is correct shwon on details page.");

        minorErrorsPage = minorErrorsPage.clickAssertion();
        Assert.assertTrue(minorErrorsPage.getAssertionMessage().contains("eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTest"), "The assertion message is correct shwon on details page.");
    }

    /**
     * Checks whether the navigation to the DEPENDENCIES tab works as expected
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #819
    public void testT13_checkDependenciesTab() {
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestPassed.class.getSimpleName(), "test_PassedMinor1");
        MethodDependenciesPage dependenciesPage = methodDetailsPage.clickDependenciesTab();
        //TODO what to check here
    }

    /**
     * Checks the infos shown in the context view of the method details in report 1.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #820
    public void testT14_checkContext() {
        TestStep.begin("Checks Context for classes without using TesterraClassContext");
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestPassed.class.getSimpleName(), "test_PassedMinor1");
        methodDetailsPage = methodDetailsPage.toggleContext();
        AssertCollector.assertEquals(methodDetailsPage.getContextClassString(), ReportTestUnderTestPassed.class.getSimpleName());
        AssertCollector.assertEquals(methodDetailsPage.getContextTestString(), "Passed Creator");
        AssertCollector.assertEquals(methodDetailsPage.getContextSuiteString(), "Report- TestsUnderTest");

        TestStep.begin("Checks Context for classes with using TesterraClassContext");
        methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), "My_Context", "test_FailedMinor1");
        methodDetailsPage = methodDetailsPage.toggleContext();
        AssertCollector.assertEquals(methodDetailsPage.getContextClassString(), "My_Context");
        AssertCollector.assertEquals(methodDetailsPage.getContextTestString(), "Failed Creator");
        AssertCollector.assertEquals(methodDetailsPage.getContextSuiteString(), "Report- TestsUnderTest");
    }

    /**
     * Check the infos shown in the duration view in report 1
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #821
    public void testT15_checkDuration() {
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestPassed.class.getSimpleName(), "test_PassedMinor1");

        DateFormat durationFormat = new SimpleDateFormat("S 'ms'");
        DateFormat dateFormat = new SimpleDateFormat("E MMM d hh:mm:ss z yyyy", Locale.ENGLISH);

        //check duratution
        checkDateStringToCorrectFormat(durationFormat, methodDetailsPage.getDuration(), null);

        //check the format of end
        checkDateStringToCorrectFormat(dateFormat, methodDetailsPage.getStartTime(), "Start ");

        //check the format of end
        checkDateStringToCorrectFormat(dateFormat, methodDetailsPage.getFinishTime(), "End ");
    }

    private void checkDateStringToCorrectFormat(DateFormat expectedDateFormat, String dateString, String contentToExclude) {
        String pureDateString = dateString;
        try {
            if (contentToExclude != null) {
                pureDateString = pureDateString.split(contentToExclude)[1];
            }
        } catch (Exception e) {
            Assert.fail(String.format("The %s does not display the correct content.", dateString));
        }
        try {
            expectedDateFormat.parse(pureDateString);
        } catch (ParseException e) {
            Assert.fail(String.format("Could not parse the string %s for %s time", dateString, contentToExclude));
        }
    }


    /**
     * Checks the error message in the method details for correctnes.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #822
    public void testT16_checkErrorMessage() {
        TestStep.begin("Checks error Message from a failed test");
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestCorridorLow.class.getSimpleName(), "test_testLowCorridorFailed3");
        AssertCollector.assertEquals(methodDetailsPage.getErrorMessageString().getText(), "ArithmeticException: / by zero", "Expected another error message.");
        AssertCollector.assertFalse(methodDetailsPage.getFingerprintString().isDisplayed(), "Expected that the fingerprint link is shown.");
        methodDetailsPage = methodDetailsPage.toggleFingerprint();
        AssertCollector.assertTrue(methodDetailsPage.getFingerprintString().getText().contains("test_testLowCorridorFailed3()"), "Expected that the detailed fingerprint message is shown.");
        AssertCollector.assertTrue(methodDetailsPage.getFingerprintString().getText().contains("ExitPointCreatorTestClass2.testCreatorForDifferentExitPoints2()"), "Expected that the detailed fingerprint message is shown.");

        TestStep.begin("Checks error Message from a failed test with minor errors");
        methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestCorridorLow.class.getSimpleName(), "test_FailedMinor1");
        AssertCollector.assertEquals(methodDetailsPage.getErrorMessageString().getText(), "Exception", "Expected another error message.");
        AssertCollector.assertFalse(methodDetailsPage.getFingerprintString().isDisplayed(), "Expected that the fingerprint link is shown.");
        methodDetailsPage = methodDetailsPage.toggleFingerprint();
        AssertCollector.assertTrue(methodDetailsPage.getFingerprintString().getText().contains("test_FailedMinor1()"), "Expected that the detailed fingerprint message is shown.");
        AssertCollector.assertTrue(methodDetailsPage.getFingerprintString().getText().contains("NonFunctionalAssert.assertTrue(false)"), "Expected that the detailed fingerprint message is shown.");
    }

    /**
     * Clicks itself from the dashboard page to the 'steps' tab of the method details. It tests whether
     * the programmatically built in steps are displayed.
     * It runs once for every test status that is not inherited in report 3. 5 times in total.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #823
    public void testT17_checkStepsTab() {
        String testmethod = "test_TestStatePassed1";
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestPassed.class.getSimpleName(), testmethod);
        MethodStepsPage stepsPage = GeneralWorkflow.doOpenReportStepsPage(methodDetailsPage);
        AssertCollector.assertEquals(stepsPage.getTestStep1Button().getText(), "2) Test-Step-1", "Test-Step-1-Button is displayed (correctly) in steps tab for " + testmethod);
        AssertCollector.assertEquals(stepsPage.getTestStep2Button().getText(), "3) Test-Step-2", "Test-Step-2-Button is displayed (correctly) in steps tab for " + testmethod);
        AssertCollector.assertEquals(stepsPage.getTestStep3Button().getText(), "4) Test-Step-3", "Test-Step-3-Button is displayed (correctly) in steps tab for " + testmethod);

    }

    /**
     * Check if a failed test case with a failure aspect matching an expected failed test sets an extra message.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    // Test case #824
    public void testT18_failureAspectMatchingExpectedFailedInfoMessage() {
        String testMethod = "test_UnexpectedFailedWithRelatedExpectedFailed";
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.getReportDirectory()), ReportTestUnderTestExpectedtoFail.class.getSimpleName(), testMethod);
        AssertCollector.assertEquals(methodDetailsPage.getErrorMessageExtraInfo().getText(), "Failure aspect matches known issue: Description: Known issue with same aspect as unmarked failed test");
    }

}
