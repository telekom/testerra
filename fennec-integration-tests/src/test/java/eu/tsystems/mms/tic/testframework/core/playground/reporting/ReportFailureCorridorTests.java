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

import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class LoggingTests.
 *
 * @author pele
 */
@Listeners(FennecListener.class)
public class ReportFailureCorridorTests extends ForceFailureCorridorActiveTest {

    private void failingTest() {
        Assert.assertTrue(false, "Eval");
    }

    @FailureCorridor.Mid
    @Test
    public void testHighPassing() throws Exception {
    }


    @FailureCorridor.High
    @Test
    public void testHigh() throws Exception {
        failingTest();
    }

    @FailureCorridor.Mid
    @Test
    public void testMid() throws Exception {
        failingTest();
    }

    @FailureCorridor.Low
    @Test
    public void testLow() throws Exception {
        failingTest();
    }

    @FailureCorridor.Mid
    @Test
    public void testResetToLow() throws Exception {
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        if (methodContext != null) {
            methodContext.failureCorridorValue = FailureCorridor.Value.Low;
        }
        failingTest();
    }

    @Test
    public void testInMethodSetToLow() throws Exception {
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        if (methodContext != null) {
            methodContext.failureCorridorValue = FailureCorridor.Value.Low;
        }
        failingTest();
    }

}
