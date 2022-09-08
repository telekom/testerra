/*
 * Testerra
 *
 * (C) 2022, Clemens Große, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package io.testerra.report.test.pretest_status.simple;

import eu.tsystems.mms.tic.testframework.execution.testng.OptionalAssert;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;

import io.testerra.report.test.AbstractTestSitesTest;
import org.testng.annotations.Test;

public class GeneratePassedStatusInTesterraReportTest extends AbstractTestSitesTest {

    @Test
    public void test_Passed() {
    }

    @Test
    public void test_Optional_Assert() {
        OptionalAssert.fail("minor fail");
    }
}
