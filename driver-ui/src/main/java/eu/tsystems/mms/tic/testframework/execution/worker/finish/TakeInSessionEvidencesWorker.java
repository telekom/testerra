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
import eu.tsystems.mms.tic.testframework.interop.TestEvidenceCollector;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import java.util.List;

public class TakeInSessionEvidencesWorker extends AbstractEvidencesWorker {

    protected void collect() {
        if (WebDriverManager.hasSessionsActiveInThisThread()) {
            // get screenshots and videos
            List<Screenshot> screenshots = TestEvidenceCollector.collectScreenshots();
            event.getMethodContext().addScreenshots(screenshots.stream());
        }
    }

    @Override
    @Subscribe
    public void onMethodEnd(MethodEndEvent event) {
        super.onMethodEnd(event);
    }
}
