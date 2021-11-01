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
package eu.tsystems.mms.tic.testframework.report.testundertest;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.report.general.TestsUnderTestGroup;
import eu.tsystems.mms.tic.testframework.report.pageobjects.ExitPointCreaterTestClass1;
import java.util.UUID;
import org.testng.annotations.Test;

public class ReportTestUnderTestExpectedtoFail extends AbstractTest {

    private String uniqueFailureAspectMessage = "matchting unique failure aspect: " + UUID.randomUUID().toString();

    @Fails(description = "This is a known bug.")
    @Test(groups = {TestsUnderTestGroup.TESTSUNDERTESTFILTER})
    public void test_FailedMinorAnnotatedWithFail_Run4() throws Exception {
        ExitPointCreaterTestClass1.testCreatorForDifferentExitPoints();
    }

    @Fails(description = "This is a known bug.")
    @Test(groups = {TestsUnderTestGroup.TESTSUNDERTESTFILTER})
    public void test_FailedMinorAnnotatedWithFail_Run5() throws Exception {
        ExitPointCreaterTestClass1.testCreatorForDifferentExitPoints();
    }

    @Fails(description = "This is a known bug.")
    @Test(groups = {TestsUnderTestGroup.TESTSUNDERTESTFILTER})
    public void test_FailedMinorAnnotatedWithFail_Run6() throws Exception {
        ExitPointCreaterTestClass1.testCreatorForDifferentExitPoints();
    }

    @Fails(description = "This is an unknown bug.", intoReport = true)
    @Test(groups = {TestsUnderTestGroup.TESTSUNDERTESTFILTER})
    public void test_FailedMinorAnnotatedWithFailInReport() throws Exception {
        ExitPointCreaterTestClass1.testCreatorForDifferentExitPoints();
    }

    @Test(groups = {TestsUnderTestGroup.TESTSUNDERTESTFILTER})
    public void test_UnexpectedFailedWithRelatedExpectedFailed() throws Exception {
        throw new Exception(uniqueFailureAspectMessage);
    }

    @Fails(description = "Known issue with same aspect as unmarked failed test")
    @Test(groups = {TestsUnderTestGroup.TESTSUNDERTESTFILTER})
    public void test_ExpectedFailedWithRelatedUnexpectedFailed() throws Exception {
        throw new Exception(uniqueFailureAspectMessage);
    }
}
