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
 package eu.tsystems.mms.tic.testframework.report.utils;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.annotations.SupportMethod;
import eu.tsystems.mms.tic.testframework.report.model.context.Cause;
import eu.tsystems.mms.tic.testframework.report.context.StackTrace;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

/**
 * ExecutionUtils
 * <p>
 * Date: 24.11.2016
 * Time: 07:19
 *
 * @author Eric Kubenka
 */
public final class ExecutionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionUtils.class);
    private static final String FAILS_CLASS_PACKAGE = "eu.tsystems.mms.tic";

    /**
     * Holds methods that are preconditions for other
     * Methods who other methods depends on
     */
    private static List<String> dependsOnMethods = Collections.synchronizedList(new ArrayList<String>());

    /**
     * Determines if there is a @Fails annotated method in StackTrace
     *
     * @param stackTrace {@link StackTraceElement}
     * @return Fails
     */
    public static Fails getFailsAnnotationInStackTrace(StackTraceElement[] stackTrace) {
        final ClassPool pool = ClassPool.getDefault();
        pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));

        for (final StackTraceElement stackTraceElement : stackTrace) {
            int lineNumberFromStackTrace = stackTraceElement.getLineNumber();

            try {
                if (stackTraceElement.getClassName().startsWith(FAILS_CLASS_PACKAGE)) {

                    final CtClass ctClass = pool.get(stackTraceElement.getClassName());
                    final CtMethod[] declaredMethods = ctClass.getDeclaredMethods();


                    Map<Integer, CtMethod> ctMethods = new TreeMap<>();
                    for (final CtMethod declaredMethod : declaredMethods) {
                        if (declaredMethod.getName().equals(stackTraceElement.getMethodName())) {
                            // methodname alleine reicht nicht
                            // !!! line numbers don't always match
                            final int lineNumberFromDeclaredMethod = declaredMethod.getMethodInfo().getLineNumber(0);

                            if (declaredMethod.hasAnnotation(Fails.class)) {
                                if (lineNumberFromDeclaredMethod == lineNumberFromStackTrace) {
                                    // we are happy when this matched
                                    return (Fails) declaredMethod.getAnnotation(Fails.class);
                                }

                                // but when it does not match...
                                // add the method to the sorted map of linenumbers-to-mathod
                                ctMethods.put(lineNumberFromDeclaredMethod, declaredMethod);
                            }
                        }
                    }

                    // check for which method is the correct one
                    int size = ctMethods.size();
                    Integer[] keys = ctMethods.keySet().toArray(new Integer[0]);
                    for (int i = 0; i < size; i++) {
                        int key = keys[i];
                        int next = i + 1;
                        boolean itsTheOne = false;

                        if (next >= size) {
                            itsTheOne = true;
                        } else {
                            int nextKey = keys[next];
                            if (lineNumberFromStackTrace >= key && lineNumberFromStackTrace < nextKey) {
                                itsTheOne = true;
                            }
                        }

                        if (itsTheOne) {
                            // its the last one OR the matching one
                            CtMethod ctMethod = ctMethods.get(key);
                            return (Fails) ctMethod.getAnnotation(Fails.class);
                        }
                    }
                }
            } catch (RuntimeException | ClassNotFoundException | NotFoundException e) {
                LOGGER.debug("Stack Trace Analysis - Checking fails annotation. Got an error, but never mind: ", e);
            }
        }

        return null;
    }

    /**
     * Determines if given method is in execution scope
     * * support method
     * * or matching exec. filter
     * * or failed last run
     * * or is precondition for other
     *
     * @param testMethod {@link Method}
     * @return boolean
     */
    public static boolean isMethodInExecutionScope(final Method testMethod, final ITestContext testContext, final ITestResult testResult, boolean withFailed) {
        final SupportMethod supportMethod = testMethod.getAnnotation(SupportMethod.class);

        // given method is a support method -> run it!
        if (supportMethod != null) {
            return true;
        }

        // this method is a precondition -> run it!
        if (ExecutionUtils.pIsMethodPreconditionForOther(testMethod)) {
            LOGGER.info("Executing method because another method depends on it");
            return true;
        }

        return true;
    }

    /**
     * Determines if given method is pre-condition for another method
     *
     * @param method {@link Method}
     * @return boolean
     */
    private static boolean pIsMethodPreconditionForOther(final Method method) {
        final String testName = String.format("%s.%s", method.getDeclaringClass().getName(), method.getName());
        return dependsOnMethods.contains(testName);
    }

    /**
     * Recursive dependency analysis
     * Add all methods to execution which are need to run a method that match the current execution filter
     *
     * @param map          {@link HashMap}
     * @param testNGMethod {@link ITestNGMethod}
     */
    private static void pAddDependencies(final HashMap<String, ITestNGMethod> map, final ITestNGMethod testNGMethod) {

        List<String> methodsDependedUpon = Arrays.asList(testNGMethod.getMethodsDependedUpon());

        if (methodsDependedUpon.size() > 0) {
            methodsDependedUpon.forEach(method -> {
                if (!dependsOnMethods.contains(method)) {
                    dependsOnMethods.add(method);
                    if (map.containsKey(method)) {
                        pAddDependencies(map, map.get(method));
                    } else {
                        LOGGER.warn("Method " + method + " is not planned for execution");
                    }
                }
            });
        }
    }

    /**
     * @deprecated Replaced by {@link Cause}
     */
    public static StackTrace createStackTrace(Throwable throwable) {
        StackTrace stackTrace = new StackTrace();
        stackTrace.stackTrace = new Cause(throwable);
        return stackTrace;
    }
}
