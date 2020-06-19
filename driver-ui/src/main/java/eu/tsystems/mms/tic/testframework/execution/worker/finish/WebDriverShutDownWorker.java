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

import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WDInternal;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

public class WebDriverShutDownWorker extends MethodWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverShutDownWorker.class);

    /**
     * WebDriverManagerShutdown routine. Shutdown if test method.
     *
     * @param invokedMethodName .
     * @param testResult        .
     */
    private static void webDriverManagerShutdownRoutine(final String invokedMethodName, final Object testResult) {
        if (WebDriverManager.hasAnySessionActive()) {
            ITestResult iTestResult = null;
            if (testResult instanceof ITestResult) {
                iTestResult = (ITestResult) testResult;
            }

            /*
             * Take Screenshot of failure and log it into report.
             */
            if (iTestResult != null) {
                WebDriverManagerConfig config = WebDriverManager.config();

                if (iTestResult.getMethod().isTest()) {
                    // shutdown in case of a test method
                    boolean close = true;
                    if (config.executeCloseWindows) {
                        if (!config.closeWindowsAfterTestMethod) {
                            close = false;
                        } else {
                            if (!iTestResult.isSuccess() && !config.closeWindowsOnFailure) {
                                close = false;
                            }
                        }
                    } else {
                        close = false;
                    }

                    if (close) {
                        WebDriverManager.shutdown();
                        // cleanup executing selenium host
                        WDInternal.cleanupExecutingSeleniumHost();
                    }
                }
            }
            // nothing more here!!
        }
    }

    @Override
    public void run() {
        webDriverManagerShutdownRoutine(methodName, testResult);

        // WDM cleanup threadlocals
        WebDriverManager.cleanupThreadlocals();
    }
}
