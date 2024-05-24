package io.testerra.report.test;

import eu.tsystems.mms.tic.testframework.common.DefaultPropertyManager;
import eu.tsystems.mms.tic.testframework.report.Status;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.pretest.NonExistingPage;
import io.testerra.report.test.pages.report.methodReport.ReportDetailsTab;
import io.testerra.report.test.pages.report.methodReport.ReportStepsTab;
import io.testerra.report.test.pages.report.sideBarPages.ReportFailureAspectsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportLogsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportThreadsPage;
import io.testerra.report.test.pages.utils.FailureAspectType;
import io.testerra.report.test.pages.utils.LogLevel;
import io.testerra.report.test.pages.utils.TestData;
import org.testng.annotations.DataProvider;

public class TestDataProvider {

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForDifferentTestMethodForEachStatus() {
        return new Object[][]{
                {"test_Passed"},
                {"test_AssertCollector"},
                {"test_SkippedNoStatus"},
                {"test_expectedFailed"},
                {"test_PassedAfterRetry"},
                {"test_expectedFailedPassed"}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForTestStates() {
        return new Object[][]{
                {Status.PASSED},
                {Status.SKIPPED},
                {Status.FAILED_EXPECTED},
                {Status.FAILED},
                {Status.REPAIRED},
                {Status.RECOVERED},
                {Status.RETRIED}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForDashBoardTestStates() {
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
                {4, Status.FAILED_EXPECTED},
                {4, Status.SKIPPED},
                {5, Status.PASSED}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForDifferentTestClasses() {
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
    public static Object[][] dataProviderForFailureAspects() {
        return new Object[][]{
                {"AssertionError"},
                {"PageFactoryException"},
                {"SkipException"},
                {"RuntimeException"},
                {"Throwable"}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForLogLevel() {
        return new Object[][]{
                {LogLevel.INFO}, {LogLevel.WARN}, {LogLevel.ERROR}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForFailureAspectsTypes() {
        return new Object[][]{
                {FailureAspectType.MAJOR},
                {FailureAspectType.MINOR}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForTestsWithoutFailureAspect() {
        return new Object[][]{
                {"test_Passed"},
                {"test_expectedFailedPassed"},
                {"test_GenerateScreenshotManually"}
        };
    }

    @DataProvider(parallel = false)
    public static Object[][] dataProviderForPreTestMethodsWithStatusFailed() {
        return new Object[][]{
                {new TestData("test_AssertCollector", "ASSERT.fail(\"failed1\")", "ASSERT.fail(\"failed2\")")},
                {new TestData("test_failedPageNotFound", "PAGE_FACTORY.createPage(NonExistingPage.class, WEB_DRIVER_MANAGER.getWebDriver());")},
                {new TestData("test_Failed", "Assert.fail")},
                {new TestData("test_Failed_WithScreenShot", "Assert.fail")}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForPreTestMethodsWithStatusExpectedFailed() {
        return new Object[][]{
                {new TestData("test_expectedFailedAssertCollector", "ASSERT.fail(\"failed1\");", "ASSERT.fail(\"failed2\");")},
                {new TestData("test_expectedFailedPageNotFound", "PAGE_FACTORY.createPage(NonExistingPage.class, WEB_DRIVER_MANAGER.getWebDriver());")},
                {new TestData("test_expectedFailed", "Assert.fail")}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForPreTestMethodsWithScreenshot() {
        return new Object[][]{
                {"test_takeScreenshotOnExclusiveSession_fails"},
                {"test_takeScreenshotViaCollectedAssertion_fails"}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForPreTestMethodsWithStatusSkipped() {
        return new Object[][]{
                {"test_SkippedNoStatus"},
                {"test_Skipped_AfterErrorInDataProvider"},
                {"test_Skipped_DependingOnFailed"},
                {"test_Skipped_AfterErrorInBeforeMethod"}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForNavigationBetweenDifferentPages() {
        return new Object[][]{
                {ReportSidebarPageType.TESTS, ReportTestsPage.class},
                {ReportSidebarPageType.FAILURE_ASPECTS, ReportFailureAspectsPage.class},
                {ReportSidebarPageType.LOGS, ReportLogsPage.class},
                {ReportSidebarPageType.THREADS, ReportThreadsPage.class}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderFailureCorridorBounds() {
        new DefaultPropertyManager().loadProperties("report-ng-tests/src/test/resources/test.properties");
        return new Object[][]{
                {"High", new DefaultPropertyManager().getLongProperty("tt.failure.corridor.allowed.failed.tests.high"), 3},
                {"Mid", new DefaultPropertyManager().getLongProperty("tt.failure.corridor.allowed.failed.tests.mid"), 2},
                {"Low", new DefaultPropertyManager().getLongProperty("tt.failure.corridor.allowed.failed.tests.low"), 0}
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
                {new TestData("test_AssertCollector", "GenerateFailedStatusInTesterraReportTest", Status.FAILED)},
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
                {new TestData("test_AssertCollector", "GenerateFailedStatusInTesterraReportTest", Status.FAILED)},
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
                {new TestData(String.format("PageFactoryException: Could not create instance of %s", NonExistingPage.class.getSimpleName()), new Status[]{Status.FAILED, Status.FAILED_EXPECTED})},
                {new TestData("AssertionError: Error in @BeforeMethod", new Status[]{Status.SKIPPED, Status.FAILED})},
                {new TestData("AssertionError: 'Failed' on reached Page.", new Status[]{Status.FAILED})},
                {new TestData("AssertionError: minor fail", new Status[]{Status.PASSED})},
                {new TestData("SkipException: Test Skipped.", new Status[]{Status.SKIPPED})},
                {new TestData("SkipException: Method skipped because of failed data provider", new Status[]{Status.SKIPPED})},
                {new TestData("Throwable: depends on not successfully finished methods", new Status[]{Status.SKIPPED})},
                {new TestData("AssertionError: test_FailedToPassedHistoryWithRetry", new Status[]{Status.RETRIED})},
                {new TestData("AssertionError: No Oil.", new Status[]{Status.FAILED_EXPECTED})}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForFailureAspectsWithCorrespondingMethodNames() {
        return new Object[][]{
                {"AssertionError: 'Failed' on reached Page.", Status.FAILED, "test_Failed_WithScreenShot"},
                {"SkipException: Method skipped because of failed data provider", Status.SKIPPED, "test_Skipped_AfterErrorInDataProvider"},
                {"AssertionError: minor fail", Status.PASSED, "test_Optional_Assert"}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] failureAspectsWithMultipleStatus() {
        return new Object[][]{
                {new TestData("AssertionError: Creating TestStatus 'Failed'", new Status[]{Status.FAILED, Status.FAILED})},
                {new TestData("AssertionError: failed1", new Status[]{Status.FAILED, Status.FAILED_EXPECTED})},
                {new TestData("AssertionError: failed2", new Status[]{Status.FAILED, Status.FAILED_EXPECTED})},
                {new TestData(String.format("PageFactoryException: Could not create instance of %s", NonExistingPage.class.getSimpleName()), new Status[]{Status.FAILED, Status.FAILED_EXPECTED})},
                {new TestData("AssertionError: Error in @BeforeMethod", new Status[]{Status.SKIPPED, Status.FAILED})}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForPreTestMethodsWithFailureAspects() {
        return new Object[][]{
                {new TestData("test_SkippedNoStatus", "SkipException: Test Skipped.")},
                {new TestData("test_Optional_Assert", "AssertionError: minor fail")},
                {new TestData("test_failedPageNotFound", String.format("PageFactoryException: Could not create instance of %s on \"\" (data:,)", NonExistingPage.class.getSimpleName()))},
                {new TestData("test_expectedFailedPageNotFound", String.format("PageFactoryException: Could not create instance of %s on \"\" (data:,)", NonExistingPage.class.getSimpleName()))},
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderForPreTestMethodsWithFailureAspect() {
        return new Object[][]{
                // skipped
                {new TestData("test_SkippedNoStatus", Status.SKIPPED)},
                // Failed
                {new TestData("test_AssertCollector", Status.FAILED)},
                // expected Failed
                {new TestData("test_expectedFailedAssertCollector", Status.FAILED_EXPECTED)},
                // retried
                {new TestData("test_PassedAfterRetry", Status.RETRIED)},
                // failed data provider method
                {new TestData( "dataProviderWithError", Status.FAILED)},
                // failed before method
                {new TestData("beforeMethodFailing", Status.FAILED)}
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderMultipleScreenShotTests() {
        return new Object[][]{
                {"test_takeScreenshotOnErrorWithMultipleActiveSessionsError", "GenerateScreenshotsInTesterraReportTest", ReportDetailsTab.class},
                {"test_takeScreenshotViaCollectedAssertion_fails", "GenerateScreenshotsInTesterraReportTest", ReportDetailsTab.class},
                {"test_takeScreenshotOnErrorWithMultipleExclusiveSessions", "GenerateScreenshotsInTesterraReportTest", ReportDetailsTab.class},
                {"test_takeScreenshotsWithMultipleActiveSessions", "GenerateScreenshotsInTesterraReportTest", ReportStepsTab.class},
                {"test_takeScreenshotWithMultipleExclusiveSessions", "GenerateScreenshotsInTesterraReportTest", ReportStepsTab.class},
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderSingleScreenShotTests() {
        return new Object[][]{
                {"test_takeScreenshotOnExclusiveSession_fails", "GenerateScreenshotsInTesterraReportTest", ReportDetailsTab.class},
                {"test_takeExclusiveSessionScreenshotWithMultipleActiveSessions", "GenerateScreenshotsInTesterraReportTest", ReportStepsTab.class},
        };
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProviderValidatorTest() {
        return new Object[][]{
                // Failed Expected
                {"test_expectedFailedWithValidator_isValid", "GenerateExpectedFailedStatusInTesterraReportTest", new String[]{
                        "AssertionError: Expected Fail - validator is: " + true}, Status.FAILED_EXPECTED},
                // Failed
                {"test_expectedFailedWithValidator_isNotValid", "GenerateExpectedFailedStatusInTesterraReportTest", new String[]{
                        "AssertionError: Expected Fail - validator is: " + false}, Status.FAILED},
        };
    }
}
