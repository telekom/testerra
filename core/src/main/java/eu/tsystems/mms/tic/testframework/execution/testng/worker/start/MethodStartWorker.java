/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.execution.testng.worker.start;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.annotations.NoRetry;
import eu.tsystems.mms.tic.testframework.events.MethodStartEvent;
import eu.tsystems.mms.tic.testframework.execution.testng.RetryAnalyzer;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.DefaultFormatter;
import eu.tsystems.mms.tic.testframework.utils.Formatter;
import org.testng.IRetryAnalyzer;
import org.testng.ITestNGMethod;
import org.testng.internal.annotations.DisabledRetryAnalyzer;

import java.lang.reflect.Method;

public class MethodStartWorker implements Loggable, MethodStartEvent.Listener {

    private final Formatter formatter = new DefaultFormatter();

    @Override
    @Subscribe
    public void onMethodStart(MethodStartEvent event) {
        event.getMethodContext().setThreadName();

        ITestNGMethod testMethod = event.getTestMethod();
        if (testMethod.isTest()) {
            addRetryAnalyzer(event);
        }

        log().info("Run " + formatter.toString(event.getTestMethod()));
    }

    private void addRetryAnalyzer(MethodStartEvent event) {
        ITestNGMethod testNGMethod = event.getTestMethod();
        final IRetryAnalyzer retryAnalyzer = testNGMethod.getRetryAnalyzer(event.getTestResult());
        Method method = event.getMethod();
        if (retryAnalyzer == null || retryAnalyzer instanceof DisabledRetryAnalyzer) {
            if (method.isAnnotationPresent(NoRetry.class)) {
                log().trace("Not adding "+ RetryAnalyzer.class.getSimpleName() +" for @NoRetry " + method.getName());
            } else {
                testNGMethod.setRetryAnalyzerClass(RetryAnalyzer.class);
                log().trace("Adding " + RetryAnalyzer.class.getSimpleName() + " for " + method.getName());
            }
        } else {
            log().info("Using a non-default retry analyzer: " + retryAnalyzer + " on " + method.getName());
        }
    }
}
