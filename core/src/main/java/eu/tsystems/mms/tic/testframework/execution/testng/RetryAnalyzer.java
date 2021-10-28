/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.execution.testng;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.annotations.Retry;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.exceptions.InheritedFailedException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.context.AbstractContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.report.utils.FailsAnnotationFilter;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.IRetryAnalyzer;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Testng Retry Analyzer.
 *
 * @author pele
 */
public class RetryAnalyzer implements IRetryAnalyzer, Loggable {

    private static final Queue<AdditionalRetryAnalyzer> ADDITIONAL_RETRY_ANALYZERS = new ConcurrentLinkedQueue();

    /**
     * Classes list.
     */
    private final List<Class> CLASSES_LIST = new ArrayList<>();

    /**
     * Messages list.
     */
    private final List<String> MESSAGES_LIST = new ArrayList<>();

    private static final Queue<MethodContext> RETRIED_METHODS = new ConcurrentLinkedQueue<>();

    /**
     * The retry counter.
     */
    private final Map<String, Integer> retryCounters = new ConcurrentHashMap<>();

    public RetryAnalyzer() {
        final String classes = PropertyManager.getProperty(TesterraProperties.FAILED_TESTS_IF_THROWABLE_CLASSES);
        if (classes != null) {
            String[] split = classes.split(",");
            for (String clazz : split) {
                try {
                    Class<?> aClass = Class.forName(clazz.trim());
                    CLASSES_LIST.add(aClass);
                } catch (ClassNotFoundException e) {
                    log().error("Error finding class", e);
                }
            }
        }

        final String messages = PropertyManager.getProperty(TesterraProperties.FAILED_TESTS_IF_THROWABLE_MESSAGES);
        if (messages != null) {
            String[] split = messages.split(",");
            for (String message : split) {
                MESSAGES_LIST.add(message.trim());
            }
        }
    }

    /**
     * The maximum number of retries. Loading every time to be able to set programmatically with System.setProperty().
     * A static final definition would be loaded to early (@ suite init).
     */
    private int getMaxRetries(ITestResult testResult) {
        if (testResult != null) {
            // check for annotation
            Method method = testResult.getMethod().getConstructorOrMethod().getMethod();
            if (method.isAnnotationPresent(Retry.class)) {
                Retry retry = method.getAnnotation(Retry.class);
                return retry.maxRetries();
            }
        }

        return PropertyManager.getIntProperty(TesterraProperties.FAILED_TESTS_MAX_RETRIES, 1);

    }

    @Override
    public boolean retry(final ITestResult testResult) {
        String retryReason = null;
        boolean retry = false;

        MethodContext methodContext = ExecutionContextController.getMethodContextFromTestResult(testResult);
        final String testMethodName = methodContext.getName();

        /*
        check retry counter
         */
        int retryCounter = getRetryCounter(testResult);
        final int maxRetries = getMaxRetries(testResult);
        final String retryMessageString = "(" + (retryCounter + 1) + "/" + (maxRetries + 1) + ")";

        if (retryCounter >= maxRetries) {
            raiseCounterAndChangeMethodContext(testResult, maxRetries);
            log().warn("Not retrying " + testMethodName + " because run limit (" + maxRetries + ")");
            return false;
        }

        /*
         * no retry when TesterraInheritedExceptions raise
         */
        final Throwable throwable1 = testResult.getThrowable();
        if (throwable1 != null && throwable1 instanceof InheritedFailedException) {
            return false;
        }

        /*
        no retry for tests with expected Fails annotation
         */
        if (testResult.getMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(Fails.class)) {

            // BUT ONLY: No retry for methods that hav a validFor
            final Fails failsAnnotation = testResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Fails.class);
            if (FailsAnnotationFilter.isFailsAnnotationValid(failsAnnotation)) {
                log().warn("Not retrying this method, because test is @Fails annotated.");
                return false;
            }
        }

        /*
        no retry for tests with fails annotaion in stacktrace
         */
        /*if (throwable1 != null && ExecutionUtils.getFailsAnnotationInStackTrace(throwable1.getStackTrace()) != null) {

            final Fails failsAnnotation = testResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Fails.class);
            if (FailsAnnotationFilter.isFailsAnnotationValid(failsAnnotation)) {
                LOGGER.warn("Not retrying this method, because a method in stacktrace is @Fails annotated.");
                return false;
            }
        }*/

        boolean containingFilteredThrowable = isTestResultContainingFilteredThrowable(testResult);
        if (containingFilteredThrowable) {
            /*
             * does the throwable filter match?
             */
            retryReason = "Found filtered throwable";
            retry = true;
        }

        /*
         * process retry
         */
        if (retry) {
            methodContext = raiseCounterAndChangeMethodContext(testResult, maxRetries);

            Method realMethod = testResult.getMethod().getConstructorOrMethod().getMethod();
            // explicitly set status if it is not set atm
            TestStatusController.setMethodStatus(methodContext, TestStatusController.Status.FAILED_RETRIED, realMethod);

            RETRIED_METHODS.add(methodContext);

            log().error(retryReason + ", send signal for retrying the test " + retryMessageString + "\n" + methodContext);

            testResult.getTestContext().getFailedTests().removeResult(testResult);
            testResult.getTestContext().getSkippedTests().removeResult(testResult);
            return true;
        }

        // Do not retry
        return false;
    }

    private MethodContext raiseCounterAndChangeMethodContext(final ITestResult testResult, final int maxRetries) {
        // raise counter
        int retryCounter = raiseRetryCounter(testResult);

        // reset method name
        MethodContext methodContext = ExecutionContextController.getMethodContextFromTestResult(testResult);
        if (maxRetries > 0) {

            final String retryLog = "(" + retryCounter + "/" + (maxRetries + 1) + ")";
            methodContext.infos.add(retryLog);
            methodContext.setRetryCounter(retryCounter);
        }

        return methodContext;
    }

    private int raiseRetryCounter(ITestResult iTestResult) {
        return getRetryCounter(iTestResult, true);
    }

    private int getRetryCounter(ITestResult iTestResult) {
        return getRetryCounter(iTestResult, false);
    }

    private int getRetryCounter(ITestResult testResult, boolean raise) {
        ITestNGMethod testNGMethod = testResult.getMethod();

        String id = testResult.getTestContext().getCurrentXmlTest().getName() + "/" +
                testNGMethod.getRealClass().getName() + "." +
                testNGMethod.getMethodName();

        Object[] parameters = testResult.getParameters();
        if (parameters != null && parameters.length > 0) {
            for (Object parameter : parameters) {
                if (parameter != null) {
                    id += "#" + parameter.toString();
                }
            }
        }

        if (!retryCounters.containsKey(id)) {
            retryCounters.put(id, 0);
        }

        // raise
        Integer counter = retryCounters.get(id);
        if (raise) {
            counter++;
            retryCounters.remove(id);
            retryCounters.put(id, counter);
        }
        return counter;
    }

    /**
     * checks if test results contain throwable results
     *
     * @param testResult .
     * @return .
     */
    private boolean isTestResultContainingFilteredThrowable(final ITestResult testResult) {
        if (testResult.getStatus() != ITestResult.FAILURE) {
            return false;
        }
        if (!testResult.isSuccess()) {
            Throwable throwable = testResult.getThrowable();

            if (throwable == null) {
                return false;
            }

            Throwable retryCause = checkThrowable(throwable);

            if (retryCause != null) {
//                MethodContext methodContext = ExecutionContextController.getMethodContextFromTestResult(testResult);
//                if (methodContext != null) {
//                    methodContext.addPriorityMessage("Retry Cause:\n" + retryCause.toString());
//                }
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a retryCause when a stacktrace match was found, otherwise null, which means, that we do not want a retry.
     *
     * @param throwable The throwable to check.
     * @return a cause or null
     */
    private Throwable checkThrowable(Throwable throwable) {
        Throwable retryCause = null;
        do {

            for (AdditionalRetryAnalyzer additionalRetryAnalyzer : ADDITIONAL_RETRY_ANALYZERS) {
                Optional<Throwable> optionalRetryCause = additionalRetryAnalyzer.analyzeThrowable(throwable);
                if (optionalRetryCause.isPresent()) {
                    retryCause = optionalRetryCause.get();
                    log().info("Retrying test because of: " + retryCause.getMessage());
                    break;
                }
            }

            for (Class aClass : CLASSES_LIST) {
                if (throwable.getClass() == aClass) {
                    log().info("Retrying test because of exception class: " + aClass.getName());
                    retryCause = throwable;
                    break;
                }
            }

            String tMessage = throwable.getMessage();
            if (tMessage != null) {
                for (String message : MESSAGES_LIST) {
                    if (tMessage.contains(message)) {
                        log().info("Retrying test because of exception message: " + message);
                        retryCause = throwable;
                        break;
                    }
                }
            }

            throwable = throwable.getCause();
        }
        while (retryCause == null && throwable != null);

        return retryCause;
    }

    public static Queue<MethodContext> getRetriedMethods() {
        return RETRIED_METHODS;
    }

    public static void registerAdditionalRetryAnalyzer(AdditionalRetryAnalyzer additionalRetryAnalyzer) {
        ADDITIONAL_RETRY_ANALYZERS.add(additionalRetryAnalyzer);
    }

    public static boolean hasMethodBeenRetried(MethodContext methodContext) {
        return RETRIED_METHODS.stream().anyMatch(m -> {

            if (m.getName().equals(methodContext.getName())) {
                if (m.getParameterValues().containsAll(methodContext.getParameterValues())) {
                    AbstractContext context = methodContext;
                    AbstractContext mContext = m;
                    while (context.getParentContext() != null) {
                        if (!context.getParentContext().equals(mContext.getParentContext())) {
                            return false;
                        }
                        context = context.getParentContext();
                        mContext = mContext.getParentContext();
                    }
                }

                return true;
            }

            return false;
        });
    }

}
