/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.playground;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SimpleFailingTest extends AbstractTest {

    @Test(groups = "passed")
    public void testPassed() {
    }

    @Test
    public void testFailing() {
        Assert.fail("This test should fail");
    }

    @Test(dependsOnMethods = "testFailing")
    public void testDependsOnFailed() {
    }

    @Test
    public void testFailing2() {
        doFail();
    }

    @Test
    public void testFailingPage() {
        doFailWithinAPage();
    }

}
