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

package eu.tsystems.mms.tic.testframework.test.execution;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import java.util.LinkedList;
import org.testng.Assert;
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

    private final LinkedList<Integer> internalOrder = new LinkedList<>();

    @Test(priority = 2, groups = {"TestNgPriorityTest", "SEQUENTIAL"})
    public void testUnderTest2_and2() {
        internalOrder.add(2);
        log().info("TestNgPriorityTest :: 2");
    }

    @Test(priority = 3, groups = {"TestNgPriorityTest", "SEQUENTIAL"})
    public void testUnderTest1_but3() {
        internalOrder.add(3);
        log().info("TestNgPriorityTest :: 3");
    }

    @Test(priority = 4, groups = {"TestNgPriorityTest", "SEQUENTIAL"})
    public void testUnderTest3_but4() {
        internalOrder.add(4);
        log().info("TestNgPriorityTest :: 4");
    }

    @Test(priority = 1, groups = {"TestNgPriorityTest", "SEQUENTIAL"})
    public void testUnderTest4_but1() {
        internalOrder.add(1);
        log().info("TestNgPriorityTest :: 1");
    }

    @Test(priority = 999, groups = "SEQUENTIAL", dependsOnGroups = "TestNgPriorityTest", alwaysRun = true)
    public void test999_AssertPriorityOfOtherTestcasesInClass() {

        Assert.assertEquals(internalOrder.size(), 4, "Method size should be 4");

        for (int i = 0; i < internalOrder.size(); i++) {
            final int actual = internalOrder.get(i); // receives actual value.
            final int expected = i + 1; // should equal, huh ?
            Assert.assertEquals(actual, expected, String.format("Method was at index %s but should be %s", actual, expected));
        }

    }

}
