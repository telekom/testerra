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
                {"test_invalidExpectedFailed_withClass", Status.FAILED}
        };
    }

    @DataProvider
    public static Object[][] provideTestMethodsSkipped() {
        return new Object[][]{
                {"test_Skipped", Status.FAILED, Status.SKIPPED.title},
                {"test_SkippedNoStatus", Status.FAILED, Status.SKIPPED.title},
                {"test_Skipped_dependingOnFailed", Status.NO_RUN, "depends on not successfully finished methods"},
                {"testT01_interceptCrashedDataProvider", Status.SKIPPED, "java.lang.AssertionError"},
                {"testT02_crashedDataProvider", Status.SKIPPED, "java.lang.AssertionError"},
                {"testT03_AssertFailedDataProvider", Status.SKIPPED, "java.lang.AssertionError"}
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

    @DataProvider
    public static Object[][] provideFinalTestResult() {
        return new Object[][]{
                {"*** Stats: SuiteContexts:  2", "SuiteContext"},
                {"*** Stats: TestContexts:   2", "TestContext"},
                {"*** Stats: ClassContexts:  5", "ClassContext"},
                {"*** Stats: MethodContexts: 44", "MethodContexts"},
                {"*** Stats: Test Methods Count: 41 (31 relevant)", "Test methods"},
                {"*** Stats: Failed: 7", "Failed tests"},
                {"*** Stats: Retried: 10", "Retried tests"},
                {"*** Stats: Expected Failed: 6", "Expected failed tests"},
                {"*** Stats: Skipped: 6", "Skipped tests"},
                {"*** Stats: Passed: 12 ⊃ Recovered: 4 ⊃ Repaired: 1", "Passed tests"}
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

    @Test(dataProvider = "provideTestMethodsRetried")
    public void testT02_verifyRetryStatus(final String methodName, final Status finalStatus) {

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
