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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.core.test.utils;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.utils.AssertUtils;
import org.testng.annotations.Test;

import java.math.BigDecimal;

/**
 * Created by toku on 13.01.2015.
 */

public class AssertUtilsTest extends AbstractTest {

    /**
     * checks if string contains expected content
     */
    @Test
    public void testT01_assertContains() {
        String string = "Dresden";
        AssertUtils.assertContains(string, "Dresden");
    }

    /**
     * checks if string contains expected content but fails. checks if assertionError is correct
     * @throws AssertionError .
     */
    @Test
    public void testT02_assertContainsFails() throws AssertionError {
        String string = "Dresden";
        try {
            AssertUtils.assertContains(string, "DresdenX");
        } catch (AssertionError e) {
           Assert.assertTrue(e.getMessage().contains("String contains " + "DresdenX" + "\nactual: " + string));
            return;
        }
        Assert.fail("");
    }

    /**
     * checks if string contains expected content
     */
    @Test
    public void testT03_assertContains() {
        String string = "Leipzig";
        AssertUtils.assertContains(string, "Leipzig", "String contains " + string + "instead of Leipzig");
    }


     /**
     * checks if string contains expected content but fails. checks if assertionError is correct
     * @throws AssertionError .
     */
    @Test
    public void testT04_assertContainsError() throws AssertionError {
        String string = "Haus";
        try {
            AssertUtils.assertContains(string, "Holz", "String contains " + string + "instead of Holz");
        } catch (AssertionError e) {
            Assert.assertTrue(e.getMessage().contains("String contains " + string + "instead of Holz"));
            return;
        }
        Assert.fail("");
    }

    @Test
    public void test_assertLowerThan() {
        AssertUtils.assertLowerEqualThan(new BigDecimal(1), new BigDecimal(2));
    }

    @Test
    public void test_assertLowerThanFails() {
        try {
            AssertUtils.assertLowerEqualThan(new BigDecimal(2), new BigDecimal(1));
        } catch (AssertionError e) {
            System.out.println(e);
        }

    }

    @Test
    public void test_assertGreaterThan() {
        AssertUtils.assertGreaterThan(new BigDecimal(2), new BigDecimal(1));
    }

    @Test
    public void test_assertGreaterThanFails() {
        try {
            AssertUtils.assertGreaterThan(new BigDecimal(1), new BigDecimal(2));
        } catch (AssertionError e) {
            System.out.println(e);
        }
    }

    @Test
    public void test_assertLowerEqualThan() {
        AssertUtils.assertLowerEqualThan(new BigDecimal(2), new BigDecimal(2));
    }

    @Test
    public void test_assertLowerEqualThanFails() {
        try {
            AssertUtils.assertLowerEqualThan(new BigDecimal(2), new BigDecimal(1));
        } catch (AssertionError e) {
            System.out.println(e);
        }
    }

    @Test
    public void test_assertGreaterEqualThan() {
        AssertUtils.assertGreaterEqualThan(new BigDecimal(2), new BigDecimal(2));
    }

    @Test
    public void test_assertGreaterEqualThanFails() {
        try {
            AssertUtils.assertGreaterEqualThan(new BigDecimal(1), new BigDecimal(2));
        } catch (AssertionError e) {
            System.out.println(e);
        }
    }

    @Test
    public void test_assertContainsNot() {
        AssertUtils.assertContainsNot("affe", "haus");
    }

    @Test
    public void test_assertContainsNotFails() {
        try {
            AssertUtils.assertContainsNot("affe", "affe");
        } catch (AssertionError e) {
            System.out.println(e);
        }
    }

}
