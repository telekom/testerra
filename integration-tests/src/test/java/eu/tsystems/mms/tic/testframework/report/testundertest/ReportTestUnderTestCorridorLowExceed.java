package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import org.testng.annotations.Test;

/**
 * Created by jlma on 07.12.2016.
 */
public class ReportTestUnderTestCorridorLowExceed extends AbstractTest {

    @FailureCorridor.Low
    @Test
    public void test_testLowExceedCorridorFailedMinor1() throws Exception {
        NonFunctionalAssert.assertTrue(2>3);
        throw new Exception();
    }
}
