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
import eu.tsystems.mms.tic.testframework.annotations.TestClassContext;
import eu.tsystems.mms.tic.testframework.events.FinalizeExecutionEvent;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.monitor.JVMMonitor;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.ReportingData;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
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
import java.util.Comparator;
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
        ExecutionContextController.getCurrentExecutionContext().readSuiteContexts().forEach(
                suiteContext -> {
                    System.out.println("Suite: " + suiteContext.getName());
                    suiteContext.readTestContexts().forEach(
                            testContext -> {
                                System.out.println("Test: " + testContext.getName());
                                testContext.readClassContexts().forEach(
                                        classContext -> {
                                            System.out.println("Class: " + classContext.getName());
                                            classContext.readMethodContexts()
                                                    .filter(methodContext -> methodContext.getStatus() == TestStatusController.Status.PASSED)
                                                    .forEach(
                                                            methodContext -> {
                                                                System.out.println("Method: " + methodContext.getName());
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
                            context.getErrorContext().setDescription(failsAnnotation.description());
                        }

                        if (failsAnnotation.ticketId() > 0) {
                            additionalErrorMessage += " Ticket: " + failsAnnotation.ticketId();
                            context.getErrorContext().setTicketId(failsAnnotation.ticketId());
                        }

                        if (!failsAnnotation.ticketString().isEmpty()) {
                            context.getErrorContext().setTicketId(failsAnnotation.ticketString());
                        }

                        context.getErrorContext().additionalErrorMessage = additionalErrorMessage;
                    }
                });
    }

    //find method context of expected failed test where it's underlying cause matches the cause of the given context
    private static Optional<MethodContext> findMatchingMethodContext(MethodContext context,
                                                                     List<MethodContext> methodContexts) {
        return methodContexts.stream()
                .filter(expectedFailedMethodContext ->
                        expectedFailedMethodContext.isExpectedFailed()
                                && context.getErrorContext().getThrowable().getMessage() != null
                                && expectedFailedMethodContext.getErrorContext().getThrowable().getCause().getMessage() != null
                                && expectedFailedMethodContext.getErrorContext().getThrowable().getCause().getMessage()
                                .equals(context.getErrorContext().getThrowable().getMessage()))
                .findFirst();
    }

    public void addMethodStats(ReportingData reportingData, ClassContext classContext) {
        Map<TestStatusController.Status, Integer> methodStats = classContext.createStats();
        Map<TestStatusController.Status, Integer> configMethodStats = classContext.createStats();
        classContext.readMethodContexts().forEach(methodContext -> {
            if (methodContext.isTestMethod()) {
                classContext.addToStats(methodStats, methodContext);
            } else if (methodContext.isConfigMethod()) {
                classContext.addToStats(configMethodStats, methodContext);
            }
        });
        reportingData.methodStatsPerClass.put(classContext, methodStats);
        reportingData.configMethodStatsPerClass.put(classContext, configMethodStats);
    }

    /**
     * Get method statistics for all effective classes.
     * The classes are all classContext from the context tree (without merged ones) AND mergedClassContexts from executionContext.
     *
     * Use in dashboard.vm and classesStatistics.vm
     */
    public void createMethodStatsPerClass(ReportingData reportingData) {
        Map<String, ClassContext> mergedClassContexts = new HashMap<>();
        reportingData.executionContext.readSuiteContexts().forEach(suiteContext -> {
            suiteContext.readTestContexts().forEach(testContext -> {
                testContext.readClassContexts().forEach(classContext -> {
                    Optional<TestClassContext> optionalTestClassContext = classContext.getTestClassContext();
                    if (optionalTestClassContext.isPresent()) {
                        TestClassContext testClassContext = optionalTestClassContext.get();
                        ClassContext mergedClassContext;
                        if (!mergedClassContexts.containsKey(testClassContext.name())) {
                            mergedClassContext = new ClassContext(classContext.getTestClass(), testContext);
                            mergedClassContext.setName(testClassContext.name());
                            mergedClassContexts.put(testClassContext.name(), mergedClassContext);
                        } else {
                            mergedClassContext = mergedClassContexts.get(testClassContext.name());
                        }
                        mergedClassContext.methodContexts.addAll(classContext.readMethodContexts().collect(Collectors.toList()));
                    } else {
                        addMethodStats(reportingData, classContext);
                    }
                });
            });
        });

        mergedClassContexts.values().forEach(classContext -> addMethodStats(reportingData, classContext));

        /*
        sort
         */
        final AtomicReference<Integer> i1 = new AtomicReference<>();
        final AtomicReference<Integer> i2 = new AtomicReference<>();
        Comparator<? super Map> comp = (Comparator<Map>) (m1, m2) -> {
            i1.set(0);
            m1.keySet().forEach(status -> i1.set(i1.get() + ((int) m1.get(status))));

            i2.set(0);
            m2.keySet().forEach(status -> i2.set(i2.get() + ((int) m2.get(status))));

            return i2.get() - i1.get();
        };
        reportingData.methodStatsPerClass = reportingData.methodStatsPerClass.entrySet().stream().sorted(Map.Entry.comparingByValue(comp))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    @Override
    @Subscribe
    public void onFinalizeExecution(FinalizeExecutionEvent event) {
        long start = System.currentTimeMillis();

        ReportingData reportingData = new ReportingData();
        reportingData.executionContext = event.getExecutionContext();
        createMethodStatsPerClass(reportingData);

        // get ALL ClassContexts
        final List<ClassContext> allClassContexts = new ArrayList<>(reportingData.methodStatsPerClass.keySet());

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
                    final String fingerprint = methodContext.getErrorContext().getExitFingerprint();
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
                    final String readableMessage = methodContext.getErrorContext().getReadableErrorMessage();
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
        reportingData.exitPoints = exitPoints;
        reportingData.failureAspects = failureAspects;

        PerfTestReportUtils.prepareMeasurementsForReport();
        JVMMonitor.label("Report");
        /*
         * Create report
         */
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
        log().debug("Took " + formattedDuration + " to create the report.");
    }
}
