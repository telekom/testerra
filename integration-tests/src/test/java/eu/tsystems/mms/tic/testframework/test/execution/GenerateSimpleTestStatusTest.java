package eu.tsystems.mms.tic.testframework.test.execution;

import eu.tsystems.mms.tic.testframework.annotations.InfoMethod;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class GenerateSimpleTestStatusTest extends TesterraTest {

    private final String SKIPPED_EXCEPTION_MESSAGE = String.format("Test %s.", Status.SKIPPED.title);

    @Test
    public void test_Passed() {
        Assert.assertTrue(true, "Creating TestStatus 'Passed'");
    }

    @Test
    public void test_Failed() {
        Assert.fail("Creating TestStatus 'Failed'");
    }

    @InfoMethod
    @Test
    public void test_SkippedNoStatus() {
        throw new SkipException(SKIPPED_EXCEPTION_MESSAGE);
    }

    @Test
    public void test_Skipped() {
        throw new SkipException(SKIPPED_EXCEPTION_MESSAGE);
    }

    @Test(dependsOnMethods = "test_Failed")
    public void test_Skipped_dependingOnFailed() {
        // will be skipped
    }

    @Test
    public void testAssertCollector() {
        AssertCollector.fail("failed1");
        AssertCollector.fail("failed2");
        AssertCollector.assertTrue(true,"passed1");
    }
}
