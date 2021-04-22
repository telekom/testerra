package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.annotations.Retry;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 22.04.2021
 *
 * @author mgn
 */
public class DependsOnTests extends AbstractWebDriverTest {

    AtomicInteger counter = new AtomicInteger(0);

    @Test
    public void testCaseSingle() {
        Assert.assertTrue(true);
    }

    @Test
    public void testCaseOne() {
        Assert.assertTrue(true);
    }

    @Test(dependsOnMethods = "testCaseOne")
    @Retry(maxRetries = 2)
    public void testCaseTwo() {
        this.counter.incrementAndGet();
        if (counter.get() < 3) {
            // Message is already defined in test.properties
            Assert.assertTrue(false, "test_FailedToPassedHistoryWithRetry");
        } else {
//            Assert.assertTrue(false);
            Assert.assertTrue(true);
        }

    }

    @Test(dependsOnMethods = "testCaseTwo")
    public void testCaseThree() {
        Assert.assertTrue(true);
    }

}
