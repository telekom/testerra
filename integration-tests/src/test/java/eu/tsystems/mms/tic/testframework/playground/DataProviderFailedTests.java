/*
 * Testerra
 *
 * (C) 2022, Martin Großmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.execution.testng.OptionalAssert;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Created on 10.03.2022
 *
 * @author mgn
 */
public class DataProviderFailedTests extends TesterraTest implements Loggable {

    // TODO:
    // - Data provider provides no data

    @DataProvider(name = "dpAssertFailed")
    public Object[][] dpAssertFailedMethod() {
        Object[][] objects = new Object[1][1];
        Assert.fail("Failed assertion in Dataprovider");
        return objects;
    }

    @Test(dataProvider = "dpAssertFailed")
    public void testDpAssertFailed(String dp) throws Exception {

    }



    @DataProvider(name = "dpOptionalAssertFailed")
    public Object[][] dpOptionalAssertFailed() {
        Object[][] objects = new Object[1][1];
        // TODO: This otpional assert cannot store because at this time no method context is created
        OptionalAssert.assertTrue(false, "Failed optional assertion in Dataprovider");
        return objects;
    }

    @Test(dataProvider = "dpOptionalAssertFailed")
    public void testDpOptionalAssertFailed(String dp) throws Exception {

    }

    @DataProvider(name = "dpException")
    public Object[][] dpExecption() {
        throw new RuntimeException("Exception in Dataprovider");
    }

    @Test(dataProvider = "dpException")
    public void testDpException(String dp) throws Exception {

    }

//    @BeforeMethod(groups = "failedGroup1")
//    public void beforeMethod() {
//        Assert.fail("Failed assertion in setup method");
//    }
//
//    @Test(groups = "failedGroup1")
//    public void testFailedBeforeMethod() {
//
//    }

}
