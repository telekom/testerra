package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import org.testng.annotations.Test;

/**
 * Created by jlma on 14.11.2016.
 */
public class ReportTestUnderTestCorridorHigh extends AbstractTest {


    @FailureCorridor.High
    @Test
    public void test_testHighCorridorFailed1() throws Exception {
        throw new Exception();
    }
    @FailureCorridor.High
    @Test
    public void test_testHighCorridorFailed2() throws Exception {
        throw new Exception();
    }


}
