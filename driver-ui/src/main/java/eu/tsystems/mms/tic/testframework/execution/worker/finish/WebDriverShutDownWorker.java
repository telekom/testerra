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
import eu.tsystems.mms.tic.testframework.events.AfterShutdownWebDriverSessionsEvent;
import eu.tsystems.mms.tic.testframework.events.BeforeShutdownWebDriverSessionsEvent;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import java.util.ArrayList;
import java.util.List;
import org.testng.ITestResult;

public class WebDriverShutDownWorker implements MethodEndEvent.Listener, WebDriverManagerProvider {

    @Override
    @Subscribe
    public void onMethodEnd(MethodEndEvent methodEndEvent) {
        ITestResult iTestResult = methodEndEvent.getTestResult();

        /*
         * Take Screenshot of failure and log it into report.
         */
        if (iTestResult != null) {
            List<SessionContext> sessionsToCloseAtOnce = new ArrayList<>();
            methodEndEvent.getMethodContext().readSessionContexts().forEach(sessionContext -> {
                WebDriverRequest webDriverRequest = sessionContext.getWebDriverRequest();
                boolean shouldClose = false;
                if (iTestResult.getMethod().isTest() && webDriverRequest.getShutdownAfterTest()) {
                    shouldClose = true;
                } else if (!iTestResult.isSuccess() && webDriverRequest.getShutdownAfterTestFailed()) {
                    shouldClose = true;
                }

                if (shouldClose) {
                    sessionsToCloseAtOnce.add(sessionContext);
                }
            });

            if (sessionsToCloseAtOnce.size() > 0) {
                Testerra.getEventBus().post(new BeforeShutdownWebDriverSessionsEvent(methodEndEvent));
                sessionsToCloseAtOnce.forEach(sessionContext -> {
                    WEB_DRIVER_MANAGER.getWebDriver(sessionContext).ifPresent(WEB_DRIVER_MANAGER::shutdownSession);
                });
                Testerra.getEventBus().post(new AfterShutdownWebDriverSessionsEvent(methodEndEvent));
            }
        }
    }
}
