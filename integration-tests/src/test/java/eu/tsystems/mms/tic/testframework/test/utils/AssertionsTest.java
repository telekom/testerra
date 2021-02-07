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

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.InstantAssertion;
import eu.tsystems.mms.tic.testframework.execution.testng.SimpleAssertion;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.annotations.Test;

/**
 * @todo Incomplete
 */
public class AssertionsTest extends TesterraTest {

    private SimpleAssertion assertion = Testerra.getInjector().getInstance(InstantAssertion.class);

    @Test
    public void testIsTrue() {
        assertion.assertTrue(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testIsTrue_fails() {
        assertion.assertTrue(false);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected that should be true \\[false\\] equals \\[true\\]")
    public void testIsTrue_fails_message() {
        assertion.assertTrue(false, "should be true");
    }

    @Test
    public void testIsFalse() {
        assertion.assertFalse(false);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testIsFalse_fails() {
        assertion.assertFalse(true);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected that should be false \\[true\\] equals \\[false\\]")
    public void testIsFalse_fails_message() {
        assertion.assertFalse(true, "should be false");
    }

    @Test
    public void testIsNull() {
        assertion.assertNull(null);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testIsNull_fails() {
        assertion.assertNull(assertion);
    }

    @Test
    public void testIsNotNull() {
        assertion.assertNotNull(assertion);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testIsNotNull_fails() {
        assertion.assertNotNull(null);
    }

    @Test
    public void testStringEquals() {
        assertion.assertEquals("Hallo Welt", "Hallo Welt");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testStringEquals_fails() {
        assertion.assertEquals("Hello World", "Hallo Welt");
    }

    @Test
    public void testStringNotEquals() {
        assertion.assertNotEquals("Hello World", "Hallo Welt");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testStringNotEquals_fails() {
        assertion.assertNotEquals("Hallo Welt", "Hallo Welt");
    }

    @Test
    public void testStartsWith() {
        assertion.assertStartsWith("a long time ago", "a long");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testStartsWith_fails() {
        assertion.assertStartsWith("a long time ago", "in the end");
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp="Expected that a fairytale \\[a long time ago\\] starts with \\[in the end\\]")
    public void testStartsWith_fails_message() {
        assertion.assertStartsWith("a long time ago", "in the end", "a fairytale");
    }

    @Test
    public void testEndsWith() {
        assertion.assertEndsWith("this is the end", "the end");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testEndsWith_fails() {
        assertion.assertEndsWith("this is the end", "the beginning");
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Expected that the sentence \\[this is the end\\] ends with \\[the beginning\\]")
    public void testEndsWith_fails_message() {
        assertion.assertEndsWith("this is the end", "the beginning", "the sentence");
    }
}
