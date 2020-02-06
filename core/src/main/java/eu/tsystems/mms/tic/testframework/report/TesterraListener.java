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
/*
 * Created on 05.04.2011
 *
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.report;

import eu.tsystems.mms.tic.testframework.boot.Booter;
import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import eu.tsystems.mms.tic.testframework.events.TesterraEventService;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.execution.testng.ListenerUtils;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.GenerateReportsWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorkerExecutor;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.Worker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.CQWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.HandleCollectedAssertsWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.MethodAnnotationCheckerWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.MethodContextUpdateWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.MethodFinishedWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.RemoveTestMethodIfRetryPassedWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.TestMethodFinishedWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.TesterraConnectorSyncEventsWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.TesterraEventsFinishWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.LoggingStartWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.MethodParametersWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.TestStartWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.TesterraEventsStartWorker;
import eu.tsystems.mms.tic.testframework.info.ReportInfo;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
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
import eu.tsystems.mms.tic.testframework.utils.FrameworkUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.testng.IConfigurable;
import org.testng.IConfigureCallBack;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener2;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Listener for JUnit and TestNg, collects test informations for testreport.
 *
 * @author mrgi, mibu, pele, sepr
 */
public class TesterraListener implements
    IInvokedMethodListener2,
    IReporter,
    IHookable,
    IConfigurable,
    IMethodInterceptor,
    ITestListener,
    ISuiteListener,
    Loggable
{
    /**
     * Global marker for positive test execution.
     */
    private static boolean allTestsPassed = true;

    /**
     * Skip test methods control.
     */
    private static boolean skipAllMethods = false;

    static final JUnitXMLReporter XML_REPORTER;

    private static final List<Class<? extends Worker>> BEFORE_INVOCATION_WORKERS = new LinkedList<>();
    private static final List<Class<? extends Worker>> AFTER_INVOCATION_WORKERS = new LinkedList<>();
    public static final List<Class<? extends Worker>> GENERATE_REPORTS_WORKERS = new LinkedList<>();

    private static List<IMethodInterceptor> METHOD_EXECUTION_FILTERS = new LinkedList<>();
    private static List<ISuiteListener> SUITE_LISTENERS = new LinkedList<>();

    /**
     * Instance counter for this reporter. *
     */
    private static int instances = 0;
    private static final Object LOCK = new Object();

    static {

        initTesterraLogger();

        /*
        Call Booter
         */
        Booter.bootOnce();

        /*
         * Add monitoring event listeners
         */
        JVMMonitor.label("Start");

        // loading flags, the latest
        if (Flags.MONITOR_MEMORY) {
            TesterraEventService.addListener(new JVMMonitor());
        }

        // start test for xml
        XML_REPORTER = new JUnitXMLReporter(true, Report.XML_DIRECTORY);
        XML_REPORTER.testSetStarting(new SimpleReportEntry("", "Test starting"));

        // start memory monitor
        JVMMonitor.start();
    }

    /**
     * Default constructor. *
     */
    public TesterraListener() {
        synchronized (LOCK) {
            // increment instance counter
            instances++;
        }
    }

    /**
     * Makes sure that the {@link TesterraLogger} is appended.
     * This is done here and not in {@link TesterraCommons#initializeLogging()} because
     * the required classes are only part of the core package.
     * @todo Change this with dependency inject of Testerra 2
     */
    public static void initTesterraLogger() {
        Logger root = Logger.getRootLogger();
        Appender testerraLogger = root.getAppender(TesterraLogger.class.getSimpleName());
        if (testerraLogger == null) {
            testerraLogger = new TesterraLogger();
            root.addAppender(testerraLogger);
            //root.setLevel(Level.INFO);
            /**
             * We set the default log level for the whole framework
             */
            Logger.getLogger(TesterraCommons.FRAMEWORK_PACKAGE).setLevel(Level.INFO);
            TesterraCommons.removeAllConsoleLoggers();
        }
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
     * This method INTERCEPTs THE XML_TEST not the methods
     * It is possible to filter methods an remove them completly from execution
     * Or you do a dependency analysis for execution filter
     *
     * @param list         All methods that should be run due to current XML-Test
     * @param iTestContext .
     * @return Alle mthods taht shuld be run
     */
    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> list, final ITestContext iTestContext) {

        if (Flags.EXECUTION_OMIT_IN_DEVELOPMENT) {
            ExecutionUtils.removeInDevelopmentMethods(list, iTestContext);
        }

        // apply method interceptors - no lambda, because list is not final
        for (final IMethodInterceptor methodInterceptor : METHOD_EXECUTION_FILTERS) {
            list = methodInterceptor.intercept(list, iTestContext);
        }

        return list;
    }


    public static void registerMethodExecutionFilter(IMethodInterceptor mi) {
        METHOD_EXECUTION_FILTERS.add(mi);
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
    public void beforeInvocation(
            IInvokedMethod method,
            ITestResult testResult,
            ITestContext context
    ) {
        try {
            pBeforeInvocation(method, testResult, context);
        } catch (Throwable t) {
            log().error("FATAL INTERNAL ERROR in beforeInvocation for " + method + ", " + testResult + ", " + context, t);
            ReportInfo.getDashboardWarning().addInfo(1, "FATAL INTERNAL ERROR during execution! Please analyze the build logs for this error!");
        }
    }

    /**
     * Override before invocation, to visualize threads.
     *
     * @param invokedMethod invoked method.
     * @param testResult    result of invoked method.
     * @param testContext
     */
    private void pBeforeInvocation(
            IInvokedMethod invokedMethod,
            ITestResult testResult,
            ITestContext testContext
    ) {
        /*
        check for listener duplicates
         */
        //        context.


        final String methodName = getMethodName(testResult);

        if (ListenerUtils.wasMethodInvokedBefore("beforeInvocationFor" + methodName, invokedMethod, testResult)) {
            return;
        }

        /*
         * store testresult, create method context
         */
        MethodContext methodContext = ExecutionContextController.setCurrentTestResult(testResult, testContext); // stores the actual testresult, auto-creates the method context
        ExecutionContextController.setCurrentMethodContext(methodContext);

        methodContext.steps().announceTestStep(TestStep.SETUP);

        final String infoText = "beforeInvocation: " + invokedMethod.getTestMethod().getTestClass().getName() + "." +
                methodName +
                " - " + Thread.currentThread().getName();

        log().trace(infoText);


        MethodWorkerExecutor workerExecutor = new MethodWorkerExecutor();

        workerExecutor.add(new TestStartWorker());
        workerExecutor.add(new MethodParametersWorker());
        workerExecutor.add(new TesterraEventsStartWorker());
        workerExecutor.add(new LoggingStartWorker());

        FrameworkUtils.addWorkersToExecutor(BEFORE_INVOCATION_WORKERS, workerExecutor);

        workerExecutor.run(testResult, methodName, methodContext, testContext, invokedMethod);

        // We don't close teardown steps, because we want to collect further actions there
        //step.close();
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
     * @param testContext   steps of test.
     */
    // CHECKSTYLE:OFF
    private void pAfterInvocation(
            IInvokedMethod invokedMethod,
            ITestResult testResult,
            ITestContext testContext
    ) {

        final String methodName;
        final String testClassName;
        if (invokedMethod != null) {
            methodName = invokedMethod.getTestMethod().getMethodName();
            testClassName = invokedMethod.getTestMethod().getTestClass().getName();
        } else {
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

        log().trace(infoText);

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
            } else if (testResult.getStatus() == ITestResult.SKIP) {
                ClassContext classContext = ExecutionContextController.getClassContextFromTestResult(testResult, testContext, invokedMethod);
                methodContext = classContext.safeAddSkipMethod(testResult, invokedMethod);
            } else {
                throw new TesterraSystemException("INTERNAL ERROR. Could not create methodContext for " + methodName + " with result: " + testResult);
            }
        }

        // set method endTime
        methodContext.endTime = new Date();

        /*
        add workers in workflow order
         */
        TestStep step = methodContext.steps().announceTestStep(TestStep.TEARDOWN);
        if (testResult.isSuccess()) {
            log().info(methodName + " PASSED");
        } else if (testResult.getStatus() == ITestResult.FAILURE) {
            log().error(methodName + " FAILED", testResult.getThrowable());
        }

        /*
        Workers #1
         */
        MethodWorkerExecutor workerExecutor = new MethodWorkerExecutor();

        workerExecutor.add(new HandleCollectedAssertsWorker());// !! must be invoked before MethodAnnotationCheckerWorker
        workerExecutor.add(new MethodAnnotationCheckerWorker()); // !! must be invoked before Container Update
        workerExecutor.add(new MethodContextUpdateWorker());
        workerExecutor.add(new RemoveTestMethodIfRetryPassedWorker());

        workerExecutor.run(testResult, methodName, methodContext, testContext, invokedMethod);

        /*
        Workers #2
         */
        workerExecutor = new MethodWorkerExecutor();

        workerExecutor.add(new TesterraEventsFinishWorker());
        workerExecutor.add(new TestMethodFinishedWorker());
        workerExecutor.add(new CQWorker());
        workerExecutor.add(new TesterraConnectorSyncEventsWorker());

        FrameworkUtils.addWorkersToExecutor(AFTER_INVOCATION_WORKERS, workerExecutor);

        // this is the last worker to be called
        workerExecutor.add(new MethodFinishedWorker());

        /*
        execute workers in correct order
         */
        workerExecutor.run(testResult, methodName, methodContext, testContext, invokedMethod);

        // We don't close teardown steps, because we want to collect further actions there
        //step.close();
    }

    @Override
    public void generateReport(
            List<XmlSuite> xmlSuites,
            List<ISuite> suites,
            String outputDirectory
    ) {
        GenerateReport.runOnce(xmlSuites, suites, outputDirectory, XML_REPORTER);
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
            ITestContext testContext = iTestResult.getTestContext();
            ExecutionContextController.setCurrentTestResult(iTestResult, testContext);
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

    public static void registerSuiteListener(final ISuiteListener listener) {
        SUITE_LISTENERS.add(listener);
    }

    /**
     * This method runs, when SUITE starts!
     *
     * @param iSuite
     */
    @Override
    public void onStart(ISuite iSuite) {

        for (final ISuiteListener suiteListener : SUITE_LISTENERS) {
            suiteListener.onStart(iSuite);
        }
    }

    /**
     * This method runs, when SUITE ends!
     *
     * @param iSuite
     */
    @Override
    public void onFinish(ISuite iSuite) {

        for (final ISuiteListener suiteListener : SUITE_LISTENERS) {
            suiteListener.onFinish(iSuite);
        }
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
