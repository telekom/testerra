/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.listeners;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.events.FinalizeExecutionEvent;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.monitor.JVMMonitor;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.ReportingData;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.perf.PerfTestContainer;
import eu.tsystems.mms.tic.testframework.report.threadvisualizer.DataSet;
import eu.tsystems.mms.tic.testframework.report.threadvisualizer.DataStorage;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.DateUtils;
import eu.tsystems.mms.tic.testframework.utils.ReportUtils;
import java.util.concurrent.atomic.AtomicReference;

public class GenerateHtmlReportListener implements
        Loggable,
        MethodEndEvent.Listener,
        FinalizeExecutionEvent.Listener
{

    @Subscribe
    @Override
    public void onMethodEnd(MethodEndEvent event) {
        try {
            ReportUtils.createMethodDetailsStepsView(event.getMethodContext());
            addThreadVisualizerDataSet(event.getMethodContext());
        } catch (Throwable e) {
            log().error("FATAL: Could not create html", e);
        }
    }

    private void addThreadVisualizerDataSet(MethodContext methodContext) {

        long startTimeTime = methodContext.startTime.getTime();
        long endTimeTime = methodContext.endTime.getTime();

        if (endTimeTime - startTimeTime <= 10) {
            endTimeTime = startTimeTime + 10;
        }

        final DataSet dataSet = new DataSet(
                methodContext,
                startTimeTime,
                endTimeTime);
        DataStorage.addDataSet(dataSet);
    }

    private static void printTestsList() {
        System.out.println("");
        System.out.println(" ## List of all test methods in this steps ##");
        System.out.println("");

        final AtomicReference<Integer> testMethodsCount = new AtomicReference<>();
        testMethodsCount.set(0);
        ExecutionContextController.getCurrentExecutionContext().suiteContexts.forEach(
                suiteContext -> {
                    System.out.println("Suite: " + suiteContext.name);
                    suiteContext.testContextModels.forEach(
                            testContext -> {
                                System.out.println("Test: " + testContext.name);
                                testContext.classContexts.forEach(
                                        classContext -> {
                                            System.out.println("Class: " + classContext.name);
                                            classContext.methodContexts
                                                    .stream()
                                                    .filter(methodContext -> methodContext.status == TestStatusController.Status.PASSED)
                                                    .forEach(
                                                            methodContext -> {
                                                                System.out.println("Method: " + methodContext.name);
                                                                testMethodsCount.set(testMethodsCount.get() + 1);
                                                            }
                                                    );
                                        }
                                );
                            });
                }
        );

        System.out.println("");
        System.out.println("Number of tests: " + testMethodsCount.get());
        System.out.println("");
    }

    @Override
    @Subscribe
    public void onFinalizeExecution(FinalizeExecutionEvent event) {

        PerfTestContainer.prepareMeasurementsForReport();
        JVMMonitor.label("Report");
        /*
         * Create report
         */
        ReportingData reportingData = new ReportingData();
        reportingData.executionContext = event.getExecutionContext();
        reportingData.failureCorridorMatched = FailureCorridor.isCorridorMatched();
        reportingData.classContexts = event.getMethodStatsPerClass().get();

        if (Flags.LIST_TESTS) {
            printTestsList();
            // discontinue
            System.exit(0);
        }
        long start = System.currentTimeMillis();
        ReportUtils.createReport(reportingData);
        long stop = System.currentTimeMillis();
        String formattedDuration = DateUtils.getFormattedDuration(stop - start, false);
        log().debug("Took " + formattedDuration + " to create the report.");
    }
}
