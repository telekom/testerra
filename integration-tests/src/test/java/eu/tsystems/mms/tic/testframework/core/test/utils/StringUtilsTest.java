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

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by toku on 02.12.2014.
 */
public class StringUtilsTest extends AbstractWebDriverTest {

    private String stringEmptyAssertMessage = "String is empty";
    private String stringLengthAssertMessage = "String length";
    private String stringContainsCharacterAssertMessage = "String contains character ";

    /**
     * checks if string is empty
     *
     * @throws Exception .
     */
    @Test
    public void testT01_IsStringEmpty_emptyString() throws Exception {
        String emptyString = "";
        boolean stringEmpty = StringUtils.isStringEmpty(emptyString);
        Assert.assertTrue(stringEmpty, stringEmptyAssertMessage);

    }

    /**
     * checks if string is not empty
     *
     * @throws Exception .
     */
    @Test
    public void testT02_IsStringEmpty_NonEmptyString() throws Exception {
        String emptyString = "GAA";
        boolean stringEmpty = StringUtils.isStringEmpty(emptyString);
        Assert.assertFalse(stringEmpty, stringEmptyAssertMessage);
    }

    /**
     * checks if string is null
     * @throws Exception .
     */
    @Test
    public void testT03_IsStringEmpty_nullString() throws Exception {
        String emptyString = null;
        boolean stringEmpty = StringUtils.isStringEmpty(emptyString);
        Assert.assertTrue(stringEmpty, stringEmptyAssertMessage);
    }

    /**
     * checks if created random string has the defined length and if it only contains characters
     * @throws Exception .
     */
    @Test
    public void testT04_RandomStringWithLength() throws Exception {
        int length = 10;
        String randomStr = StringUtils.getRandomStringWithLength(length);
        Assert.assertEquals(randomStr.length(), length, stringLengthAssertMessage);

        String regex = "[0-9]";
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(randomStr);
        Assert.assertFalse(matcher.find(), stringContainsCharacterAssertMessage + regex);

    }

    /**
     * checks if created string has defined length and if it only contains numbers
     *
     * @throws Exception .
     */
    @Test
    public void testT05_RandomStringOfNumbersWithLength() throws Exception {
        int length = 5;
        String randomStr = StringUtils.getRandomStringOfNumbersWithLength(length);
        Assert.assertEquals(randomStr.length(), length);

        String regex = "[A-Za-z]";
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(randomStr);
        Assert.assertFalse(matcher.find(), stringContainsCharacterAssertMessage + regex);

    }

    /**
     * checks if created string has defined length and if it only contains special characters
     *
     * @throws Exception .
     */
    @Test
    public void testT06_RandomSpecialCharacterStringWithLength() throws Exception {
        int length = 3;
        String randomStr = StringUtils.getRandomSpecialCharacterStringWithLength(length);
        Assert.assertEquals(randomStr.length(), length);

        String regex = "[A-Za-z0-9]";
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(randomStr);
        Assert.assertFalse(matcher.find(), stringContainsCharacterAssertMessage + regex);
    }

    @Test
    public void testT07_ConcatObjects() throws Exception {
        String concat = StringUtils.concat(",", 1, 2, 3, 4);
        Assert.assertEquals(concat, "1,2,3,4");
    }
}
