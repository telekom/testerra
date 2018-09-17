/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
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
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.execution.testng.worker.start;

import eu.tsystems.mms.tic.testframework.events.FennecEvent;
import eu.tsystems.mms.tic.testframework.events.FennecEventDataType;
import eu.tsystems.mms.tic.testframework.events.FennecEventService;
import eu.tsystems.mms.tic.testframework.events.FennecEventType;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.report.FennecListener;

/**
 * Created by pele on 19.01.2017.
 */
public class FennecEventsStartWorker extends MethodWorker {

    @Override
    public void run() {
        if (wasMethodInvoked()) {
            if (isTest()) {
                // fire event
                FennecEventService.getInstance().fireEvent(new FennecEvent(FennecEventType.TEST_METHOD_START)
                        .addUserData()
                        .addData(FennecEventDataType.METHOD_NAME, methodName)
                        .addData(FennecEventDataType.TIMESTAMP, FennecListener.getThreadStartTime())
                        .addData(FennecEventDataType.ITestResult, testResult)
                        .addData(FennecEventDataType.IInvokedMethod, invokedMethod)
                );
            } else {
                // fire event
                FennecEventService.getInstance().fireEvent(new FennecEvent(FennecEventType.CONFIGURATION_METHOD_START)
                        .addUserData()
                        .addData(FennecEventDataType.METHOD_NAME, methodName)
                        .addData(FennecEventDataType.TIMESTAMP, FennecListener.getThreadStartTime())
                        .addData(FennecEventDataType.ITestResult, testResult)
                        .addData(FennecEventDataType.IInvokedMethod, invokedMethod)
                );
            }
        }
    }
}
