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

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;

import java.math.BigDecimal;

@Deprecated
public final class AssertUtils {

    private static final Assertion assertion;

    static {
        assertion = Testerra.ioc().getInstance(InstantAssertion.class);
    }

    public static void assertContains(String actual, String expected) {
        assertContains(actual, expected, null);
    }

    public static void assertContainsNot(String actual, String expected) {
        assertContainsNot(actual, expected, null);
    }

    public static void assertContains(String actual, String expected, String description) {
        assertion.assertContains(actual, expected, description);
    }

    public static void assertContainsNot(String actual, String expected, String description) {
        assertion.assertContainsNot(actual, expected, description);
    }

    public static void assertGreaterThan(BigDecimal actual, BigDecimal expected) {
        assertGreaterThan(actual, expected, null);
    }

    public static void assertGreaterEqualThan(BigDecimal actual, BigDecimal expected) {
        assertGreaterEqualThan(actual, expected, null);
    }

    public static void assertLowerThan(BigDecimal actual, BigDecimal expected) {
        assertLowerThan(actual, expected, null);
    }

    public static void assertLowerEqualThan(BigDecimal actual, BigDecimal expected) {
        assertLowerEqualThan(actual, expected, null);
    }

    public static void assertGreaterThan(BigDecimal actual, BigDecimal expected, String description) {
        assertion.assertGreaterThan(actual, expected, description);
    }

    public static void assertGreaterEqualThan(BigDecimal actual, BigDecimal expected, String description) {
        assertion.assertGreaterEqualThan(actual, expected, description);
    }

    public static void assertLowerThan(BigDecimal actual, BigDecimal expected, String description) {
        assertion.assertLowerThan(actual, expected, description);
    }

    public static void assertLowerEqualThan(BigDecimal actual, BigDecimal expected, String description) {
        assertion.assertLowerThan(actual, expected, description);
    }
}
