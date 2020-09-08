/*
 * Testerra
 *
 * (C) 2020, Alex Rockstroh, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import eu.tsystems.mms.tic.testframework.annotations.DismissDryRun;
import eu.tsystems.mms.tic.testframework.annotations.InDevelopment;
import eu.tsystems.mms.tic.testframework.annotations.InfoMethod;
import eu.tsystems.mms.tic.testframework.annotations.New;
import eu.tsystems.mms.tic.testframework.annotations.NoRetry;
import eu.tsystems.mms.tic.testframework.annotations.ReadyForApproval;
import eu.tsystems.mms.tic.testframework.annotations.SupportMethod;
import org.testng.annotations.Test;

public class ReportTestUnderTestAnnotations extends AbstractTest {

    @Test
    @SupportMethod
    @New
    @ReadyForApproval
    @InDevelopment
    @NoRetry
    public void testAllMarkers() throws Exception {
    }

    @Test
    @SupportMethod
    public void testSupportMethodMarker() {
    }

    @Test
    @InDevelopment
    public void testInDevelopmentMarker() {
    }

    @Test
    @NoRetry
    public void testNoRetryMarker() {
    }

    @Test
    @New
    public void testNewMarkerSuccess() {
    }

    @Test
    @ReadyForApproval
    public void testReadyForApprovalMarker() {
    }

    @Test
    @New
    public void testNewMarkerFailure() {
        Assert.assertTrue(false);
    }

    //@Test
    @InfoMethod
    // TODO how is this displayed in report?
    public void testNoStatusMethodMarker() {
    }

    //@Test
    @DismissDryRun
    // TODO how is this displayed in report?
    public void testDismissDryRunMarker() {
    }

}
