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
package eu.tsystems.mms.tic.testframework.execution.testng.worker.finish;

import eu.tsystems.mms.tic.testframework.events.fennecEvent;
import eu.tsystems.mms.tic.testframework.events.fennecEventDataType;
import eu.tsystems.mms.tic.testframework.events.fennecEventService;
import eu.tsystems.mms.tic.testframework.events.fennecEventType;
import eu.tsystems.mms.tic.testframework.execution.testng.RetryAnalyzer;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.fennecListener;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.threadvisualizer.DataSet;
import eu.tsystems.mms.tic.testframework.report.threadvisualizer.DataStorage;
import eu.tsystems.mms.tic.testframework.report.threadvisualizer.Util;

/**
 * Created by pele on 19.01.2017.
 */
public class fennecEventsFinishWorker extends MethodWorker {

    private void addThreadVisualizerDataSet() {
        final String formattedContent = Util.getFormattedContent(methodContext);

        long startTimeTime = methodContext.startTime.getTime();
        long endTimeTime = methodContext.endTime.getTime();

        if (endTimeTime - startTimeTime <= 10) {
            endTimeTime = startTimeTime + 10;
        }

        final DataSet dataSet = new DataSet(
                Thread.currentThread().getName() + "#" + Thread.currentThread().getId(), formattedContent,
                startTimeTime,
                endTimeTime);
        DataStorage.addDataSet(dataSet);
    }

    @Override
    public void run() {
        if (wasMethodInvoked()) {
            /*
            calculate addThreadVisualizerDataSet
             */
            addThreadVisualizerDataSet();

            /*
            fire END event
             */
            if (isTest()) {
                long duration = methodContext.endTime.getTime() - methodContext.startTime.getTime();
                fennecEventService.getInstance().fireEvent(new fennecEvent(fennecEventType.TEST_METHOD_END)
                        .addUserData()
                        .addData(fennecEventDataType.METHOD_NAME, methodName)
                        .addData(fennecEventDataType.TIMESTAMP, System.currentTimeMillis())
                        .addData(fennecEventDataType.TESTRESULT_STATUS, testResult.getStatus())
                        .addData(fennecEventDataType.DURATION, duration)
                        .addData(fennecEventDataType.ITestResult, testResult)
                        .addData(fennecEventDataType.IInvokedMethod, invokedMethod)
                );

                /*
                 * If a test is failed and has a filtered throwable, then fire an event.
                 */
                boolean hasFilteredThrowable;
                if (isFailed()) {
                    hasFilteredThrowable = RetryAnalyzer.isTestResultContainingFilteredThrowable(testResult);
                    if (hasFilteredThrowable) {
                        fennecEventService.getInstance().fireEvent(new fennecEvent(fennecEventType.TEST_WITH_FILTERED_THROWABLE)
                                .addUserData()
                                .addData(fennecEventDataType.TIMESTAMP, System.currentTimeMillis())
                                .addData(fennecEventDataType.METHOD_NAME, methodName)
                                .addData(fennecEventDataType.ITestResult, testResult)
                                .addData(fennecEventDataType.IInvokedMethod, invokedMethod)
                        );
                    }
                }

                /*
                 * Create an event for the first failed test.
                 */
                if (isFailed() && TestStatusController.areAllTestsPassedYet()) {
                    fennecEventService.getInstance().fireEvent(new fennecEvent(fennecEventType.FIRST_FAILED_TEST)
                            .addUserData()
                            .addData(fennecEventDataType.TIMESTAMP, System.currentTimeMillis())
                            .addData(fennecEventDataType.METHOD_NAME, methodName)
                            .addData(fennecEventDataType.ITestResult, testResult)
                            .addData(fennecEventDataType.IInvokedMethod, invokedMethod)
                    );
                }
            } else {
                // fire event
                fennecEventService.getInstance().fireEvent(new fennecEvent(fennecEventType.CONFIGURATION_METHOD_END)
                        .addUserData()
                        .addData(fennecEventDataType.METHOD_NAME, methodName)
                        .addData(fennecEventDataType.TIMESTAMP, System.currentTimeMillis())
                        .addData(fennecEventDataType.TESTRESULT_STATUS, testResult.getStatus())
                        .addData(fennecEventDataType.ITestResult, testResult)
                        .addData(fennecEventDataType.IInvokedMethod, invokedMethod)
                );

            }
        }

        /*
        clean timer
         */
        fennecListener.cleanMethodTimer();
    }
}
