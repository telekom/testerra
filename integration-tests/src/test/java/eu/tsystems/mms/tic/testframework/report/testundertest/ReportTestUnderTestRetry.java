package eu.tsystems.mms.tic.testframework.report.testundertest;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.sql.SQLRecoverableException;

/**
 * Created by jlma on 16.05.2017.
 */
public class ReportTestUnderTestRetry extends AbstractTest {

    private static int countertest_ExceptionRetryTest = 0;
    private static int countertest_MethodWillPassOnRetry = 0;

    @Test
    public void test_TestRetryExceptionTrigger() throws SQLRecoverableException {
        throw new SQLRecoverableException("Getrennte Verbindung");
    }

    @Test
    public void test_TestRetryMessageTrigger() throws Exception {
        throw new Exception("RetryUnderTest");
    }

    @Test
    public void test_ExceptionRetryTest() throws Exception {
        countertest_ExceptionRetryTest++;
        if (countertest_ExceptionRetryTest < 2) {
            throw new Exception("RetryUnderTest");
        }
    }

    @Test
    public void test_TestRetryTriggerInASubcause() throws Exception {
        Exception exception = new Exception("RetryUnderTest");
        throw new Exception("Nothing of interest", exception);
    }

    @Test
    public void test_TestNoRetryTriggerInASubcause() throws Exception {
        Exception exception = new Exception("Nothing of interest");
        throw new Exception("Nothing of interest at all", exception);
    }

    @DataProvider(name = "retryDP")
    public Object[][] createRetryDP() {
        Object[][] objects = new Object[3][1];
        objects[0][0] = "Retry1";
        objects[1][0] = "Retry2";
        objects[2][0] = "Retry3";
        return objects;
    }

    @Test(dataProvider = "retryDP")
    public void test_DataProviderTest(String retryDPLabel) throws Exception {
        // retry 2 times with 3 data provider objects
        throw new Exception("RetryUnderTest " + retryDPLabel);
    }

    @Test()
    public void test_MethodWillPassOnRetry() throws Exception {

        if (countertest_MethodWillPassOnRetry == 0) {
            countertest_MethodWillPassOnRetry++;
            throw new Exception("RetryUnderTest");
        }
    }

    @Test(dependsOnMethods = "test_MethodWillPassOnRetry")
    public void test_MethodDependsOnMethodThatPassesInRetry() {
        Assert.assertTrue(true);
    }

    @AfterMethod(alwaysRun = true)
    public void test_AlwaysRunAfterMethod() {
        Assert.assertTrue(true);
    }

}
