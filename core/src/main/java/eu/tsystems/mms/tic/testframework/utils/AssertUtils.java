/*
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
package eu.tsystems.mms.tic.testframework.utils;

import org.testng.Assert;

import java.math.BigDecimal;

/**
 * Created by pele on 05.01.2015.
 */
public final class AssertUtils {

    public static void assertContains(String actual, String expected) {
        assertContains(actual, expected, "expected");
    }

    public static void assertContainsNot(String actual, String expected) {
        assertContainsNot(actual, expected, "expected");
    }

    public static void assertContains(String actual, String expected, String description) {
        if (!actual.contains(expected)) {
            Assert.fail(String.format("%s [%s] contains [%s]", description, actual, expected));
        }
    }

    public static void assertContainsNot(String actual, String expected, String description) {
        if (actual.contains(expected)) {
            Assert.fail(String.format("%s [%s] contains not [%s]", description, actual, expected));
        }
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
        if (actual.compareTo(expected)!=1) {
            Assert.fail(String.format("%s [%s] is greater than [%s]", description, actual, expected));
        }
    }

    public static void assertGreaterEqualThan(BigDecimal actual, BigDecimal expected, String description) {
        if (actual.compareTo(expected) < 0) {
            Assert.fail(String.format("%s [%s] is greater or equal than [%s]", description, actual, expected));
        }
    }

    public static void assertLowerThan(BigDecimal actual, BigDecimal expected, String description) {
        if (actual.compareTo(expected)!=-1) {
            Assert.fail(String.format("%s [%s] is lower than [%s]", description, actual, expected));
        }
    }

    public static void assertLowerEqualThan(BigDecimal actual, BigDecimal expected, String description) {
        if (actual.compareTo(expected) > 0) {
            Assert.fail(String.format("%s [%s] is lower or equal than [%s]", description, actual, expected));
        }
    }
}
