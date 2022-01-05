package eu.tsystems.mms.tic.testframework.test.status;

import eu.tsystems.mms.tic.testframework.core.utils.Log4jFileReader;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CheckTestStatusTest extends TesterraTest {

    // log file reader
    private final Log4jFileReader LOG_4_J_FILE_READER = new Log4jFileReader("logs/pre-test-log4j.log");

    // terms for searching log file
    private final String METHOD_START_WORKER_SEARCH_TERM = "start.MethodStartWorker";
    private final String METHOD_END_WORKER_SEARCH_TERM = "finish.MethodEndWorker";
    private final String RETRY_ANALYZER_SEARCH_TERM = "testng.RetryAnalyzer";

    // only test method names that have one specific Status shown in entry with the method name
    @DataProvider
    public static Object[][] provideExpectedStatusPerMethod() {
        return new Object[][]{
                {"test_Failed", Status.FAILED },
                {"test_Passed", Status.PASSED },
                {"testAssertCollector", Status.FAILED},
                {"test_expectedFailed", Status.FAILED_EXPECTED},
                {"test_expectedFailedPassed", Status.PASSED},
                {"test_validExpectedFailed_withMethod", Status.FAILED_EXPECTED},
                {"test_invalidExpectedFailed_withMethod", Status.FAILED},
                {"test_validExpectedFailed_withClass", Status.FAILED_EXPECTED},
                {"test_invalidExpectedFailed_withClass", Status.FAILED}
        };
    }

    @DataProvider
    public static Object[][] provideTestMethodsSkipped() {
        return new Object[][]{
                {"test_Skipped", Status.FAILED, Status.SKIPPED.title},
                {"test_SkippedNoStatus", Status.FAILED, Status.SKIPPED.title},
                {"test_Skipped_dependingOnFailed", Status.NO_RUN, "depends on not successfully finished methods"},
        };
    }

    @DataProvider
    public static Object[][] providePriorityPerMethod() {
        return new Object[][]{
                {"testUnderTest2_and2", Status.PASSED, 2},
                {"testUnderTest1_but3", Status.PASSED, 3},
                {"testUnderTest3_but4", Status.PASSED, 4},
                {"testUnderTest4_but1", Status.PASSED, 1},
        };
    }

    @DataProvider
    public static Object[][] provideTestMethodsRetried() {
        return new Object[][]{
                {"testCaseOne", Status.RECOVERED},
                {"test_retryOnWebDriverException", Status.RECOVERED},
                {"test_retryOnUnreachableBrowserException", Status.RECOVERED},
                {"test_retryOnJsonException", Status.RECOVERED},
                {"test_RetryFailed", Status.FAILED},
                {"test_RetryExpectedFailed", Status.FAILED_EXPECTED},
                {"test_RetryValidExpectedFailed", Status.FAILED_EXPECTED},
                {"test_RetryInvalidExpectedFailed", Status.FAILED}
        };
    }

    @DataProvider
    public static Object[][] provideTestMethodsNoRetry() {
        return new Object[][]{
                {"test_NoRetry"}
        };
    }

    @BeforeClass
    public void verifyLogFileExists() {
        Assert.assertTrue(LOG_4_J_FILE_READER.existsFile(), "log file exists.");
    }

    @Test(dataProvider = "provideExpectedStatusPerMethod")
    public void verifySimpleTestStatus(final String methodName, final Status expectedTestStatus) {
        LOG_4_J_FILE_READER.assertTestStatusPerMethod(METHOD_END_WORKER_SEARCH_TERM, methodName, expectedTestStatus);
    }

    @Test(dataProvider = "providePriorityPerMethod")
    public void verifyPriorityStatus(final String methodName, final Status expectedTestStatus, final int expectedPriority) {
        // check basic test status
        LOG_4_J_FILE_READER.assertTestStatusPerMethod(METHOD_END_WORKER_SEARCH_TERM, methodName, expectedTestStatus);

        // check priority information
        final List<String> methodStartEntries = LOG_4_J_FILE_READER.filterLogForTestMethod(METHOD_START_WORKER_SEARCH_TERM, methodName);
        Assert.assertEquals(methodStartEntries.size(), 1,
                "correct amount of method start entries found.");

        final String priorityEntry = LOG_4_J_FILE_READER.filterLogForFollowingEntry(methodStartEntries.get(0));
        Assert.assertTrue(priorityEntry.contains(String.valueOf(expectedPriority)),
                String.format("priority '%s' found in entry '%s'", expectedPriority, priorityEntry));
    }

    @Test(dataProvider = "provideTestMethodsRetried")
    public void verifyRetryStatus(final String methodName, final Status finalStatus) {


        final String failedStatus = Status.FAILED.title;
        final String finalStatusTitle = finalStatus.title;

        // check basic test status in log
        final List<String> methodEndEntries = LOG_4_J_FILE_READER.filterLogForTestMethod(METHOD_END_WORKER_SEARCH_TERM, methodName);

        Assert.assertEquals(methodEndEntries.size(), 2,
                "correct amount of test status entries found.");
        Assert.assertTrue(methodEndEntries.get(0).contains(failedStatus),
                String.format("'%s' has status '%s'", methodName, failedStatus));
        Assert.assertTrue(methodEndEntries.get(1).contains(finalStatusTitle),
                String.format("'%s' has status '%s'", methodName, finalStatusTitle));

        // check retry information, check of actual status 'retried' not possible in log
        final List<String> retryEntries = LOG_4_J_FILE_READER.filterLogForTestMethod(RETRY_ANALYZER_SEARCH_TERM, methodName);

        final String onlyRetryEntry = retryEntries.get(0);
        Assert.assertTrue(onlyRetryEntry.contains("Send signal for retrying the test (1/1)"),
                String.format("entry '%s' has information on initiation of one retry", onlyRetryEntry));

        // run limit information only logged for failed tests
        if (finalStatus.equals(Status.FAILED) || finalStatus.equals(Status.FAILED_EXPECTED)) {
            final String retryLimitEntry = retryEntries.get(1);
            Assert.assertTrue(retryLimitEntry.contains("limit (1)"),
                    String.format("entry '%s' has information on abortion of retry due to exceeded limit", retryLimitEntry));
        } else {
            Assert.assertEquals(retryEntries.size(), 1,
                    String.format("'%s' was retried '%s' time", methodName, 1));
        }
    }

    @Test(dataProvider = "provideTestMethodsNoRetry")
    public void verifyNoRetryStatus(final String methodName) {

        final String failedStatus = Status.FAILED.title;

        final List<String> methodEndEntries = LOG_4_J_FILE_READER.filterLogForTestMethod(METHOD_END_WORKER_SEARCH_TERM, methodName);
        final String logLine = methodEndEntries.get(0);

        Assert.assertEquals(methodEndEntries.size(), 1,
                "correct amount of test status entries found.");
        Assert.assertTrue(logLine.contains(failedStatus),
                String.format("'%s' has status '%s' in log line '%s'", methodName, failedStatus, logLine));

        final List<String> retryEntries = LOG_4_J_FILE_READER.filterLogForTestMethod(RETRY_ANALYZER_SEARCH_TERM, methodName);
        Assert.assertEquals(retryEntries.size(), 0,
                String.format("no information on initiation of retry found for test '%s'", methodName));
    }

    @Test
    public void testExpectedFailsStatus() {

        final String methodName = "test_retriedExpectedFailed";

        // check basic test status
        LOG_4_J_FILE_READER.assertTestStatusPerMethod(METHOD_END_WORKER_SEARCH_TERM, methodName, Status.FAILED_EXPECTED);

        // check retry information
        final List<String> retryEntries = LOG_4_J_FILE_READER.filterLogForTestMethod(RETRY_ANALYZER_SEARCH_TERM, methodName);

        Assert.assertEquals(retryEntries.size(), 3,
                String.format("'%s' was retried '%s' time", methodName, 3));

        final String firstRetryEntry = retryEntries.get(0);
        final String secondRetryEntry = retryEntries.get(1);
        final String retryLimitEntry = retryEntries.get(2);

        Assert.assertTrue(firstRetryEntry.contains("Send signal for retrying the test (1/2)"),
                String.format("entry '%s' has information on initiation of first retry", firstRetryEntry));
        Assert.assertTrue(secondRetryEntry.contains("Send signal for retrying the test (2/2)"),
                String.format("entry '%s' has information on initiation of second retry", secondRetryEntry));
        Assert.assertTrue(retryLimitEntry.contains("run limit (2)"),
                String.format("entry '%s' has information on maximum retries", retryLimitEntry));
    }

    @Test(dataProvider = "provideTestMethodsSkipped")
    public void testSkippedStatus(final String methodName, final Status expectedInitialStatus, final String skippedInformationInLog) {

        final String expectedInitialStatusTitle = expectedInitialStatus.title;

        // check basic test status in log
        final List<String> foundEntries = LOG_4_J_FILE_READER.filterLogForTestMethod(METHOD_END_WORKER_SEARCH_TERM, methodName);
        final String logLine = foundEntries.get(0);

        Assert.assertEquals(foundEntries.size(), 1,
                "correct amount of method end entries found.");
        Assert.assertTrue(logLine.contains(expectedInitialStatusTitle),
                String.format("'%s' has status '%s' in log line '%s'", methodName, expectedInitialStatusTitle, logLine));

        // check entry with information for skipped
        final String skippedMethodEntry = LOG_4_J_FILE_READER.filterLogForFollowingEntry(logLine);
        Assert.assertTrue(skippedMethodEntry.contains(skippedInformationInLog),
                String.format("status '%s' found in entry '%s'", skippedInformationInLog, skippedMethodEntry));
    }
}
