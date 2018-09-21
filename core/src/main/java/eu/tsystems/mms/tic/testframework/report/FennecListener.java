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
/*
 * Created on 05.04.2011
 *
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.report;

import eu.tsystems.mms.tic.testframework.annotations.NoRetry;
import eu.tsystems.mms.tic.testframework.boot.Booter;
import eu.tsystems.mms.tic.testframework.events.FennecEventService;
import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;
import eu.tsystems.mms.tic.testframework.execution.testng.ListenerUtils;
import eu.tsystems.mms.tic.testframework.execution.testng.RetryAnalyzer;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.GenerateReportsWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorkerExecutor;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.Worker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.*;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.FennecEventsStartWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.LoggingStartWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.MethodParametersWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.TestStartWorker;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.monitor.JVMMonitor;
import eu.tsystems.mms.tic.testframework.report.external.junit.JUnitXMLReporter;
import eu.tsystems.mms.tic.testframework.report.external.junit.SimpleReportEntry;
import eu.tsystems.mms.tic.testframework.report.hooks.ConfigMethodHook;
import eu.tsystems.mms.tic.testframework.report.hooks.TestMethodHook;
import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.report.Report;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionUtils;
import eu.tsystems.mms.tic.testframework.report.utils.GenerateReport;
import eu.tsystems.mms.tic.testframework.utils.FennecUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;
import org.testng.annotations.ITestAnnotation;
import org.testng.xml.XmlSuite;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Listener for JUnit and TestNg, collects test informations for testreport.
 *
 * @author mrgi, mibu, pele, sepr
 */
public class FennecListener implements IInvokedMethodListener2, IReporter, IAnnotationTransformer,
        IHookable, IConfigurable, IMethodInterceptor, ITestListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FennecListener.class);

    /**
     * Global marker for positive test execution.
     */
    private static boolean allTestsPassed = true;

    /**
     * Skip test methods control.
     */
    private static boolean skipAllMethods = false;

    static final JUnitXMLReporter XML_REPORTER = new JUnitXMLReporter(true, Report.XML_DIRECTORY);

    private static final List<Class<? extends Worker>> BEFORE_INVOCATION_WORKERS = new LinkedList<>();
    private static final List<Class<? extends Worker>> AFTER_INVOCATION_WORKERS = new LinkedList<>();
    public static final List<Class<? extends Worker>> GENERATE_REPORTS_WORKERS = new LinkedList<>();

    /**
     * Instance counter for this reporter. *
     */
    private static int instances = 0;

    static {
        /*
        Call Booter
         */
        Booter.bootOnce();

        /*
         * Add monitoring event listeners
         */
        JVMMonitor.label("Start");

        if (Flags.MONITOR_MEMORY) {
            FennecEventService.addListener(new JVMMonitor());
        }

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        LOGGER.info("Context ClassLoader for FennecListener: " + contextClassLoader);

        // start test for xml
        XML_REPORTER.testSetStarting(new SimpleReportEntry("", "Test starting"));

        // start memory monitor
        JVMMonitor.start();
    }

    /**
     * Default constructor. *
     */
    public FennecListener() {
    }

    /**
     * Determines whether there was an error in the current test. This is necessary to not synchronize a test result
     * twice if the test was failed, because first 'testFailure' is called and afterwards 'testFinished' and there is no
     * way in 'testFinished' to determine whether the finished test was successful or not.
     */
    private static ThreadLocal<Boolean> failureInCurrentTest = new ThreadLocal<Boolean>();

    /*
     * Thread Visualizer
     */

    /**
     * Thread safe long representing the startTime of the actual thread.
     */
    private static final ThreadLocal<Long> THREADSTART = new ThreadLocal<Long>();

    /**
     * Set thread start time.
     */
    public static void startMethodTimer() {
        THREADSTART.set(System.currentTimeMillis());
    }

    /**
     * Clean threadtimes.
     */
    public static void cleanMethodTimer() {
        THREADSTART.remove();
    }

    /**
     * Threadsafe Getter.
     *
     * @return threadStartTimes value.
     */
    public static Long getThreadStartTime() {
        return THREADSTART.get();
    }

    /*
     * TESTNG
     */

    /**
     * This method INTERCEPT THE XML_TEST not the methods
     * It is possible to filter methods an remove them completly from execution
     * Or you do a dependency analysis for execution filter
     *
     * @param list         All methods that should be run due to current XML-Test
     * @param iTestContext .
     * @return Alle mthods taht shuld be run
     */
    @Override
    public List<IMethodInstance> intercept(final List<IMethodInstance> list, final ITestContext iTestContext) {
        if (Flags.EXECUTION_OMIT_IN_DEVELOPMENT) {
            ExecutionUtils.removeInDevelopmentMethods(list, iTestContext);
        }

        return list;
    }

    /**
     * @param method     .
     * @param testResult .
     */
    @Override
    public void beforeInvocation(final IInvokedMethod method, final ITestResult testResult) {
        // we don't need that-
    }

    /**
     * @param method     .
     * @param method     .
     * @param testResult .
     */
    @Override
    public void afterInvocation(final IInvokedMethod method, final ITestResult testResult) {
        // we don't need that-
    }

    /**
     * Override before invocation, to visualize threads.
     *
     * @param method     invoked method.
     * @param testResult result of invoked method.
     * @param context    steps of test.
     */
    @Override
    public void beforeInvocation(final IInvokedMethod method, final ITestResult testResult,
                                 final ITestContext context) {
        pBeforeInvocation(method, testResult, context);
    }

    /**
     * Override before invocation, to visualize threads.
     *
     * @param invokedMethod invoked method.
     * @param testResult    result of invoked method.
     * @param context
     */
    private void pBeforeInvocation(final IInvokedMethod invokedMethod, final ITestResult testResult, ITestContext context) {
        /*
        check for listener duplicates
         */
//        context.


        final String methodName = getMethodName(testResult);

        if (ListenerUtils.wasMethodInvokedBefore("beforeInvocationFor" + methodName, invokedMethod, testResult)) {
            return;
        }

        TestStep.begin("fennec Setup");

        /*
         * store testresult, create method context
         */
        final MethodContext methodContext = ExecutionContextController.setCurrentTestResult(testResult); // stores the actual testresult, auto-creates the method context
        ExecutionContextController.setCurrentMethodContext(methodContext);

        final String infoText = "beforeInvocation: " + invokedMethod.getTestMethod().getTestClass().getName() + "." +
                methodName +
                " - " + Thread.currentThread().getName();

        LOGGER.trace(infoText);


        MethodWorkerExecutor workerExecutor = new MethodWorkerExecutor();

        workerExecutor.add(new TestStartWorker());
        workerExecutor.add(new MethodParametersWorker());
        workerExecutor.add(new FennecEventsStartWorker());
        workerExecutor.add(new LoggingStartWorker());

        FennecUtils.addWorkersToExecutor(BEFORE_INVOCATION_WORKERS, workerExecutor);

        workerExecutor.run(testResult, methodName, methodContext, context, invokedMethod);

        TestStep.end();
    }

    /**
     * Override after invocation, to visualize threads and finish reporting.
     *
     * @param method     invoked method.
     * @param testResult result of invoked method.
     * @param context    steps of test.
     */
    @Override
    public void afterInvocation(final IInvokedMethod method, final ITestResult testResult,
                                             final ITestContext context) {
        pAfterInvocation(method, testResult, context);
    }

    private static final String getMethodName(ITestResult testResult) {
        ITestNGMethod testMethod = testResult.getMethod();
        String methodName = testMethod.getMethodName();
        Object[] parameters = testResult.getParameters();
        if (parameters != null) {
            methodName += "(";
            for (Object parameter : parameters) {
                methodName += parameter + ", ";
            }
            if (parameters.length > 0) {
                methodName = methodName.substring(0, methodName.length() - 2);
            }
            methodName += ")";
        }
        return methodName;
    }

    /**
     * Override after invocation, to visualize threads and finish reporting.
     *
     * @param invokedMethod invoked method.
     * @param testResult    result of invoked method.
     * @param testContext       steps of test.
     */
    // CHECKSTYLE:OFF
    private void pAfterInvocation(final IInvokedMethod invokedMethod, final ITestResult testResult,
                                  final ITestContext testContext) {

        final String methodName;
        final String testClassName;
        if (invokedMethod != null) {
            methodName = invokedMethod.getTestMethod().getMethodName();
            testClassName = invokedMethod.getTestMethod().getTestClass().getName();
        }
        else {
            methodName = testResult.getMethod().getConstructorOrMethod().getName();
            testClassName = testResult.getTestClass().getName();
        }

        // CHECKSTYLE:ON
        if (ListenerUtils.wasMethodInvokedBefore("afterInvocation", testClassName, methodName, testResult, testContext)) {
            return;
        }

        /*
        Log
         */
        final String infoText = "afterInvocation: " + testClassName + "." + methodName + " - " + Thread.currentThread().getName();

        LOGGER.trace(infoText);

        /*
         * Get test method container
         */
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        if (methodContext == null) {

            if (testResult.getStatus() == ITestResult.CREATED) {
                /*
                 * TestNG bug or whatever ?!?!
                 */
                ClassContext classContext = ExecutionContextController.getClassContextFromTestResult(testResult, testContext, invokedMethod);
                methodContext = classContext.safeAddSkipMethod(testResult, invokedMethod);
            }
            else if (testResult.getStatus() == ITestResult.SKIP) {
                ClassContext classContext = ExecutionContextController.getClassContextFromTestResult(testResult, testContext, invokedMethod);
                methodContext = classContext.safeAddSkipMethod(testResult, invokedMethod);
            }
            else {
                throw new FennecSystemException("INTERNAL ERROR. Could not create methodContext for " + methodName + " with result: " + testResult);
            }
        }

        // set method endTime
        methodContext.endTime = new Date();

        /*
        add workers in workflow order
         */
        TestStep.begin("fennec TearDown");

        /*
        Workers #1
         */
        MethodWorkerExecutor workerExecutor = new MethodWorkerExecutor();

        workerExecutor.add(new HandleCollectedAssertsWorker());// !! must be invoked before MethodAnnotationCheckerWorker
        workerExecutor.add(new MethodAnnotationCheckerWorker()); // !! must be invoked before Container Update
        workerExecutor.add(new MethodContextUpdateWorker());

        workerExecutor.run(testResult, methodName, methodContext, testContext, invokedMethod);

        /*
        Workers #2
         */
        workerExecutor = new MethodWorkerExecutor();

        workerExecutor.add(new FennecEventsFinishWorker());
        workerExecutor.add(new TestMethodFinishedWorker());
        workerExecutor.add(new CQWorker());
        workerExecutor.add(new FennecConnectorSyncEventsWorker());

        FennecUtils.addWorkersToExecutor(AFTER_INVOCATION_WORKERS, workerExecutor);

        // this is the last worker to be called
        workerExecutor.add(new MethodFinishedWorker());

        /*
        execute workers in correct order
         */
        workerExecutor.run(testResult, methodName, methodContext, testContext, invokedMethod);

        TestStep.end();
    }

    @Override
    public void generateReport(final List<XmlSuite> xmlSuites, final List<ISuite> suites,
                               final String outputDirectory) {
        GenerateReport.runOnce(xmlSuites, suites, outputDirectory, XML_REPORTER);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void transform(ITestAnnotation iTestAnnotation, Class aClass, Constructor constructor, Method method) {
        /*
         * Checks the test method annotation for a retry analyzer. If no one is specified, it uses the fennec retry
         * analyzer.
         */
        if (method != null) {
            final IRetryAnalyzer retryAnalyzer = iTestAnnotation.getRetryAnalyzer();
            if (retryAnalyzer == null) {

                if (method.isAnnotationPresent(NoRetry.class)) {
                    LOGGER.debug("Not adding fennec RetryAnalyzer for @NoRetry " + method.getName());
                } else {
                    iTestAnnotation.setRetryAnalyzer(RetryAnalyzer.class);
                    LOGGER.info("Adding fennec RetryAnalyzer for " + method.getName());
                }
            } else {
                LOGGER.info("Using a non-fennec retry analyzer: " + retryAnalyzer + " on " + aClass.getName() + "."
                        + method.getName());
            }
        }
    }

    @Override
    public void run(IHookCallBack callBack, ITestResult testResult) {
        TestMethodHook.runHook(callBack, testResult);
    }

    @Override
    public void run(IConfigureCallBack callBack, ITestResult testResult) {
        ConfigMethodHook.runHook(callBack, testResult);
    }

    public static boolean isSkipAllMethods() {
        return skipAllMethods;
    }

    public static void skipAllTests() {
        skipAllMethods = true;
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {

    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {

    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {

    }

    private static final String SKIP_FAILED_DEPENDENCY_MSG = "depends on";

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        /*
        Find methods that are ignored due to failed dependency
         */
        Throwable throwable = iTestResult.getThrowable();
        if (throwable != null && throwable.toString().contains(SKIP_FAILED_DEPENDENCY_MSG)) {
            ExecutionContextController.setCurrentTestResult(iTestResult);
            ITestContext testContext = iTestResult.getTestContext();
            pAfterInvocation(null, iTestResult, testContext);
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }

    @Override
    public void onStart(ITestContext iTestContext) {

    }

    @Override
    public void onFinish(ITestContext iTestContext) {

    }

    public static boolean isActive() {
        return instances > 0;
    }

    public static void registerBeforeMethodWorker(Class<? extends MethodWorker> worker) {
        BEFORE_INVOCATION_WORKERS.add(worker);
    }

    public static void registerAfterMethodWorker(Class<? extends MethodWorker> worker) {
        AFTER_INVOCATION_WORKERS.add(worker);
    }

    public static void registerGenerateReportsWorker(Class<? extends GenerateReportsWorker> worker) {
        GENERATE_REPORTS_WORKERS.add(worker);
    }

}
