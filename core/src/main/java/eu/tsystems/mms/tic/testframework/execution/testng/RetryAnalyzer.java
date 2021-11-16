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

import eu.tsystems.mms.tic.testframework.annotations.NoRetry;
import eu.tsystems.mms.tic.testframework.annotations.Retry;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.events.TestStatusUpdateEvent;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.model.context.AbstractContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
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

    private static final Queue<AdditionalRetryAnalyzer> ADDITIONAL_RETRY_ANALYZERS = new ConcurrentLinkedQueue<>();

    /**
     * Classes list.
     */
    private static final List<Class> CLASSES_LIST = new ArrayList<>();

    /**
     * Messages list.
     */
    private static final List<String> MESSAGES_LIST = new ArrayList<>();

    private static final Queue<MethodContext> RETRIED_METHODS = new ConcurrentLinkedQueue<>();

    /**
     * The retry counter.
     */
    private static final Map<String, Integer> retryCounters = new ConcurrentHashMap<>();

    static {
        final String classes = PropertyManager.getProperty(TesterraProperties.FAILED_TESTS_IF_THROWABLE_CLASSES);
        if (classes != null) {
            String[] split = classes.split(",");
            for (String clazz : split) {
                try {
                    Class<?> aClass = Class.forName(clazz.trim());
                    CLASSES_LIST.add(aClass);
                } catch (ClassNotFoundException e) {
                    new RetryAnalyzer().log().error("Error finding class", e);
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

    @Override
    public boolean retry(final ITestResult testResult) {
        MethodContext methodContext = ExecutionContextController.getMethodContextFromTestResult(testResult);
        boolean retry = shouldRetry(testResult, methodContext);
        /**
         * Announce the test status change
         */
        if (methodContext.isStatusOneOf(Status.RETRIED, Status.RECOVERED, Status.FAILED)) {
            TesterraListener.getEventBus().post(new TestStatusUpdateEvent(methodContext));
        }
        return retry;
    }

    private boolean shouldRetry(ITestResult testResult, MethodContext methodContext) {
        Method method = testResult.getMethod().getConstructorOrMethod().getMethod();

        if (method.isAnnotationPresent(NoRetry.class)) {
            return false;
        }

        final String testMethodName = methodContext.getName();
        int retryCounter = getRetryCounter(testResult);

        int annotatedRetries = 0;
        Optional<Retry> optionalRetry = methodContext.getAnnotation(Retry.class);
        if (optionalRetry.isPresent()) {
            annotatedRetries = optionalRetry.get().maxRetries();
        }

        int defaultRetries = PropertyManager.getIntProperty(TesterraProperties.FAILED_TESTS_MAX_RETRIES, 1);
        int maxRetries = Math.max(defaultRetries, annotatedRetries);

        final String retryMessageString = "(" + (retryCounter + 1) + "/" + (maxRetries + 1) + ")";

        if (retryCounter >= maxRetries) {
            removeFromRetryCache(methodContext);
            log().warn("Not retrying " + testMethodName + " because run limit (" + maxRetries + ")");
            return false;
        }

        if (annotatedRetries > 0 || (isTestResultContainingFilteredThrowable(testResult) && defaultRetries > 0)) {
            methodHasBeenRetried(methodContext);
            RETRIED_METHODS.add(methodContext);
            log().info("Send signal for retrying the test " + retryMessageString + ": " + testMethodName);
            return true;
        }

        // Do not retry
        return false;
    }

    private static void raiseCounterAndChangeMethodContext(MethodContext methodContext) {
        methodContext.getTestNgResult().ifPresent(iTestResult -> {
            int retryCounter = raiseRetryCounter(iTestResult);
            methodContext.setRetryCounter(retryCounter);
        });
    }

    private static int raiseRetryCounter(ITestResult iTestResult) {
        return getRetryCounter(iTestResult, true);
    }

    private int getRetryCounter(ITestResult iTestResult) {
        return getRetryCounter(iTestResult, false);
    }

    private static int getRetryCounter(ITestResult testResult, boolean raise) {
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
            retryCounters.replace(id, counter);
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
        Throwable throwable = testResult.getThrowable();

        if (throwable != null) {
            Throwable retryCause = checkThrowable(throwable);
            return retryCause != null;
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
                    log().info(String.format("Found retry cause: \"%s\"", retryCause.getMessage()));
                    break;
                }
            }

            for (Class aClass : CLASSES_LIST) {
                if (throwable.getClass() == aClass) {
                    log().info(String.format("Found retry cause: exception class=\"%s\"", aClass.getName()));
                    retryCause = throwable;
                    break;
                }
            }

            String tMessage = throwable.getMessage();
            if (tMessage != null) {
                for (String message : MESSAGES_LIST) {
                    if (tMessage.contains(message)) {
                        log().info(String.format("Found retry cause: exception message=\"%s\"", message));
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

    @Deprecated
    public static Queue<MethodContext> getRetriedMethods() {
        return RETRIED_METHODS;
    }

    public static void registerAdditionalRetryAnalyzer(AdditionalRetryAnalyzer additionalRetryAnalyzer) {
        ADDITIONAL_RETRY_ANALYZERS.add(additionalRetryAnalyzer);
    }

    private static Stream<MethodContext> readRetriedMethodsForMethod(MethodContext methodContext) {
        return RETRIED_METHODS.stream().filter(m -> {
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

    /**
     * Tells the RetryAnalyzer that a method has been passed
     * @param methodContext
     */
    public static void methodHasBeenPassed(MethodContext methodContext) {
        RetryAnalyzer.readRetriedMethodsForMethod(methodContext).findFirst().ifPresent(retriedMethod -> {
            methodContext.setStatus(Status.RECOVERED);
            raiseCounterAndChangeMethodContext(methodContext);

            methodContext.addDependsOnMethod(retriedMethod);
            retriedMethod.addRelatedMethodContext(methodContext);
            RETRIED_METHODS.remove(retriedMethod);
        });
    }

    private static void methodHasBeenRetried(MethodContext methodContext) {
        methodContext.setStatus(Status.RETRIED);
        raiseCounterAndChangeMethodContext(methodContext);
        removeFromRetryCache(methodContext);
    }

    private static void removeFromRetryCache(MethodContext methodContext) {
        readRetriedMethodsForMethod(methodContext).forEach(retriedMethod -> {
            retriedMethod.addRelatedMethodContext(methodContext);
            RETRIED_METHODS.remove(retriedMethod);
        });
    }
}
