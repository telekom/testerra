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
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import org.testng.ITestResult;

public class WebDriverShutDownWorker implements MethodEndEvent.Listener, WebDriverManagerProvider {

    @Override
    @Subscribe
    public void onMethodEnd(MethodEndEvent methodEndEvent) {
        ITestResult iTestResult = methodEndEvent.getTestResult();
        if (iTestResult != null) {
            methodEndEvent.getMethodContext().readSessionContexts().forEach(sessionContext -> {
                WebDriverRequest webDriverRequest = sessionContext.getWebDriverRequest();
                boolean shouldClose = false;
                /**
                 * Shutting down sessions when the method is ONLY a test.
                 * Testerra does not shutdown sessions after configuration methods.
                 */
                if (iTestResult.getMethod().isTest() && webDriverRequest.getShutdownAfterTest()) {
                    shouldClose = true;
                } else if (!iTestResult.isSuccess() && webDriverRequest.getShutdownAfterTestFailed()) {
                    shouldClose = true;
                }

                if (shouldClose) {
                    WEB_DRIVER_MANAGER.getWebDriver(sessionContext).ifPresent(WEB_DRIVER_MANAGER::shutdownSession);
                }
            });
        }
    }
}
