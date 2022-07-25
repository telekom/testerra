/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.core.server.Server;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.POConfig;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.useragents.ChromeConfig;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.ReportMethodPageType;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportFailureAspectsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportLogsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportThreadsPage;
import io.testerra.report.test.pages.utils.LogLevel;
import io.testerra.report.test.pages.utils.TestData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.lang.reflect.Method;
import java.net.BindException;

/**
 * Abstract test class for tests based on static test site resources
 */
public abstract class AbstractReportTest extends TesterraTest implements Loggable {

    private final static File serverRootDir = FileUtils.getResourceFile("reports");
    private final static Server server = new Server(serverRootDir);

    @DataProvider
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

    @DataProvider
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

    @DataProvider
    public Object[][] dataProviderForDashBoardTestStates() {
        return new Object[][]{{Status.PASSED},
                {Status.FAILED},
                {Status.FAILED_EXPECTED},
                {Status.SKIPPED}};
    }

    @DataProvider
    public static Object[][] dataProviderForDifferentTestStatesWithAmounts() {
        return new Object[][]{
                {5, Status.FAILED},
                {3, Status.FAILED_EXPECTED},
                {4, Status.SKIPPED},
                {5, Status.PASSED}
        };
    }

    @DataProvider
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

    @DataProvider
    public Object[][] dataProviderForFailureAspects() {
        return new Object[][]{
                {"AssertionError"},
                {"PageNotFoundException"},
                {"SkipException"},
                {"RuntimeException"},
                {"Throwable"}
        };
    }

    @DataProvider
    public Object[][] dataProviderForLogLevel() {
        return new Object[][]{
                {LogLevel.INFO}, {LogLevel.WARN}, {LogLevel.ERROR}
        };
    }

    @DataProvider
    public Object[][] dataProviderForFailureAspectsTypes() {
        return new Object[][]{
                {"Major"},
                {"Minor"}
        };
    }

    @DataProvider
    public Object[][] dataProviderForTestsWithoutFailureAspect() {
        return new Object[][]{
                {"test_Passed"},
                {"test_expectedFailedPassed"},
                {"test_GenerateScreenshotManually"}
        };
    }

    @DataProvider
    public Object[][] dataProviderForPreTestMethodsWithStatusFailed() {
        return new Object[][]{
                {"testAssertCollector"},
                {"test_failedPageNotFound"},
                {"test_Failed"},
                {"test_Failed_WithScreenShot"}
        };
    }

    @DataProvider
    public Object[][] dataProviderForPreTestMethodsWithStatusExpectedFailed() {
        return new Object[][]{
                {"test_expectedFailedAssertCollector"},
                {"test_expectedFailedPageNotFound"},
                {"test_expectedFailed"}
        };
    }

    @DataProvider
    public Object[][] dataProviderForPreTestMethodsWithStatusSkipped() {
        return new Object[][]{
                {"test_SkippedNoStatus"},
                {"test_Skipped_AfterErrorInDataProvider"},
                {"test_Skipped_DependingOnFailed"},
                {"test_Skipped_AfterErrorInBeforeMethod"}
        };
    }

    @DataProvider
    public Object[][] dataProviderForNavigationBetweenDifferentPages() {
        return new Object[][]{
                {ReportSidebarPageType.TESTS, ReportTestsPage.class},
                {ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class},
                {ReportSidebarPageType.LOGS, ReportLogsPage.class},
                {ReportSidebarPageType.THREADS, ReportThreadsPage.class}
        };
    }

    @DataProvider
    public Object[][] dataProviderFailureCorridorBounds() {
        PropertyManager.loadProperties("report-ng-tests/src/test/resources/test.properties");
        return new Object[][]{
                {"High", PropertyManager.getIntProperty("tt.failure.corridor.allowed.failed.tests.high")},
                {"Mid", PropertyManager.getIntProperty("tt.failure.corridor.allowed.failed.tests.mid")},
                {"Low", PropertyManager.getIntProperty("tt.failure.corridor.allowed.failed.tests.low")}
        };
    }

    @DataProvider
    public Object[][] dataProviderForPreTestMethods() {
        return new Object[][]{
                //passed
                {new TestData("test_Passed", "GeneratePassedStatusInTesterraReportTest", Status.PASSED, (String) null)},
                // recovered
                {new TestData("test_PassedAfterRetry", "GenerateExpectedFailedStatusInTesterraReportTest", Status.RECOVERED, (String) null)},
                // repaired
                {new TestData("test_expectedFailedPassed", "GenerateExpectedFailedStatusInTesterraReportTest", Status.REPAIRED, (String) null)},
                // skipped
                {new TestData("test_SkippedNoStatus", "GenerateSkippedStatusInTesterraReportTest", Status.SKIPPED, "SkipException: Test Skipped.")},
                // Failed
                {new TestData("testAssertCollector", "GenerateFailedStatusInTesterraReportTest", Status.FAILED, "AssertionError: failed1\n AssertionError: failed2")},
                // expected Failed
                {new TestData("test_expectedFailedAssertCollector", "GenerateExpectedFailedStatusInTesterraReportTest", Status.FAILED_EXPECTED, "AssertionError: failed1\n AssertionError: failed2")},
                // retried
                {new TestData("test_PassedAfterRetry", "GenerateExpectedFailedStatusInTesterraReportTest", Status.RETRIED, "AssertionError: test_FailedToPassedHistoryWithRetry")}
        };
    }

    @DataProvider
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

    @DataProvider
    public static Object[][] dataProviderForPreTestMethods_Classes_States_Types() {
        return new Object[][]{
                //passed
                {new TestData("test_Passed", "GeneratePassedStatusInTesterraReportTest", Status.PASSED, ReportMethodPageType.STEPS)},
                // recovered
                {new TestData("test_PassedAfterRetry", "GenerateExpectedFailedStatusInTesterraReportTest", Status.RECOVERED, ReportMethodPageType.STEPS)},
                // repaired
                {new TestData("test_expectedFailedPassed", "GenerateExpectedFailedStatusInTesterraReportTest", Status.REPAIRED, ReportMethodPageType.STEPS)},
                // skipped
                {new TestData("test_SkippedNoStatus", "GenerateSkippedStatusInTesterraReportTest", Status.SKIPPED, ReportMethodPageType.DETAILS)},
                // Failed
                {new TestData("testAssertCollector", "GenerateFailedStatusInTesterraReportTest", Status.FAILED, ReportMethodPageType.DETAILS)},
                // expected Failed
                {new TestData("test_expectedFailedAssertCollector", "GenerateExpectedFailedStatusInTesterraReportTest", Status.FAILED_EXPECTED, ReportMethodPageType.DETAILS)},
                // retried
                {new TestData("test_PassedAfterRetry", "GenerateExpectedFailedStatusInTesterraReportTest", Status.RETRIED, ReportMethodPageType.DETAILS)}
        };
    }

    @DataProvider
    public static Object[][] dataProviderForFailureAspectsWithCorrespondingStates() {
        return new Object[][]{
                {new TestData("AssertionError: Creating TestStatus 'Failed'", Status.FAILED, Status.FAILED)},
                {new TestData("AssertionError: failed1", Status.FAILED, Status.FAILED_EXPECTED)},
                {new TestData("AssertionError: failed2", Status.FAILED, Status.FAILED_EXPECTED)},
                {new TestData("PageNotFoundException: Test page not reached.", Status.FAILED, Status.FAILED_EXPECTED)},
                {new TestData("AssertionError: Error in @BeforeMethod", Status.SKIPPED, Status.FAILED)},
                {new TestData("AssertionError: 'Failed' on reached Page.", Status.FAILED, null)},
                {new TestData("AssertionError: minor fail", Status.PASSED, null)},
                {new TestData("SkipException: Test Skipped.", Status.SKIPPED, null)},
                {new TestData("RuntimeException: Error in DataProvider.", Status.SKIPPED, null)},
                {new TestData(/*[...]*/"depends on not successfully finished methods", Status.SKIPPED, null)},
                {new TestData("AssertionError: test_FailedToPassedHistoryWithRetry", Status.RETRIED, null)},
                {new TestData("AssertionError: No Oil.", Status.FAILED_EXPECTED, null)}
        };
    }

    @DataProvider
    public Object[][] failureAspectsWithMultipleStatus() {
        return new Object[][]{
                {new TestData("AssertionError: Creating TestStatus 'Failed'", Status.FAILED, Status.FAILED)},
                {new TestData("AssertionError: failed1", Status.FAILED, Status.FAILED_EXPECTED)},
                {new TestData("AssertionError: failed2", Status.FAILED, Status.FAILED_EXPECTED)},
                {new TestData("PageNotFoundException: Test page not reached.", Status.FAILED, Status.FAILED_EXPECTED)},
                {new TestData("AssertionError: Error in @BeforeMethod", Status.SKIPPED, Status.FAILED)}
        };
    }


    @DataProvider
    public Object[][] dataProviderForPreTestMethodsWithFailureAspects() {
        return new Object[][]{
                {new TestData("test_SkippedNoStatus", "SkipException: Test Skipped.")},
                {new TestData("test_Optional_Assert", "AssertionError: minor fail")},
                {new TestData("test_failedPageNotFound", "PageNotFoundException: Test page not reached.")},
                {new TestData("test_expectedFailedPageNotFound", "PageNotFoundException: Test page not reached.")},
        };
    }


    @DataProvider
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


    @BeforeMethod(alwaysRun = true)
    public void configureChromeOptions(Method method) {
        WebDriverManager.setUserAgentConfig(Browsers.chromeHeadless, new ChromeConfig() {
            @Override
            public void configure(ChromeOptions options) {
                options.addArguments("--disable-dev-shm-usage");
            }
        });
    }

    @BeforeTest(alwaysRun = true)
    public void setUp() throws Exception {
        POConfig.setUiElementTimeoutInSeconds(3);
        try {
            server.start(80);
        } catch (BindException e) {
            log().warn("Use already running WebServer: " + e.getMessage());
        }
    }

//    @BeforeMethod(alwaysRun = true)
//    public void visitTestPage(Method method) {
////        visitTestPage(getWebDriver());
//        visitTestPage(WebDriverManager.getWebDriver());
//    }

    /**
     * Open a custom Webdriver session with the default test page.
     *
     * @param driver {@link WebDriver} Current webDriver Instance
     */
    public synchronized void visitTestPage(WebDriver driver) {
        visitTestPage(ReportDashBoardPage.class, driver, getReportDir());
    }


    public synchronized <T extends AbstractReportPage> T visitTestPage(final Class<T> reportPageClass, final WebDriver driver) {
        return visitTestPage(reportPageClass, driver, PropertyManager.getProperty("file.path.content.root"));
    }


    /**
     * Open a custom Webdriver session with the default test page.
     *
     * @param reportPageClass report page that should be reached
     * @param driver          {@link WebDriver} Current Instance
     * @param directory       {@link TestPage} page to open
     */
    public synchronized <T extends AbstractReportPage> T visitTestPage(final Class<T> reportPageClass, final WebDriver driver, final String directory) {
        Assert.assertTrue(serverRootDir.exists(), String.format("Server root directory '%s' doesn't exists", serverRootDir));

        File reportDir = new File(serverRootDir, directory);
        Assert.assertTrue(reportDir.exists(), String.format("Report directory '%s' doesn't exists", reportDir));

        if (!driver.getCurrentUrl().contains(directory)) {
            String baseUrl = String.format("http://localhost:%d/%s", server.getPort(), directory);
            driver.get(baseUrl);
        }
        return PageFactory.create(reportPageClass, driver);
    }

    protected String getReportDir() {
        return PropertyManager.getProperty(TesterraProperties.REPORTDIR, "test-report");
    }


}
