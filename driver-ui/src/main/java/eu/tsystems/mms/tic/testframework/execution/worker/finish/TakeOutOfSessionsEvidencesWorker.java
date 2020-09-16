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
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.SharedTestResultAttributes;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.interop.TestEvidenceCollector;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.context.Video;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;

import java.util.List;

public class TakeOutOfSessionsEvidencesWorker extends AbstractEvidencesWorker implements Loggable {

    protected void collect() {
        if (event.getTestMethod().isTest() && WebDriverManager.config().areSessionsClosedAfterTestMethod()) {
            /*
            videos are now fetched only after test methods
             */
            List<Video> videos = TestEvidenceCollector.collectVideos();
            log().debug("Evidence Videos: " + videos);
            if (videos != null) {
                videos.forEach(v -> v.errorContextId = event.getMethodContext().id);
                event.getMethodContext().videos.addAll(videos);
            }
        }
    }

    @Override
    @Subscribe
    public void onMethodEnd(MethodEndEvent event) {
        this.event = event;

        if (event.isFailed() && Flags.SCREENCASTER_ACTIVE_ON_FAILED) {
            Object attribute = event.getTestResult().getAttribute(SharedTestResultAttributes.failsFromCollectedAssertsOnly);

            if (attribute != Boolean.TRUE) {
                collect();
            }
        } else if (event.isPassed() && Flags.SCREENCASTER_ACTIVE_ON_SUCCESS) {
            collect();

        } else if (event.isSkipped()) {
            if (event.getMethodContext().status == TestStatusController.Status.FAILED_RETRIED) {
                collect();
            }
        }
    }
}
