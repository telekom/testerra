package eu.tsystems.mms.tic.testframework.report.test;

import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractMethodStateEntry;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractTestReportNumbers;
import eu.tsystems.mms.tic.testframework.report.general.AbstractMethodStateTest;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.*;
import eu.tsystems.mms.tic.testframework.report.pageobjects.StateChangesPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by jlma on 13.06.2017.
 */
@FennecClassContext("View-StateChanges")
public class StateChangesPageTest extends AbstractMethodStateTest {

    @BeforeMethod(alwaysRun = true)
    @Override
    public void initTestObjects() {
        this.methodStateTestObjects = TestReportOneStateChanges.getAllMethodStateChangesEntries();
    }

    /**
     * Assures that the "bubbles" that indicate the current and previous test state of a passed test with a
     * history are displayed correctly
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2}, enabled=false)
    public void testT07_checkIndicationsForInheritedPassedTestWithHistory() {
        StateChangesPage stateChangesPage = openMethodStatePage(ReportDirectory.REPORT_DIRECTORY_2);
        MethodStateChangesEntry scEntry = TestReportTwoStateChanges.SC4;
        stateChangesPage.assertTestIndications(scEntry);
    }

    /**
     * Assures that the "bubbles" that indicate the current and previous test state of a failed test with a
     * history are displayed correctly
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2}, enabled=false)
    public void testT08_checkIndicationsForFailedTestWithHistory() {
        StateChangesPage stateChangesPage = openMethodStatePage(ReportDirectory.REPORT_DIRECTORY_2);
        MethodStateChangesEntry scEntry = TestReportTwoStateChanges.SC5;
        stateChangesPage.assertTestIndications(scEntry);
    }

    /**
     * Assures that the "bubbles" that indicate the current and previous test state of a skipped test with a
     * history are displayed correctly
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2}, enabled=false)
    public void testT09_checkIndicationsForSkippedTestWithHistory() {
        StateChangesPage stateChangesPage = openMethodStatePage(ReportDirectory.REPORT_DIRECTORY_2);
        MethodStateChangesEntry scEntry = TestReportTwoStateChanges.SC6;
        stateChangesPage.assertTestIndications(scEntry);
    }

    /**
     * Assures that the "bubbles" that indicate the current and previous test state of a failed test without
     * history are displayed correctly in a test report that does have a history
     */
    //@Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    /*public void testT07_checkIndicationsForFailedTestWithoutOwnHistoryButOverallHistory() {
        StateChangesPage stateChangesPage = GeneralWorkflow.doOpenBrowserAndReportStateChangesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.toString()));
        String testName = "test_testHighExceedCorridorFailedMinor1";
        stateChangesPage.assertTestIndications(TestResultHelper.TestResultChangedMethodState.NO_RUN, TestResultHelper.TestResultChangedMethodState.FAILED, testName);
    }*/

    /**
     * Assures that the "bubbles" that indicate the current and previous test state of a test without history
     * that is expected to fail are displayed correctly
     */
    //@Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2})
    /*public void testT08_checkIndicationsForExpectedToFailTestWithoutHistory() {
        StateChangesPage stateChangesPage = GeneralWorkflow.doOpenBrowserAndReportStateChangesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_2.toString()));
        String testName = "test_FailedMinorAnnotatedWithFail";
        stateChangesPage.assertTestIndications(TestResultHelper.TestResultChangedMethodState.NO_RUN, TestResultHelper.TestResultChangedMethodState.FAILED_EXPECTED, testName);
    }*/

    /**
     * Assures that the number of listed state changes is correct in the 1st report
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, enabled=false)
    public void testT10_testNumberOfListedStateChanges() {
        TestReportOneNumbers testReportOneNumbers = new TestReportOneNumbers();
        testNumberOfListedStateChanges(ReportDirectory.REPORT_DIRECTORY_1, testReportOneNumbers);
    }

    /**
     * Assures that the number of listed state changes is correct in the 2nd report
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER2}, enabled=false)
    public void testT11_testNumberOfListedStateChanges() {
        TestReportTwoNumbers testReportTwoNumbers = new TestReportTwoNumbers();
        testNumberOfListedStateChanges(ReportDirectory.REPORT_DIRECTORY_2, testReportTwoNumbers);
    }

    /**
     * Assures that the number of listed state changes is correct in the 3rd report
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER3}, enabled=false)
    public void testT12_testNumberOfListedStateChanges() {
        TestReportThreeNumbers testReportThreeNumbers = new TestReportThreeNumbers();
        testNumberOfListedStateChanges(ReportDirectory.REPORT_DIRECTORY_3, testReportThreeNumbers);
    }

    /**
     * Assures that the number of listed state changes is correct in the 4th report
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER4}, enabled=false)
    public void testT13_testNumberOfListedStateChanges() {
        TestReportFourNumbers testReportFourNumbers = new TestReportFourNumbers();
        testNumberOfListedStateChanges(ReportDirectory.REPORT_DIRECTORY_4, testReportFourNumbers);
    }

    /**
     * Assures that the number of listed state changes is correct in the 5th report
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER5}, enabled=false)
    public void testT14_testNumberOfListedStateChanges() {
        TestReportFiveNumbers testReportFiveNumbers = new TestReportFiveNumbers();
        testNumberOfListedStateChanges(ReportDirectory.REPORT_DIRECTORY_5, testReportFiveNumbers);
    }

    /**
     * Assures that the number of listed state changes is correct in the 6th reportFilter
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER6}, enabled=false)
    public void testT15_testNumberOfListedStateChanges() {
        TestReportSixNumbers testReportSixNumbers = new TestReportSixNumbers();
        testNumberOfListedStateChanges(ReportDirectory.REPORT_DIRECTORY_6, testReportSixNumbers);
    }

    private void testNumberOfListedStateChanges(ReportDirectory reportDirectory, TestNumberHelper testNumberHelper) {
        StateChangesPage stateChangesPage = openMethodStatePage(reportDirectory);
        Assert.assertEquals(stateChangesPage.getNumberOfListedStateChanges(), testNumberHelper.getNumberOfStateChanges(), "The number of listed state changes is correct in the " + reportDirectory);
    }

    @Override
    protected List<? extends AbstractMethodStateEntry> getMethodStateTestObjectForReport(ReportDirectory reportDirectory) {
        switch (reportDirectory) {
            case REPORT_DIRECTORY_1:
                return TestReportOneStateChanges.getAllMethodStateChangesEntries();
            case REPORT_DIRECTORY_2:
                return TestReportTwoStateChanges.getAllMethodStateChangesEntries();
            default: {
                throw new FennecRuntimeException("Report " + reportDirectory + " is not supported");
            }

        }
    }

    @Override
    protected StateChangesPage openMethodStatePage(ReportDirectory reportDirectory) {
        return GeneralWorkflow.doOpenBrowserAndReportStateChangesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(reportDirectory.toString()));
    }

    @Override
    protected int getExpectedNumberOfMethods(AbstractTestReportNumbers expectedNumbers) {
        return expectedNumbers.getNumberOfStateChanges();
    }

}
