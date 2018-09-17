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

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testframework.utils.RandomUtils;
import eu.tsystems.mms.tic.testframework.utils.TestUtils;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class LoggingTests.
 *
 * @author pele
 */
@Listeners(FennecListener.class)
public class ReportFailingTests2 {

    static {
        FailureCorridor.setFailureCorridorActive(true);
        FailureCorridor.setFailureCorridor(0,0,0);
    }

    private void failingTest() {
        Assert.assertTrue(false, "Eval");
    }

    @FailureCorridor.Mid
    @Test
    public void test2() throws Exception {
        failingTest();
    }

    @FailureCorridor.Low
    @Test
    public void test3() throws Exception {
        failingTest();
    }

    @FailureCorridor.High
    @Test
    public void test4() throws Exception {
        failingTest();
    }

    @FailureCorridor.High
    @Test
    public void test5NF() throws Exception {
        NonFunctionalAssert.assertTrue(false);
        failingTest();
    }

    @FailureCorridor.Low
    @Test
    public void test6NF() throws Exception {
        NonFunctionalAssert.assertTrue(false);
        failingTest();
    }

    @FailureCorridor.Low
    @Test
    @Fails(description = "huhu")
    public void test7NF_Fails() throws Exception {
        NonFunctionalAssert.assertTrue(false);
        failingTest();
    }


}
