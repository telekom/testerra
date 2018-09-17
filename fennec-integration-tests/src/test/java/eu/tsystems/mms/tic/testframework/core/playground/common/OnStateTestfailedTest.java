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
 * Created on 26.11.13
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.common;

import eu.tsystems.mms.tic.testframework.constants.FennecProperties;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * <Beschreibung der Klasse>
 * 
 * @author pele
 */
public class OnStateTestfailedTest extends AbstractTest {

    static {
        System.setProperty(FennecProperties.ON_STATE_TESTFAILED_SKIP_FOLLOWING_TESTS, "true");
        System.setProperty(FennecProperties.ON_STATE_TESTFAILED_SKIP_SHUTDOWN, "true");
    }

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testT01_failingTest() {
        WebDriverManager.getWebDriver();
        Assert.assertTrue(false);
    }

    @Test
    public void testT02_shouldBeSkipped1() {
        logger.info("Should be skipped 1");
    }

    @Test
    public void testT02_shouldBeSkipped2() {
        logger.info("Should be skipped 2");
        Assert.assertTrue(false);
    }
}
