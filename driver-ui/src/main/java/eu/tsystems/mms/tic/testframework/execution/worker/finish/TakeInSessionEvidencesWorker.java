/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.execution.worker.finish;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.SharedTestResultAttributes;
import eu.tsystems.mms.tic.testframework.interop.TestEvidenceCollector;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import java.util.List;

public class TakeInSessionEvidencesWorker implements MethodEndEvent.Listener {

    protected void collect(MethodEndEvent methodEndEvent) {
        // get screenshots and videos
        List<Screenshot> screenshots = TestEvidenceCollector.collectScreenshots();

        if (screenshots != null) {
            Report report = Testerra.getInjector().getInstance(Report.class);
            screenshots.forEach(screenshot -> {
                methodEndEvent.getMethodContext().addScreenshot(screenshot);
                report.addScreenshot(screenshot, Report.FileMode.MOVE);
            });
        }
    }

    @Override
    @Subscribe
    public void onMethodEnd(MethodEndEvent methodEndEvent) {
        if (methodEndEvent.isFailed()) {
            Object attribute = methodEndEvent.getTestResult().getAttribute(SharedTestResultAttributes.failsFromCollectedAssertsOnly);

            if (attribute != Boolean.TRUE) {
                collect(methodEndEvent);
            }
        } else if (methodEndEvent.isSkipped()) {
            if (methodEndEvent.getMethodContext().getStatus() == TestStatusController.Status.FAILED_RETRIED) {
                collect(methodEndEvent);
            }
        }
    }
}
