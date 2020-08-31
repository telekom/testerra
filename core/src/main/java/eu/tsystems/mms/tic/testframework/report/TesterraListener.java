/*
 * Testerra
 *
 * (C) 2020,  Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.report;

import com.google.common.eventbus.EventBus;
import eu.tsystems.mms.tic.testframework.boot.Booter;
import eu.tsystems.mms.tic.testframework.events.ExecutionEndEvent;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.events.AbstractMethodEvent;
import eu.tsystems.mms.tic.testframework.events.InterceptMethodsEvent;
import eu.tsystems.mms.tic.testframework.events.MethodStartEvent;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.execution.testng.ListenerUtils;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.HandleCollectedAssertsWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.MethodAnnotationCheckerWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.MethodContextUpdateWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.MethodEndWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.RemoveTestMethodIfRetryPassedWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.MethodParametersWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.OmitInDevelopmentMethodInterceptor;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.MethodStartWorker;
import eu.tsystems.mms.tic.testframework.info.ReportInfo;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.internal.MethodRelations;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.monitor.JVMMonitor;
import eu.tsystems.mms.tic.testframework.report.hooks.ConfigMethodHook;
import eu.tsystems.mms.tic.testframework.report.hooks.TestMethodHook;
import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.testng.IConfigurable;
import org.testng.IConfigureCallBack;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
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

/**
 * Listener for JUnit and TestNg, collects test informations for testreport.
 *
 * @author mrgi, mibu, pele, sepr
 */
public class TesterraListener implements
        IInvokedMethodListener,
        IReporter,
        IHookable,
        IConfigurable,
        IMethodInterceptor,
        ITestListener,
        ISuiteListener,
        Loggable
{
    /**
     * Skip test methods control.
     */
    private static boolean skipAllMethods = false;

    private static EventBus eventBus = new EventBus();

    /**
     * Instance counter for this reporter. *
     */
    private static int instances = 0;
    private static final Object LOCK = new Object();

    static {
        /*
        Call Booter
         */
        Booter.bootOnce();

        /*
         * Add monitoring event listeners
         */
        JVMMonitor.label("Start");

        // start memory monitor
        JVMMonitor.start();
    }

    public static EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Default constructor. *
     */
    public TesterraListener() {
        synchronized (LOCK) {
            // increment instance counter
            instances++;
        }

        eventBus.register(new MethodStartWorker());
        eventBus.register(new MethodParametersWorker());
        eventBus.register(new HandleCollectedAssertsWorker());// !! must be invoked before MethodAnnotationCheckerWorker
        eventBus.register(new MethodAnnotationCheckerWorker()); // !! must be invoked before Container Update
        eventBus.register(new MethodContextUpdateWorker());
        eventBus.register(new RemoveTestMethodIfRetryPassedWorker());

        // this is the last worker to be called
        eventBus.register(new MethodEndWorker());

        eventBus.register(new OmitInDevelopmentMethodInterceptor());
    }

    /**
     * Thread safe long representing the startTime of the actual thread.
     */
    private static final ThreadLocal<Long> THREADSTART = new ThreadLocal<Long>();

    /**
     * Set thread start time.
     */
    private static void startMethodTimer() {
        THREADSTART.set(System.currentTimeMillis());
    }

    /**
     * Clean threadtimes.
     */
    private static void cleanMethodTimer() {
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
     * It is possible to filter methods an remove them completely from execution
     * Or you do a dependency analysis for execution filter
     *
     * @param list         All methods that should be run due to current XML-Test
     * @param iTestContext .
     * @return Alle mthods taht shuld be run
     */
    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> list, final ITestContext iTestContext) {
        InterceptMethodsEvent event = new InterceptMethodsEvent()
                .setMethodInstanceList(list)
                .setTestContext(iTestContext);
        eventBus.post(event);
        return event.getMethodInstanceList();
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
        MethodContext methodContext = ExecutionContextController
                .setCurrentTestResult(testResult, testContext); // stores the actual testresult, auto-creates the method context
        ExecutionContextController.setCurrentMethodContext(methodContext);

        methodContext.steps().announceTestStep(TestStep.SETUP);

        final String infoText = "beforeInvocation: " + invokedMethod.getTestMethod().getTestClass().getName() + "." +
                methodName +
                " - " + Thread.currentThread().getName();

        log().trace(infoText);

        startMethodTimer();

        AbstractMethodEvent event = new MethodStartEvent()
                .setTestResult(testResult)
                .setInvokedMethod(invokedMethod)
                .setMethodName(methodName)
                .setTestContext(testContext)
                .setMethodContext(methodContext);

        eventBus.post(event);

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
        methodContext.steps().announceTestStep(TestStep.TEARDOWN);

        cleanMethodTimer();

        AbstractMethodEvent event = new MethodEndEvent()
                .setTestResult(testResult)
                .setInvokedMethod(invokedMethod)
                .setMethodName(methodName)
                .setTestContext(testContext)
                .setMethodContext(methodContext);

        eventBus.post(event);
    }

    @Override
    public void generateReport(
            List<XmlSuite> xmlSuites,
            List<ISuite> suites,
            String outputDirectory
    ) {
        MethodRelations.flushAll();

        // set the testRunFinished flag
        ExecutionContextController.testRunFinished = true;

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

        ExecutionEndEvent event = new ExecutionEndEvent()
                .setSuites(suites)
                .setXmlSuites(xmlSuites);

        eventBus.post(event);
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

        final ITestContext testContext = iTestResult.getTestContext();

        /*
        Find methods that are ignored due to failed dependency
         */
        final Throwable throwable = iTestResult.getThrowable();
        if (throwable != null && throwable.toString().contains(SKIP_FAILED_DEPENDENCY_MSG)) {
            ExecutionContextController.setCurrentTestResult(iTestResult, testContext);
            pAfterInvocation(null, iTestResult, testContext);
        }

        /*
         add missing method parameters for skipped test methods
         */
        final Class<?>[] parameterTypes = iTestResult.getMethod().getConstructorOrMethod().getMethod().getParameterTypes();
        if (parameterTypes.length > 0) {
            final MethodContext methodContextFromTestResult = ExecutionContextController.getMethodContextFromTestResult(iTestResult, testContext);
            methodContextFromTestResult.parameters.addAll(Arrays.asList(parameterTypes));
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

    /**
     * This method runs, when SUITE starts!
     *
     * @param iSuite
     */
    @Override
    public void onStart(ISuite iSuite) {
        eventBus.post(iSuite);
    }

    /**
     * This method runs, when SUITE ends!
     *
     * @param iSuite
     */
    @Override
    public void onFinish(ISuite iSuite) {
        eventBus.post(iSuite);
    }

    public static boolean isActive() {
        return instances > 0;
    }
}
