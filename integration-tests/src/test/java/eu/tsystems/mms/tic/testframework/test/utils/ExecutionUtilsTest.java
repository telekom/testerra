/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems MMS GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.test.utils;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.internal.utils.ExceptionUtils;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.ExecutionUtils;
import java.util.Optional;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ExecutionUtilsTest extends TesterraTest {

    @Test
    public void test_getFailsafe() {
        String expected = "katze";
        ExecutionUtils executionUtils = Testerra.getInjector().getInstance(ExecutionUtils.class);
        Optional<Object> failsafe = executionUtils.getFailsafe(() -> {
            return expected;
        });

        Assert.assertEquals(failsafe.get(), expected);
    }

    @Test
    public void test_getFailsafe_null() {
        ExecutionUtils executionUtils = Testerra.getInjector().getInstance(ExecutionUtils.class);
        Optional<Object> failsafe = executionUtils.getFailsafe(() -> {
            return null;
        });

        Assert.assertFalse(failsafe.isPresent());
    }

    @Test
    public void test_getFailsafe_NPE() {
        ExecutionUtils executionUtils = Testerra.getInjector().getInstance(ExecutionUtils.class);
        Optional<Object> failsafe = executionUtils.getFailsafe(() -> {
            String expected = null;
            return expected.trim();
        });

        Assert.assertFalse(failsafe.isPresent());
    }
}
