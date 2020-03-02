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

import eu.tsystems.mms.tic.testframework.events.TesterraEvent;
import eu.tsystems.mms.tic.testframework.events.TesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.TesterraEventService;
import eu.tsystems.mms.tic.testframework.events.TesterraEventType;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;

public class TesterraEventsStartWorker extends MethodWorker {

    @Override
    public void run() {
        if (wasMethodInvoked()) {
            if (isTest()) {
                // fire event
                TesterraEventService.getInstance().fireEvent(new TesterraEvent(TesterraEventType.TEST_METHOD_START)
                        .addUserData()
                        .addData(TesterraEventDataType.METHOD_NAME, methodName)
                        .addData(TesterraEventDataType.TIMESTAMP, TesterraListener.getThreadStartTime())
                        .addData(TesterraEventDataType.ITestResult, testResult)
                        .addData(TesterraEventDataType.IInvokedMethod, invokedMethod)
                );
            } else {
                // fire event
                TesterraEventService.getInstance().fireEvent(new TesterraEvent(TesterraEventType.CONFIGURATION_METHOD_START)
                        .addUserData()
                        .addData(TesterraEventDataType.METHOD_NAME, methodName)
                        .addData(TesterraEventDataType.TIMESTAMP, TesterraListener.getThreadStartTime())
                        .addData(TesterraEventDataType.ITestResult, testResult)
                        .addData(TesterraEventDataType.IInvokedMethod, invokedMethod)
                );
            }
        }
    }
}
