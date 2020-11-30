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
import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.events.FinalizeExecutionEvent;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.monitor.JVMMonitor;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.ReportingData;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.perf.PerfTestReportUtils;
import eu.tsystems.mms.tic.testframework.report.threadvisualizer.DataSet;
import eu.tsystems.mms.tic.testframework.report.threadvisualizer.DataStorage;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.DateUtils;
import eu.tsystems.mms.tic.testframework.utils.ReportUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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

        long startTimeTime = methodContext.getStartTime().getTime();
        long endTimeTime = methodContext.getEndTime().getTime();

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

    private static Map<String, List<MethodContext>> sortTestMethodContainerMap(final Map<String, List<MethodContext>> inputMap) {

        final Map<List, String> reverseOrigMap = new HashMap<>();
        final List<List> listOfLists = new LinkedList<>();
        for (String key : inputMap.keySet()) {
            List<MethodContext> list = inputMap.get(key);
            listOfLists.add(list);
            reverseOrigMap.put(list, key);
        }
        listOfLists.sort((o1, o2) -> o2.size() - o1.size());
        final Map<String, List<MethodContext>> newMap = new LinkedHashMap<>();
        for (List list : listOfLists) {
            newMap.put(reverseOrigMap.get(list), list);
        }
        return newMap;
    }

    // check if failed tests have an expected failed with the same root cause and a message about it to the failed test
    private static void addMatchingExpectedFailedMessage(Map<String, List<MethodContext>> failureAspects) {
        List<MethodContext> expectedFailedMethodContexts =
                failureAspects.values().stream()
                        //only one context per expected failed required
                        .map(e -> e.get(0))
                        .filter(MethodContext::isExpectedFailed)
                        .collect(Collectors.toList());

        List<MethodContext> unexpectedFailedMethodContexts =
                failureAspects.values().stream()
                        .flatMap(Collection::stream)
                        .filter(methodContext -> !methodContext.isExpectedFailed())
                        .collect(Collectors.toList());

        unexpectedFailedMethodContexts.forEach(
                context -> {
                    final Optional<MethodContext> methodContext = findMatchingMethodContext(context, expectedFailedMethodContexts);

                    if (methodContext.isPresent()) {

                        final Fails failsAnnotation = methodContext.get().testResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Fails.class);
                        String additionalErrorMessage = "Failure aspect matches known issue:";

                        if (StringUtils.isNotBlank(failsAnnotation.description())) {
                            additionalErrorMessage += " Description: " + failsAnnotation.description();
                            context.errorContext().setDescription(failsAnnotation.description());
                        }

                        if (failsAnnotation.ticketId() > 0) {
                            additionalErrorMessage += " Ticket: " + failsAnnotation.ticketId();
                            context.errorContext().setTicketId(failsAnnotation.ticketId());
                        }

                        if (!failsAnnotation.ticketString().isEmpty()) {
                            context.errorContext().setTicketId(failsAnnotation.ticketString());
                        }

                        context.errorContext().additionalErrorMessage = additionalErrorMessage;
                    }
                });
    }

    //find method context of expected failed test where it's underlying cause matches the cause of the given context
    private static Optional<MethodContext> findMatchingMethodContext(MethodContext context,
                                                                     List<MethodContext> methodContexts) {
        return methodContexts.stream()
                .filter(expectedFailedMethodContext ->
                        expectedFailedMethodContext.isExpectedFailed()
                                && context.errorContext().getThrowable().getMessage() != null
                                && expectedFailedMethodContext.errorContext().getThrowable().getCause().getMessage() != null
                                && expectedFailedMethodContext.errorContext().getThrowable().getCause().getMessage()
                                .equals(context.errorContext().getThrowable().getMessage()))
                .findFirst();
    }

    @Override
    @Subscribe
    public void onFinalizeExecution(FinalizeExecutionEvent event) {

        long start = System.currentTimeMillis();
        ExecutionContext currentExecutionContext = event.getExecutionContext();

        // get ALL ClassContexts
        final List<ClassContext> allClassContexts =
                new ArrayList<>(currentExecutionContext.getMethodStatsPerClass(true, false).keySet());

        /*
         * Build maps for exit points and failure aspects
         */
        log().trace("Build maps for exit points and failure aspects...");
        Map<String, List<MethodContext>> exitPoints = new TreeMap<>();
        Map<String, List<MethodContext>> failureAspects = new TreeMap<>();
        int unknownCounter = 0;
        // scan
        for (ClassContext classContext : allClassContexts) {
            for (MethodContext methodContext : classContext.methodContexts) {
                /*
                 * Every failed method that is not flagged config or retry
                 */
                if (methodContext.isFailed() && !methodContext.isConfigMethod() && !methodContext.isRetry()) {
                    /*
                    get exit points (this is the fingerprint)
                     */
                    final String fingerprint = methodContext.errorContext().getExitFingerprint();
                    final String failuresMapKey;
                    if (StringUtils.isStringEmpty(fingerprint)) {
                        // fingerprint unknown -> "others"
                        unknownCounter++;
                        failuresMapKey = "unknown exit #" + unknownCounter;
                    } else {
                        // fingerprint found
                        failuresMapKey = fingerprint;
                    }

                    // push info into map
                    if (!exitPoints.containsKey(failuresMapKey)) {
                        exitPoints.put(failuresMapKey, new LinkedList<>());
                    }
                    exitPoints.get(failuresMapKey).add(methodContext);

                    /*
                    get failure aspects (this is the error message)
                     */
                    final String readableMessage = methodContext.errorContext().getReadableErrorMessage();
                    if (!failureAspects.containsKey(readableMessage)) {
                        failureAspects.put(readableMessage, new LinkedList<>());
                    }
                    failureAspects.get(readableMessage).add(methodContext);
                }
            }
        }

        /*
        Sort exitPoints
         */
        exitPoints = sortTestMethodContainerMap(exitPoints);

        /*
        Sort failureAspects
         */
        failureAspects = sortTestMethodContainerMap(failureAspects);

        /*
        check if failed tests have an expected failed with the same root cause and a message about it to the failed test
         */
        addMatchingExpectedFailedMessage(failureAspects);

        /*
        Store
         */
        currentExecutionContext.exitPoints = exitPoints;
        currentExecutionContext.failureAspects = failureAspects;

        PerfTestReportUtils.prepareMeasurementsForReport();
        JVMMonitor.label("Report");
        /*
         * Create report
         */
        ReportingData reportingData = new ReportingData();
        reportingData.executionContext = event.getExecutionContext();
        reportingData.failureCorridorMatched = FailureCorridor.isCorridorMatched();
        reportingData.classContexts = allClassContexts;

        if (Flags.LIST_TESTS) {
            printTestsList();
            // discontinue
            System.exit(0);
        }
        ReportUtils.createReport(reportingData);
        long stop = System.currentTimeMillis();
        String formattedDuration = DateUtils.getFormattedDuration(stop - start, false);
        log().info("Took " + formattedDuration + " to create the report.");
    }
}
