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

package eu.tsystems.mms.tic.testframework.test.execution;

import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Testclass to test the behaviour of TestNG in case of a retried test if it is a precondition of another test
 *
 * @author mgn
 */
public class TestNgDependsOnRetryTest extends TesterraTest {

    AtomicInteger counter = new AtomicInteger(0);

    @Test(priority = 1, groups = "SEQUENTIAL")
    public void testCaseOne() {
        this.counter.incrementAndGet();
        if (counter.get() == 1) {
            // Message is already defined in test.properties
            Assert.assertTrue(false, "test_FailedToPassedHistoryWithRetry");
        } else {
            Assert.assertTrue(true);
        }

    }

    @Test(dependsOnMethods = "testCaseOne", priority = 2, groups = "SEQUENTIAL")
    public void testCaseTwo() {
        this.counter.incrementAndGet();
        Assert.assertTrue(true);
    }

    @Test(priority = 999, groups = "SEQUENTIAL")
    public void testCaseThree() {
        Assert.assertEquals(this.counter.get(), 3, "testCaseTwo should executed after retried 'dependsOn' method.");
    }


}
