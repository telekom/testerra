/*
 * Testerra
 *
 * (C) 2020, Alex Rockstroh, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 *
 */
package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import org.testng.annotations.Test;

public class ReportTestUnderTestAssertCollector extends AbstractTest {

    @Test
    public void test_assertCollectorAllFailed() {
        AssertCollector.assertTrue(false, "Intentionally failed first");
        AssertCollector.assertTrue(false,"Intentionally failed second");
        AssertCollector.assertTrue(false, "Intentionally failed third");
    }

    @Test
    public void test_assertCollectorPassedAndFailed() {
        AssertCollector.assertTrue(true);
        AssertCollector.assertTrue(false, "Intentionally failed first");
        AssertCollector.assertTrue(true);
    }

    @Test
    public void test_assertCollectorAllPassed() {
        AssertCollector.assertTrue(true);
        AssertCollector.assertTrue(true);
        AssertCollector.assertTrue(true);
    }


}
