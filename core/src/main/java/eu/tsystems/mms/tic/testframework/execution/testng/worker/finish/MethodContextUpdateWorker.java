/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.execution.testng.worker.finish;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.execution.testng.RetryAnalyzer;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.utils.FailsAnnotationFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.StringUtils;
import org.testng.ITestResult;

public class MethodContextUpdateWorker implements MethodEndEvent.Listener {

    @Subscribe
    @Override
    public void onMethodEnd(MethodEndEvent event) {
        MethodContext methodContext = event.getMethodContext();
        ITestResult testResult = event.getTestResult();

        // Handle collected assertions if we have more than one
        if (testResult.isSuccess() && methodContext.readErrors().anyMatch(ErrorContext::isNotOptional)) {
            // let the test fail
            testResult.setStatus(ITestResult.FAILURE);
            StringBuilder sb = new StringBuilder();
            sb.append("The following assertions failed:");
            AtomicInteger i = new AtomicInteger();
            methodContext.readErrors()
                    .filter(ErrorContext::isNotOptional)
                    .forEach(errorContext -> {
                        i.incrementAndGet();
                        sb.append("\n").append(i).append(") ").append(errorContext.getThrowable().getMessage());
                    });

            AssertionError testMethodContainerError = new AssertionError(sb.toString());
            testResult.setThrowable(testMethodContainerError);
        } else {
            Throwable throwable = testResult.getThrowable();
            if (throwable != null) {
                methodContext.addError(throwable);
            }
        }

        /*
         * method container status and steps
         */
        if (event.isFailed()) {
            methodContext.setStatus(Status.FAILED);

            /**
             * Validate the {@link Fails} annotation
             */
            Optional<Fails> failsAnnotation = methodContext.getFailsAnnotation();
            failsAnnotation.ifPresent(fails -> {
                Boolean isFailsAnnotationValid = false;

                if (!fails.intoReport()) {
                    String validator = fails.validator();
                    if (StringUtils.isNotBlank(validator)) {
                        Class<?> validatorClass = fails.validatorClass();
                        if (validatorClass == null) {
                            validatorClass = event.getMethod().getDeclaringClass();
                        }

                        try {
                            Method validatorMethod = validatorClass.getMethod(validator, MethodContext.class);
                            isFailsAnnotationValid = (Boolean) validatorMethod.invoke(validatorClass, methodContext);
                        } catch (Throwable t) {
                            methodContext.addError(t);
                        }
                    } else {
                        isFailsAnnotationValid = FailsAnnotationFilter.isFailsAnnotationValid(fails);
                    }
                }
                if (isFailsAnnotationValid) {
                    methodContext.setStatus(Status.FAILED_EXPECTED);
                }
            });
            TestStep failedStep = methodContext.getCurrentTestStep();
            methodContext.setFailedStep(failedStep);
        } else if (testResult.isSuccess()) {
            methodContext.setStatus(Status.PASSED);
            RetryAnalyzer.methodHasBeenPassed(methodContext);

            methodContext.getFailsAnnotation().ifPresent(fails -> {
                methodContext.setStatus(Status.REPAIRED);
            });
        }
    }
}
