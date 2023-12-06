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

package eu.tsystems.mms.tic.testframework.test.utils;

import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;

public class AssertionsTest extends TesterraTest implements AssertProvider {

    @Test
    public void test_IsTrue() {
        ASSERT.assertTrue(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_IsTrue_fails() {
        ASSERT.assertTrue(false);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected that should be true \\[false\\] equals \\[true\\]")
    public void test_IsTrue_fails_message() {
        ASSERT.assertTrue(false, "should be true");
    }

    @Test
    public void test_IsFalse() {
        ASSERT.assertFalse(false);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_IsFalse_fails() {
        ASSERT.assertFalse(true);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected that should be false \\[true\\] equals \\[false\\]")
    public void test_IsFalse_fails_message() {
        ASSERT.assertFalse(true, "should be false");
    }

    @Test
    public void test_IsNull() {
        ASSERT.assertNull(null);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_IsNull_fails() {
        ASSERT.assertNull(ASSERT);
    }

    @Test
    public void test_IsNotNull() {
        ASSERT.assertNotNull(ASSERT);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_IsNotNull_fails() {
        ASSERT.assertNotNull(null);
    }

    @Test
    public void test_StringEquals() {
        ASSERT.assertEquals("Hallo Welt", "Hallo Welt");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_StringEquals_fails() {
        ASSERT.assertEquals("Hello World", "Hallo Welt");
    }

    @Test
    public void test_StringNotEquals() {
        ASSERT.assertNotEquals("Hello World", "Hallo Welt");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_StringNotEquals_fails() {
        ASSERT.assertNotEquals("Hallo Welt", "Hallo Welt");
    }

    @Test
    public void test_StartsWith() {
        ASSERT.assertStartsWith("a long time ago", "a long");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_StartsWith_fails() {
        ASSERT.assertStartsWith("a long time ago", "in the end");
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected that a fairytale \\[a long time ago\\] starts with \\[in the end\\]")
    public void test_StartsWith_fails_message() {
        ASSERT.assertStartsWith("a long time ago", "in the end", "a fairytale");
    }

    @Test
    public void test_EndsWith() {
        ASSERT.assertEndsWith("this is the end", "the end");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_EndsWith_fails() {
        ASSERT.assertEndsWith("this is the end", "the beginning");
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected that the sentence \\[this is the end\\] ends with \\[the beginning\\]")
    public void test_EndsWith_fails_message() {
        ASSERT.assertEndsWith("this is the end", "the beginning", "the sentence");
    }

    @Test
    public void test_Contains() {
        ASSERT.assertContains("Hello world", "world");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_Contains_fails() {
        ASSERT.assertContains("Hello world", "planet");
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected that the greeting \\[Hello world\\] contains \\[planet\\]")
    public void test_Contains_fails_subject() {
        ASSERT.assertContains("Hello world", "planet", "the greeting");
    }

    @Test
    public void test_LowerThan() {
        ASSERT.assertLowerEqualThan(new BigDecimal(1), new BigDecimal(2));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_LowerThanFails() {
        ASSERT.assertLowerEqualThan(new BigDecimal(2), new BigDecimal(1));
    }

    @Test
    public void test_GreaterThan() {
        ASSERT.assertGreaterThan(new BigDecimal(2), new BigDecimal(1));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_GreaterThanFails() {
        ASSERT.assertGreaterThan(new BigDecimal(1), new BigDecimal(2));
    }

    @Test
    public void test_LowerEqualThan() {
        ASSERT.assertLowerEqualThan(new BigDecimal(2), new BigDecimal(2));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_LowerEqualThanFails() {
        ASSERT.assertLowerEqualThan(new BigDecimal(2), new BigDecimal(1));
    }

    @Test
    public void test_GreaterEqualThan() {
        ASSERT.assertGreaterEqualThan(new BigDecimal(2), new BigDecimal(2));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_GreaterEqualThanFails() {
        ASSERT.assertGreaterEqualThan(new BigDecimal(1), new BigDecimal(2));
    }

    @Test
    public void test_ContainsNot() {
        ASSERT.assertContainsNot("affe", "haus");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_ContainsNotFails() {
        ASSERT.assertContainsNot("affe", "affe");
    }
}
