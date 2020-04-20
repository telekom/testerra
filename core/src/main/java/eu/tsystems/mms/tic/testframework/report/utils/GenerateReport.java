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
 *     Sebastian Kiehne
 *     Eric Kubenka
 */
package eu.tsystems.mms.tic.testframework.report.utils;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.boot.Booter;
import eu.tsystems.mms.tic.testframework.events.TesterraEvent;
import eu.tsystems.mms.tic.testframework.events.TesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.TesterraEventService;
import eu.tsystems.mms.tic.testframework.events.TesterraEventType;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.GenerateReportsWorkerExecutor;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.shutdown.GenerateOtherOutputsWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.shutdown.GenerateTesterraReportWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.shutdown.TestEndEventWorker;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.internal.MethodRelations;
import eu.tsystems.mms.tic.testframework.monitor.JVMMonitor;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.external.junit.JUnitXMLReporter;
import eu.tsystems.mms.tic.testframework.report.model.ReportingData;
import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.report.Report;
import eu.tsystems.mms.tic.testframework.utils.FrameworkUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class GenerateReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateReport.class);

    static {
        // flush run contexts
        MethodRelations.flushAll();

        JVMMonitor.label("Tests Finished");

        JVMMonitor.stop();
        JVMMonitor.start(1000);

        /*
        List tests only
         */
        if (Flags.LIST_TESTS) {
            GenerateReport.printTestsList();
            // discontinue
            System.exit(0);
        }
    }

    private static boolean ran = false;

    public static void runOnce(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory, JUnitXMLReporter xmlReporter) {
        // static block is executed before!!!

        if (!ran) {
            // just run once, just in case we are not exiting...
            ran = true;

            // set the testRunFinished flag
            ExecutionContextController.testRunFinished = true;

            /*
            workers
             */
            GenerateReportsWorkerExecutor workerExecutor = new GenerateReportsWorkerExecutor();

            workerExecutor.add(new TestEndEventWorker());

            FrameworkUtils.addWorkersToExecutor(TesterraListener.GENERATE_REPORTS_WORKERS, workerExecutor);

            workerExecutor.add(new GenerateTesterraReportWorker());
            workerExecutor.add(new GenerateOtherOutputsWorker());

            // run workers
            workerExecutor.run(xmlSuites, suites, outputDirectory, xmlReporter);

            Report report = new Report();
            File finalDirectory = report.finalizeReport();
            LOGGER.info("Report written to " + finalDirectory.getAbsolutePath());

            /*
             * Shutdown local services and hooks
             */
            JVMMonitor.stop();
            Booter.shutdown();

            /*
            print stats
             */
            ExecutionContextController.printExecutionStatistics();

            /*
             * Check failure corridor and set exit code and state
             */
            if (Flags.FAILURE_CORRIDOR_ACTIVE) {
                FailureCorridor.printStatusToStdOut();
            }
        }
    }

    /**
     * Stops logging of TesterraCommands. Statistics are filled and reports are generated.
     */
    public static void generateReport() {
        ExecutionContextController.EXECUTION_CONTEXT.endTime = new Date();
        JVMMonitor.label("Report");
        pGenerateReport();
    }

    private static void pGenerateReport() {
        /*
        get ALL ClassContexts
         */
        final List<ClassContext> allClassContexts = new ArrayList<>(ExecutionContextController.EXECUTION_CONTEXT.getMethodStatsPerClass(true, false).keySet());

        /*
         * Build maps for exit points and failure aspects
         */
        LOGGER.info("Build maps for exit points and failure aspects...");
        Map<String, List<MethodContext>> exitPoints = new TreeMap<>();
        Map<String, List<MethodContext>> failureAspects = new TreeMap<>();
        int unknownCounter = 0;
        // scan
        for (ClassContext classContext : allClassContexts) {
            List<MethodContext> methodContexts = classContext.copyOfMethodContexts();
            for (MethodContext methodContext : methodContexts) {
                /*
                 * Every failed method that is not flagged config or retry
                 */
                if (methodContext.isFailed() && !methodContext.isConfigMethod() && !methodContext.isRetry()) {
                    /*
                    get exit points (this is the fingerprint)
                     */
                    final String fingerprint = methodContext.errorContext().errorFingerprint;
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
        ExecutionContextController.EXECUTION_CONTEXT.exitPoints = exitPoints;
        ExecutionContextController.EXECUTION_CONTEXT.failureAspects = failureAspects;

        /*
        final execution context sync
         */
        TesterraEventService.getInstance().fireEvent(
                new TesterraEvent(TesterraEventType.CONTEXT_UPDATE)
                        .addUserData()
                        .addData(TesterraEventDataType.CONTEXT, ExecutionContextController.EXECUTION_CONTEXT)
        );

        /*
         * Create report
         */
        LOGGER.info("Create Report...");
        ReportingData reportingData = new ReportingData();
        reportingData.executionContext = ExecutionContextController.EXECUTION_CONTEXT;
        reportingData.failureCorridorMatched = FailureCorridor.isCorridorMatched();
        reportingData.classContexts = allClassContexts;

        ReportUtils.createReport(reportingData);
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

    private static void printTestsList() {
        System.out.println("");
        System.out.println(" ## List of all test methods in this steps ##");
        System.out.println("");

        final AtomicReference<Integer> testMethodsCount = new AtomicReference<>();
        testMethodsCount.set(0);
        ExecutionContextController.EXECUTION_CONTEXT.copyOfSuiteContexts().forEach(
                suiteContext -> {
                    System.out.println("Suite: " + suiteContext.name);
                    suiteContext.copyOfTestContexts().forEach(
                            testContext -> {
                                System.out.println("Test: " + testContext.name);
                                testContext.copyOfClassContexts().forEach(
                                        classContext -> {
                                            System.out.println("Class: " + classContext.name);
                                            classContext.copyOfMethodContexts()
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
                        }

                        if (failsAnnotation.ticketId() > 0) {
                            additionalErrorMessage += " Ticket: " + failsAnnotation.ticketId();
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
}
