/*
 * Testerra
 *
 * (C) 2022, Clemens Große, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package io.testerra.report.test.pretest_status.simple;

import eu.tsystems.mms.tic.testframework.report.Status;
import io.testerra.report.test.AbstractTestSitesTest;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class GenerateSkippedStatusInTesterraReportTest extends AbstractTestSitesTest {

    private final String SKIPPED_EXCEPTION_MESSAGE = String.format("Test %s.", Status.SKIPPED.title);

    @Test(groups = {Groups.BASIC})
    public void test_Failed() {
        Assert.fail("Creating TestStatus 'Failed'");
    }

    @Test(groups = {Groups.BASIC})
    public void test_SkippedNoStatus() {
        throw new SkipException(SKIPPED_EXCEPTION_MESSAGE);
    }

    @Test(dependsOnMethods = "test_Failed", groups = {Groups.BASIC})
    public void test_Skipped_DependingOnFailed() {
        // will be skipped
    }

    @DataProvider
    public Object[][] dataProviderWithError() {
        throw new RuntimeException("Error in DataProvider.");
    }

    @Test(dataProvider = "dataProviderWithError", groups = {Groups.BASIC})
    public void test_Skipped_AfterErrorInDataProvider() {
        // will be skipped
    }
}
