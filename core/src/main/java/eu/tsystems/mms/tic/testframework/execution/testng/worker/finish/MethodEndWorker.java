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
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.DefaultFormatter;
import eu.tsystems.mms.tic.testframework.utils.Formatter;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

public class MethodEndWorker implements MethodEndEvent.Listener, Loggable {

    private final Formatter formatter = new DefaultFormatter();
    private final ExecutionContext executionContext = ExecutionContextController.getCurrentExecutionContext();

    @Subscribe
    @Override
    public void onMethodEnd(MethodEndEvent event) {
        // clear current result
        ExecutionContextController.clearCurrentTestResult();
        ITestResult testResult = event.getTestResult();
        ITestNGMethod testMethod = event.getTestMethod();

        StringBuilder sb = new StringBuilder();
        if (event.isFailed()) {
            sb
                    .append(TestStatusController.Status.FAILED.title)
                    .append(" ")
                    .append(formatter.toString(testMethod));
            log().error(sb.toString(), testResult.getThrowable());
        }
        else if (event.getTestResult().isSuccess()) {
            sb
                    .append(TestStatusController.Status.PASSED.title)
                    .append(" ")
                    .append(formatter.toString(testMethod));
            log().info(sb.toString(), testResult.getThrowable());
        }
        else if (event.isSkipped()) {
            sb
                    .append(TestStatusController.Status.SKIPPED.title)
                    .append(" ")
                    .append(formatter.toString(testMethod));
            log().warn(sb.toString(), testResult.getThrowable());
        }

        if (testMethod.isTest()) {
            // cleanup thread locals from PropertyManager
            PropertyManager.clearThreadlocalProperties();
        }

        executionContext.addStatusCount(event.getMethodContext());
        TestStatusController.writeCounterToLog();

        TesterraListener.getEventBus().post(new ContextUpdateEvent().setContext(event.getMethodContext()));
    }
}
