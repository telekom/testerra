package io.testerra.test.pretest_status;

import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class GenerateSimpleTestStatusTest extends TesterraTest implements AssertProvider {

    private final String SKIPPED_EXCEPTION_MESSAGE = String.format("Test %s.", Status.SKIPPED.title);

    @Test
    public void test_Passed() {
    }

    @Test
    public void test_Failed() {
        Assert.fail("Creating TestStatus 'Failed'");
    }

    @Test
    public void test_Optional_Assert() {
        CONTROL.optionalAssertions(() -> {
            ASSERT.fail("minor fail");
        });
    }

    @Test
    public void test_SkippedNoStatus() {
        throw new SkipException(SKIPPED_EXCEPTION_MESSAGE);
    }

    @Test(dependsOnMethods = "test_Failed")
    public void test_Skipped_dependingOnFailed() {
        // will be skipped
    }

    @Test
    public void testAssertCollector() {
        CONTROL.collectAssertions(() -> {
            ASSERT.fail("failed1");
            ASSERT.fail("failed2");
            ASSERT.assertTrue(true, "passed1");
        });

    }
}
