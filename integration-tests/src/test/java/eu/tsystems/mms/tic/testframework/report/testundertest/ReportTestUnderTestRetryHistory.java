package eu.tsystems.mms.tic.testframework.report.testundertest;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Created by fakr on 04.07.2017.
 */
public class ReportTestUnderTestRetryHistory extends AbstractTest {

    private static int counter = 0;

    @Test
    @Parameters("reportNumber")
    public void test_FailedToPassedHistoryWithRetry(int reportNumber) throws Exception {
        counter++;
        if (counter < 2 && reportNumber < 2) {
            throw new Exception("test_FailedToPassedHistoryWithRetry");
        }
    }

    @Test
    @Parameters("reportNumber")
    public void test_FailedToPassedHistoryNoRetry(int reportNumber) throws Exception {
        if (reportNumber < 2) {
            throw new Exception("test_FailedToPassedHistoryNoRetry");
        }
    }

}
