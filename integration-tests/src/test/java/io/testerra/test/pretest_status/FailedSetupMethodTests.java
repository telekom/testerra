/*
 * Testerra
 *
 * (C) 2023, Martin Großmann,  Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
package io.testerra.test.pretest_status;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created on 2023-06-29
 *
 * @author mgn
 */
public class FailedSetupMethodTests extends TesterraTest implements AssertProvider, Loggable {

    public static class BeforeClassAssertionTests {
        @BeforeClass
        public void beforeClassSetup01() {
            ASSERT.fail("beforeClass setup fails");
        }

        @Test
        public void testT01_BeforeClassWithAssertion() {

        }
    }

    public static class BeforeClassExceptionTests {

        @BeforeClass
        public void beforeClassSetup02() {
            throw new RuntimeException("afterClass setup fails");
        }

        @Test
        public void testT02_BeforeClassWithException() {

        }
    }

    public static class BeforeMethodAssertionTests {
        @BeforeMethod
        public void beforeMethodSetup01() {
            ASSERT.fail("beforeMethod setup fails");
        }

        @Test
        public void testT03_BeforeMethodWithAssertion() {

        }
    }

    public static class BeforeMethodExceptionTests {
        @BeforeMethod
        public void beforeMethodSetup02() {
            throw new RuntimeException("beforeMethod setup fails");
        }

        @Test
        public void testT04_BeforeMethodWithException() {

        }
    }

    public static class AfterMethodAssertionTests implements Loggable {
        @Test
        public void testT05_AfterMethodWithAssertion() {
            log().info("Test method passes with Assertion error in 'afterMethod' setup.");
        }

        @AfterMethod
        public void afterMethodSetup01() {
            ASSERT.fail("afterClass setup fails");
        }
    }

    public static class AfterMethodExceptionTests implements Loggable {
        @Test
        public void testT06_AfterMethodWithException() {
            log().info("Test method passes with Exception in 'afterMethod' setup.");
        }

        @AfterMethod
        public void afterMethodSetup02() {
            throw new RuntimeException("afterClass setup fails");
        }
    }

    public static class AfterClassAssertionTests implements Loggable {

        @Test
        public void testT07_AfterClassWithAssertion() {
            log().info("Test method passes with Assertion error in 'afterClass' setup.");
        }

        @AfterClass
        public void afterClassSetup01() {
            ASSERT.fail("afterClass setup fails");
        }
    }

    public static class AfterClassExceptionTests implements Loggable {

        @Test
        public void testT08_AfterClassWithException() {
            log().info("Test method passes with RunTimeException in 'afterClass' setup.");
        }

        @AfterClass
        public void afterClassSetup02() {
            throw new RuntimeException("afterClass setup fails");
        }
    }

//    public static class SetupAfterTest implements Loggable {
//
//        @Test
//        public void testPassedMethodBeforeAFailedAfterMethod() {
//            log().info("Info log of test method.");
//        }
//
//        @AfterMethod
//        public void setupAfterMethod() {
//            ASSERT.fail("Setup method must fail.");
//        }
//    }
//
//    public static class SetupAfterClass1 implements Loggable {
//        @Test
//        public void testPassedMethodBeforeAFailedAfterClass() {
//            log().info("Info log of test method.");
//        }
//
//        @AfterClass
//        public void setupAfterClass() {
//            ASSERT.fail("Setup method must fail.");
//        }
//    }
//
//    public static class SetupAfterClass2 implements Loggable {
//        @Test
//        public void testPassedMethodBeforeAFailedAfterClass() {
//            log().info("Info log of test method.");
//        }
//
//        @AfterClass
//        public void setupAfterClass() {
//            throw new RuntimeException("Setup method must fail.");
//        }
//    }

}
