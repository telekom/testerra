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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.execution.testng.worker.finish;

import eu.tsystems.mms.tic.testframework.events.TesterraEvent;
import eu.tsystems.mms.tic.testframework.events.TesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.TesterraEventService;
import eu.tsystems.mms.tic.testframework.events.TesterraEventType;
import eu.tsystems.mms.tic.testframework.execution.testng.RetryAnalyzer;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.threadvisualizer.DataSet;
import eu.tsystems.mms.tic.testframework.report.threadvisualizer.DataStorage;
import eu.tsystems.mms.tic.testframework.report.threadvisualizer.Util;

/**
 * Created by pele on 19.01.2017.
 */
public class TesterraEventsFinishWorker extends MethodWorker {

    private void addThreadVisualizerDataSet() {
        final String formattedContent = Util.getFormattedContent(methodContext);

        long startTimeTime = methodContext.startTime.getTime();
        long endTimeTime = methodContext.endTime.getTime();

        if (endTimeTime - startTimeTime <= 10) {
            endTimeTime = startTimeTime + 10;
        }

        final DataSet dataSet = new DataSet(
                methodContext.threadName, formattedContent,
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
                TesterraEventService.getInstance().fireEvent(new TesterraEvent(TesterraEventType.TEST_METHOD_END)
                        .addUserData()
                        .addData(TesterraEventDataType.METHOD_NAME, methodName)
                        .addData(TesterraEventDataType.TIMESTAMP, System.currentTimeMillis())
                        .addData(TesterraEventDataType.TESTRESULT_STATUS, testResult.getStatus())
                        .addData(TesterraEventDataType.DURATION, duration)
                        .addData(TesterraEventDataType.ITestResult, testResult)
                        .addData(TesterraEventDataType.IInvokedMethod, invokedMethod)
                );

                /*
                 * If a test is failed and has a filtered throwable, then fire an event.
                 */
                boolean hasFilteredThrowable;
                if (isFailed()) {
                    hasFilteredThrowable = RetryAnalyzer.isTestResultContainingFilteredThrowable(testResult);
                    if (hasFilteredThrowable) {
                        TesterraEventService.getInstance().fireEvent(new TesterraEvent(TesterraEventType.TEST_WITH_FILTERED_THROWABLE)
                                .addUserData()
                                .addData(TesterraEventDataType.TIMESTAMP, System.currentTimeMillis())
                                .addData(TesterraEventDataType.METHOD_NAME, methodName)
                                .addData(TesterraEventDataType.ITestResult, testResult)
                                .addData(TesterraEventDataType.IInvokedMethod, invokedMethod)
                        );
                    }
                }

                /*
                 * Create an event for the first failed test.
                 */
                if (isFailed() && TestStatusController.areAllTestsPassedYet()) {
                    TesterraEventService.getInstance().fireEvent(new TesterraEvent(TesterraEventType.FIRST_FAILED_TEST)
                            .addUserData()
                            .addData(TesterraEventDataType.TIMESTAMP, System.currentTimeMillis())
                            .addData(TesterraEventDataType.METHOD_NAME, methodName)
                            .addData(TesterraEventDataType.ITestResult, testResult)
                            .addData(TesterraEventDataType.IInvokedMethod, invokedMethod)
                    );
                }
            } else {
                // fire event
                TesterraEventService.getInstance().fireEvent(new TesterraEvent(TesterraEventType.CONFIGURATION_METHOD_END)
                        .addUserData()
                        .addData(TesterraEventDataType.METHOD_NAME, methodName)
                        .addData(TesterraEventDataType.TIMESTAMP, System.currentTimeMillis())
                        .addData(TesterraEventDataType.TESTRESULT_STATUS, testResult.getStatus())
                        .addData(TesterraEventDataType.ITestResult, testResult)
                        .addData(TesterraEventDataType.IInvokedMethod, invokedMethod)
                );

            }
        }

        /*
        clean timer
         */
        TesterraListener.cleanMethodTimer();
    }
}
