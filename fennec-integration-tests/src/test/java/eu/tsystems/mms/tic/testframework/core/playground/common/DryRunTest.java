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
package eu.tsystems.mms.tic.testframework.core.playground.common;

import eu.tsystems.mms.tic.testframework.annotations.DismissDryRun;
import eu.tsystems.mms.tic.testframework.common.FennecCommons;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * Created by pele on 02.02.2016.
 */
@Listeners(FennecListener.class)
public class DryRunTest extends AbstractTest {

    private static Logger logger = LoggerFactory.getLogger(DryRunTest.class);

    static {
        Flags.DRY_RUN = true;
    }

    @BeforeMethod
    public void setUp(Method method) throws Exception {
        logger.info("SETUP");
    }

    @Test
    public void testName1() throws Exception {
        logger.info("TEST1");
    }

    @Test
    @DismissDryRun
    public void testName2() throws Exception {
        logger.info("TEST2");
    }

    @AfterMethod
    @DismissDryRun
    public void tearDown() throws Exception {
        logger.info("TEARDOWN");
    }
}
