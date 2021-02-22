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
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.events.AfterShutdownWebDriverSessionsEvent;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.interop.TestEvidenceCollector;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.context.Video;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import java.util.List;

public class TakeOutOfSessionsEvidencesWorker implements Loggable, AfterShutdownWebDriverSessionsEvent.Listener {

    private final boolean SCREENCASTER_ACTIVE_ON_SUCCESS = PropertyManager.getBooleanProperty(TesterraProperties.SCREENCASTER_ACTIVE_ON_SUCCESS, false);
    private final boolean SCREENCASTER_ACTIVE_ON_FAILED = PropertyManager.getBooleanProperty(TesterraProperties.SCREENCASTER_ACTIVE_ON_FAILED, true);

    protected void collect(MethodEndEvent methodEndEvent) {
        if (methodEndEvent.getTestMethod().isTest() && WebDriverManager.getConfig().shouldShutdownSessionAfterTestMethod()) {
            /*
            videos are now fetched only after test methods
             */
            List<Video> videos = TestEvidenceCollector.collectVideos();
            log().debug("Evidence Videos: " + videos);
        }
    }

    @Override
    @Subscribe
    public void onAfterShutdownWebDriverSessionsEvent(AfterShutdownWebDriverSessionsEvent event) {
        MethodEndEvent methodEndEvent = event.getMethodEndEvent();
        if (methodEndEvent.isFailed() && SCREENCASTER_ACTIVE_ON_FAILED) {
            collect(methodEndEvent);
        } else if (methodEndEvent.isPassed() && SCREENCASTER_ACTIVE_ON_SUCCESS) {
            collect(methodEndEvent);

        } else if (methodEndEvent.isSkipped()) {
            if (methodEndEvent.getMethodContext().getStatus() == TestStatusController.Status.FAILED_RETRIED) {
                collect(methodEndEvent);
            }
        }
    }
}
