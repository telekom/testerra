package eu.tsystems.mms.tic.testframework.report.test;

import eu.tsystems.mms.tic.testframework.AbstractReportTest;
import eu.tsystems.mms.tic.testframework.annotations.TestContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.*;
import eu.tsystems.mms.tic.testframework.report.pageobjects.MethodDetailsPage;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ThreadsPage;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@TestContext(name = "View-Threads")
public class ThreadsPageTest extends AbstractReportTest {

    @DataProvider(parallel = true)
    public Object[][] testMethods(){
        Object[][] result = new Object[][]{
                new Object[]{PropertyManager.getProperty("threadsPageTestMethodNameForPassed"), TestResultHelper.TestResult.PASSED},
                new Object[]{PropertyManager.getProperty("threadsPageTestMethodNameForFailed"), TestResultHelper.TestResult.FAILED},
                new Object[]{PropertyManager.getProperty("threadsPageTestMethodNameForSkipped"), TestResultHelper.TestResult.SKIPPED},
                new Object[]{PropertyManager.getProperty("threadsPageTestMethodNameForFailedMinor"), TestResultHelper.TestResult.FAILEDMINOR},
                new Object[]{PropertyManager.getProperty("threadsPageTestMethodNameForPassedMinor"), TestResultHelper.TestResult.PASSEDMINOR},
                new Object[]{PropertyManager.getProperty("threadsPageTestMethodNameForFailedExpected"), TestResultHelper.TestResult.FAILEDEXPECTED},
                new Object[]{PropertyManager.getProperty("threadsPageTestMethodNameForPassedRetry"), TestResultHelper.TestResult.PASSEDRETRY}
        };
        return result;
    }

    static{
        //load systemtest property file
        PropertyManager.loadProperties("lang_en.properties");
    }

    /**
     * Checks whether the threadsPage will be displayed correctly
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1})
    public void testT01_checkCorrectDisplayOfThreadsPage() {
        ThreadsPage threadsPage = GeneralWorkflow.doOpenBrowserAndReportThreadsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        threadsPage.assertPageIsDisplayedCorrectly();
    }

    /**
     * This test checks for every test status whether the link from the threads page corresponds to the correct methodDetailsPage.
     * It runs once for each possible test status.
     */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER1}, dataProvider = "testMethods")
    public void testT02_checkCorrectLinksFromThreadsPageToMethodDetailsPage(String methodName, TestResultHelper.TestResult methodResult) {
        ThreadsPage threadsPage = GeneralWorkflow.doOpenBrowserAndReportThreadsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(ReportDirectory.REPORT_DIRECTORY_1.getReportDirectory()));
        MethodDetailsPage methodDetailsPage = threadsPage.clickMethodAndOpenMethodDetailsPage(methodName);
        methodDetailsPage.assertCorrectTestMethodIsDisplayed(methodName, methodResult);
    }
}