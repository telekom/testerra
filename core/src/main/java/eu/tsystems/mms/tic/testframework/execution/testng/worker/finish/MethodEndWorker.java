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

package eu.tsystems.mms.tic.testframework.execution.testng.worker.finish;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.events.TestStatusUpdateEvent;
import eu.tsystems.mms.tic.testframework.execution.testng.RetryAnalyzer;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.Formatter;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

public class MethodEndWorker implements MethodEndEvent.Listener, Loggable {
    private final Formatter formatter = Testerra.getInjector().getInstance(Formatter.class);

    @Subscribe
    @Override
    public void onMethodEnd(MethodEndEvent event) {
        ITestResult testResult = event.getTestResult();
        ITestNGMethod testMethod = event.getTestMethod();
        MethodContext methodContext = event.getMethodContext();

        String msg = String.format("%s %s", methodContext.getStatus().title, formatter.toString(testMethod));
        if (event.isFailed()) {
            log().error(msg, testResult.getThrowable());
        } else if (event.getTestResult().isSuccess()) {
            log().info(msg, testResult.getThrowable());
        } else if (event.isSkipped()) {
            log().warn(msg, testResult.getThrowable());
        }

        /**
         * When the test did not fail, then we announce the test status to update immediately.
         * Otherwise, we wait for the {@link RetryAnalyzer} or {@link TesterraListener#onTestSkipped(ITestResult)} to update it.
         */
        if (methodContext.getStatus() != Status.FAILED) {
            Testerra.getEventBus().post(new TestStatusUpdateEvent(methodContext));
        }

        ExecutionContextController.clearCurrentTestResult();
    }
}
