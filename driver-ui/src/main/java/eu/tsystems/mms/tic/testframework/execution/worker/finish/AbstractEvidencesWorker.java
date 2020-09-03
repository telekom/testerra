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
package eu.tsystems.mms.tic.testframework.execution.worker.finish;

import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.SharedTestResultAttributes;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;

public abstract class AbstractEvidencesWorker implements MethodEndEvent.Listener {

    protected MethodEndEvent event;

    protected abstract void collect();

    @Override
    public void onMethodEnd(MethodEndEvent event) {
        this.event = event;
        if (event.isFailed()) {
            Object attribute = event.getTestResult().getAttribute(SharedTestResultAttributes.failsFromCollectedAssertsOnly);

            if (attribute != Boolean.TRUE) {
                collect();
            }
        } else if (event.isSkipped()) {
            if (event.getMethodContext().status == TestStatusController.Status.FAILED_RETRIED) {
                collect();
            }
        }
    }

}
