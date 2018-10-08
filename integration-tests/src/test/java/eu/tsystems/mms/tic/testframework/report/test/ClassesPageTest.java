package eu.tsystems.mms.tic.testframework.report.test;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.general.AbstractTest;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ClassesDetailsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ClassesPage;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestDependsOn;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestPassed;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestSyncFailed;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fakr on 31.05.2017.
 */
@FennecClassContext("View-Classes")
public class ClassesPageTest extends AbstractTest {


    /**
     * checkAllNumbersOfOneTest
     * Checks whether the numbers of all testresults columns for a single testclass are correct
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT01_checkAllNumbersOfOneTest() {
        //TODO sagu check all rows
        final String className = ReportTestUnderTestDependsOn.class.getSimpleName();

        final Map<TestResultHelper.TestResultClassesColumn, String> expectedClassesTableRowNumbers = new HashMap<>();
        expectedClassesTableRowNumbers.put(TestResultHelper.TestResultClassesColumn.PASSED, "3 (7)");
        expectedClassesTableRowNumbers.put(TestResultHelper.TestResultClassesColumn.PASSEDMINOR, "0");
        expectedClassesTableRowNumbers.put(TestResultHelper.TestResultClassesColumn.FAILED, "4");
        expectedClassesTableRowNumbers.put(TestResultHelper.TestResultClassesColumn.FAILEDMINOR, "0");
        expectedClassesTableRowNumbers.put(TestResultHelper.TestResultClassesColumn.FAILEDEXPECTED, "0");
        expectedClassesTableRowNumbers.put(TestResultHelper.TestResultClassesColumn.SKIPPED, "3");

        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()));

        classesPage.assertNumbersForTestResultsOfOneTestClass(expectedClassesTableRowNumbers, className);

    }

    /**
     * checkClassIndicators
     * Checks whether the success/broken symbol is displayed for a passed/failed testundertest class
     *
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT02_checkClassIndicators() {

        final String successClass = ReportTestUnderTestPassed.class.getSimpleName();
        final String brokenClass = ReportTestUnderTestDependsOn.class.getSimpleName();

        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()));
        classesPage.assertSuccessIndicatorIsDisplayedForClass(successClass);
        classesPage.assertBrokenIndicatorIsShownForClass(brokenClass);
    }

    /**
     * checkHidePassedFilter
     * Checks whether the "Hide passed Tests" Checkbox on the CLASSES Page is working.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT03_checkHidePassedFilter() {
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()));
        classesPage.hidePassedTests();
        classesPage.assertClassesAreDisplayedForHidePassedTestFilter();
    }

    /**
     * checkFailedWarningSymbolForTableRow
     * Checks whether the "Sync failed warning" symbol is displayed for a testundertest class
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, enabled=false)
    public void testT04_checkFailedWarningSymbolForTableRow() {

        final String className = ReportTestUnderTestSyncFailed.class.getSimpleName();

        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()));
        classesPage.assertSyncFailedWarningIsDisplayedForTestclass(className);
    }

    /**
     * checkLinkToClassesDetails
     * Checks whether the pageflow to ClassesDetailsPage by clicking on a testundertest class works
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT05_checkLinkToClassesDetails() {
        final String className = ReportTestUnderTestPassed.class.getSimpleName();
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()));
        classesPage.waitForPageToLoad();
        ClassesDetailsPage classesDetailsPage = classesPage.gotoClassesDetailsPageForClass(className);
        classesDetailsPage.assertPageIsShown();

    }

    /**
     * checkFennecInformationIsDisplayed
     * Checks whether the "XETA INFORMATION" is displayed
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    @Fails(ticketString = "XETA-684")
    public void testT06_checkFennecInformationIsDisplayed() throws ParseException {
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()));
        classesPage.assertFennecInformationIsDisplayed();
    }

    /**
     * checkLegendSymbolsAreDisplayed
     * Checks whether the legend symbols in the class table footer are displayed
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT07_checkLegendSymbolsAreDisplayed() {
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.toString()));
        classesPage.assertAllLegendSymbolsAreDisplayed();
    }

}
