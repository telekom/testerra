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
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.ITestResult;

public class HandleCollectedAssertsWorker implements MethodEndEvent.Listener {

    @Override
    @Subscribe
    public void onMethodEnd(MethodEndEvent event) {
        ITestResult testResult = event.getTestResult();
        MethodContext methodContext = event.getMethodContext();
        if (testResult.isSuccess() && methodContext.getNumAssertions() > 0) {
            // let the test fail
            testResult.setStatus(ITestResult.FAILURE);
            StringBuilder sb = new StringBuilder();
            sb.append("The following assertions failed:");
            AtomicInteger i = new AtomicInteger();
            methodContext.readCollectedAssertions().forEach(errorContext -> {
                i.incrementAndGet();
                sb.append("\n").append(i).append(") ").append(errorContext.getReadableErrorMessage());
            });

            AssertionError testMethodContainerError = new AssertionError(sb.toString());
            testResult.setThrowable(testMethodContainerError);

            // update test method container
            methodContext.getErrorContext().setThrowable(null, testMethodContainerError);
        }
    }
}
