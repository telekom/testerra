/*
 * Testerra
 *
 * (C) 2021, Clemens Große, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package io.testerra.test.status;

import eu.tsystems.mms.tic.testframework.core.utils.Log4jFileReader;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

public class CheckTestStatusTest extends TesterraTest {

    // log file reader
    private final Log4jFileReader LOG_4_J_FILE_READER = new Log4jFileReader("logs/pre-test-log4j.log");

    // terms for searching log file
    private final String METHOD_END_WORKER_SEARCH_TERM = "finish.MethodEndWorker";
    private final String RETRY_ANALYZER_SEARCH_TERM = "testng.RetryAnalyzer";

    // only test method names that have one specific Status shown in entry with the method name
    @DataProvider
    public static Object[][] provideExpectedStatusPerMethod() {
        return new Object[][]{
                {"test_Failed", Status.FAILED},
                {"test_Optional_Assert", Status.PASSED},
                {"test_Passed", Status.PASSED},
                {"testAssertCollector", Status.FAILED},
                {"test_expectedFailed", Status.FAILED_EXPECTED},
                {"test_expectedFailedPassed", Status.PASSED},
                {"test_validExpectedFailed_withMethod", Status.FAILED_EXPECTED},
                {"test_invalidExpectedFailed_withMethod", Status.FAILED},
                {"test_validExpectedFailed_withClass", Status.FAILED_EXPECTED},
                {"test_invalidExpectedFailed_withClass", Status.FAILED},
                {"testT04_DataProviderWithFailedTests(failed)", Status.FAILED},
                {"testT04_DataProviderWithFailedTests(passed)", Status.PASSED},
                {"testT05_DataProviderWithFailedTestsOptional(passed)", Status.PASSED},
                {"testT05_DataProviderWithFailedTestsOptional(failed)", Status.PASSED},
                {"testT06_DataProviderWithCollectedAssertions(passed)", Status.PASSED},
                {"testT06_DataProviderWithCollectedAssertions(failed)", Status.FAILED},
                {"testT07_NonExistingDataProvider", Status.FAILED},
                {"testT07_AfterClassWithAssertion", Status.PASSED},
                {"testT08_AfterClassWithException", Status.PASSED},
                {"testT05_AfterMethodWithAssertion", Status.PASSED},
                {"testT06_AfterMethodWithException", Status.PASSED},
                {"beforeClassSetup01", Status.FAILED},
                {"beforeClassSetup02", Status.FAILED},
                {"beforeClassSetup03", Status.FAILED},
                {"beforeMethodSetup01", Status.FAILED},
                {"beforeMethodSetup02", Status.FAILED},
                {"afterMethodSetup01", Status.FAILED},
                {"afterMethodSetup02", Status.FAILED},
                {"afterClassSetup01", Status.FAILED},
                {"afterClassSetup02", Status.FAILED},
                {"testT08_DataProviderWithRetry", Status.PASSED},
                {"dataProviderSimple", Status.PASSED},
                {"dataProviderThrowingAssertion", Status.FAILED},
                {"dataProviderThrowingException", Status.FAILED},
                {"dataProviderInClassThrowingException", Status.FAILED},
                {"dataProviderWithNoData", Status.PASSED},
                {"beforeMethodOfDataProvider2Tests", Status.FAILED},
                {"dataProviderOfDataProvider2Tests", Status.PASSED}
        };
    }

    @DataProvider
    public static Object[][] provideTestMethodsSkipped() {
        final String failedDpMessage = "Method skipped because of failed data provider";
        return new Object[][]{
                {"test_SkippedNoStatus", Status.SKIPPED, "org.testng.SkipException"},
                {"test_Skipped_dependingOnFailed", Status.SKIPPED, "depends on not successfully finished methods"},
                {"testT01_interceptCrashedDataProvider", Status.SKIPPED, failedDpMessage},
                {"testT02_crashedDataProvider", Status.SKIPPED, failedDpMessage},
                {"testT03_AssertFailedDataProvider", Status.SKIPPED, failedDpMessage},
                {"testT01_BeforeClassWithAssertion", Status.SKIPPED, "java.lang.AssertionError"},
                {"testT02_BeforeClassWithException", Status.SKIPPED, "java.lang.RuntimeException"},
                {"beforeClassBeforeMethodSetup03", Status.SKIPPED, ""},
                {"testT03_BeforeMethodWithAssertion", Status.SKIPPED, "java.lang.AssertionError"},
                {"testT03_BeforeClassWithException", Status.SKIPPED, "java.lang.RuntimeException"},
                {"testT04_BeforeMethodWithException", Status.SKIPPED, "java.lang.RuntimeException"},
                {"testT01_DataProviderWithFailedSetup(run01)", Status.SKIPPED, "java.lang.AssertionError"},
                {"testT01_DataProviderWithFailedSetup(run02)", Status.SKIPPED, "java.lang.AssertionError"}
        };
    }

    @DataProvider
    public static Object[][] provideTestMethodsRetried() {
        return new Object[][]{
                {"testCaseOne", Status.RECOVERED, 2, 0, 1},
                {"test_retryOnWebDriverException", Status.RECOVERED, 2, 0, 1},
                {"test_retryOnUnreachableBrowserException", Status.RECOVERED, 2, 0, 1},
                {"test_retryOnJsonException", Status.RECOVERED, 2, 0, 1},
                {"test_RetryFailed", Status.FAILED, 2, 0, 1},
                {"test_RetryExpectedFailed", Status.FAILED_EXPECTED, 2, 0, 1},
                {"test_RetryValidExpectedFailed", Status.FAILED_EXPECTED, 2, 0, 1},
                {"test_RetryInvalidExpectedFailed", Status.FAILED, 2, 0, 1},
                {"testT08_DataProviderWithRetry", Status.RECOVERED, 3, 1, 2}
        };
    }

    @DataProvider
    public static Object[][] provideTestMethodsNoRetry() {
        return new Object[][]{
                {"test_NoRetry"}
        };
    }

    @DataProvider
    public static Object[][] provideFinalTestResult() {
        return new Object[][]{
                {"*** Stats: SuiteContexts:  3", "SuiteContext"},
                {"*** Stats: TestContexts:   3", "TestContext"},
                {"*** Stats: ClassContexts:  17", "ClassContext"},
                {"*** Stats: MethodContexts: 89", "MethodContexts"},
                {"*** Stats: Test Methods Count: 62 (51 relevant)", "Test methods"},
                {"*** Stats: Failed: 10", "Failed tests"},
                {"*** Stats: Retried: 11", "Retried tests"},
                {"*** Stats: Expected Failed: 7", "Expected failed tests"},
                {"*** Stats: Skipped: 12", "Skipped tests"},
                {"*** Stats: Passed: 22 ⊃ Recovered: 5 ⊃ Repaired: 1", "Passed tests"}
        };
    }

    @BeforeClass
    public void verifyLogFileExists() {
        Assert.assertTrue(LOG_4_J_FILE_READER.existsFile(), "Log file exists.");
    }

    @Test(dataProvider = "provideExpectedStatusPerMethod")
    public void testT01_verifySimpleTestStatus(final String methodName, final Status expectedTestStatus) {
        LOG_4_J_FILE_READER.assertTestStatusPerMethod(METHOD_END_WORKER_SEARCH_TERM, methodName, expectedTestStatus);
    }

    /**
     *
     * @param methodName Searched name of test method
     * @param finalStatus Final status of test method
     * @param count Count of found methods in log
     * @param retriedIndex Index with first retried in log
     * @param finalIndex Index with final status in log
     */
    @Test(dataProvider = "provideTestMethodsRetried")
    public void testT02_verifyRetryStatus(final String methodName, final Status finalStatus, final int count, final int retriedIndex, final int finalIndex) {

        final String failedStatus = Status.FAILED.title;
        final String finalStatusTitle = finalStatus.title;

        // check basic test status in log
        final List<String> methodEndEntries = LOG_4_J_FILE_READER.filterLogForTestMethod(METHOD_END_WORKER_SEARCH_TERM, methodName);

        Assert.assertEquals(methodEndEntries.size(), count, "correct amount of test status entries found.");
        Assert.assertTrue(methodEndEntries.get(retriedIndex).contains(failedStatus), String.format("'%s' has status '%s'", methodName, failedStatus));
        Assert.assertTrue(methodEndEntries.get(finalIndex).contains(finalStatusTitle), String.format("'%s' has status '%s'", methodName, finalStatusTitle));

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
                    String.format("'%s' should retried '%s' time", methodName, 1));
        }
    }

    @Test(dataProvider = "provideTestMethodsNoRetry")
    public void testT03_verifyNoRetryStatus(final String methodName) {

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
    public void testT04_verifyExpectedFailsStatus() {

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
    public void testT05_verifySkippedStatus(final String methodName, final Status expectedInitialStatus, final String skippedInformationInLog) {

        final String expectedInitialStatusTitle = expectedInitialStatus.title;

        // check basic test status in log
        final List<String> foundEntries = LOG_4_J_FILE_READER.filterLogForTestMethod(METHOD_END_WORKER_SEARCH_TERM, methodName);
        final String logLine = foundEntries.get(0);

        Assert.assertEquals(foundEntries.size(), 1, "correct amount of method end entries found.");
        Assert.assertTrue(logLine.contains(expectedInitialStatusTitle), String.format("'%s' has status '%s' in log line '%s'", methodName, expectedInitialStatusTitle, logLine));

        // check entry with information for skipped
        final String skippedMethodEntry = LOG_4_J_FILE_READER.filterLogForFollowingEntry(logLine);
        Assert.assertTrue(skippedMethodEntry.contains(skippedInformationInLog),
                String.format("status '%s' found in entry '%s'", skippedInformationInLog, skippedMethodEntry));
    }

    @Test(dataProvider = "provideFinalTestResult")
    public void testT06_verifyCompleteResult(final String resultString, final String testObject) {
        List<String> foundEntries = LOG_4_J_FILE_READER.filterLogForString(resultString);
        Assert.assertEquals(foundEntries.size(), 1, String.format("The count of %s should contains in log with the string '%s'.", testObject, resultString));
    }
}
