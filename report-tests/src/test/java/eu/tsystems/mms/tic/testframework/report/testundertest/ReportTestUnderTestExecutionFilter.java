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
import eu.tsystems.mms.tic.testframework.execution.testng.OptionalAssert;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import io.testerra.report.test.pages.ExitPointCreaterTestClass1;

public class ReportTestUnderTestExecutionFilter extends AbstractTest {

    private static int retryCounter1, retryCounter2 = 0;

/*
    •	Mindestens einen Test der fehlschlägt ohne Minor Error
    •	Mindestens einen Test der fehlschlägt mit Minor Error
    •	Mindestens einen Test der fehlschlägt ohne Minor Error, sich nach einem Retry selbst korrigiert
    •	Mindestens einen Test der fehlschlägt mit Minor Error, sich nach einem Retry selbst korrigiert
    •	Mindestens einen Test der fehlschlägt ohne Minor Error, sich nach einem Retry NICHT korrigiert
    •	Mindestens einen Test der fehlschlägt mit Minor Error, sich nach einem Retry NICHT korrigiert
    •	Mindestens einen Test der erfolgreich ist ohne Minor Error
    •	Mindestens einen Test der erfolgreich ist mit Minor Error
    •	Mindestens einen Test der geskipped ist
    •	Mindestens einen Test der inherited skipped ist
    •	Mindestens einen Test der inherited failed ist
    •	Mindestens einen Test der inherited passed ist
    •	Mindestens einen Test der expected to fail ist

*/

    @Test
    public void test_FilterFailedNoMinor() {
        Assert.assertTrue(false);
    }

    @Test
    public void test_FilterFailedMinor() {
        OptionalAssert.assertTrue(false);
        Assert.assertTrue(false);
    }

    @Test
    public synchronized void test_FilterFailedNoMinorWithPassedRetry() throws Exception {
        retryCounter1++;
        if (retryCounter1 < 2) {
            throw new Exception("RetryUnderTest");
        }
    }

    @Test
    public synchronized void test_FilterFailedMinorWithPassedRetry() throws Exception {
        retryCounter2++;
        if (retryCounter2 < 2) {
            OptionalAssert.assertTrue(false);
            throw new Exception("RetryUnderTest");
        }
    }

    @Test
    public void test_FilterFailedNoMinorWithFailedRetry() throws Exception {
        throw new Exception("RetryUnderTest");
    }

    @Test
    public void test_FilterFailedMinorWithFailedRetry() throws Exception {
        OptionalAssert.assertTrue(false);
        throw new Exception("RetryUnderTest");
    }

    @Test
    public void test_FilterPassedMinor() {
        OptionalAssert.assertTrue(false);
    }

    @Test
    public void test_FilterPassedNoMinor() {
        Assert.assertTrue(true);
    }

    @Test
    public void test_FilterSkipped() {
        throw new SkipException("Skipped because of Skip Exception");
    }

    @Test
    public void test_SkippedInheritedFilter() {
        throw new SkipException("Skipped because of Skip Exception");
    }

    @Test
    public void test_FailedInheritedFilter() {
        Assert.assertTrue(false);
    }

    @Test
    public void test_PassedInheritedFilter() {
        Assert.assertTrue(true);
    }

    @Fails(description = "This is a known bug.")
    @Test
    public void test_FailedMinorAnnotatedWithFail_Run1() throws Exception {
        ExitPointCreaterTestClass1.testCreatorForDifferentExitPoints();
    }

    @Fails(description = "This is a known bug.")
    @Test
    public void test_FailedMinorAnnotatedWithFail_Run2() throws Exception {
        ExitPointCreaterTestClass1.testCreatorForDifferentExitPoints();
    }

    @Fails(description = "This is a known bug.")
    @Test
    public void test_FailedMinorAnnotatedWithFail_Run3() throws Exception {
        ExitPointCreaterTestClass1.testCreatorForDifferentExitPoints();
    }
}
