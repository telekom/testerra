package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import org.testng.annotations.Test;

public class ReportTestUnderTestAssertCollector extends AbstractTest {

    @Test
    public void test_assertCollectorAllFailed() {
        AssertCollector.assertTrue(false, "Intentionally failed");
        AssertCollector.assertTrue(false,"Intentionally failed");
        AssertCollector.assertTrue(false, "Intentionally failed");
    }

    @Test
    public void test_assertCollectorPassedAndFailed() {
        AssertCollector.assertTrue(true);
        AssertCollector.assertTrue(false, "Intentionally failed");
        AssertCollector.assertTrue(true);
    }

    @Test
    public void test_assertCollectorAllPassed() {
        AssertCollector.assertTrue(true);
        AssertCollector.assertTrue(true);
        AssertCollector.assertTrue(true);
    }


}
