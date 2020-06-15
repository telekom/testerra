package eu.tsystems.mms.tic.testframework.report.test.functional;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
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
import org.testng.annotations.Test;

@TestContext(name = "Functional-AssertCollector")
public class AssertCollectorTest extends AbstractReportTest {

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
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, enabled = false)
    @Fails(ticketString = "996")
    // Test case #995
    public void testT04_checkCorrectDisplayOfMultipleAssertionsInAssertionsTab() {
        String testMethod = "test_assertCollectorAllFailed";

        MethodDetailsPage methodDetailsPage = GeneralWorkflow.doOpenBrowserAndReportMethodDetailsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()), ReportTestUnderTestAssertCollector.class.getSimpleName(), testMethod);
        MethodAssertionsPage methodAssertionsPage = GeneralWorkflow.doOpenReportMethodAssertionsPage(methodDetailsPage);

        //TODO write test case after bug ticket 966 is solved
    }
}
