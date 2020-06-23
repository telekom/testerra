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
package eu.tsystems.mms.tic.testframework.report.test.functional;

import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.AbstractReportTest;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodAssertionsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodDetailsPage;
import eu.tsystems.mms.tic.testframework.report.testundertest.ReportTestUnderTestAssertCollector;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@TestContext(name = "Functional-AssertCollector")
public class AssertCollectorTest extends AbstractReportTest {

    @DataProvider(parallel = true)
    public Object[][] assertionTitles(){
        Object[][] result = new Object[][]{
                new Object[]{"Intentionally failed first"},
                new Object[]{"Intentionally failed second"},
                new Object[]{"Intentionally failed third"},
        };
        return result;
    }

    /**
     * Checks whether the assertionsTab will be displayed when all assertions of the assertCollector are failed.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #992
    public void testT01_checkDisplayOfAssertionsTabAllFailedAssertions() {
        String testMethod = "test_assertCollectorAllFailed";

        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestAssertCollector.class.getSimpleName(), testMethod);
        methodDetailsPage.assertAssertionsTabIsDisplayed();
    }

    /**
     * Checks whether the assertionsTab will be displayed when some assertions of the assertCollector are failed and some passed.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #993
    public void testT02_checkDisplayOfAssertionsTabPassedAndFailedAssertions() {
        String testMethod = "test_assertCollectorPassedAndFailed";

        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestAssertCollector.class.getSimpleName(), testMethod);
        methodDetailsPage.assertAssertionsTabIsDisplayed();
    }

    /**
     * Checks whether the assertionsTab won't be displayed when all assertions of the assertCollector are passed.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    // Test case #994
    public void testT03_checkDisplayOfAssertionsTabAllPassedAssertions() {
        String testMethod = "assertCollectorAllPassed";

        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestAssertCollector.class.getSimpleName(), testMethod);
        methodDetailsPage.assertAssertionsTabIsNotDisplayed();
    }
    
    /**
     * Checks whether all the assertions texts will be correctly displayed in the in the assertionsTab
     * in the case of multiple failed assertions of the assertCollector.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, dataProvider = "assertionTitles")
    // Test case #995
    public void testT04_checkCorrectDisplayOfMultipleAssertionsInAssertionsTab(String assertionTitle) {
        String testMethod = "test_assertCollectorAllFailed";

        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestAssertCollector.class.getSimpleName(), testMethod);
        MethodAssertionsPage methodAssertionsPage = GeneralWorkflow.doOpenReportMethodAssertionsPage(methodDetailsPage);
        methodAssertionsPage.checkWhetherNameOfAssertionExists(assertionTitle);
    }

}
