/*
 * Testerra
 *
 * (C) 2024, Martin Großmann,  Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Created on 2024-11-05
 *
 * @author mgn
 */
public class DataProvider2Test extends TesterraTest {

    @BeforeMethod
    public void beforeMethodOfDataProvider2Tests() {
        Assert.fail("Before method has an assertion error.");
    }

    @DataProvider
    public Object[][] dataProviderOfDataProvider2Tests() {
        return new String[][]{{"run01"}, {"run02"}};
    }

    @Test(dataProvider = "dataProviderOfDataProvider2Tests")
    public void testT01_DataProviderWithFailedSetup(String dp) {
    }

}
