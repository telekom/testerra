package eu.tsystems.mms.tic.testframework.report.test.functional;

import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.AbstractReportTest;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.BeforeConfiguration;
import eu.tsystems.mms.tic.testframework.report.model.TestReportEightNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestReportNineNumbers;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjetcs.ClassesDetailsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjetcs.ClassesPage;
import eu.tsystems.mms.tic.testframework.report.pageobjetcs.DashboardPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.XrayTest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@TestContext(name = "Functional-TestNGBefore")
public class BeforeScenarioTest extends AbstractReportTest {

    private final String passedControlMethod = "controlMethodAfterBeforeScenarioPassed";
    private final String failedControlMethod = "controlMethodAfterBeforeScenarioFailed";

    /**
     * Checks whether the core test numbers for BeforeSuite test report are as expected
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER8})
    @XrayTest(key = "TAP2DEV-798")
    public void testT01_beforeSuiteScenario_dashboardPage_checkTestNumbers() {
        TestReportEightNumbers testReportEightNumbers = new TestReportEightNumbers();
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_8.getReportDirectory()));
        dashboardPage.dashboardModuleTestResultNumberBreakdown.assertCoreTestNumbers(testReportEightNumbers);
    }

    /**
     * Checks whether the core test numbers for all Before[...] (excl. BeforeSuite) test reports are as expected
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9})
    @XrayTest(key = "TAP2DEV-799")
    public void testT02_beforeScenario_dashboardPage_checkTestNumbers() {
        TestReportNineNumbers testReportNineNumbers = new TestReportNineNumbers();
        DashboardPage dashboardPage = GeneralWorkflow.doOpenBrowserAndReportDashboardPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_9.getReportDirectory()));
        dashboardPage.dashboardModuleTestResultNumberBreakdown.assertCoreTestNumbers(testReportNineNumbers);
    }

    /**
     * Checks whether the BeforeSuite method causes the skipping of the control methods
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER8})
    @XrayTest(key = "TAP2DEV-800")
    public void testT03_checkControlMethodsAreSkippedIfBeforeSuiteMethodsFailed() {
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_8.getReportDirectory()));
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
    @XrayTest(key = "TAP2DEV-801")
    public void testT04_checkControlMethodsAreSkippedIfBeforeMethodsFailed(BeforeConfiguration beforeConfig) {
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_9.getReportDirectory()));
        WebDriverManager.getWebDriver().manage().window().setSize(new Dimension(1920, 1080));
        ClassesDetailsPage classesDetailsPage = classesPage.gotoClassesDetailsPageForClass(beforeConfig.getReportClassName());
        /* Check SKIP-causing method */
        String[] controlMethods = beforeConfig.getControlMethodName();
        if(null != controlMethods){
            for(String controlMethod : controlMethods){
                classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory(beforeConfig.getReportMethodName(), controlMethod, TestResultHelper.TestResult.FAILED);
            }
        } else {
            classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory(beforeConfig.getReportMethodName(), TestResultHelper.TestResult.FAILED);
        }
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory(passedControlMethod, TestResultHelper.TestResult.SKIPPED);
        classesDetailsPage.assertMethodIsDisplayedInTheCorrectTestResultCategory(failedControlMethod, TestResultHelper.TestResult.SKIPPED);
    }

    /**
     * Checks whether the control methods run as expected if all Before[...] methods pass
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9})
    @XrayTest(key = "TAP2DEV-802")
    public void testT05_checkControlMethodsRunIfBeforeMethodsPassed() {
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_9.getReportDirectory()));
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
