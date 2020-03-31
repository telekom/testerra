package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ReportTestUnderTestCorridorMid extends AbstractTest {

    @FailureCorridor.Mid
    @Test
    public void test_testMidCorridorFailed1() throws Exception {
        throw new Exception();
    }

    @FailureCorridor.Mid
    @Test
    public void test_testMidCorridorFailed2() throws Exception {
        Assert.assertTrue(false);
    }

    @FailureCorridor.Mid
    @Test
    public void test_testMidCorridorFailed3() throws Exception {
        Assert.assertTrue(false);
    }


}
