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
import eu.tsystems.mms.tic.testframework.execution.testng.worker.SharedTestResultAttributes;
import eu.tsystems.mms.tic.testframework.internal.CollectedAssertions;
import eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import org.testng.ITestResult;

import java.util.List;

public class HandleCollectedAssertsWorker implements MethodEndEvent.Listener {

    @Override
    @Subscribe
    public void onMethodEnd(MethodEndEvent event) {
        if (CollectedAssertions.hasEntries()) {
            // add all collected assertions
            List<ErrorContext> entries = CollectedAssertions.getEntries();
            event.getMethodContext().addCollectedAssertions(entries);
            ITestResult testResult = event.getTestResult();
            MethodContext methodContext = event.getMethodContext();
            if (testResult.isSuccess()) {
                // let the test fail
                testResult.setStatus(ITestResult.FAILURE);
                String msg = "The following assertions failed:";
                int i = 0;
                for (ErrorContext entry : entries) {
                    i++;
                    msg += "\n" + i + ") " + entry.getReadableErrorMessage();
                }

                AssertionError testMethodContainerError = new AssertionError(msg);
                testResult.setThrowable(testMethodContainerError);

                // update test method container
                methodContext.getErrorContext().setThrowable(null, testMethodContainerError);
                testResult.setAttribute(SharedTestResultAttributes.failsFromCollectedAssertsOnly, Boolean.TRUE);
            }
        }
    }
}
