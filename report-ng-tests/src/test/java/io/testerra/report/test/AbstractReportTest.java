/*
 * Testerra
 *
 * (C) 2022, Marc Dietrich, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package io.testerra.report.test;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.core.server.Server;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportFailureAspectsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportLogsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportThreadsPage;
import io.testerra.report.test.pages.utils.FailureAspectType;
import io.testerra.report.test.pages.utils.LogLevel;
import io.testerra.report.test.pages.utils.TestData;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.net.BindException;

/**
 * Abstract test class for tests based on static test site resources
 */
public abstract class AbstractReportTest extends AbstractTest implements Loggable {

    private final static File serverRootDir = FileUtils.getResourceFile("reports");
    private final static Server server = new Server(serverRootDir);

    @DataProvider(parallel = true)
    public Object[][] dataProviderForDifferentTestMethodForEachStatus() {
        return new Object[][]{
                {"test_Passed"},
                {"testAssertCollector"},
                {"test_SkippedNoStatus"},
                {"test_expectedFailed"},
                {"test_PassedAfterRetry"},
                {"test_expectedFailedPassed"}
        };
    }

    @DataProvider(parallel = true)
    public Object[][] dataProviderForTestStates() {
        return new Object[][]{
                {Status.PASSED},
                {Status.SKIPPED},
                {Status.FAILED_EXPECTED},
                {Status.FAILED},
                {Status.REPAIRED},
                {Status.RECOVERED},
                {Status.RETRIED}};
    }

    @DataProvider(parallel = true)
    public Object[][] dataProviderForDashBoardTestStates() {
        return new Object[][]{
                {Status.PASSED},
                {Status.FAILED},
                {Status.FAILED_EXPECTED},
                {Status.SKIPPED}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForDifferentTestStatesWithAmounts() {
        return new Object[][]{
                {5, Status.FAILED},
                {3, Status.FAILED_EXPECTED},
                {4, Status.SKIPPED},
                {5, Status.PASSED}
        };
    }

    @DataProvider(parallel = true)
    public Object[][] dataProviderForDifferentTestClasses() {
        return new Object[][]{
                {"GeneratePassedStatusInTesterraReportTest"},
                {"GenerateFailedStatusInTesterraReportTest"},
                {"GenerateSkippedStatusInTesterraReportTest"},
                {"GenerateSkippedStatusViaBeforeMethodInTesterraReportTest"},
                {"GenerateExpectedFailedStatusInTesterraReportTest"},
                {"GenerateScreenshotsInTesterraReportTest"}
        };
    }

    @DataProvider(parallel = true)
    public Object[][] dataProviderForFailureAspects() {
        return new Object[][]{
                {"AssertionError"},
                {"PageNotFoundException"},
                {"SkipException"},
                {"RuntimeException"},
                {"Throwable"}
        };
    }

    @DataProvider(parallel = true)
    public Object[][] dataProviderForLogLevel() {
        return new Object[][]{
                {LogLevel.INFO}, {LogLevel.WARN}, {LogLevel.ERROR}
        };
    }

    @DataProvider(parallel = true)
    public Object[][] dataProviderForFailureAspectsTypes() {
        return new Object[][]{
                { FailureAspectType.MAJOR },
                { FailureAspectType.MINOR }
        };
    }

    @DataProvider(parallel = true)
    public Object[][] dataProviderForTestsWithoutFailureAspect() {
        return new Object[][]{
                {"test_Passed"},
                {"test_expectedFailedPassed"},
                {"test_GenerateScreenshotManually"}
        };
    }

    @DataProvider(parallel = true)
    public Object[][] dataProviderForPreTestMethodsWithStatusFailed() {
        return new Object[][]{
                {new TestData("testAssertCollector", "AssertCollector.fail")},
                {new TestData("test_failedPageNotFound", "PageNotFoundException")},
                {new TestData("test_Failed", "Assert.fail")},
                {new TestData("test_Failed_WithScreenShot", "Assert.fail")}
        };
    }

    @DataProvider(parallel = true)
    public Object[][] dataProviderForPreTestMethodsWithStatusExpectedFailed() {
        return new Object[][]{
                {new TestData("test_expectedFailedAssertCollector", "AssertCollector.fail")},
                {new TestData("test_expectedFailedPageNotFound", "PageNotFoundException")},
                {new TestData("test_expectedFailed", "Assert.fail")}
        };
    }

    @DataProvider(parallel = true)
    public Object[][] dataProviderForPreTestMethodsWithStatusSkipped() {
        return new Object[][]{
                {"test_SkippedNoStatus"},
                {"test_Skipped_AfterErrorInDataProvider"},
                {"test_Skipped_DependingOnFailed"},
                {"test_Skipped_AfterErrorInBeforeMethod"}
        };
    }

    @DataProvider(parallel = true)
    public Object[][] dataProviderForNavigationBetweenDifferentPages() {
        return new Object[][]{
                {ReportSidebarPageType.TESTS, ReportTestsPage.class},
                {ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class},
                {ReportSidebarPageType.LOGS, ReportLogsPage.class},
                {ReportSidebarPageType.THREADS, ReportThreadsPage.class}
        };
    }

    @DataProvider(parallel = true)
    public Object[][] dataProviderFailureCorridorBounds() {
        PropertyManager.loadProperties("report-ng-tests/src/test/resources/test.properties");
        return new Object[][]{
                {"High", PropertyManager.getIntProperty("tt.failure.corridor.allowed.failed.tests.high"), 3},
                {"Mid", PropertyManager.getIntProperty("tt.failure.corridor.allowed.failed.tests.mid"), 2},
                {"Low", PropertyManager.getIntProperty("tt.failure.corridor.allowed.failed.tests.low"), 0}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForPreTestMethods_Classes_States() {
        return new Object[][]{
                //passed
                {new TestData("test_Passed", "GeneratePassedStatusInTesterraReportTest", Status.PASSED)},
                // recovered
                {new TestData("test_PassedAfterRetry", "GenerateExpectedFailedStatusInTesterraReportTest", Status.RECOVERED)},
                // repaired
                {new TestData("test_expectedFailedPassed", "GenerateExpectedFailedStatusInTesterraReportTest", Status.REPAIRED)},
                // skipped
                {new TestData("test_SkippedNoStatus", "GenerateSkippedStatusInTesterraReportTest", Status.SKIPPED)},
                // Failed
                {new TestData("testAssertCollector", "GenerateFailedStatusInTesterraReportTest", Status.FAILED)},
                // expected Failed
                {new TestData("test_expectedFailedAssertCollector", "GenerateExpectedFailedStatusInTesterraReportTest", Status.FAILED_EXPECTED)},
                // retried
                {new TestData("test_PassedAfterRetry", "GenerateExpectedFailedStatusInTesterraReportTest", Status.RETRIED)}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForPreTestMethods_Classes_States_ForStepsType() {
        return new Object[][]{
                //passed
                {new TestData("test_Passed", "GeneratePassedStatusInTesterraReportTest", Status.PASSED)},
                // recovered
                {new TestData("test_PassedAfterRetry", "GenerateExpectedFailedStatusInTesterraReportTest", Status.RECOVERED)},
                // repaired
                {new TestData("test_expectedFailedPassed", "GenerateExpectedFailedStatusInTesterraReportTest", Status.REPAIRED)}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForPreTestMethods_Classes_States_ForDetailsType() {
        return new Object[][]{
                // skipped
                {new TestData("test_SkippedNoStatus", "GenerateSkippedStatusInTesterraReportTest", Status.SKIPPED)},
                // Failed
                {new TestData("testAssertCollector", "GenerateFailedStatusInTesterraReportTest", Status.FAILED)},
                // expected Failed
                {new TestData("test_expectedFailedAssertCollector", "GenerateExpectedFailedStatusInTesterraReportTest", Status.FAILED_EXPECTED)},
                // retried
                {new TestData("test_PassedAfterRetry", "GenerateExpectedFailedStatusInTesterraReportTest", Status.RETRIED)}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForFailureAspectsWithCorrespondingStates() {
        return new Object[][]{
                {new TestData("AssertionError: Creating TestStatus 'Failed'", new Status[]{Status.FAILED, Status.FAILED})},
                {new TestData("AssertionError: failed1", new Status[]{Status.FAILED, Status.FAILED_EXPECTED})},
                {new TestData("AssertionError: failed2", new Status[]{Status.FAILED, Status.FAILED_EXPECTED})},
                {new TestData("PageNotFoundException: Test page not reached.", new Status[]{Status.FAILED, Status.FAILED_EXPECTED})},
                {new TestData("AssertionError: Error in @BeforeMethod", new Status[]{Status.SKIPPED, Status.FAILED})},
                {new TestData("AssertionError: 'Failed' on reached Page.", new Status[]{Status.FAILED})},
                {new TestData("AssertionError: minor fail", new Status[]{Status.PASSED})},
                {new TestData("SkipException: Test Skipped.", new Status[]{Status.SKIPPED})},
                {new TestData("RuntimeException: Error in DataProvider.", new Status[]{Status.SKIPPED})},
                {new TestData(/*[...]*/"depends on not successfully finished methods", new Status[]{Status.SKIPPED})},
                {new TestData("AssertionError: test_FailedToPassedHistoryWithRetry", new Status[]{Status.RETRIED})},
                {new TestData("AssertionError: No Oil.", new Status[]{Status.FAILED_EXPECTED})}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForFailureAspectsWithCorrespondingMethodNames() {
        return new Object[][]{
                {"AssertionError: 'Failed' on reached Page.", Status.FAILED, "test_Failed_WithScreenShot"},
                {"RuntimeException: Error in DataProvider.", Status.SKIPPED, "test_Skipped_AfterErrorInDataProvider"},
                {"AssertionError: minor fail", Status.PASSED, "test_Optional_Assert"}
        };
    }

    @DataProvider(parallel = true)
    public Object[][] failureAspectsWithMultipleStatus() {
        return new Object[][]{
                {new TestData("AssertionError: Creating TestStatus 'Failed'", new Status[]{Status.FAILED, Status.FAILED})},
                {new TestData("AssertionError: failed1", new Status[]{Status.FAILED, Status.FAILED_EXPECTED})},
                {new TestData("AssertionError: failed2", new Status[]{Status.FAILED, Status.FAILED_EXPECTED})},
                {new TestData("PageNotFoundException: Test page not reached.", new Status[]{Status.FAILED, Status.FAILED_EXPECTED})},
                {new TestData("AssertionError: Error in @BeforeMethod", new Status[]{Status.SKIPPED, Status.FAILED})}
        };
    }


    @DataProvider(parallel = true)
    public Object[][] dataProviderForPreTestMethodsWithFailureAspects() {
        return new Object[][]{
                {new TestData("test_SkippedNoStatus", "SkipException: Test Skipped.")},
                {new TestData("test_Optional_Assert", "AssertionError: minor fail")},
                {new TestData("test_failedPageNotFound", "PageNotFoundException: Test page not reached.")},
                {new TestData("test_expectedFailedPageNotFound", "PageNotFoundException: Test page not reached.")},
        };
    }


    @DataProvider(parallel = true)
    public Object[][] dataProviderForPreTestMethodsWithFailureAspect() {
        return new Object[][]{
                // skipped
                {new TestData("test_SkippedNoStatus", Status.SKIPPED)},
                // Failed
                {new TestData("testAssertCollector", Status.FAILED)},
                // expected Failed
                {new TestData("test_expectedFailedAssertCollector", Status.FAILED_EXPECTED)},
                // retried
                {new TestData("test_PassedAfterRetry", Status.RETRIED)}
        };
    }

    @BeforeTest(alwaysRun = true)
    public void setUp() throws Exception {
        try {
            server.start(80);
        } catch (BindException e) {
            log().warn("Use already running WebServer: " + e.getMessage());
        }
    }

    public synchronized <T extends AbstractReportPage> T visitReportPage(final Class<T> reportPageClass, final WebDriver driver) {
        return visitReportPage(reportPageClass, driver, PropertyManager.getProperty("file.path.content.root"));
    }


    /**
     * Open a custom Webdriver session with the default test page.
     *
     * @param reportPageClass report page that should be reached
     * @param driver          {@link WebDriver} Current Instance
     * @param directory       {@link TestPage} page to open
     */
    public synchronized <T extends AbstractReportPage> T visitReportPage(final Class<T> reportPageClass, final WebDriver driver, final String directory) {
        Assert.assertTrue(serverRootDir.exists(), String.format("Server root directory '%s' doesn't exists", serverRootDir));

        File reportDir = new File(serverRootDir, directory);
        Assert.assertTrue(reportDir.exists(), String.format("Report directory '%s' doesn't exists", reportDir));

        final String baseUrl = String.format("http://localhost:%d/%s", server.getPort(), directory);
        driver.get(baseUrl);

        return PageFactory.create(reportPageClass, driver);
    }

    protected String getReportDir() {
        return PropertyManager.getProperty(Report.Properties.BASE_DIR.toString(), "test-report");
    }


}
