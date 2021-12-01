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

    private final Log4jFileReader log4jFileReader = new Log4jFileReader("logs/pre-test-log4j.log");
    private final String methodEndWorkerName = "finish.MethodEndWorker";

    // only test method names that have one specific Status shown in entry with the method name
    @DataProvider
    public static Object[][] provideExpectedStatusPerMethod() {
        return new Object[][]{
                {"test_Failed", Status.FAILED },
                {"test_Passed", Status.PASSED },
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
                {"test_Skipped"},
                {"test_SkippedNoStatus"},
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
                {"testCaseOne"},
                {"test_retryOnWebDriverException"},
                {"test_retryOnUnreachableBrowserException"},
                {"test_retryOnJsonException"}
        };
    }

    @BeforeClass
    public void verifyLogFileExists() {
        Assert.assertTrue(log4jFileReader.existsFile(), "log file exists.");
    }

    @Test(dataProvider = "provideExpectedStatusPerMethod")
    public void verifySimpleTestStatus(final String methodName, final Status expectedTestStatus) {
        log4jFileReader.assertTestStatusPerMethod(methodEndWorkerName, methodName, expectedTestStatus);
    }

    @Test(dataProvider = "providePriorityPerMethod")
    public void verifyPriorityStatus(final String methodName, final Status expectedTestStatus, final int expectedPriority) {

        final String methodStartWorkerName = "start.MethodStartWorker";

        // check basic test status
        log4jFileReader.assertTestStatusPerMethod(methodEndWorkerName, methodName, expectedTestStatus);

        // check priority information
        final List<String> methodEndEntries = log4jFileReader.filterLogForTestMethod(methodStartWorkerName, methodName);
        Assert.assertEquals(methodEndEntries.size(), 1,
                "correct amount of method start entries found.");

        final String priorityEntry = log4jFileReader.filterLogForFollowingEntry(methodEndEntries.get(0));
        Assert.assertTrue(priorityEntry.contains(String.valueOf(expectedPriority)),
                String.format("priority '%s' found in entry '%s'", expectedPriority, priorityEntry));
    }

    @Test(dataProvider = "provideTestMethodsRetried")
    public void verifyRetryStatus(final String methodName) {

        final String classNameForRetrySlug = "testng.RetryAnalyzer";
        final String failedStatus = Status.FAILED.title;
        final String recoveredStatus = Status.RECOVERED.title;

        // check basic test status in log
        final List<String> methodEndEntries = log4jFileReader.filterLogForTestMethod(methodEndWorkerName, methodName);

        Assert.assertEquals(methodEndEntries.size(), 2,
                "correct amount of test status entries found.");
        Assert.assertTrue(methodEndEntries.get(0).contains(failedStatus),
                String.format("'%s' has status '%s'", methodName, failedStatus));
        Assert.assertTrue(methodEndEntries.get(1).contains(recoveredStatus),
                String.format("'%s' has status '%s'", methodName, recoveredStatus));

        // check retry information
        final List<String> retryEntries = log4jFileReader.filterLogForTestMethod(classNameForRetrySlug, methodName);

        Assert.assertEquals(retryEntries.size(), 1,
                String.format("'%s' was retried '%s' time", methodName, 1));

        final String onlyRetryEntry = retryEntries.get(0);
        Assert.assertTrue(onlyRetryEntry.contains("1/1"),
                String.format("entry '%s' has information on initiation of one retry", onlyRetryEntry));
    }

    @Test
    public void testExpectedFailsStatus() {

        final String methodName = "test_retriedExpectedFailed";
        final String classNameForRetrySlug = "testng.RetryAnalyzer";

        // check basic test status
        log4jFileReader.assertTestStatusPerMethod(methodEndWorkerName, methodName, Status.FAILED_EXPECTED);

        // check retry information
        final List<String> retryEntries = log4jFileReader.filterLogForTestMethod(classNameForRetrySlug, methodName);

        Assert.assertEquals(retryEntries.size(), 3,
                String.format("'%s' was retried '%s' time", methodName, 3));

        final String firstRetryEntry = retryEntries.get(0);
        final String secondRetryEntry = retryEntries.get(1);
        final String retryLimitEntry = retryEntries.get(2);

        Assert.assertTrue(firstRetryEntry.contains("1/2"),
                String.format("entry '%s' has information on initiation of first retry", firstRetryEntry));
        Assert.assertTrue(secondRetryEntry.contains("2/2"),
                String.format("entry '%s' has information on initiation of second retry", secondRetryEntry));
        Assert.assertTrue(retryLimitEntry.contains("run limit (2)"),
                String.format("entry '%s' has information on maximum retries", retryLimitEntry));
    }

    @Test(dataProvider = "provideTestMethodsSkipped")
    public void testSkippedStatus(final String methodName) {
        final String failedStatus = Status.FAILED.title;
        final String skippedStatus = Status.SKIPPED.title.toLowerCase();

        // check basic test status in log
        final List<String> foundEntries = log4jFileReader.filterLogForTestMethod(methodEndWorkerName, methodName);
        Assert.assertEquals(foundEntries.size(), 1,
                "correct amount of method end entries found.");
        Assert.assertTrue(foundEntries.get(0).contains(failedStatus),
                String.format("'%s' has status '%s'", methodName, failedStatus));

        // check entry with information for skipped
        final String skippedEntry = log4jFileReader.filterLogForFollowingEntry(foundEntries.get(0));
        Assert.assertTrue(skippedEntry.contains(skippedStatus),
                String.format("status '%s' found in entry '%s'", skippedStatus, skippedEntry));
    }
}
