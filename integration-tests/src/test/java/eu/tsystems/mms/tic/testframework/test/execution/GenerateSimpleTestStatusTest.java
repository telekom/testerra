package eu.tsystems.mms.tic.testframework.test.execution;

import eu.tsystems.mms.tic.testframework.annotations.InfoMethod;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class GenerateSimpleTestStatusTest extends TesterraTest {

    @Test
    public void testPassed() {
        Assert.assertTrue(true, "Creating TestStatus 'Passed'");
    }

    @Test
    public void testFailed() {
        Assert.fail("Creating TestStatus 'Failed'");
    }

    @InfoMethod
    @Test
    public void testSkippedNoStatus() {

        throw new SkipException("Test skipped.");
    }

    @Test
    public void testSkipped() {

        throw new SkipException("Test skipped.");
    }
}
