package eu.tsystems.mms.tic.tapas;

import eu.tsystems.mms.tic.testframework.report.testundertest.AbstractTest;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * Created by sagu on 30.08.2019
 **/
public class TapasTaskTest extends AbstractTest {

    @Test
    public void test_TestStatePassed() {
        Assert.assertTrue(true);
    }

    @Test
    public void test_TestStateFailed() {
        Assert.assertTrue(false);
    }

    @Test
    public void test_TestStateSkipped() {
        throw new SkipException("Skipped because of Skip Exception");
    }

    @Test
    public void test_TestStateExecuting() {
        //sleeps for three minutes
        TimerUtils.sleep(180000);
    }

    @Test
    public void test_TestStateCrushed() {
        //TODO how to do this: test throws an ExitCode between 1 and 98
        //throw new ExitCode();
    }
}
