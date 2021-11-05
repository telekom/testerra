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
import eu.tsystems.mms.tic.testframework.annotations.Fails;
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
