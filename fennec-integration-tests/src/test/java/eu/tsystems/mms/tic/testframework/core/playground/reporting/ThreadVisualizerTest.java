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
/*
 * Created on 13.03.14
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.utils.RandomUtils;
import eu.tsystems.mms.tic.testframework.utils.TestUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public class ThreadVisualizerTest extends AbstractTest {

    @DataProvider(name = "dp", parallel = true)
    public Object[][] dp() {
        int size = 40;
        Object[][] objects = new Object[size][2];
        for (int i = 0; i < size; i++) {
            objects[i] = new Object[]{"set " + i, RandomUtils.generateRandomInt(100)};
        }
        return objects;
    }

    @Test(dataProvider = "dp")
    public void testT01Pass(String set, Integer time) {
        TestUtils.sleep(time);
    }

    @Test(dataProvider = "dp")
    public void testT01Fail(String set, Integer time) {
        TestUtils.sleep(time);
        Assert.assertTrue(false, "Must fail");
    }

    @Test(dataProvider = "dp")
    public void testT01Exception(String set, Integer time) {
        TestUtils.sleep(time);
        throw new RuntimeException("huhu");
    }

    @Test(dataProvider = "dp")
    public void testT01Minor(String set, Integer time) {
        TestUtils.sleep(time);
        NonFunctionalAssert.assertTrue(false, "minor");
    }

}
