/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ReportTestUnderTestBeforeScenarios extends AbstractTest {

    @BeforeSuite(groups = {SystemTestsGroup.SYSTEMTESTSFILTER8})
    public void beforeSuiteFailed() {
        Assert.assertTrue(false);
    }

    @BeforeSuite(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9})
    public void beforeSuitePassed() {
        Assert.assertTrue(true);
    }

    @BeforeTest(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9, SystemTestsGroup.BEFORE_TEST_FAILED})
    public void beforeTestFailed() {
        Assert.assertTrue(false);
    }

    @BeforeTest(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9})
    public void beforeTestPassed() {
        Assert.assertTrue(true);
    }

    @BeforeGroups(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9, SystemTestsGroup.BEFORE_GROUPS_FAILED})
    public void beforeGroupsFailed() {
        Assert.assertTrue(false);
    }

    @BeforeGroups(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9})
    public void beforeGroupsPassed() {
        Assert.assertTrue(true);
    }

    @BeforeClass(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9, SystemTestsGroup.BEFORE_CLASS_FAILED})
    public void beforeClassFailed() {
        Assert.assertTrue(false);
    }

    @BeforeClass(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9})
    public void beforeClassPassed() {
        Assert.assertTrue(true);
    }

    @BeforeMethod(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9, SystemTestsGroup.BEFORE_METHOD_FAILED})
    public void beforeMethodFailed(Method method) {
        Assert.assertTrue(false);
    }

    @BeforeMethod(groups = {SystemTestsGroup.SYSTEMTESTSFILTER9})
    public void beforeMethodPassed(Method method) {
        Assert.assertTrue(true);
    }

    /* CONTROL METHODS */
    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER8, SystemTestsGroup.SYSTEMTESTSFILTER9})
    public void controlMethodAfterBeforeScenarioPassed() {
        Assert.assertTrue(true);
    }

    @Test(groups = {SystemTestsGroup.SYSTEMTESTSFILTER8, SystemTestsGroup.SYSTEMTESTSFILTER9})
    public void controlMethodAfterBeforeScenarioFailed() {
        Assert.assertTrue(false);
    }


}
