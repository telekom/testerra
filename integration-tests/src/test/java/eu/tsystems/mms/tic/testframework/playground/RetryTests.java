/*
 * Testerra
 *
 * (C) 2021, Martin Großmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.annotations.Retry;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The retry of these tests is controlled by
 * tt.failed.tests.if.throwable.messages in test.properties
 *
 * @author mgn
 */
public class RetryTests extends AbstractWebDriverTest {

    AtomicInteger counter1 = new AtomicInteger(0);
    AtomicInteger counter2 = new AtomicInteger(0);
    AtomicInteger counter3 = new AtomicInteger(0);
    AtomicInteger counter4 = new AtomicInteger(0);

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

    @Test
    @Retry(maxRetries = 4)
    public void test_willPassOnThirdRetry() {
        if (counter3.incrementAndGet() < 3) {
            Assert.fail();
        }
    }

    @Test
    @Retry(maxRetries = 4)
    public void test_willPassOnFifthRetry() {
        if (counter4.incrementAndGet() <= 4) {
            Assert.fail();
        }
    }


}
