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
package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import eu.tsystems.mms.tic.testframework.execution.testng.IAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;

import java.math.BigDecimal;

@Deprecated
public final class AssertUtils {

    private static final IAssertion realAssert;

    static {
        realAssert = TesterraCommons.ioc().getInstance(InstantAssertion.class);
    }

    public static void assertContains(String actual, String expected) {
        assertContains(actual, expected, "expected");
    }

    public static void assertContainsNot(String actual, String expected) {
        assertContainsNot(actual, expected, "expected");
    }

    public static void assertContains(String actual, String expected, String description) {
        realAssert.assertContains(actual, expected, description);
    }

    public static void assertContainsNot(String actual, String expected, String description) {
        realAssert.assertContainsNot(actual, expected, description);
    }

    public static void assertGreaterThan(BigDecimal actual, BigDecimal expected) {
        assertGreaterThan(actual, expected, "expected");
    }

    public static void assertGreaterEqualThan(BigDecimal actual, BigDecimal expected) {
        assertGreaterEqualThan(actual, expected, "expected");
    }

    public static void assertLowerThan(BigDecimal actual, BigDecimal expected) {
        assertLowerThan(actual, expected, "expected");
    }

    public static void assertLowerEqualThan(BigDecimal actual, BigDecimal expected) {
        assertLowerEqualThan(actual, expected, "expected");
    }

    public static void assertGreaterThan(BigDecimal actual, BigDecimal expected, String description) {
        realAssert.assertGreaterThan(actual, expected, description);
    }

    public static void assertGreaterEqualThan(BigDecimal actual, BigDecimal expected, String description) {
        realAssert.assertGreaterEqualThan(actual, expected, description);
    }

    public static void assertLowerThan(BigDecimal actual, BigDecimal expected, String description) {
        realAssert.assertLowerThan(actual, expected, description);
    }

    public static void assertLowerEqualThan(BigDecimal actual, BigDecimal expected, String description) {
        realAssert.assertLowerThan(actual, expected, description);
    }
}
