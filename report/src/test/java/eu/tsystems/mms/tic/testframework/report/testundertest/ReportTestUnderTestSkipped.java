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

import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class ReportTestUnderTestSkipped extends AbstractTest {


    @Test
    public void test_TestStateSkipped1() {
        TestStep.begin("Test-Step-1");
        TestStep.begin("Test-Step-2");
        TestStep.begin("Test-Step-3");
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkipped2() { throw new SkipException("Skipped because of Skip Exception");}
    @Test
    public void test_TestStateSkipped3() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkipped4() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkipped5() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkipped6() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkipped7() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkipped8() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkipped9() {throw new SkipException("Skipped because of Skip Exception");}
    @Test
    public void test_TestStateSkippedInherited1() {
        TestStep.begin("Test-Step-1");
        TestStep.begin("Test-Step-2");
        TestStep.begin("Test-Step-3");
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkippedInherited2() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkippedInherited3() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkippedInherited4() {
        throw new SkipException("Skipped because of Skip Exception");
    }
    @Test
    public void test_TestStateSkippedInherited5() {
        throw new SkipException("Skipped because of Skip Exception");
    }



}
