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
 package eu.tsystems.mms.tic.testframework.hooks;

import com.google.common.eventbus.EventBus;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.RetryAnalyzer;
import eu.tsystems.mms.tic.testframework.execution.testng.WebDriverRetryAnalyzer;
import eu.tsystems.mms.tic.testframework.execution.worker.finish.ConditionalBehaviourWorker;
import eu.tsystems.mms.tic.testframework.execution.worker.finish.LogWDSessionsWorker;
import eu.tsystems.mms.tic.testframework.execution.worker.finish.TakeInSessionEvidencesWorker;
import eu.tsystems.mms.tic.testframework.execution.worker.finish.TakeOutOfSessionsEvidencesWorker;
import eu.tsystems.mms.tic.testframework.execution.worker.finish.WebDriverSessionsAfterMethodWorker;
import eu.tsystems.mms.tic.testframework.execution.worker.finish.WebDriverShutDownWorker;
import eu.tsystems.mms.tic.testframework.execution.worker.start.PerformanceTestWorker;
import eu.tsystems.mms.tic.testframework.interop.TestEvidenceCollector;
import eu.tsystems.mms.tic.testframework.report.ScreenshotGrabber;
import eu.tsystems.mms.tic.testframework.report.SourceGrabber;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.UITestStepIntegration;
import eu.tsystems.mms.tic.testframework.watchdog.WebDriverWatchDog;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;

public class DriverUiHook implements ModuleHook {

    @Override
    public void init() {
        /*
        init test step integration
         */
        UITestStepIntegration.init();

        /*
        init TesterraListener Workers
         */
        //start
        EventBus eventBus = TesterraListener.getEventBus();

        if (Testerra.Properties.PERF_GENERATE_STATISTICS.asBool()) {
            eventBus.register(new PerformanceTestWorker());
        }
        //eventBus.register(new WebDriverLoggingStartWorker());

        //finish
        eventBus.register(new ConditionalBehaviourWorker());
        eventBus.register(new LogWDSessionsWorker());
        eventBus.register(new TakeInSessionEvidencesWorker());

        eventBus.register(new WebDriverSessionsAfterMethodWorker()); // the utilizable one

        /*
        ********* SESSIONS SHUTDOWN *********
         */
        eventBus.register(new WebDriverShutDownWorker());
        eventBus.register(new TakeOutOfSessionsEvidencesWorker());

        /*
        register services
         */
        // RetryAnalyzer
        RetryAnalyzer.registerAdditionalRetryAnalyzer(new WebDriverRetryAnalyzer());
        // Screenshots and Videos
        TestEvidenceCollector.registerScreenshotCollector(new ScreenshotGrabber());
        TestEvidenceCollector.registerSourceCollector(new SourceGrabber());
    }

    @Override
    public void terminate() {
        WebDriverManager.forceShutdownAllThreads();
        WebDriverWatchDog.stop();
    }
}
