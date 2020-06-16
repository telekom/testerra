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

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.internal.AssertionsCollector;
import eu.tsystems.mms.tic.testframework.report.model.AssertionInfo;
import org.testng.ITestResult;

import java.util.List;

public class HandleCollectedAssertsWorker extends MethodWorker {

    final AssertionsCollector assertionsCollector = Testerra.injector.getInstance(AssertionsCollector.class);

    @Override
    public void run() {
        if (assertionsCollector.hasEntries()) {
            // add all collected assertions
            List<AssertionInfo> entries = assertionsCollector.getEntries();
            methodContext.addCollectedAssertions(entries);

            if (isSuccess()) {
                // let the test fail
                testResult.setStatus(ITestResult.FAILURE);
                String msg = "The following assertions failed:";
                int i = 0;
                for (AssertionInfo entry : entries) {
                    i++;
                    msg += "\n" + i + ") " + entry.getReadableErrorMessage();
                }

                AssertionError testMethodContainerError = new AssertionError(msg);
                testResult.setThrowable(testMethodContainerError);

                // update test method container
                methodContext.errorContext().setThrowable(null, testMethodContainerError);
                testResult.setAttribute(SharedTestResultAttributes.failsFromCollectedAssertsOnly, Boolean.TRUE);
            }
        }
    }
}
