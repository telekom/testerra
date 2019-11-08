package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ExitPointCreaterTestClass1;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * Created by fakr on 04.07.2017.
 */
public class ReportTestUnderTestExecutionFilter extends AbstractTest {

    private static int retryCounter1, retryCounter2 = 0;

/*
    •	Mindestens einen Test der fehlschlägt ohne Minor Error
    •	Mindestens einen Test der fehlschlägt mit Minor Error
    •	Mindestens einen Test der fehlschlägt ohne Minor Error, sich nach einem Retry selbst korrigiert
    •	Mindestens einen Test der fehlschlägt mit Minor Error, sich nach einem Retry selbst korrigiert
    •	Mindestens einen Test der fehlschlägt ohne Minor Error, sich nach einem Retry NICHT korrigiert
    •	Mindestens einen Test der fehlschlägt mit Minor Error, sich nach einem Retry NICHT korrigiert
    •	Mindestens einen Test der erfolgreich ist ohne Minor Error
    •	Mindestens einen Test der erfolgreich ist mit Minor Error
    •	Mindestens einen Test der geskipped ist
    •	Mindestens einen Test der inherited skipped ist
    •	Mindestens einen Test der inherited failed ist
    •	Mindestens einen Test der inherited passed ist
    •	Mindestens einen Test der expected to fail ist

*/

    @Test
    public void test_FilterFailedNoMinor() {
        Assert.assertTrue(false);
    }

    @Test
    public void test_FilterFailedMinor() {
        NonFunctionalAssert.assertTrue(false);
        Assert.assertTrue(false);
    }

    @Test
    public synchronized void test_FilterFailedNoMinorWithPassedRetry() throws Exception {
        retryCounter1++;
        if (retryCounter1 < 2) {
            throw new Exception("RetryUnderTest");
        }
    }

    @Test
    public synchronized void test_FilterFailedMinorWithPassedRetry() throws Exception {
        retryCounter2++;
        if (retryCounter2 < 2) {
            NonFunctionalAssert.assertTrue(false);
            throw new Exception("RetryUnderTest");
        }
    }

    @Test
    public void test_FilterFailedNoMinorWithFailedRetry() throws Exception {
        throw new Exception("RetryUnderTest");
    }

    @Test
    public void test_FilterFailedMinorWithFailedRetry() throws Exception {
        NonFunctionalAssert.assertTrue(false);
        throw new Exception("RetryUnderTest");
    }

    @Test
    public void test_FilterPassedMinor() {
        NonFunctionalAssert.assertTrue(false);
    }

    @Test
    public void test_FilterPassedNoMinor() {
        Assert.assertTrue(true);
    }

    @Test
    public void test_FilterSkipped() {
        throw new SkipException("Skipped because of Skip Exception");
    }

    @Test
    public void test_SkippedInheritedFilter() {
        throw new SkipException("Skipped because of Skip Exception");
    }

    @Test
    public void test_FailedInheritedFilter() {
        Assert.assertTrue(false);
    }

    @Test
    public void test_PassedInheritedFilter() {
        Assert.assertTrue(true);
    }

    @Fails(description = "This is a known bug.")
    @Test
    public void test_FailedMinorAnnotatedWithFail_Run1() throws Exception {
        ExitPointCreaterTestClass1.testCreatorForDifferentExitPoints();
    }

    @Fails(description = "This is a known bug.")
    @Test
    public void test_FailedMinorAnnotatedWithFail_Run2() throws Exception {
        ExitPointCreaterTestClass1.testCreatorForDifferentExitPoints();
    }

    @Fails(description = "This is a known bug.")
    @Test
    public void test_FailedMinorAnnotatedWithFail_Run3() throws Exception {
        ExitPointCreaterTestClass1.testCreatorForDifferentExitPoints();
    }
}
