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
 * Created on 07.03.14
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public class ReportingConfigMethodsFail4Test extends AbstractTest {

    /*
    AnnotationTransformerListener only works with global listener. So execute the AnnotationTransformerListener.xml from resources.
     */

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @BeforeClass
    public void setUpClass() throws Exception {
    }


    @BeforeMethod
    public void setUp(Method method) throws Exception {
    }

    @Test
    public void testT01Passed() {
    }

    @AfterMethod
    public void tearDown() throws Exception {
    }

    @AfterClass
    public void tearDownClass() throws Exception {
        throw new RuntimeException("Must Fail");
    }
}
