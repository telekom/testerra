/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import pageobjects.ExitPointCreaterTestClass1;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class ReportTestUnderTestThreadsDisplay extends AbstractTest {

    private static int retryCounter = 0;


    @Test
    public void test_FilterFailedNoMinor() {
        TimerUtils.sleep(1_000);
        Assert.assertTrue(false);
    }

    @Test
    public void test_FilterFailedMinor() {
        TimerUtils.sleep(1_000);
        NonFunctionalAssert.assertTrue(false);
        Assert.assertTrue(false);
    }

    @Test
    public synchronized void test_FilterPassedRetry() throws Exception {
        retryCounter++;
        if (retryCounter < 2) {
            TimerUtils.sleep(1_000);
            throw new Exception("RetryUnderTest");
        }

        TimerUtils.sleep(1_000);
    }

    @Test
    public void test_FilterPassedMinor() {
        TimerUtils.sleep(1_000);
        NonFunctionalAssert.assertTrue(false);
    }

    @Test
    public void test_FilterPassedNoMinor() {
        TimerUtils.sleep(1_000);
        Assert.assertTrue(true);
    }

    @Test
    public void test_FilterSkipped() {
        TimerUtils.sleep(1_000);
        throw new SkipException("Skipped because of Skip Exception");
    }

    @Fails(description = "This is a known bug.")
    @Test
    public void test_FilterExpectedFailed() throws Exception {
        TimerUtils.sleep(1_000);
        ExitPointCreaterTestClass1.testCreatorForDifferentExitPoints();
    }
}
