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
package eu.tsystems.mms.tic.testframework.core.test.utils;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.utils.NumberUtils;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toku on 07.01.2015.
 */
public class NumberUtilsTest extends AbstractTest {

    /**
     * checks the average value of the list
     */
    @Test
    public void testT01_AverageValue() {
        List<Long> testList = new ArrayList<Long>();
        testList.add(37L);
        testList.add(16L);
        testList.add(22L);

        Long value = NumberUtils.getAverageValue(testList);
        Long expectedValue = 25L;
        Assert.assertEquals(value, expectedValue, "Error while determining the average");
    }

    /**
     * gets the maximum of a list
     */
    @Test
    public void testT02_MaxValue() {
        List<Long> testList = new ArrayList<Long>();
        testList.add(14L);
        testList.add(13L);
        testList.add(15L);

        Long maximum = NumberUtils.getMaxValue(testList);
        Long expectedMax = 15L;
        Assert.assertEquals(maximum, expectedMax, "Error while determining the correct maximum");

    }

    /**
     * gets the minimum of a list
     */
    @Test
    public void testT03_MinValue() {
        List<Long> testList = new ArrayList<Long>();
        testList.add(163L);
        testList.add(154L);
        testList.add(187L);

        Long minimum = NumberUtils.getMinValue(testList);
        Long expectedMin = 154L;
        Assert.assertEquals(minimum, expectedMin, "Error while determining the correct minimum");


    }
}
