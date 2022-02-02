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
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.execution.testng.RetryAnalyzer;
import eu.tsystems.mms.tic.testframework.execution.testng.WebDriverRetryAnalyzer;
import eu.tsystems.mms.tic.testframework.execution.worker.finish.ConditionalBehaviourWorker;
import eu.tsystems.mms.tic.testframework.execution.worker.finish.TakeInSessionEvidencesWorker;
import eu.tsystems.mms.tic.testframework.execution.worker.finish.WebDriverShutDownWorker;
import eu.tsystems.mms.tic.testframework.execution.worker.start.PerformanceTestWorker;
import eu.tsystems.mms.tic.testframework.internal.asserts.DefaultPropertyAssertionFactory;
import eu.tsystems.mms.tic.testframework.internal.asserts.PropertyAssertionFactory;
import eu.tsystems.mms.tic.testframework.interop.TestEvidenceCollector;
import eu.tsystems.mms.tic.testframework.listeners.ShutdownSessionsListener;
import eu.tsystems.mms.tic.testframework.listeners.WatchdogStartupListener;
import eu.tsystems.mms.tic.testframework.pageobjects.DefaultUiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.AriaElementLocator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.DefaultPageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.DefaultUiElementFinderFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.PageFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementFinderFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementLabelLocator;
import eu.tsystems.mms.tic.testframework.report.ScreenshotGrabber;
import eu.tsystems.mms.tic.testframework.report.SourceGrabber;
import eu.tsystems.mms.tic.testframework.report.UITestStepIntegration;
import eu.tsystems.mms.tic.testframework.testing.TestController;
import eu.tsystems.mms.tic.testframework.testing.UiElementOverrides;
import eu.tsystems.mms.tic.testframework.useragents.BrowserInformation;
import eu.tsystems.mms.tic.testframework.useragents.UapBrowserInformation;
import eu.tsystems.mms.tic.testframework.watchdog.WebDriverWatchDog;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DefaultWebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.IWebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverCapabilities;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverSessionsManager;

public class DriverUiHook extends AbstractModule implements ModuleHook {

    private static IWebDriverManager webDriverManager;

    @Override
    protected void configure() {
        // Singletons
        bind(UiElementFactory.class).to(DefaultUiElementFactory.class).in(Scopes.SINGLETON);
        bind(PageFactory.class).to(DefaultPageFactory.class).in(Scopes.SINGLETON);
        bind(TestController.Overrides.class).to(UiElementOverrides.class).in(Scopes.SINGLETON);
        bind(IWebDriverManager.class).to(DefaultWebDriverManager.class).in(Scopes.SINGLETON);
        bind(UiElementLabelLocator.class).to(AriaElementLocator.class).in(Scopes.SINGLETON);
        bind(UiElementFinderFactory.class).to(DefaultUiElementFinderFactory.class).in(Scopes.SINGLETON);
        bind(PropertyAssertionFactory.class).to(DefaultPropertyAssertionFactory.class).in(Scopes.SINGLETON);

        // Instances
        bind(BrowserInformation.class).to(UapBrowserInformation.class);
    }

    @Override
    public void init() {
        webDriverManager = Testerra.getInjector().getInstance(IWebDriverManager.class);
        webDriverManager.registerWebDriverRequestConfigurator(new WebDriverCapabilities());
        /*
        init test step integration
         */
        UITestStepIntegration.init();

        /*
        init TesterraListener Workers
         */
        //start
        EventBus eventBus = Testerra.getEventBus();

        if (Testerra.Properties.PERF_GENERATE_STATISTICS.asBool()) {
            eventBus.register(new PerformanceTestWorker());
        }
        //eventBus.register(new WebDriverLoggingStartWorker());

        //finish
        eventBus.register(new ConditionalBehaviourWorker());
        eventBus.register(new TakeInSessionEvidencesWorker());

        /*
         ********* SESSIONS SHUTDOWN *********
         */
        eventBus.register(new WebDriverShutDownWorker());
        eventBus.register(new ShutdownSessionsListener());

        /*
        register services
         */
        // RetryAnalyzer
        RetryAnalyzer.registerAdditionalRetryAnalyzer(new WebDriverRetryAnalyzer());
        // Screenshots and Videos
        TestEvidenceCollector.registerScreenshotCollector(new ScreenshotGrabber());
        TestEvidenceCollector.registerSourceCollector(new SourceGrabber());

        // start WatchDog for hanging sessions
        boolean watchdogEnabled = PropertyManager.getBooleanProperty(TesterraProperties.WATCHDOG_ENABLE, false);
        if (watchdogEnabled) {
            WebDriverSessionsManager.registerWebDriverAfterStartupHandler(new WatchdogStartupListener());
        }
    }

    @Override
    public void terminate() {
        shutdownModule();
    }

    public static void shutdownModule() {
        webDriverManager.requestShutdownAllSessions();
        WebDriverWatchDog.stop();
    }
}
