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
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.events.AbstractMethodEvent;
import eu.tsystems.mms.tic.testframework.events.ExecutionFinishEvent;
import eu.tsystems.mms.tic.testframework.events.InterceptMethodsEvent;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.events.MethodStartEvent;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.execution.testng.ListenerUtils;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.HandleCollectedAssertsWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.MethodAnnotationCheckerWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.MethodContextUpdateWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.MethodEndWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.finish.RemoveTestMethodIfRetryPassedWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.MethodParametersWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.MethodStartWorker;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.OmitInDevelopmentMethodInterceptor;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.start.SortMethodsByPriorityMethodInterceptor;
import eu.tsystems.mms.tic.testframework.info.ReportInfo;
import eu.tsystems.mms.tic.testframework.internal.BuildInformation;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.monitor.JVMMonitor;
import eu.tsystems.mms.tic.testframework.report.hooks.ConfigMethodHook;
import eu.tsystems.mms.tic.testframework.report.hooks.TestMethodHook;
import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.report.utils.DefaultTestNGContextGenerator;
import java.util.List;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
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
     * Default package namespace for project tests
     */
    public final static String DEFAULT_PACKAGE = "eu.tsystems.mms.tic";
    public static final String PROJECT_PACKAGE = PropertyManager.getProperty(TesterraProperties.PROJECT_PACKAGE, DEFAULT_PACKAGE);

    /**
     * Skip test methods control.
     */
    private static boolean skipAllMethods = false;

    private static final EventBus eventBus;
    /**
     * Instance counter for this reporter. *
     */
    private static int instances = 0;
    private static final Object LOCK = new Object();
    private static final LoggerContext loggerContext;
    private static final BuildInformation buildInformation;
    private static final Report report;
    private static DefaultTestNGContextGenerator contextGenerator;

    static {
        DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
        loggerContext = Configurator.initialize(defaultConfiguration);
        buildInformation = new BuildInformation();
        eventBus = new EventBus();
        report = new DefaultReport();
        contextGenerator = new DefaultTestNGContextGenerator();

        /*
         * Add monitoring event listeners
         */
        JVMMonitor.label("Start");

        // start memory monitor
        JVMMonitor.start();

        eventBus.register(new MethodStartWorker());
        eventBus.register(new MethodParametersWorker());
        eventBus.register(new HandleCollectedAssertsWorker());// !! must be invoked before MethodAnnotationCheckerWorker
        eventBus.register(new MethodAnnotationCheckerWorker()); // !! must be invoked before Container Update
        eventBus.register(new MethodContextUpdateWorker());
        eventBus.register(new RemoveTestMethodIfRetryPassedWorker());

        // this is the last worker to be called
        eventBus.register(new MethodEndWorker());

        eventBus.register(new OmitInDevelopmentMethodInterceptor());
        eventBus.register(new SortMethodsByPriorityMethodInterceptor());

        eventBus.register(new ExecutionEndListener());

        /*
        Call Booter
         */
        Booter.bootOnce();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            /*
             * Shutdown local services and hooks
             */
            JVMMonitor.stop();
            Booter.shutdown();
        }));
    }

    public static EventBus getEventBus() {
        return eventBus;
    }

    public static LoggerContext getLoggerContext() {
        return loggerContext;
    }

    public static BuildInformation getBuildInformation() {
        return buildInformation;
    }

    public static Report getReport() {
        return report;
    }

    public static DefaultTestNGContextGenerator getContextGenerator() {
        if (contextGenerator == null) {
            contextGenerator = new DefaultTestNGContextGenerator();
        }
        return contextGenerator;
    }

    public static void setContextGenerator(DefaultTestNGContextGenerator newContextGenerator) {
        contextGenerator = newContextGenerator;
    }

    /**
     * Default constructor. *
     */
    public TesterraListener() {
        synchronized (LOCK) {
            // increment instance counter
            instances++;

            if (instances==1) {
                // The finalize listener has to be registered AFTER all modules ONCE
                eventBus.register(new FinalizeListener());
            }
        }
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
     * @return All methods that should be executed
     */
    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> list, final ITestContext iTestContext) {
        InterceptMethodsEvent event = new InterceptMethodsEvent()
                .setMethodInstances(list)
                .setTestContext(iTestContext);
        eventBus.post(event);
        return event.getMethodInstances();
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
        final String methodName = getMethodName(testResult);

        if (ListenerUtils.wasMethodInvokedBefore("beforeInvocationFor" + methodName, invokedMethod, testResult)) {
            return;
        }

        /*
         * store testresult, create method context
         */
        MethodContext methodContext = ExecutionContextController.setCurrentTestResult(testResult, testContext); // stores the actual testresult, auto-creates the method context
        ExecutionContextController.setCurrentMethodContext(methodContext);

        methodContext.getTestStep(TestStep.SETUP);

        final String infoText = "beforeInvocation: " + invokedMethod.getTestMethod().getTestClass().getName() + "." +
                methodName +
                " - " + Thread.currentThread().getName();

        log().trace(infoText);

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
    public void afterInvocation(
            IInvokedMethod method,
            ITestResult testResult,
            ITestContext context
    ) {
        pAfterInvocation(method, testResult, context);
    }

    private static String getMethodName(ITestResult testResult) {
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

            if (
                    testResult.getStatus() == ITestResult.CREATED
                    || testResult.getStatus() == ITestResult.SKIP
            ) {
                /*
                 * TestNG bug or whatever ?!?!
                 */
                ClassContext classContext = ExecutionContextController.getClassContextFromTestResult(testResult, testContext, invokedMethod);
                methodContext = classContext.safeAddSkipMethod(testResult, invokedMethod);
            } else {
                throw new SystemException("INTERNAL ERROR. Could not create methodContext for " + methodName + " with result: " + testResult);
            }
        }

        AbstractMethodEvent event = new MethodEndEvent()
                .setTestResult(testResult)
                .setInvokedMethod(invokedMethod)
                .setMethodName(methodName)
                .setTestContext(testContext)
                .setMethodContext(methodContext);

        eventBus.post(event);

        methodContext.getTestStep(TestStep.TEARDOWN);
    }

    @Override
    public void generateReport(
            List<XmlSuite> xmlSuites,
            List<ISuite> suites,
            String outputDirectory
    ) {
        ExecutionFinishEvent event = new ExecutionFinishEvent()
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
            methodContextFromTestResult.setParameterValues(parameterTypes);
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
