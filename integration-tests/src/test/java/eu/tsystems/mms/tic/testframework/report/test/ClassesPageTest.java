package eu.tsystems.mms.tic.testframework.report.test;

import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.AbstractReportTest;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjetcs.ClassesDetailsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjetcs.ClassesPage;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestDependsOn;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestPassed;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.XrayTest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@TestContext(name = "View-Classes")
public class ClassesPageTest extends AbstractReportTest {


    /**
     * checkAllNumbersOfOneTest
     * Checks whether the numbers of all testresults columns for a single testclass are correct
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    @XrayTest(key = "TAP2DEV-825")
    public void testT01_checkAllNumbersOfOneTest() {
        final String className = ReportTestUnderTestDependsOn.class.getSimpleName();

        final Map<TestResultHelper.TestResultClassesColumn, String> expectedClassesTableRowNumbers = new HashMap<>();
        expectedClassesTableRowNumbers.put(TestResultHelper.TestResultClassesColumn.PASSED, "3 (7)");
        expectedClassesTableRowNumbers.put(TestResultHelper.TestResultClassesColumn.PASSEDMINOR, "0");
        expectedClassesTableRowNumbers.put(TestResultHelper.TestResultClassesColumn.FAILED, "4");
        expectedClassesTableRowNumbers.put(TestResultHelper.TestResultClassesColumn.FAILEDMINOR, "0");
        expectedClassesTableRowNumbers.put(TestResultHelper.TestResultClassesColumn.FAILEDEXPECTED, "0");
        expectedClassesTableRowNumbers.put(TestResultHelper.TestResultClassesColumn.SKIPPED, "3");

        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));

        classesPage.assertNumbersForTestResultsOfOneTestClass(expectedClassesTableRowNumbers, className);

    }

    /**
     * checkClassIndicators
     * Checks whether the success/broken symbol is displayed for a passed/failed testundertest class
     *
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    @XrayTest(key = "TAP2DEV-826")
    public void testT02_checkClassIndicators() {

        final String successClass = ReportTestUnderTestPassed.class.getSimpleName();
        final String brokenClass = ReportTestUnderTestDependsOn.class.getSimpleName();

        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        classesPage.assertSuccessIndicatorIsDisplayedForClass(successClass);
        classesPage.assertBrokenIndicatorIsShownForClass(brokenClass);
    }

    /**
     * checkHidePassedFilter
     * Checks whether the "Hide passed Tests" Checkbox on the CLASSES Page is working.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    @XrayTest(key = "TAP2DEV-827")
    public void testT03_checkHidePassedFilter() {
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        classesPage.hidePassedTests();
        classesPage.assertClassesAreDisplayedForHidePassedTestFilter();
    }

    /**
     * checkLinkToClassesDetails
     * Checks whether the pageflow to ClassesDetailsPage by clicking on a testundertest class works
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    @XrayTest(key = "TAP2DEV-828")
    public void testT04_checkLinkToClassesDetails() {
        final String className = ReportTestUnderTestPassed.class.getSimpleName();
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        classesPage.waitForPageToLoad();
        ClassesDetailsPage classesDetailsPage = classesPage.gotoClassesDetailsPageForClass(className);
        classesDetailsPage.assertPageIsShown();

    }

    /**
     * checkTesterraInformationIsDisplayed
     * Checks whether the "INFORMATION" is displayed
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    @XrayTest(key = "TAP2DEV-829")
    public void testT05_checkTesterraInformationIsDisplayed() throws ParseException {
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        classesPage.assertTesterraInformationIsDisplayed();
    }

    /**
     * checkLegendSymbolsAreDisplayed
     * Checks whether the legend symbols in the class table footer are displayed
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    @XrayTest(key = "TAP2DEV-830")
    public void testT06_checkLegendSymbolsAreDisplayed() {
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        classesPage.assertAllLegendSymbolsAreDisplayed();
    }

}
