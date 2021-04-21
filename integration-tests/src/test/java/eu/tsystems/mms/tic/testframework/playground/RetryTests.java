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

    AtomicInteger counter = new AtomicInteger(0);

    @Test
    public void retryAlwaysFailed() {

        // Message is already defined in test.properties
        Assert.assertTrue(false, "test_FailedToPassedHistoryWithRetry");
    }

    @Test
    public void secondRetryPassed() {
        this.counter.incrementAndGet();

        if (this.counter.get() == 1) {
            Assert.assertTrue(false, "test_FailedToPassedHistoryWithRetry");
        } else {
            Assert.assertTrue(true);
        }
    }

}
