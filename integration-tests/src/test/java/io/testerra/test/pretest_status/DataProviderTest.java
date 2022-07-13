/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package io.testerra.test.pretest_status;

import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DataProviderTest extends TesterraTest implements AssertProvider {

    /**
     * This test uses an external data provider throwing an exception
     */
    @Test(dataProviderClass = MyDataProvider.class, dataProvider = "dataProviderThrowingException")
    public void testT01_interceptCrashedDataProvider() {
    }

    @DataProvider()
    public Object[][] dataProviderThrowingException() {
        throw new RuntimeException("Crashed data provider");
    }

    /**
     * This test uses an internal data provider throwing an exception
     */
    @Test(dataProvider = "dataProviderThrowingException")
    public void testT02_crashedDataProvider(Object object) {
    }

    @DataProvider
    public Object[][] dataProviderThrowingAssertion() {
        Object[][] objects = new Object[1][1];
        Assert.fail("Failed assertion in data provider");
        return objects;
    }

    /**
     * This test uses an internal data provider throwing an assertion
     */
    @Test(dataProvider = "dataProviderThrowingAssertion")
    public void testT03_AssertFailedDataProvider(String dp) throws Exception {
    }

    @DataProvider
    public Object[][] dataProviderSimple() {
        String[][] objects = {{"passed"}, {"failed"}};
        return objects;
    }

    /**
     * This tests uses a default dataprovider with 2 elements, 1 passed, 1 failed.
     */
    @Test(dataProvider = "dataProviderSimple")
    public void testT04_DataProviderWithFailedTests(String dp) {
        Assert.assertEquals(dp, "passed");
    }

    /**
     * This tests uses a default dataprovider with 2 elements.
     * Failed assertion is only optional (also passed)
     */
    @Test(dataProvider = "dataProviderSimple")
    public void testT05_DataProviderWithFailedTestsOptional(String dp) {
        CONTROL.optionalAssertions(() -> {
            ASSERT.assertEquals(dp, "passed");
        });
    }

}
