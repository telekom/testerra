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
package eu.tsystems.mms.tic.testframework.report.utils;

import eu.tsystems.mms.tic.testframework.boot.Booter;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.GenerateReportsWorkerExecutor;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.shutdown.GenerateOtherOutputsWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.shutdown.GeneratefennecReportWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.shutdown.MetricsWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.shutdown.fennecEventsWorker;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.internal.MethodRelations;
import eu.tsystems.mms.tic.testframework.interop.CollectAssertionInfoArtefacts;
import eu.tsystems.mms.tic.testframework.monitor.JVMMonitor;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.fennecListener;
import eu.tsystems.mms.tic.testframework.report.external.junit.JUnitXMLReporter;
import eu.tsystems.mms.tic.testframework.report.model.ReportingData;
import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.utils.SourceUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.utils.fennecUtils;
import eu.tsystems.mms.tic.testframework.utils.reference.IntRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

import java.util.*;

/**
 * Created by piet on 08.12.16.
 */
public class GenerateReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateReport.class);

    static {
        // flush run contexts
        MethodRelations.flushAll();

        LOGGER.info("Generating Report...");
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

            /*
            workers
             */
            GenerateReportsWorkerExecutor workerExecutor = new GenerateReportsWorkerExecutor();

            workerExecutor.add(new fennecEventsWorker());

            fennecUtils.addWorkersToExecutor(fennecListener.GENERATE_REPORTS_WORKERS, workerExecutor);

            workerExecutor.add(new MetricsWorker());
            workerExecutor.add(new GeneratefennecReportWorker());
            workerExecutor.add(new GenerateOtherOutputsWorker());

            // run workers
            workerExecutor.run(xmlSuites, suites, outputDirectory, xmlReporter);

            /*
             * Shutdown local services and hooks
             */
            JVMMonitor.stop();
            Booter.shutdown();

            /*
             * Check failure corridor and set exit code and state
             */
            if (Flags.fennec_FAILURE_CORRIDOR_ACTIVE) {
                FailureCorridor.printStatusAndJumpOut();
            }
        }
    }

    /**
     * Stops logging of fennecCommands. Statistics are filled and reports are generated.
     */
    public static void generateReport() {
        ExecutionContextController.RUN_CONTEXT.endTime = new Date();
        JVMMonitor.label("Report");
        LOGGER.info("Generating Report...");
        pGenerateReport();
    }

    private static void pGenerateReport() {

        /*
         * Re-scan for fennecClassContexts
         */
        ExecutionContextController.RUN_CONTEXT.rescanForClassContextNames();

        /*
        get ALL ClassContexts
         */
        final List<ClassContext> allClassContexts = new ArrayList<>(ExecutionContextController.RUN_CONTEXT.getMethodStatsPerClass(true, false).keySet());

        /*
         * get sources for errors in pages
         */
        LOGGER.info("Catching script sources...");
        for (ClassContext classContext : allClassContexts) {
            List<MethodContext> methodContexts = classContext.copyOfMethodContexts();
            for (MethodContext methodContext : methodContexts) {
                if (methodContext.isFailed()) {
                    Throwable throwable = methodContext.getThrowable();
                    if (throwable != null) {
                        /*
                        find script source
                         */
                        // look for script source
                        String scriptSourceForThrowable = SourceUtils.findScriptSourceForThrowable(throwable);
                        if (scriptSourceForThrowable != null) {
                            methodContext.scriptSource = scriptSourceForThrowable;
                        }

                        /*
                        find execution object source
                         */
                        methodContext.executionObjectSource = CollectAssertionInfoArtefacts.getSourceFor(throwable);
                    }
                }
            }
        }

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
                    final String fingerprint = methodContext.errorFingerprint;
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
                    final String readableMessage = methodContext.getReadableErrorMessage();
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
        Store
         */
        ExecutionContextController.RUN_CONTEXT.exitPoints = exitPoints;
        ExecutionContextController.RUN_CONTEXT.failureAspects = failureAspects;

        /*
         * Create report
         */
        LOGGER.info("Create Report...");
        ReportingData reportingData = new ReportingData();
        reportingData.runContext = ExecutionContextController.RUN_CONTEXT;
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

        IntRef testMethodsCount = new IntRef();
        ExecutionContextController.RUN_CONTEXT.copyOfSuiteContexts().forEach(
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
                                                            testMethodsCount.increase();
                                                        }
                                                    );
                                        }
                                );
                            });
                }
        );

        System.out.println("");
        System.out.println("Number of tests: " + testMethodsCount.getI());
        System.out.println("");
    }

}
