/*
 * Testerra
 *
 * (C) 2021, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.annotations.Test;

/**
 * Simple Testclass to test behavoiur of priorites
 * <p>
 * Date: 28.01.2021
 * Time: 08:00
 *
 * @author Eric Kubenka
 */
public class TestNgPriorityTest extends TesterraTest implements Loggable {

    @Test(priority = 2)
    public void test2_and2() {
        log().info("TestNgPriorityTest :: 2");
    }

    @Test(priority = 3)
    public void test1_but3() {
        log().info("TestNgPriorityTest :: 3");
    }

    @Test(priority = 4)
    public void test3_but4() {
        log().info("TestNgPriorityTest :: 4");
    }

    @Test(priority = 1)
    public void test4_but1() {
        log().info("TestNgPriorityTest :: 1");
    }
}
