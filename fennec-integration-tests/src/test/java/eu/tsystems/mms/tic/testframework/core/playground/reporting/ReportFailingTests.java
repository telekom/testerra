/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
/*
 * Created on 22.02.2013
 *
 * Copyright(c) 2013 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.utils.RandomUtils;
import eu.tsystems.mms.tic.testframework.utils.TestUtils;

/**
 * Class LoggingTests.
 *
 * @author pele
 */
//@Listeners(FennecListener.class)
@FennecClassContext(value = "MyContext")
//@FennecClassContext()
public class ReportFailingTests {

    // @BeforeMethod
    // public void beforeMethod(Method method) {
    // throw new RuntimeException("some error");
    // }

    private void failingTest() {
        Assert.assertTrue(false, "Eval");
    }

    @Test(groups = "Foo")
    public void test1Eval() throws Exception {
        TestUtils.sleep(RandomUtils.generateRandomInt(20));
        Assert.assertTrue(true, "Eval");
    }

    @FailureCorridor.Mid
    @Test
    public void test2() throws Exception {
        TestUtils.sleep(RandomUtils.generateRandomInt(20));
        Assert.assertEquals("bla\naaaa\nbbb", "blubb", "huhu");
    }

    @FailureCorridor.Low
    @Test
    public void test3() throws Exception {
        failingTest();
    }

    @FailureCorridor.High
    @Test
    public void test4() throws Exception {
        failingTest();
    }

    @Test
    public void test4contains() throws Exception {
        failingTest();
    }

    @Test(dependsOnMethods = "test4")
    public void testSkipCauseOfDependsOn() {
        Assert.assertTrue(true, "Never reach this comment - Failed because depends-on");
    }

    @Test(dependsOnMethods = "testSkipCauseOfDependsOn")
    public void testDependsOnHierarchy() {
        Assert.assertTrue(true, "Never reach this comment - Failed because depends-on");
    }

    @Test(dependsOnMethods = "test1Eval")
    public void testDependsOnPassedMethodButReTest() {
        Assert.fail("This test should fail.");
    }

    @Test(dependsOnGroups = "Foo")
    public void testDependsOnGroups() {
        Assert.fail("This test should fail.");
    }

    @Test
    public void testRerunTest() {
        TestUtils.sleep(RandomUtils.generateRandomInt(20));
        throw new WebDriverException("Error communicating with the remote browser. It may have died.");
    }

    @Test
    public void testSkip() throws Exception {
        throw new SkipException("yeah skipped");
    }

}
