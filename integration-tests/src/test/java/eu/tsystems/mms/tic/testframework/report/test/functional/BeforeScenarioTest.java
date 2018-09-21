package eu.tsystems.mms.tic.testframework.report.test.functional;

import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.BeforeConfiguration;
import eu.tsystems.mms.tic.testframework.report.model.TestReportEightNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestReportNineNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ClassesDetailsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ClassesPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Created by fakr on 07.11.2017
 */
@FennecClassContext("Functional-TestNGBefore")
public class BeforeScenarioTest {

    private final String passedControlMethod = "controlMethodAfterBeforeScenarioPassed";
    private final String failedControlMethod = "controlMethodAfterBeforeScenarioFailed";

    /**
     * Checks whether the core test numbers for BeforeSuite test report are as expected
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER8})
    public void testT01_checkTestNumbersBeforeSuite() {
        TestReportEightNumbers testReportEightNumbers = new TestReportEightNumbers();
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_8.toString()));
        dashboardPage.dashboardModuleTestResultNumberBreakdown.assertCoreTestNumbers(testReportEightNumbers, false, false, true, false);
    }

    /**
     * Checks whether the core test numbers for all Before[...] (excl. BeforeSuite) test reports are as expected
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9})
    public void testT02_checkTestNumbersBefore() {
        TestReportNineNumbers testReportNineNumbers = new TestReportNineNumbers();
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_9.toString()));
        dashboardPage.dashboardModuleTestResultNumberBreakdown.assertCoreTestNumbers(testReportNineNumbers, true, true, true, false);
    }

    /**
     * Checks whether the BeforeSuite method causes the skipping of the control methods
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER8})
    public void testT03_checkControlMethodsAreSkippedIfBeforeSuiteMethodsFailed() {
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_8.toString()));
        ClassesDetailsPage classesDetailsPage = classesPage.gotoClassesDetailsPageForClass(BeforeConfiguration.BEFORE_SUITE.getReportClassName());
        /* Check SKIP-causing method */
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory(BeforeConfiguration.BEFORE_SUITE.getReportMethodName(), TestResultHelper.TestResult.FAILED);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory(passedControlMethod, TestResultHelper.TestResult.SKIPPED);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory(failedControlMethod, TestResultHelper.TestResult.SKIPPED);

    }

    /**
     * Checks whether the Before[...] methods cause the skipping of the control methods
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9}, dataProvider = "beforeDP")
    public void testT04_checkControlMethodsAreSkippedIfBeforeMethodsFailed(BeforeConfiguration beforeConfig) {
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_9.toString()));
        ClassesDetailsPage classesDetailsPage = classesPage.gotoClassesDetailsPageForClass(beforeConfig.getReportClassName());
        /* Check SKIP-causing method */
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory(beforeConfig.getReportMethodName(), TestResultHelper.TestResult.FAILED);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory(passedControlMethod, TestResultHelper.TestResult.SKIPPED);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory(failedControlMethod, TestResultHelper.TestResult.SKIPPED);
    }

    /**
     * Checks whether the control methods run as expected if all Before[...] methods pass
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9})
    public void testT05_checkControlMethodsRunIfBeforeMethodsPassed() {
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_9.toString()));
        ClassesDetailsPage classesDetailsPage = classesPage.gotoClassesDetailsPageForClass(BeforeConfiguration.ALL.getReportClassName());
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory(passedControlMethod, TestResultHelper.TestResult.PASSED);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory(failedControlMethod, TestResultHelper.TestResult.FAILED);
    }

    /**
     * Provides data for Before[...] (excl. BeforeSuite) methods: class name and method name
     *
     * @return data object with the reliable Before[...] configuration
     */
    @DataProvider(name = "beforeDP")
    public Object[][] createBeforeDP() {
        Object[][] objects = new Object[4][1];
        objects[0][0] = BeforeConfiguration.BEFORE_TEST;
        objects[1][0] = BeforeConfiguration.BEFORE_GROUPS;
        objects[2][0] = BeforeConfiguration.BEFORE_CLASS;
        objects[3][0] = BeforeConfiguration.BEFORE_METHOD;
        return objects;
    }


}
