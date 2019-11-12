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
package eu.tsystems.mms.tic.testframework.execution.testng;

import eu.tsystems.mms.tic.testframework.common.Testerra;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A dummy class for collecting asserts.
 */
@Deprecated
public class AssertCollector {
    private static Assertion assertion =  Testerra.ioc().getInstance(CollectedAssertion.class);

    /**
     * Protect constructor since it is a static only class
     */
    protected AssertCollector() {
        // hide constructor
    }

    static public void assertTrue(boolean condition, String message) {
        assertion.assertTrue(condition, message);
    }
    static public void assertTrue(boolean condition) {
        assertion.assertTrue(condition,null);
    }
    static public void assertFalse(boolean condition, String message) {
        assertion.assertFalse(condition, message);
    }
    static public void assertFalse(boolean condition) {
        assertion.assertFalse(condition,null);
    }
    static public void fail(String message, Throwable realCause) {
        assertion.fail(message, realCause);
    }
    static public void fail(String message) {
        assertion.fail(message);
    }
    static public void fail() {
        fail(null);
    }
    public static void assertEquals(Object actual, Object expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }
    public static void assertEquals(Object actual, Object expected) {
        assertEquals(actual, expected, null);
    }
    static public void assertEquals(String actual, String expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }
    static public void assertEquals(String actual, String expected) {
        assertEquals(actual, expected, null);
    }
    static public void assertEquals(long actual, long expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }
    static public void assertEquals(long actual, long expected) {
        assertEquals(actual, expected, null);
    }
    static public void assertEquals(boolean actual, boolean expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }
    static public void assertEquals(boolean actual, boolean expected) {
        assertEquals(actual, expected, null);
    }
    static public void assertEquals(byte actual, byte expected, String message) {
       assertion.assertEquals(actual, expected, message);
    }
    static public void assertEquals(byte actual, byte expected) {
        assertEquals(actual, expected, null);
    }
    static public void assertEquals(char actual, char expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }
    static public void assertEquals(char actual, char expected) {
        assertEquals(actual, expected, null);
    }
    static public void assertEquals(short actual, short expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }
    static public void assertEquals(short actual, short expected) {
        assertEquals(actual, expected, null);
    }
    static public void assertEquals(int actual, int expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }
    static public void assertEquals(int actual, int expected) {
        assertEquals(actual, expected, null);
    }
    static public void assertNotNull(Object object) {
        assertNotNull(object, null);
    }
    static public void assertNotNull(Object object, String message) {
        assertion.assertNotNull(object, message);
    }
    static public void assertNull(Object object) {
        assertNull(object, null);
    }
    static public void assertNull(Object object, String message) {
        assertion.assertNull(object, message);
    }
    static public void assertSame(Object actual, Object expected, String message) {
        assertion.assertSame(actual, expected, message);
    }
    static public void assertSame(Object actual, Object expected) {
        assertSame(actual, expected, null);
    }
    static public void assertNotSame(Object actual, Object expected, String message) {
        assertion.assertNotSame(actual, expected, message);
    }
    static public void assertNotSame(Object actual, Object expected) {
        assertNotSame(actual, expected, null);
    }
    static public void assertEquals(Collection<?> actual, Collection<?> expected) {
        assertEquals(actual, expected, null);
    }
    static public void assertEquals(Collection<?> actual, Collection<?> expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }
    static public void assertEquals(Iterator<?> actual, Iterator<?> expected) {
        assertEquals(actual, expected, null);
    }
    static public void assertEquals(Iterator<?> actual, Iterator<?> expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }
    static public void assertEquals(Iterable<?> actual, Iterable<?> expected) {
        assertEquals(actual, expected, null);
    }
    static public void assertEquals(Iterable<?> actual, Iterable<?> expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }
    static public void assertEquals(Object[] actual, Object[] expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }
    static public void assertEqualsNoOrder(Object[] actual, Object[] expected, String message) {
        assertion.assertEqualsNoOrder(actual, expected, message);
    }
    static public void assertEquals(Object[] actual, Object[] expected) {
        assertEquals(actual, expected, null);
    }
    static public void assertEqualsNoOrder(Object[] actual, Object[] expected) {
        assertEqualsNoOrder(actual, expected, null);
    }
    public static void assertEquals(Set<?> actual, Set<?> expected) {
        assertEquals(actual, expected, null);
    }
    public static void assertEquals(Set<?> actual, Set<?> expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }
    public static void assertEqualsDeep(Set<?> actual, Set<?> expected, String message) {
        assertion.assertEqualsDeep(actual, expected, message);
    }
    public static void assertEquals(Map<?, ?> actual, Map<?, ?> expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }
    public static void assertEqualsDeep(Map<?, ?> actual, Map<?, ?> expected) {
        assertEqualsDeep(actual, expected, null);
    }
    public static void assertEqualsDeep(Map<?, ?> actual, Map<?, ?> expected, String message) {
        assertion.assertEqualsDeep(actual, expected, message);
    }
    public static void assertNotEquals(Object actual1, Object actual2, String message) {
        assertion.assertNotEquals(actual1, actual2, message);
    }
    public static void assertNotEquals(Object actual1, Object actual2) {
        assertNotEquals(actual1, actual2, null);
    }
    static void assertNotEquals(String actual1, String actual2, String message) {
        assertion.assertNotEquals(actual1, actual2, message);
    }
    static void assertNotEquals(String actual1, String actual2) {
        assertNotEquals(actual1, actual2, null);
    }
    static void assertNotEquals(long actual1, long actual2, String message) {
        assertion.assertNotEquals(actual1, actual2, message);
    }
    static void assertNotEquals(long actual1, long actual2) {
        assertNotEquals(actual1, actual2, null);
    }
    static void assertNotEquals(boolean actual1, boolean actual2, String message) {
        assertion.assertNotEquals(actual1, actual2, message);
    }
    static void assertNotEquals(boolean actual1, boolean actual2) {
        assertNotEquals(actual1, actual2, null);
    }
    static void assertNotEquals(byte actual1, byte actual2, String message) {
        assertion.assertNotEquals(actual1, actual2, message);
    }
    static void assertNotEquals(byte actual1, byte actual2) {
        assertNotEquals(actual1, actual2, null);
    }
    static void assertNotEquals(char actual1, char actual2, String message) {
        assertion.assertNotEquals(actual1, actual2, message);
    }
    static void assertNotEquals(char actual1, char actual2) {
        assertNotEquals(actual1, actual2, null);
    }
    static void assertNotEquals(short actual1, short actual2, String message) {
        assertion.assertNotEquals(actual1, actual2, message);
    }
    static void assertNotEquals(short actual1, short actual2) {
        assertNotEquals(actual1, actual2, null);
    }
    static void assertNotEquals(int actual1, int actual2, String message) {
        assertion.assertNotEquals(actual1, actual2, message);
    }
    static void assertNotEquals(int actual1, int actual2) {
        assertNotEquals(actual1, actual2, null);
    }
    public static void assertNotEquals(Set<?> actual, Set<?> expected) {
        assertNotEquals(actual, expected, null);
    }
    public static void assertNotEquals(Set<?> actual, Set<?> expected, String message) {
        assertion.assertNotEquals(actual, expected, message);
    }
    public static void assertNotEquals(Map<?, ?> actual, Map<?, ?> expected) {
        assertNotEquals(actual, expected, null);
    }
    public static void assertNotEquals(Map<?, ?> actual, Map<?, ?> expected, String message) {
        assertion.assertNotEquals(actual, expected, message);
    }
}
