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
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.events.TestStatusUpdateEvent;
import eu.tsystems.mms.tic.testframework.execution.testng.RetryAnalyzer;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.DefaultFormatter;
import eu.tsystems.mms.tic.testframework.utils.Formatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

public class MethodEndWorker implements MethodEndEvent.Listener, Loggable {
    private final Formatter formatter = new DefaultFormatter();

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
            /*
             * set status
             */
            Optional<Fails> failsAnnotation = methodContext.getFailsAnnotation();
            if (failsAnnotation.isPresent() && !failsAnnotation.get().intoReport()) {
                methodContext.setStatus(Status.FAILED_EXPECTED);
                // expected failed
            }

            /*
             * Enhance step infos
             */
            TestStep failedStep = methodContext.getCurrentTestStep();
            methodContext.setFailedStep(failedStep);
        } else if (testResult.isSuccess()) {
            methodContext.setStatus(Status.PASSED);
            RetryAnalyzer.methodHasBeenPassed(methodContext);

            methodContext.getFailsAnnotation().ifPresent(fails -> {
                methodContext.setStatus(Status.REPAIRED);
            });

        } else if (event.isSkipped()) {
            methodContext.setStatus(Status.SKIPPED);
        }

        // clear current result
        ExecutionContextController.clearCurrentTestResult();
        ITestNGMethod testMethod = event.getTestMethod();

        StringBuilder sb = new StringBuilder();
        if (event.isFailed()) {
            sb
                    .append(Status.FAILED.title)
                    .append(" ")
                    .append(formatter.toString(testMethod));
            log().error(sb.toString(), testResult.getThrowable());
        }
        else if (event.getTestResult().isSuccess()) {
            sb
                    .append(Status.PASSED.title)
                    .append(" ")
                    .append(formatter.toString(testMethod));
            log().info(sb.toString(), testResult.getThrowable());
        }
        else if (event.isSkipped()) {
            sb
                    .append(Status.SKIPPED.title)
                    .append(" ")
                    .append(formatter.toString(testMethod));
            log().warn(sb.toString(), testResult.getThrowable());
        }

        if (testMethod.isTest()) {
            // cleanup thread locals from PropertyManager
            PropertyManager.clearThreadlocalProperties();
        }

        /**
         * When the test status is not {@link Status#FAILED}
         * then we announce the test status to update immediately.
         * Otherwise, we wait for the {@link RetryAnalyzer} to update it.
         */
        if (methodContext.getStatus() != Status.FAILED) {
            TesterraListener.getEventBus().post(new TestStatusUpdateEvent(methodContext));
        }
    }
}
