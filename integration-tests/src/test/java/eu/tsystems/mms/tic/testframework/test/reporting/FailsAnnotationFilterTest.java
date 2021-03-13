/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 *
 */
 package eu.tsystems.mms.tic.testframework.test.reporting;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.report.utils.FailsAnnotationFilter;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * FailsAnnotationFilterTest
 * <p>
 * Date: 15.05.2017
 * Time: 12:13
 *
 * @author erku
 */
public class FailsAnnotationFilterTest extends TesterraTest {

    private static String PROPERTY_ONE = "test.foobar.fails.annotation.test.property.one";
    private static String PROPERTY_TWO = "test.foobar.fails.annotation.test.property.two";
    private static String PROPERTY_THREE = "test.foobar.fails.annotation.test.property.three";

    static {
        System.setProperty(PROPERTY_ONE, "one");
        System.setProperty(PROPERTY_TWO, "true");
        System.setProperty(PROPERTY_THREE, "3");
    }

    @Test
    @Fails(validFor = {"test.foobar.fails.annotation.test.property.one=one"})
    public void testValidForWithOneKeyValue() {

        final String[] validFor = {"test.foobar.fails.annotation.test.property.one=one"};

        boolean failsAnnotationValid = FailsAnnotationFilter.isFailsAnnotationValid(validFor);
        Assert.assertTrue(failsAnnotationValid);
    }

    @Test
    @Fails(validFor = {"test.foobar.fails.annotation.test.property.one=one", "test.foobar.fails.annotation.test.property.two=true"})
    public void testValidForWithTwoKeyValues() {

        final String[] validFor = {"test.foobar.fails.annotation.test.property.one=one", "test.foobar.fails.annotation.test.property.two=true"};

        boolean failsAnnotationValid = FailsAnnotationFilter.isFailsAnnotationValid(validFor);
        Assert.assertTrue(failsAnnotationValid);
    }

    @Test
    @Fails(validFor = {})
    public void testValidForWithNoneInput() {

        final String[] validFor = {};
        boolean failsAnnotationValid = FailsAnnotationFilter.isFailsAnnotationValid(validFor);
        Assert.assertTrue(failsAnnotationValid);
    }

    @Test
    @Fails(validFor = {"", ""})
    public void testValidForWithEmptyInput() {

        final String[] validFor = {"", ""};
        boolean failsAnnotationValid = FailsAnnotationFilter.isFailsAnnotationValid(validFor);
        Assert.assertTrue(failsAnnotationValid);
    }

    @Test
    @Fails(validFor = {"foo", "bar"})
    public void testValidForWithNotWellFormedInput() {

        final String[] validFor = {"foo", "bar"};
        boolean failsAnnotationValid = FailsAnnotationFilter.isFailsAnnotationValid(validFor);
        Assert.assertTrue(failsAnnotationValid);
    }

    @Test
    @Fails(validFor = {"foo="})
    public void testValidForWithCorruptInput() {

        final String[] validFor = {"foo="};
        boolean failsAnnotationValid = FailsAnnotationFilter.isFailsAnnotationValid(validFor);
        Assert.assertTrue(failsAnnotationValid);
    }

    @Test
    @Fails(validFor = {"test.foobar.fails.annotation.test.property.one=two"}, description = "This should fail", ticketString = "https://jira.com/TICKET-ID")
    public void testNotValidForKeyValue() {

        final String[] validFor = {"test.foobar.fails.annotation.test.property.one=two"};

        boolean failsAnnotationValid = FailsAnnotationFilter.isFailsAnnotationValid(validFor);
        Assert.assertFalse(failsAnnotationValid);
    }

    @Test
    @Fails(validFor = {"test.foobar.fails.annotation.test.property.one=one", "test.foobar.fails.annotation.test.property.three=1"})
    public void testNotValidForMultipleKeyValues() {

        final String[] validFor = {"test.foobar.fails.annotation.test.property.one=one", "test.foobar.fails.annotation.test.property.three=1"};

        boolean failsAnnotationValid = FailsAnnotationFilter.isFailsAnnotationValid(validFor);
        Assert.assertFalse(failsAnnotationValid);
    }

}
