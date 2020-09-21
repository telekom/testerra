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
import eu.tsystems.mms.tic.testframework.report.AbstractReportTest;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ClassesDetailsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ClassesPage;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestDependsOn;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestPassed;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Test;

@TestContext(name = "View-Classes")
public class ClassesPageTest extends AbstractReportTest {


    /**
     * checkAllNumbersOfOneTest
     * Checks whether the numbers of all testresults columns for a single testclass are correct
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #825
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
    // Test case #826
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
    // Test case #827
    public void testT03_checkHidePassedFilter() {
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));

        classesPage.assertAllPassedClassesAreDisplayed();
        classesPage.assertAllFailedClassesAreDisplayed();

        classesPage.clickButtonToHidePassedTests();
        classesPage.assertAllPassedClassesAreNotDisplayed();
        classesPage.assertAllFailedClassesAreDisplayed();
    }

    /**
     * checkLinkToClassesDetails
     * Checks whether the pageflow to ClassesDetailsPage by clicking on a testundertest class works
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #828
    public void testT04_checkLinkToClassesDetails() {
        final String className = ReportTestUnderTestPassed.class.getSimpleName();
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        ClassesDetailsPage classesDetailsPage = classesPage.gotoClassesDetailsPageForClass(className);
        classesDetailsPage.assertPageIsShown();

    }

    /**
     * checkTesterraInformationIsDisplayed
     * Checks whether the "INFORMATION" is displayed
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #829
    public void testT05_checkTesterraInformationIsDisplayed() throws ParseException {
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        classesPage.assertTesterraInformationIsDisplayed();
    }

    /**
     * checkLegendSymbolsAreDisplayed
     * Checks whether the legend symbols in the class table footer are displayed
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #830
    public void testT06_checkLegendSymbolsAreDisplayed() {
        ClassesPage classesPage = GeneralWorkflow.doOpenBrowserAndReportClassesPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        classesPage.assertAllLegendSymbolsAreDisplayed();
    }

}
