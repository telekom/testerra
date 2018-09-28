package eu.tsystems.mms.tic.testframework.report.test;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.report.general.AbstractAnnotationMarkerTest;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.ReportAnnotationType;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.*;
import eu.tsystems.mms.tic.testframework.report.pageobjects.dashboard.modules.DashboardModuleMethodChart;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestAnnotations;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestPassed;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestRetryHistory;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static eu.tsystems.mms.tic.testframework.report.model.TestResultHelper.TestResult;

/**
 * Created by riwa on 24.11.2016.
 */
@FennecClassContext("View-MethodDetails")
public class MethodDetailsPageTest extends AbstractAnnotationMarkerTest {

    /**
     * Checks the method details page for the correct method name, status of the test and the step it failed in.
     * It runs once for every test status in report 4. 8 times in total.
     */
    @Test(dataProviderClass = TestResultHelper.class, dataProvider = "getAllTestResults", groups = {SystemTestsGroup.SYSTEMTESTSFILTER4})
    public void testT01_checkMethodNameStatusAndStep(TestResult testResult) throws Exception {
        //TODO use other way - open classes page
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_4.toString()));
        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(testResult);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(0));

        switch (testResult) {
            case PASSED:
                testMethSpec(dashboardPage, testResult, "test_TestStatePassed2", "Passed");
                break;
            case PASSEDMINOR:
                testMethSpec(dashboardPage, testResult, "test_PassedMinor1", "Passed with Minor");
                break;
            case FAILED:
                testMethSpec(dashboardPage, testResult, "test_testLowCorridorFailed1", "Failed");
                break;
            case FAILEDMINOR:
                testMethSpec(dashboardPage, testResult, "test_FailedMinor1", "Failed with Minor");
                break;
            case SKIPPED:
                testMethSpec(dashboardPage, testResult, "test_TestStateSkipped1", "Skipped");
                break;
            default:
                break;

        }
    }

    /**
     * Clicks itself from the dashboard page to the 'screenshots' tab of the method details. It tests whether
     * there actually is a screenshot provided, the 'info' button works and test infos are provided.
     * It runs once for a test that failed with a minor in the 1st report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT02_checkExistenceOfMethodDetailsScreenshot() throws Exception {
        //TODO use other way - open classes page
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()));
        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(TestResult.FAILEDMINOR);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(1));
        GuiElement method1 = null;
        for (GuiElement method : dashboardPage.getMethodChartModule().getCurrentMethods()) {
            if (method.getText().contains("test_FailedInheritedMinor2")) {
                method1 = method;
                break;
            }
        }
        if (method1 == null) {
            Assert.fail("Method for testing screenshot was not found.");
        }

        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, method1);
        MethodScreenshotPage screenshotPage = GeneralWorkflow.doOpenReportMethodScreenshotPage(methodDetailsPage);
        AssertCollector.assertTrue(screenshotPage.getScreenShot().isDisplayed(), "There is no screenshot in the methodDetailsPage of the first report.");

    }

    /**
     * Clicks itself from the dashboard page to the 'stack' tab of the method details. It tests whether
     * the provided stackstrace is correct.
     * It runs once for a failed test in the 6th report.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    public void testT03_checkStackTraceTabForCorrectness() throws Exception {
        //TODO use other way - open classes page
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.toString()));
        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(TestResult.FAILED);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(1));

        GuiElement method1 = null;
        for (GuiElement method : dashboardPage.getMethodChartModule().getCurrentMethods()) {
            if (method.getText().contains("test_testLowCorridorFailed1")) {
                method1 = method;
                break;
            }
        }
        if (method1 == null) {
            Assert.fail("Method for testing stack trace was not found.");
        }

        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, method1);
        MethodStackPage stackPage = GeneralWorkflow.doOpenReportStracktracePage(methodDetailsPage);
        Assert.assertTrue(stackPage.getStackTrace().contains("java.lang.Exception\n" +
                "eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestCorridorLow.test_testLowCorridorFailed1(ReportTestUnderTestCorridorLow"), "The stacktrace of the ReportTestUnderTestCorridorLow.test_testLowCorridorFailed1 is displayed correctly in the sixth report.");
    }


    /**
     * Clicks itself from the dashboard page to the 'steps' tab of the method details. It tests whether
     * the programmatically built in steps are displayed.
     * It runs once for every test status that is not inherited in report 3. 5 times in total.
     */
    @Test(dataProviderClass = TestResultHelper.class, dataProvider = "getTestResultsExceptInherited", groups = {SystemTestsGroup.SYSTEMTESTSFILTER3})
    public void testT04_checkForManuallyBuiltInSteps(TestResult testResult) throws Exception {
        //TODO use other way - open classes page
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_3.toString()));
        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(testResult);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(0));
        List<GuiElement> methods = dashboardPage.getMethodChartModule().getCurrentMethods();
        GuiElement method1 = null;
        switch (testResult) {
            case PASSED:
                for (GuiElement method : methods) {
                    if (method.getText().equals("test_TestStatePassed1")) {
                        method1 = method;
                        break;
                    }
                }
                break;
            case FAILED:
                for (GuiElement method : methods) {
                    if (method.getText().equals("test_testLowCorridorFailed1")) {
                        method1 = method;
                        break;
                    }
                }
                break;
            case SKIPPED:
                for (GuiElement method : methods) {
                    if (method.getText().equals("test_TestStateSkipped1")) {
                        method1 = method;
                        break;
                    }
                }
                break;
            case PASSEDMINOR:
                for (GuiElement method : methods) {
                    if (method.getText().equals("test_PassedMinor1")) {
                        method1 = method;
                        break;
                    }
                }
                break;
            case FAILEDMINOR:
                for (GuiElement method : methods) {
                    if (method.getText().equals("test_FailedMinor1")) {
                        method1 = method;
                        break;
                    }
                }
                break;
            default:
                throw new FennecRuntimeException("TestResult not supported: " + testResult);
        }

        MethodDetailsPage methodDetailsPage;
        if (method1 != null) {
            methodDetailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, method1);
            MethodStepsPage stepsPage = GeneralWorkflow.doOpenReportStepsPage(methodDetailsPage);

            switch (testResult) {
                case PASSED:
                    AssertCollector.assertEquals(stepsPage.getTestStep1Button().getText(), "2) Test-Step-1", "Test-Step-1-Button is displayed (correctly) in steps tab for test_TestStatePassed1.");
                    AssertCollector.assertEquals(stepsPage.getTestStep2Button().getText(), "3) Test-Step-2", "Test-Step-2-Button is displayed (correctly) in steps tab for test_TestStatePassed1.");
                    AssertCollector.assertEquals(stepsPage.getTestStep3Button().getText(), "4) Test-Step-3", "Test-Step-3-Button is displayed (correctly) in steps tab for test_TestStatePassed1.");
                    break;
                case FAILED:
                    AssertCollector.assertEquals(stepsPage.getTestStep1Button().getText(), "2) Test-Step-1", "Test-Step-1-Button is displayed (correctly) in steps tab for test_testLowCorridorFailed1.");
                    AssertCollector.assertEquals(stepsPage.getTestStep2Button().getText(), "3) Test-Step-2", "Test-Step-2-Button is displayed (correctly) in steps tab for test_testLowCorridorFailed1.");
                    AssertCollector.assertEquals(stepsPage.getTestStep3Button().getText(), "4) Test-Step-3", "Test-Step-3-Button is displayed (correctly) in steps tab for test_testLowCorridorFailed1.");
                    //AssertCollector.assertEquals(stepsPage.getButton3Point1().getText(), "3.1) Internal", "'Internal' button is displayed (correctly) in steps tab for test_testLowCorridorFailed1.");
                    break;
                case SKIPPED:
                    AssertCollector.assertEquals(stepsPage.getTestStep1Button().getText(), "2) Test-Step-1", "Test-Step-1-Button is displayed (correctly) in steps tab for test_TestStateSkipped1.");
                    AssertCollector.assertEquals(stepsPage.getTestStep2Button().getText(), "3) Test-Step-2", "Test-Step-2-Button is displayed (correctly) in steps tab for test_TestStateSkipped1.");
                    AssertCollector.assertEquals(stepsPage.getTestStep3Button().getText(), "4) Test-Step-3", "Test-Step-3-Button is displayed (correctly) in steps tab for test_TestStateSkipped1.");
                    break;
                case PASSEDMINOR:
                    AssertCollector.assertEquals(stepsPage.getTestStep1Button().getText(), "2) Test-Step-1", "Test-Step-1-Button is displayed (correctly) in steps tab for test_PassedMinor1.");
                    AssertCollector.assertEquals(stepsPage.getTestStep2Button().getText(), "3) Test-Step-2", "Test-Step-2-Button is displayed (correctly) in steps tab for test_PassedMinor1.");
                    AssertCollector.assertEquals(stepsPage.getTestStep3Button().getText(), "4) Test-Step-3", "Test-Step-3-Button is displayed (correctly) in steps tab for test_PassedMinor1.");
                    break;
                case FAILEDMINOR:
                    AssertCollector.assertEquals(stepsPage.getTestStep1Button().getText(), "2) Test-Step-1", "Test-Step-1-Button is displayed (correctly) in steps tab for test_FailedMinor1.");
                    AssertCollector.assertEquals(stepsPage.getTestStep2Button().getText(), "3) Test-Step-2", "Test-Step-2-Button is displayed (correctly) in steps tab for test_FailedMinor1.");
                    AssertCollector.assertEquals(stepsPage.getTestStep3Button().getText(), "4) Test-Step-3", "Test-Step-3-Button is displayed (correctly) in steps tab for test_FailedMinor1.");
                    //AssertCollector.assertEquals(stepsPage.getButton3Point1().getText(), "3.1) Internal", "'Internal' button is displayed (correctly) in steps tab for test_FailedMinor1.");
                    break;
                default:
                    throw new FennecRuntimeException("TestResult not implemented: " + testResult.toString());
            }
        } else {
            Assert.fail(testResult + " method for testing steps was not found.");
        }
    }

    /**
     * Checks the tabs of the method details which depend on the status of the test in report 3
     *
     * @param testResult
     */
    @Test(dataProviderClass = TestResultHelper.class, dataProvider = "getAllTestResults", groups = {SystemTestsGroup.SYSTEMTESTSFILTER3})
    public void testT05_checkMenuBarForExistenceOfCorrectTabs(TestResult testResult) throws Exception {
        //TODO use other way - open classes page
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_3.toString()));
        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(testResult);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(0));

        String message = "The %s tab is displayed in a %s method detail page.";
        String passed = "passed";
        String passedMinor = "passed with minor";
        String failed = "failed";
        String failedMinor = "failed with minor";
        String skipped = "skipped";
        String back = "'back'";
        String details = "'details'";
        String steps = "'steps'";
        String stack = "'stack'";
        String minorErrors = "'minor Errors'";
        String evolution = "'evolution'";

        MethodDetailsPage methodDetailsPage;
        DashboardModuleMethodChart dashboardModuleMethodChart = dashboardPage.getMethodChartModule();
        switch (testResult) {
            case PASSED:
                for (GuiElement methodPassed : dashboardModuleMethodChart.getCurrentMethods()) {
                    if (methodPassed.getText().equals("test_TestStatePassed1")) {
                        methodDetailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, methodPassed);
                        AssertCollector.assertTrue(methodDetailsPage.getBackTab().isDisplayed(), String.format(message, back, passed));
                        AssertCollector.assertTrue(methodDetailsPage.getDetailsTab().isDisplayed(), String.format(message, details, passed));
                        AssertCollector.assertTrue(methodDetailsPage.getStepsTab().isDisplayed(), String.format(message, steps, passed));
                        break;
                    }
                }
                break;
            case FAILED:
                GuiElement methodFailed = dashboardModuleMethodChart.getCurrentMethods().get(0);
                methodDetailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, methodFailed);
                AssertCollector.assertTrue(methodDetailsPage.getBackTab().isDisplayed(), String.format(message, back, failed));
                AssertCollector.assertTrue(methodDetailsPage.getDetailsTab().isDisplayed(), String.format(message, details, failed));
                AssertCollector.assertTrue(methodDetailsPage.getStepsTab().isDisplayed(), String.format(message, steps, failed));
                AssertCollector.assertTrue(methodDetailsPage.getStackTab().isDisplayed(), String.format(message, stack, failed));
                AssertCollector.assertTrue(methodDetailsPage.getEvolutionTab().isDisplayed(), String.format(message, evolution, failed));
                break;
            case SKIPPED:
                for (GuiElement methodSkipped : dashboardModuleMethodChart.getCurrentMethods()) {
                    if (methodSkipped.getText().equals("test_TestStateSkipped1")) {
                        methodDetailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, methodSkipped);
                        AssertCollector.assertTrue(methodDetailsPage.getBackTab().isDisplayed(), String.format(message, back, skipped));
                        AssertCollector.assertTrue(methodDetailsPage.getDetailsTab().isDisplayed(), String.format(message, details, skipped));
                        AssertCollector.assertTrue(methodDetailsPage.getStepsTab().isDisplayed(), String.format(message, steps, skipped));
                        break;
                    }
                }
                break;
            case PASSEDMINOR:
                for (GuiElement methodPassedMinor : dashboardModuleMethodChart.getCurrentMethods()) {
                    if (methodPassedMinor.getText().equals("test_PassedMinor1")) {
                        methodDetailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, methodPassedMinor);
                        AssertCollector.assertTrue(methodDetailsPage.getBackTab().isDisplayed(), String.format(message, back, passedMinor));
                        AssertCollector.assertTrue(methodDetailsPage.getDetailsTab().isDisplayed(), String.format(message, details, passedMinor));
                        AssertCollector.assertTrue(methodDetailsPage.getStepsTab().isDisplayed(), String.format(message, steps, passedMinor));
                        AssertCollector.assertTrue(methodDetailsPage.getMinorErrorTab().isDisplayed(), String.format(message, minorErrors, passedMinor));
                        AssertCollector.assertTrue(methodDetailsPage.getEvolutionTab().isDisplayed(), String.format(message, evolution, passedMinor));
                        break;
                    }
                }
                break;
            case FAILEDMINOR:
                GuiElement methodFailedMinor = dashboardModuleMethodChart.getCurrentMethods().get(0);
                methodDetailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, methodFailedMinor);
                AssertCollector.assertTrue(methodDetailsPage.getBackTab().isDisplayed(), String.format(message, back, failedMinor));
                AssertCollector.assertTrue(methodDetailsPage.getDetailsTab().isDisplayed(), String.format(message, details, failedMinor));
                AssertCollector.assertTrue(methodDetailsPage.getStepsTab().isDisplayed(), String.format(message, steps, failedMinor));
                AssertCollector.assertTrue(methodDetailsPage.getStackTab().isDisplayed(), String.format(message, stack, failedMinor));
                AssertCollector.assertTrue(methodDetailsPage.getMinorErrorTab().isDisplayed(), String.format(message, minorErrors, failedMinor));
                AssertCollector.assertTrue(methodDetailsPage.getEvolutionTab().isDisplayed(), String.format(message, evolution, failedMinor));
                break;
            default:
                throw new FennecRuntimeException("TestResult not supported: " + testResult);
        }
    }

    /**
     * This test checks the number of entries in the history of the method details of one chosen method view in report 1
     *
     * @param testResult
     */
    @Test(enabled = false, dataProviderClass = TestResultHelper.class, dataProvider = "getTestResultsExceptInherited", groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT06_checkHistoryViewForNumberOfEntries(TestResult testResult) throws Exception {
        testHistoryViewForNumberOfEntries(testResult, PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()), 1);
    }

    /**
     * Checks the number of entries in the history of the method details of one chosen method view in report 2
     *
     * @param testResult
     */
    @Test(enabled = false, dataProviderClass = TestResultHelper.class, dataProvider = "getAllTestResults", groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    public void testT07_checkHistoryViewForNumberOfEntries(TestResult testResult) throws Exception {
        testHistoryViewForNumberOfEntries(testResult, PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.toString()), 2);
    }

    /**
     * This test checks the number of entries in the history of the method details of one chosen method view in report 3
     *
     * @param testResult
     */
    @Test(enabled = false, dataProviderClass = TestResultHelper.class, dataProvider = "getAllTestResults", groups = {SystemTestsGroup.SYSTEMTESTSFILTER3})
    public void testT08_checkHistoryViewForNumberOfEntries(TestResult testResult) throws Exception {
        testHistoryViewForNumberOfEntries(testResult, PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_3.toString()), 3);
    }

    /**
     * Checks the number of entries in the history of the method details of one chosen method view in report 4
     *
     * @param testResult
     */
    @Test(enabled = false, dataProviderClass = TestResultHelper.class, dataProvider = "getAllTestResults", groups = {SystemTestsGroup.SYSTEMTESTSFILTER4})
    public void testT09_checkHistoryViewForNumberOfEntries(TestResult testResult) throws Exception {
        testHistoryViewForNumberOfEntries(testResult, PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_4.toString()), 4);
    }

    /**
     * Checks the number of entries in the history of the method details of one chosen method view in report 5
     *
     * @param testResult
     */
    @Test(enabled = false, dataProviderClass = TestResultHelper.class, dataProvider = "getAllTestResults", groups = {SystemTestsGroup.SYSTEMTESTSFILTER5})
    public void testT10_checkHistoryViewForNumberOfEntries(TestResult testResult) throws Exception {
        testHistoryViewForNumberOfEntries(testResult, PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_5.toString()), 5);
    }

    /**
     * Checks the number of entries in the history of the method details of one chosen method view in report 6
     *
     * @param testResult
     */
    @Test(enabled = false, dataProviderClass = TestResultHelper.class, dataProvider = "getTestResultsExceptSkipped", groups = {SystemTestsGroup.SYSTEMTESTSFILTER6})
    public void testT11_checkHistoryViewForNumberOfEntries(TestResult testResult) throws Exception {
        testHistoryViewForNumberOfEntries(testResult, PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_6.toString()), 6);
    }

    /**
     * Checks the infos shown in the context view of the method details in report 4
     *
     * @param testResult
     */
    @Test(dataProviderClass = TestResultHelper.class, dataProvider = "getTestResultsPassedAndFailedWithMinor", groups = {SystemTestsGroup.SYSTEMTESTSFILTER4})
    public void testT12_checkContextViewInfos(TestResult testResult) throws Exception {
        //TODO use other way - open classes page
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()));
        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(testResult);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(0));
        for (GuiElement method : dashboardPage.getMethodChartModule().getCurrentMethods()) {
            if (method.getText().equals("test_TestStatePassed1")) {
                MethodDetailsPage detailsPage = dashboardPage.clickMethodDetail(method);
                detailsPage = detailsPage.toggleContext();
                AssertCollector.assertEquals(detailsPage.getContextClassString(), ReportTestUnderTestPassed.class.getSimpleName());
                AssertCollector.assertEquals(detailsPage.getContextSuiteString(), "Report- TestsUnderTest");
                AssertCollector.assertEquals(detailsPage.getContextTestString(), "Passed Creator");
                break;
            } else if (method.getText().equals("test_FailedMinor1")) {
                MethodDetailsPage detailsPage = dashboardPage.clickMethodDetail(method);
                detailsPage = detailsPage.toggleContext();
                AssertCollector.assertEquals(detailsPage.getContextClassString(), "My_Context");
                AssertCollector.assertEquals(detailsPage.getContextSuiteString(), "Report- TestsUnderTest");
                AssertCollector.assertEquals(detailsPage.getContextTestString(), "Failed Creator");
                break;
            }

        }
    }

    /**
     * Check the infos shown in the duration view in report 5
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER5})
    public void testT13_checkDurationViewInfos() throws Exception {
        //TODO use other way - open classes page
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_5.toString()));
        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(TestResult.PASSED);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(0));
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, dashboardPage.getMethodChartModule().getCurrentMethods().get(0));

        AssertCollector.assertTrue(methodDetailsPage.getDuration().contains("h") && methodDetailsPage.getDuration().contains("m") && methodDetailsPage.getDuration().contains("s"), "Time Format of test duration is correct");

        String startDateString = methodDetailsPage.getStartTime();
        try {
            startDateString = startDateString.split("Start ")[1];
        } catch (Exception e) {
            Assert.fail("The start date does not display the 'Start' indicator.");
        }
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMAN);
            Date date = dateFormat.parse(startDateString);
            Date now = new Date();
            AssertCollector.assertTrue(date.before(now), "The start date is in the past");
        } catch (Exception e) {
            Assert.fail("The start date does not have the correct format.");
        }

        String finishDateString = methodDetailsPage.getFinishTime();
        try {
            finishDateString = finishDateString.split("Finish ")[1];
        } catch (Exception e) {
            Assert.fail("The finish date does not display the 'Finish' indicator.");
        }
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMAN);
            Date date = dateFormat.parse(finishDateString);
            Date now = new Date();
            AssertCollector.assertTrue(date.before(now), "The finish date is in the past");
        } catch (Exception e) {
            Assert.fail("The finish date does not have the correct format.");
        }


    }

    /**
     * Checks the evolution tab of the method details in report3
     *
     * @param testResult
     */
    @Test(enabled = false, dataProviderClass = TestResultHelper.class, dataProvider = "getTestResultsExceptSkipped", groups = {SystemTestsGroup.SYSTEMTESTSFILTER3})
    public void testT14_checkEvolutionTab(TestResult testResult) throws Exception {

        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_3.toString()));
        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(testResult);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(0));
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, dashboardPage.getMethodChartModule().getCurrentMethods().get(0));

        switch (testResult) {
            case PASSED:
                AssertCollector.assertTrue(methodDetailsPage.getEvolutionEntry1().isDisplayed() && methodDetailsPage.getEvolutionEntry2().isDisplayed(), "The evolution chart is displayed in a " + testResult + " method details page.");
                break;
            case PASSEDMINOR:
                methodDetailsPage.clickEvolutionTab();
                AssertCollector.assertTrue(methodDetailsPage.getEvolutionEntry1().isDisplayed() && methodDetailsPage.getEvolutionEntry2().isDisplayed(), "The evolution chart is displayed in a " + testResult + " method details page.");
                break;
            case FAILED:
            case FAILEDMINOR:
            default:
                throw new FennecRuntimeException("TestResult not supported: " + testResult);
        }
    }

    /**
     * Checks the number of minor errors in the method details page and the minor errors tab in the method
     * details in report 1
     *
     * @param testResult
     */
    @Test(dataProviderClass = TestResultHelper.class, dataProvider = "getTestResultsMinor", groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT15_checkMinorErrorsPage(TestResult testResult) throws Exception {
        //TODO use other way - open classes page
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()));
        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(testResult);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(0));
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, dashboardPage.getMethodChartModule().getCurrentMethods().get(0));

        AssertCollector.assertTrue(methodDetailsPage.getMinorCount().isDisplayed(), "The button that indicates how many minor errors the test contains is displayed in a " + testResult + " with minor details page.");
        AssertCollector.assertTrue(methodDetailsPage.getMinorCount().getText().contains("1"), "The button that indicates how many errors the test contains indicates correct numbers in a " + testResult + " with minor details page.");

        MethodMinorErrorsPage minorErrorsPage = methodDetailsPage.clickMinorErrorsTab();

        AssertCollector.assertTrue(minorErrorsPage.getAssertion().isDisplayed(), "The assertion message button is displayed in a " + testResult + " with minor details page.");
        AssertCollector.assertTrue(minorErrorsPage.getAssertion().getText().equals("Assert: expected [true] but found [false]"), "The assertion message is correct in a " + testResult + " with minor details page.");

        minorErrorsPage = minorErrorsPage.clickAssertion();

        Assert.assertTrue(minorErrorsPage.getAssertionMessage().contains("eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTest"), "The assertion message is displayed correctly in a " + testResult + " details page.");
    }

    /**
     * Checks the error message in the method details for correctnes in report 2
     *
     * @param testResult
     */
    @Test(dataProviderClass = TestResultHelper.class, dataProvider = "getTestResultsFailed", groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    public void testT16_checkMethodDetailsErrorMessage(TestResult testResult) throws Exception {
        //TODO use other way - open classes page
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.toString()));
        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(testResult);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(0));

        MethodDetailsPage methodDetailsPage;
        String errorMessageMessage = "The error message in a " + testResult + " details page is correct.";
        String fingerprintMessage = "The fingerprint is displayed correctly in a " + testResult + " details page.";
        DashboardModuleMethodChart dashboardModuleMethodChart = dashboardPage.getMethodChartModule();
        switch (testResult) {
            case FAILED:
                for (GuiElement indexMethod : dashboardModuleMethodChart.getCurrentMethods()) {
                    if (indexMethod.getText().equals("test_testLowCorridorFailed3 (1/1)")) {
                        methodDetailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, indexMethod);
                        AssertCollector.assertFalse(methodDetailsPage.getFingerprintString().isDisplayed(), "The fingerprint is not displayed when " + testResult + " details page is first opened.");
                        AssertCollector.assertEquals("ArithmeticException: / by zero", methodDetailsPage.getErrorMessageString().getText(), errorMessageMessage);
                        methodDetailsPage = methodDetailsPage.toggleFingerprint();
                        AssertCollector.assertTrue(methodDetailsPage.getFingerprintString().getText().contains("pageobjects.ExitPointCreatorTestClass2.testCreatorForDifferentExitPoints2"), fingerprintMessage);
                        AssertCollector.assertTrue(methodDetailsPage.getFingerprintString().isDisplayed(), "The fingerprint is displayed in " + testResult + " details page after clicking the fingerprint button.");
                        break;
                    }
                }
                break;
            case FAILEDMINOR:
                for (GuiElement indexMethod : dashboardModuleMethodChart.getCurrentMethods()) {
                    if (indexMethod.getText().equals("test_FailedMinor1 (1/1)")) {
                        methodDetailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, indexMethod);
                        AssertCollector.assertFalse(methodDetailsPage.getFingerprintString().isDisplayed(), "The fingerprint is not displayed when " + testResult + " details page is first opened");
                        AssertCollector.assertEquals("Exception", methodDetailsPage.getErrorMessageString().getText(), errorMessageMessage);
                        methodDetailsPage = methodDetailsPage.toggleFingerprint();
                        AssertCollector.assertEquals("", methodDetailsPage.getFingerprintString().getText(), fingerprintMessage);
                        AssertCollector.assertTrue(methodDetailsPage.getFingerprintString().isDisplayed(), "The fingerprint is displayed in " + testResult + " details page after clicking the fingerprint button.");
                        break;
                    }
                }
                break;
        }
    }

    /**
     * Checks whether a test that is passed but still annotated with @Fails is indicated as so in report 4
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER4})
    public void testT17_checkMethodDetailsIndicationThatPassedTestIsAnnotatedWithFails() throws Exception {
        //TODO use other way - open classes page
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_4.toString()));
        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(TestResult.PASSED);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(0));
        for (GuiElement indexMethod : dashboardPage.getMethodChartModule().getCurrentMethods()) {
            if (indexMethod.getText().contains("test_TestStatePassed2")) {
                MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, indexMethod);
                AssertCollector.assertTrue(methodDetailsPage.getRepairedFailsIndication().isDisplayed(), "The method details page of test_TestStatePassed2 indicates that it is annotated with @Fails.");
                AssertCollector.assertTrue(methodDetailsPage.getRepairedFailsIndication().getText().contains("ticketId=1"), "The indicator that test_TestStatePassed2 is falsely annotated with @Fails does contain the correct ticketId.");
                AssertCollector.assertTrue(methodDetailsPage.getRepairedFailsIndication().getText().contains("description=Does not actually fail."), "The indicator that test_TestStatePassed2 is falsely annotated with @Fails does contain the correct description.");
                break;
            }
        }
    }

    /**
     * Checks whether the annotation marks are displayed correctly on Method Details Page
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT18_checkAnnotationsAreDisplayed() {
        final String methodAllMarkers = "testAllMarkers";
        final String methodRetry = "test_FailedToPassedHistoryWithRetry (1/2)";
        HashMap<String, List<ReportAnnotationType>> methodsTestObjects = new HashMap<>();
        methodsTestObjects.put(methodAllMarkers, Arrays.asList(ReportAnnotationType.NEW, ReportAnnotationType.READY_FOR_APPROVAL, ReportAnnotationType.SUPPORT_METHOD));
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(
                WebDriverManager.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()),
                ReportTestUnderTestAnnotations.class,
                methodAllMarkers
        );
        checkAnnotationsAreDisplayed(methodDetailsPage, methodsTestObjects);
        methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(
                WebDriverManager.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()),
                ReportTestUnderTestRetryHistory.class,
                methodRetry
        );
        checkRetryAnnotationIsDisplayed(methodDetailsPage, methodRetry);
    }

    /**
     * Checks whether the navigation to the DEPENDENCIES tab works as expected
     */
    @Test
    public void testT19_checkDependenciesTabNavigation() {
        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(
                WebDriverManager.getWebDriver(),
                PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()),
                ReportTestUnderTestPassed.class,
                "test_PassedMinor1"
        );
        MethodDependenciesPage dependenciesPage = methodDetailsPage.clickDependenciesTab();
    }

    private void testMethSpec(DashboardPage dashboardPage, TestResult testResult, String methodName, String methodResult) {
        List<GuiElement> methods = dashboardPage.getMethodChartModule().getCurrentMethods();
        GuiElement method1 = null;

        for (GuiElement method : methods) {
            String actualMethodName = method.getText();
            if (actualMethodName.contains(methodName)) {
                method1 = method;
                break;
            }
        }

        if (method1 != null) {
            MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, method1);
            AssertCollector.assertEquals(methodDetailsPage.getMethodNameString(), methodName, "The method name is displayed correctly for " + methodName);
            AssertCollector.assertEquals(methodDetailsPage.getMethodResultString(), methodResult, "The method status is displayed correctly for " + methodName);
            switch (testResult) {
                case FAILEDMINOR:
                    AssertCollector.assertEquals(methodDetailsPage.getStepString(), "in\nTest-Step-3", "The step in which test_FailedMinor1 failed is displayed correctly.");
                    break;
                case FAILED:
                    AssertCollector.assertEquals(methodDetailsPage.getStepString(), "in\nTest-Step-3", "The step in which test_testLowCorridorFailed1 failed is displayed correctly.");
                    break;
                default:
                    break;
            }

        } else {
            AssertCollector.fail(methodName + " method not found in dashboard.");
        }
    }

    private void testHistoryViewForNumberOfEntries(TestResult testResult, String reportDirectory, int numberOfDirectory) throws Exception {
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), reportDirectory);

        dashboardPage.dashboardModuleTestResultPieChart.clickActualRunPieSegmentForTestResult(testResult);
        dashboardPage.click(dashboardPage.dashboardModuleClassBarChart.getCurrentBars().get(0));

        String message = "The history chart is displaying the correct amount of entries for a %s method and report #" + numberOfDirectory + ".";

        DashboardModuleMethodChart dashboardModuleMethodChart = dashboardPage.getMethodChartModule();
        switch (testResult) {
            case PASSED:
                for (GuiElement method : dashboardModuleMethodChart.getCurrentMethods()) {
                    if (method.getText().equals("test_TestStatePassed1")) {
                        MethodDetailsPage detailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, method);
                        if (numberOfDirectory == 1)
                            AssertCollector.assertEquals(detailsPage.getNumberOfAllEntries(), 1, String.format(message, testResult));
                        else
                            AssertCollector.assertEquals(detailsPage.getNumberOfAllEntries(), 1, String.format(message, testResult));
                        break;
                    }
                }
                //TODO must we insert here an Assert.fail
                break;
            case PASSEDMINOR:
                for (GuiElement method : dashboardModuleMethodChart.getCurrentMethods()) {
                    if (method.getText().equals("test_PassedMinor1")) {
                        MethodDetailsPage detailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, method);
                        if (numberOfDirectory == 1)
                            AssertCollector.assertEquals(detailsPage.getNumberOfAllEntries(), 1, String.format(message, testResult));
                        else
                            AssertCollector.assertEquals(detailsPage.getNumberOfAllEntries(), 1, String.format(message, testResult));
                        break;
                    }
                }
                //TODO must we insert here an Assert.fail
                break;
            case FAILED:
                for (GuiElement method : dashboardModuleMethodChart.getCurrentMethods()) {
                    if (method.getText().equals("test_testLowCorridorFailed1 (1/1)")) {
                        MethodDetailsPage detailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, method);
                        AssertCollector.assertTrue(detailsPage.getNumberOfAllEntries() == numberOfDirectory - 1, String.format(message, testResult));
                        break;
                    }
                }
                //TODO must we insert here an Assert.fail
                break;
            case FAILEDMINOR:
                for (GuiElement method : dashboardModuleMethodChart.getCurrentMethods()) {
                    if (method.getText().equals("test_FailedMinor1 (1/1)")) {
                        MethodDetailsPage detailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, method);
                        AssertCollector.assertTrue(detailsPage.getNumberOfAllEntries() == numberOfDirectory - 1, String.format(message, testResult));
                        break;
                    }
                }
                //TODO must we insert here an Assert.fail
                break;
            case SKIPPED:
                for (GuiElement method : dashboardModuleMethodChart.getCurrentMethods()) {
                    if (method.getText().equals("test_TestStateSkipped1")) {
                        MethodDetailsPage detailsPage = GeneralWorkflow.doOpenReportMethodDetailsPage(dashboardPage, method);
                        if (numberOfDirectory == 1)
                            AssertCollector.assertEquals(detailsPage.getNumberOfAllEntries(), 1, String.format(message, testResult));
                        else
                            AssertCollector.assertEquals(detailsPage.getNumberOfAllEntries(), 1, String.format(message, testResult));
                        break;
                    }
                    //TODO must we insert here an Assert.fail
                }
                break;
            default:
                throw new FennecRuntimeException("Unsupported TestResult: " + testResult);
        }
    }

}
