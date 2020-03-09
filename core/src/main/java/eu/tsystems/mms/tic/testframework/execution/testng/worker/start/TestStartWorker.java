/*
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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.execution.testng.worker.start;

import eu.tsystems.mms.tic.testframework.annotations.NoRetry;
import eu.tsystems.mms.tic.testframework.events.TesterraEvent;
import eu.tsystems.mms.tic.testframework.events.TesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.TesterraEventService;
import eu.tsystems.mms.tic.testframework.events.TesterraEventType;
import eu.tsystems.mms.tic.testframework.execution.testng.RetryAnalyzer;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.testng.IRetryAnalyzer;
import org.testng.ITestNGMethod;
import org.testng.internal.annotations.DisabledRetryAnalyzer;

import java.lang.reflect.Method;

public class TestStartWorker extends MethodWorker {

    @Override
    public void run() {
        // set StartTime at very first test invocation.
        synchronized (TesterraListener.class) {
            if (wasMethodInvoked()) {
                // fire event for test start
                TesterraEventService.getInstance().fireEvent(new TesterraEvent(TesterraEventType.TEST_START)
                        .addUserData()
                        .addData(TesterraEventDataType.TIMESTAMP, ExecutionContextController.EXECUTION_CONTEXT.startTime.getTime())
                        .addData(TesterraEventDataType.ITestResult, testResult)
                        .addData(TesterraEventDataType.IInvokedMethod, method)
                );
            }
        }

        // set the thread name
        methodContext.setThreadName();

        // Thread visualizer and method timer start.
        TesterraListener.startMethodTimer();

        // add retry analyzer
        if (isTest()) {
            addRetryAnalyzer(testMethod, method);
        }
    }

    private void addRetryAnalyzer(ITestNGMethod testNGMethod, Method method) {
        /*
         * Checks the test testNGMethod annotation for a retry analyzer. If no one is specified, it uses the tt. retry
         * analyzer.
         */
        if (testNGMethod != null) {
            final IRetryAnalyzer retryAnalyzer = testNGMethod.getRetryAnalyzer(testResult);
            if (retryAnalyzer == null || retryAnalyzer instanceof DisabledRetryAnalyzer) {

                if (method.isAnnotationPresent(NoRetry.class)) {
                    LOGGER.debug("Not adding testerra RetryAnalyzer for @NoRetry " + method.getName());
                } else {
                    testNGMethod.setRetryAnalyzerClass(RetryAnalyzer.class);
                    LOGGER.info("Adding testerra RetryAnalyzer for " + method.getName());
                }
            } else {
                LOGGER.info("Using a non-testerra retry analyzer: " + retryAnalyzer + " on " + method.getName());
            }
        }

    }
}
