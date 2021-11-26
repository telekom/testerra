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
    private final String methodWorkerName = "finish.MethodEndWorker";

    // only test method names that occur once in log file == exactly one status per method
    @DataProvider
    public static Object[][] provideExpectedStatusPerMethod() {
        return new Object[][]{
                {"testFailed", Status.FAILED },
                {"testPassed", Status.PASSED },
                {"test_expectedFailed", Status.FAILED_EXPECTED},
                {"test_expectedFailedPassed", Status.PASSED},
                {"test_validExpectedFailed_withMethod", Status.FAILED_EXPECTED},
                {"test_invalidExpectedFailed_withMethod", Status.FAILED},
                {"test_validExpectedFailed_withClass", Status.FAILED_EXPECTED},
                {"test_invalidExpectedFailed_withClass", Status.FAILED}
        };
    }

    @BeforeClass
    public void verifyLogFileExists() {
        Assert.assertTrue(log4jFileReader.existsFile(), "log file exists.");
    }

    @Test(dataProvider = "provideExpectedStatusPerMethod")
    public void verifySimpleTestStatus(final String methodName, final Status expectedTestStatus) {
        log4jFileReader.assertTestStatusPerMethod(methodWorkerName, methodName, expectedTestStatus);
    }

    @Test
    public void testRetryOnWebDriverExceptionStatus() {

        final String methodName = "test_retryOnWebDriverException";
        final List<String> foundEntries = log4jFileReader.filterLogForTestMethod(methodWorkerName, methodName);

        Assert.assertTrue(foundEntries.get(0).contains(Status.FAILED.title), String.format("'%s' has status '%s'", methodName, Status.FAILED.title));
        Assert.assertTrue(foundEntries.get(1).contains(Status.RECOVERED.title), String.format("'%s' has status '%s'", methodName, Status.RECOVERED.title));
    }

    @Test
    public void testRetryOnUnreachableBrowserExceptionStatus() {

        final String methodName = "test_retryOnUnreachableBrowserException";
        final String expectedInitialStatus = Status.RETRIED.title;
        final String expectedStatusAfterRetry = Status.RECOVERED.title;

        final List<String> foundEntries = log4jFileReader.filterLogForTestMethod(methodWorkerName, methodName);

        Assert.assertEquals(foundEntries.size(), 2);
        Assert.assertTrue(foundEntries.get(0).contains(expectedInitialStatus), String.format("'%s' has status '%s'", methodName, expectedInitialStatus));
        Assert.assertTrue(foundEntries.get(1).contains(expectedStatusAfterRetry), String.format("'%s' has status '%s' after retry", methodName, expectedStatusAfterRetry));
    }

    @Test
    public void testRetryOnJsonExceptionStatus() {

        final String methodName = "test_retryOnJsonException";
        final String expectedInitialStatus = Status.RETRIED.title;
        final String expectedStatusAfterRetry = Status.RECOVERED.title;

        final List<String> foundEntries = log4jFileReader.filterLogForTestMethod(methodWorkerName, methodName);

        Assert.assertEquals(foundEntries.size(), 2);
        Assert.assertTrue(foundEntries.get(0).contains(expectedInitialStatus), String.format("'%s' has status '%s'", methodName, expectedInitialStatus));
        Assert.assertTrue(foundEntries.get(1).contains(expectedStatusAfterRetry), String.format("'%s' has status '%s' after retry", methodName, expectedStatusAfterRetry));
    }

    @Test
    public void testExpectedFailsStatus() {

        final String methodName = "test_retriedExpectedFailed";
        final List<String> foundEntries = log4jFileReader.filterLogForTestMethod(methodWorkerName, methodName);

        Assert.assertEquals(foundEntries.size(), 3);
        Assert.assertEquals(foundEntries.get(0), Status.RETRIED.title);
        Assert.assertEquals(foundEntries.get(1), Status.RETRIED.title);
        Assert.assertEquals(foundEntries.get(2), Status.FAILED_EXPECTED.title);
    }
}
