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

import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testhelper.AbstractReportingTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
@Listeners(FennecListener.class)
//@FennecClassContext("ReportingAllStatesTest")
public class ConfigurationMethodsTest extends AbstractReportingTest {

    int counter = 0;
    String info = "";

    @Test
    public void testPassed() throws Exception {
    }

    @Test
    public void testFailed() throws Exception {
        failingStep(How.HARD);
    }

    @Test
    public void testFailed1() throws Exception {
        failingStep(How.HARD);
    }

    @Test
    public void testFailed2() throws Exception {
        failingStep(How.HARD);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        counter++;
        info += "\nafterMethod";
    }

    @AfterClass
    public void afterClass() {
        counter++;
        info += "\nafterClass";
    }

    @AfterTest
    public void afterTest() {
        counter++;
        info += "\nafterTest";
    }

    @AfterSuite
    public void afterSuite() {
        System.out.println(info);
        Assert.assertEquals(counter, 6, "method ran: " + info);
    }
}
