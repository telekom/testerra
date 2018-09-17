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
 * Created on 22.02.2013
 *
 * Copyright(c) 2013 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.utils.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * Class LoggingTests.
 *
 * @author pele
 */
//@Listeners(FennecListener.class)
public class DynamicReportingTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicReportingTests.class);

    public enum Mode {
        pass,

        faillow,
        failmid,
        failhigh,
        failall,
        failallowed,

        skipbm,
        skipbc
    }

    private final String modeSysProp = PropertyManager.getProperty("testmode", "pass");
//    private final String modeSysProp = PropertyManager.getProperty("testmode", "faillow");

    private Mode mode = Mode.pass;

    private void tryToMapMode() {
        try {
            mode = Mode.valueOf(modeSysProp);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Could not map: " + modeSysProp);
        }
    }

    {
        tryToMapMode();
    }

    private void fail() {
        Assert.assertTrue(false, "Must fail");
    }

    private void skip() {
        throw new SkipException("must skip");
    }

    @FailureCorridor.Low
    @Test
    public void test3_Low() {
        TestUtils.sleep(200);
        if (mode == Mode.failall || mode == Mode.faillow) {
            fail();
        }
    }

    @FailureCorridor.Mid
    @Test
    public void test2_Mid1() {
        if (mode == Mode.failall || mode == Mode.failmid) {
            fail();
        }
    }

    @FailureCorridor.Mid
    @Test
    public void test2_Mid2() {
        if (mode == Mode.failall || mode == Mode.failmid) {
            fail();
        }
    }

    @FailureCorridor.High
    @Test
    public void test1_High() {
        if (mode == Mode.failall || mode == Mode.failhigh) {
            fail();
        }
    }

    @Test
    public void test0_Skip1() {
        skip();
    }

    @Test
    public void test0_Skip2() {
        skip();
    }

    @Test
    public void test3_FailingAllowed1() {
        if (mode == Mode.failallowed) {
            fail();
        }
    }

    @Test
    public void test3_FailingAllowed2() {
        if (mode == Mode.failallowed) {
            fail();
        }
    }

    @BeforeMethod
    public void bm(Method method) {
        if (mode == Mode.skipbm) {
            fail();
        }
    }

    @BeforeClass
    public void bc() {
        if (mode == Mode.skipbc) {
            fail();
        }
    }
}
