package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 21.04.2021
 *
 * @author mgn
 */
public class RetryTests extends AbstractWebDriverTest {

    AtomicInteger counter1 = new AtomicInteger(0);
    AtomicInteger counter2 = new AtomicInteger(0);

    @Test
    public void retryAlwaysFailed() {
        // Message is already defined in test.properties
        Assert.assertTrue(false, "test_FailedToPassedHistoryWithRetry");
    }

    @Test
    public void secondRetryPassed() {
        this.counter1.incrementAndGet();

        if (this.counter1.get() == 1) {
            Assert.assertTrue(false, "test_FailedToPassedHistoryWithRetry");
        } else {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void secondRetryDependsOnMethod() {
        this.counter2.incrementAndGet();

        if (this.counter2.get() == 1) {
            Assert.assertTrue(false, "test_FailedToPassedHistoryWithRetry");
        } else {
            Assert.assertTrue(true);
        }
    }

    @Test(dependsOnMethods = "secondRetryDependsOnMethod")
    public void secondRetryCallOfDependsOn() {
        Assert.assertTrue(true);
    }


}
