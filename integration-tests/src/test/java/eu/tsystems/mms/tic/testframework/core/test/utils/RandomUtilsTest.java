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
import eu.tsystems.mms.tic.testframework.utils.RandomUtils;
import org.testng.annotations.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by toku on 09.01.2015.
 */
public class RandomUtilsTest extends AbstractWebDriverTest {

    private String stringContainsChars = "String contains not only ";
    private String stringLength = "String has not expected length";
    private String intMax = "Number is higher than expected maximum";

    /**
     * checks if random string only contains a-z
     */
    @Test
    public void testT01_randomString() {
        String testString = RandomUtils.generateRandomString();

        String regex = "^[a-z]";
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(testString);
        Assert.assertTrue(matcher.find(), stringContainsChars + regex);
    }


    /**
     * checks if random number only contains chars  0-9
     */
    @Test
    public void testT02_randomNumbers() {
        int length = 5;
        String number = RandomUtils.generateRandomNumber(length);

        Assert.assertEquals(length, 5, stringLength);

        String regex = "^[0-9]";
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(number);
        Assert.assertTrue(matcher.find(), stringContainsChars + regex);
    }

    /**
     * checks if random number only contains chars  0-9
     */
    @Test
    public void testT03_randomNumbers() {
        int length = 27;
        String number = RandomUtils.generateRandomNumber(length);

        Assert.assertEquals(length, 27, stringLength);

        String regex = "^[0-9]";
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(number);
        Assert.assertTrue(matcher.find(), stringContainsChars + regex);
    }

    /**
     * checks if random number only contains chars  0-9
     */
    @Test
    public void testT04_randomNumbersInt01() {
        int max = 1;
        Integer number = RandomUtils.generateRandomInt(max);

        Assert.assertTrue(number < 1, intMax);

        String a = "" + number;

        String regex = "^[0-9]";
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(a);
        Assert.assertTrue(matcher.find(), stringContainsChars + regex);
    }

    /**
     * checks if random number only contains chars  0-9
     */
    @Test
    public void testT05_randomNumbersInt02() {
        int max = 10;
        Integer number = RandomUtils.generateRandomInt(max);

        Assert.assertTrue(number < 10, intMax);

        String a = "" + number;

        String regex = "^[0-9]";
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(a);
        Assert.assertTrue(matcher.find(), stringContainsChars + regex);
    }

}
