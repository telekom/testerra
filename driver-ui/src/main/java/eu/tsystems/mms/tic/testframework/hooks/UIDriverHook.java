/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.hooks;

import eu.tsystems.mms.tic.testframework.execution.testng.RetryAnalyzer;
import eu.tsystems.mms.tic.testframework.execution.testng.WebDriverRetryAnalyzer;
import eu.tsystems.mms.tic.testframework.execution.worker.finish.*;
import eu.tsystems.mms.tic.testframework.execution.worker.shutdown.WebDriverShutDownAfterTestsWorker;
import eu.tsystems.mms.tic.testframework.execution.worker.start.PerformanceStartWorker;
import eu.tsystems.mms.tic.testframework.execution.worker.start.WebDriverLoggingStartWorker;
import eu.tsystems.mms.tic.testframework.interop.CollectAssertionInfoArtefacts;
import eu.tsystems.mms.tic.testframework.report.*;
import eu.tsystems.mms.tic.testframework.watchdog.WebDriverWatchDog;

public class UIDriverHook implements ModuleHook {

    @Override
    public void init() {
        /*
        init test step integration
         */
        UITestStepIntegration.init();

        /*
        init FennecListener Workers
         */
        //start
        FennecListener.registerBeforeMethodWorker(PerformanceStartWorker.class);
        FennecListener.registerBeforeMethodWorker(WebDriverLoggingStartWorker.class);

        //finish
        FennecListener.registerAfterMethodWorker(ConditionalBehaviourWorker.class);
        FennecListener.registerAfterMethodWorker(LogWDSessionsWorker.class);
        FennecListener.registerAfterMethodWorker(TakeScreenshotsWorker.class);

        FennecListener.registerAfterMethodWorker(WebDriverSessionsAfterMethodWorker.class); // the utilizable one

        FennecListener.registerAfterMethodWorker(WebDriverShutDownWorker.class);
        FennecListener.registerAfterMethodWorker(TestMethodFinishWorker.class);

        //shutdown
        FennecListener.registerGenerateReportsWorker(WebDriverShutDownAfterTestsWorker.class);

        /*
        register services
         */
        // RetryAnalyzer
        RetryAnalyzer.registerAdditionalRetryAnalyzer(new WebDriverRetryAnalyzer());
        // Screenshots and Videos
        CollectAssertionInfoArtefacts.registerScreenshotCollector(new ScreenshotGrabber());
        CollectAssertionInfoArtefacts.registerVideoCollector(new VideoGrabber());
        CollectAssertionInfoArtefacts.registerSourceCollector(new SourceGrabber());

    }

    @Override
    public void terminate() {
        WebDriverWatchDog.stop();
    }
}
