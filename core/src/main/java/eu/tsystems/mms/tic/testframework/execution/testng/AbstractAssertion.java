/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.execution.testng;

import org.testng.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of {@link Assertion}
 *
 * @author Mike Reiche
 */
public abstract class AbstractAssertion implements Assertion {

    /*
    Big decimal values are round by default with given scale and RoundingMode.HALF_UP
    eg. 1.005 -> 1.01, 1.114 -> 1.11
    */
    private static final int BIGDECIMAL_ROUND_SCALE = 3;

    public String format(Object actual, Object expected, Object subject) {
        StringBuilder builder = new StringBuilder();
        builder.append("Expected");
        if (subject != null) {
            builder.append(" that ").append(subject);

        }
        if (actual != null) {
//            if (subject != null) {
//                builder.append(" actual");
//            }
            builder.append(" [").append(actual).append("]");
        }
        builder.append(" ").append(expected);
        return builder.toString();
    }

    @Override
    abstract public void fail(Error error);

    @Override
    public boolean isTrue(boolean actual) {
        return actual;
    }

    @Override
    public String formatExpectTrue(boolean actual, Object subject) {
        return formatExpectEquals(actual, Boolean.TRUE, subject);
    }

    @Override
    public void assertTrue(boolean actual, Object subject) {
        if (!isTrue(actual)) {
            fail(formatExpectTrue(actual, subject));
        }
    }

    @Override
    public boolean isFalse(boolean actual) {
        return !actual;
    }

    @Override
    public String formatExpectFalse(boolean actual, Object subject) {
        return formatExpectEquals(actual, Boolean.FALSE, subject);
    }

    @Override
    public void assertFalse(boolean actual, Object subject) {
        if (!isFalse(actual)) {
            fail(formatExpectFalse(actual, subject));
        }
    }

    @Override
    public boolean isSame(Object actual, Object expected) {
        return expected == actual;
    }

    @Override
    public String formatExpectSame(Object actual, Object expected, Object subject) {
        return format(actual, String.format("is the same like [%s]", expected), subject);
    }

    @Override
    public void assertSame(Object actual, Object expected, Object subject) {
        if (!isSame(actual, expected)) {
            fail(formatExpectSame(actual, expected, subject));
        }
    }

    @Override
    public boolean isNotSame(Object actual, Object expected) {
        return expected != actual;
    }

    @Override
    public String formatExpectNotSame(Object actual, Object expected, Object subject) {
        return format(actual, String.format("is not the same like [%s]", expected), subject);
    }

    @Override
    public void assertNotSame(Object actual, Object expected, Object subject) {
        if (!isNotSame(actual, expected)) {
            fail(formatExpectNotSame(actual, expected, subject));
        }
    }

    @Override
    public boolean isNull(Object actual) {
        return actual == null;
    }

    @Override
    public String formatExpectNull(Object actual, Object subject) {
        return format(actual, "is null", subject);
    }

    @Override
    public void assertNull(Object actual, Object subject) {
        if (!isNull(actual)) {
            fail(formatExpectNull(actual, subject));
        }
    }

    @Override
    public boolean isNotNull(Object actual) {
        return actual != null;
    }

    @Override
    public String formatExpectNotNull(Object actual, Object subject) {
        return format(actual, "is not null", subject);
    }

    @Override
    public void assertNotNull(Object actual, Object subject) {
        if (!isNotNull(actual)) {
            fail(formatExpectNotNull(actual, subject));
        }
    }

    @Override
    public boolean contains(String actual, String expected) {
        if (actual == null || !actual.contains(expected)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String formatExpectContains(String actual, String expected, Object subject) {
        return format(actual, String.format("contains [%s]", expected), subject);
    }

    @Override
    public void assertContains(String actual, String expected, Object subject) {
        if (!contains(actual, expected)) {
            fail(formatExpectContains(actual, expected, subject));
        }
    }

    @Override
    public boolean containsNot(String actual, String expected) {
        return actual != null && !actual.contains(expected);
    }

    @Override
    public String formatExpectContainsNot(String actual, String expected, Object subject) {
        return format(actual, String.format("contains not [%s]", expected), subject);
    }

    @Override
    public void assertContainsNot(String actual, String expected, Object subject) {
        if (!containsNot(actual, expected)) {
            fail(formatExpectContainsNot(actual, expected, subject));
        }
    }

    @Override
    public boolean isGreaterThan(BigDecimal actual, BigDecimal expected) {
        return actual.compareTo(expected) > 0;
    }

    @Override
    public String formatExpectGreaterThan(BigDecimal actual, BigDecimal expected, Object subject) {
        return format(scaleBigDecimal(actual), String.format("is greater than [%s]", scaleBigDecimal(expected)), subject);
    }

    @Override
    public void assertGreaterThan(BigDecimal actual, BigDecimal expected, Object subject) {
        if (!isGreaterThan(actual, expected)) {
            fail(formatExpectGreaterThan(actual, expected, subject));
        }
    }

    @Override
    public boolean isGreaterEqualThan(BigDecimal actual, BigDecimal expected) {
        return actual.compareTo(expected) >= 0;
    }

    @Override
    public String formatExpectGreaterEqualThan(BigDecimal actual, BigDecimal expected, Object subject) {
        return format(scaleBigDecimal(actual), String.format("is greater or equal than [%s]", scaleBigDecimal(expected)), subject);
    }

    @Override
    public void assertGreaterEqualThan(BigDecimal actual, BigDecimal expected, Object subject) {
        if (!isGreaterEqualThan(actual, expected)) {
            fail(formatExpectGreaterEqualThan(actual, expected, subject));
        }
    }

    @Override
    public boolean isLowerThan(BigDecimal actual, BigDecimal expected) {
        return actual.compareTo(expected) < 0;
    }

    @Override
    public String formatExpectLowerThan(BigDecimal actual, BigDecimal expected, Object subject) {
        return format(scaleBigDecimal(actual), String.format("is lower than [%s]", scaleBigDecimal(expected)), subject);
    }

    @Override
    public void assertLowerThan(BigDecimal actual, BigDecimal expected, Object subject) {
        if (!isLowerThan(actual, expected)) {
            fail(formatExpectLowerThan(actual, expected, subject));
        }
    }

    @Override
    public boolean isLowerEqualThan(BigDecimal actual, BigDecimal expected) {
        return actual.compareTo(expected) <= 0;
    }

    @Override
    public String formatExpectLowerEqualThan(BigDecimal actual, BigDecimal expected, Object subject) {
        return format(scaleBigDecimal(actual), String.format("is lower or equal than [%s]", scaleBigDecimal(expected)), subject);
    }

    @Override
    public void assertLowerEqualThan(BigDecimal actual, BigDecimal expected, Object subject) {
        if (!isLowerEqualThan(actual, expected)) {
            fail(formatExpectLowerEqualThan(actual, expected, subject));
        }
    }

    @Override
    public boolean isBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher) {
        return isGreaterEqualThan(actual, lower) && isLowerEqualThan(actual, higher);
    }

    @Override
    public String formatExpectIsBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher, Object subject) {
        return format(scaleBigDecimal(actual), String.format("is between [%s] and [%s]", scaleBigDecimal(lower), scaleBigDecimal(higher)), subject);
    }

    @Override
    public void assertBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher, Object subject) {
        if (!isBetween(actual, lower, higher)) {
            fail(formatExpectIsBetween(scaleBigDecimal(actual), scaleBigDecimal(lower), scaleBigDecimal(higher), subject));
        }
    }

    @Override
    public boolean equals(Object actual, Object expected) {
        try {
            Assert.assertEquals(actual, expected);
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    @Override
    public String formatExpectEquals(Object actual, Object expected, Object subject) {
        return format(actual, String.format("equals [%s]", expected), subject);
    }

    @Override
    public void assertEquals(Object actual, Object expected, Object subject) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (AssertionError e) {
            fail(formatExpectEquals(actual, expected, subject));
        }
    }

    @Override
    public void assertEquals(Collection<?> actual, Collection<?> expected, Object subject) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (AssertionError e) {
            fail(formatExpectEquals(actual, expected, subject));
        }
    }

    @Override
    public void assertEquals(Iterator<?> actual, Iterator<?> expected, Object subject) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (AssertionError e) {
            fail(formatExpectEquals(actual, expected, subject));
        }
    }

    @Override
    public void assertEquals(Iterable<?> actual, Iterable<?> expected, Object subject) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (AssertionError e) {
            fail(formatExpectEquals(actual, expected, subject));
        }
    }

    @Override
    public void assertEquals(Object[] actual, Object[] expected, Object subject) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (AssertionError e) {
            fail(formatExpectEquals(actual, expected, subject));
        }
    }

    @Override
    public void assertEqualsNoOrder(Object[] actual, Object[] expected, Object subject) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (AssertionError e) {
            fail(formatExpectEquals(actual, expected, subject));
        }
    }

    @Override
    public void assertEquals(Set<?> actual, Set<?> expected, Object subject) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (AssertionError e) {
            fail(formatExpectEquals(actual, expected, subject));
        }
    }

    @Override
    public void assertEqualsDeep(Set<?> actual, Set<?> expected, Object subject) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (AssertionError e) {
            fail(formatExpectEquals(actual, expected, subject));
        }
    }

    @Override
    public void assertEquals(Map<?, ?> actual, Map<?, ?> expected, Object subject) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (AssertionError e) {
            fail(formatExpectEquals(actual, expected, subject));
        }
    }

    @Override
    public void assertEqualsDeep(Map<?, ?> actual, Map<?, ?> expected, Object subject) {
        try {
            Assert.assertEquals(actual, expected);
        } catch (AssertionError e) {
            fail(formatExpectEquals(actual, expected, subject));
        }
    }

    @Override
    public boolean notEquals(Object actual1, Object actual2) {
        try {
            Assert.assertNotEquals(actual1, actual2);
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    @Override
    public String formatExpectNotEquals(Object actual, Object expected, Object subject) {
        return format(actual, String.format("equals not [%s]", expected), subject);
    }

    @Override
    public void assertNotEquals(Object actual1, Object actual2, Object subject) {
        try {
            Assert.assertNotEquals(actual1, actual2);
        } catch (AssertionError e) {
            fail(formatExpectNotEquals(actual1, actual2, subject));
        }
    }

    @Override
    public void assertNotEquals(Set<?> actual1, Set<?> actual2, Object subject) {
        try {
            Assert.assertNotEquals(actual1, actual2);
        } catch (AssertionError e) {
            fail(formatExpectNotEquals(actual1, actual2, subject));
        }
    }

    @Override
    public void assertNotEquals(Map<?, ?> actual1, Map<?, ?> actual2, Object subject) {
        try {
            Assert.assertNotEquals(actual1, actual2);
        } catch (AssertionError e) {
            fail(formatExpectNotEquals(actual1, actual2, subject));
        }
    }

    @Override
    public boolean startsWith(Object actual, Object expected) {
        return actual != null
                && expected != null
                && actual.toString().startsWith(expected.toString());
    }

    @Override
    public String formatExpectStartsWith(Object actual, Object expected, Object subject) {
        return format(actual, String.format("starts with [%s]", expected), subject);
    }

    @Override
    public void assertStartsWith(Object actual, Object expected, Object subject) {
        if (!startsWith(actual, expected)) {
            fail(formatExpectStartsWith(actual, expected, subject));
        }
    }

    @Override
    public boolean endsWith(Object actual, Object expected) {
        return actual != null
                && expected != null
                && actual.toString().endsWith(expected.toString());
    }

    @Override
    public String formatExpectEndsWith(Object actual, Object expected, Object subject) {
        return format(actual, String.format("ends with [%s]", expected), subject);
    }

    @Override
    public void assertEndsWith(Object actual, Object expected, Object subject) {
        if (!endsWith(actual, expected)) {
            fail(formatExpectEndsWith(actual, expected, subject));
        }
    }

    @Override
    public void assertInstanceOf(Object actual, Class expected, Object subject) {
        if (!expected.isInstance(actual)) {
            fail(formatExpectEquals(actual, String.format("instance of [%s]", expected), subject));
        }
    }

    private BigDecimal scaleBigDecimal(BigDecimal value) {
        return value == null ? null :
                (value.scale() >= BIGDECIMAL_ROUND_SCALE ? value.setScale(BIGDECIMAL_ROUND_SCALE, RoundingMode.HALF_UP) : value);

    }
}
