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
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;

/**
 * Created by sagu on 18.10.2016.
 *
 * Test class with all possible test results.
 *
 */
public class ReportFilterTest extends AbstractTest {

    @Test
    public void testFilterPassed(){
        Assert.assertTrue(true, "What a wonderful test.");
    }

    @Test
    public void testFilterPassedWithMinorErrors(){
        NonFunctionalAssert.assertTrue(false);
        Assert.assertTrue(true, "Something went wrong.");
    }

    @Test
    public void testFilterFailed(){
        Assert.assertTrue(false, "What a stupid test.");
    }

    @Test
    public void testFilterFailedWithMinorErrors(){
        NonFunctionalAssert.assertTrue(false);
        Assert.assertTrue(false, "Something went terribly wrong.");
    }

    @Test(dependsOnMethods = "testFilterFailed")
    public void testFilterSkipped(){
        Assert.assertTrue(true, "What a wonderful test.");
    }

    @Test
    @Fails (description = "Expected to fail")
    public void testFilterExpectedFail(){
        Assert.assertTrue(false);
    }
}
