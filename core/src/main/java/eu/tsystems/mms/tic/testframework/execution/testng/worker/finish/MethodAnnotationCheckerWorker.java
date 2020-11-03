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
import eu.tsystems.mms.tic.testframework.exceptions.TestFailureException;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.SharedTestResultAttributes;
import eu.tsystems.mms.tic.testframework.info.ReportInfo;
import eu.tsystems.mms.tic.testframework.report.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionUtils;
import eu.tsystems.mms.tic.testframework.report.utils.FailsAnnotationFilter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.testng.ITestResult;
import org.testng.annotations.Test;

public class MethodAnnotationCheckerWorker implements MethodEndEvent.Listener {

    @Subscribe
    @Override
    public void onMethodEnd(MethodEndEvent event) {
        Method method = event.getMethod();
        MethodContext methodContext = event.getMethodContext();
        ITestResult testResult = event.getTestResult();
        /*
        write method annotations info
         */
        Annotation[] annotations = method.getAnnotations();
        methodContext.methodTags = Arrays.stream(annotations).collect(Collectors.toList());

        if (event.getTestMethod().isTest()) {
            // get fails annotation
            Fails fails = null;
            if (method.isAnnotationPresent(Fails.class)) {
                fails = method.getAnnotation(Fails.class);

                // check for dataProvider information
                final String dataProviderName = method.getAnnotation(Test.class).dataProvider();
                if (!dataProviderName.isEmpty()) {
                    // fails annotation in conjunction with dataProvider -> warn
                    methodContext.addPriorityMessage("@Fails and @DataProvider should not be used together. Please remove @Fails from this test method.");
                }
            }

            if (testResult.isSuccess()) {
                if (fails != null && FailsAnnotationFilter.isFailsAnnotationValid(fails)) {
                    // fails annotation is present but test is passed -> warn
                    ReportInfo.getDashboardInfo().addInfo(0, "Repaired >" + method.getName() + "< marked @Fails", "methods/" + methodContext.methodRunIndex + ".html");
                    methodContext.addPriorityMessage("@Fails annotation can be removed: " + fails);
                }
            }
            else if (event.isFailed()) {
                Throwable throwable = testResult.getThrowable();

                // may be there is a deeper @Fails annotataion present
                if (fails == null && testResult != null && throwable != null) {
                    Throwable scanThrowable = throwable;
                    while (fails == null && scanThrowable != null) {
                        fails = ExecutionUtils.getFailsAnnotationInStackTrace(scanThrowable.getStackTrace());
                        scanThrowable = scanThrowable.getCause();
                    }
                }

                // override throwable with found annotation
                if (fails != null && testResult != null && FailsAnnotationFilter.isFailsAnnotationValid(fails)) {
                    final StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Failing of test expected.");

                    if (fails.ticketId() != 0) {
                        stringBuilder.append(" TicketID: ").append(fails.ticketId()).append(".");
                    }
                    if (!"".equals(fails.ticketString())) {
                        stringBuilder.append(" Ticket: ").append(fails.ticketString()).append(".");
                    }
                    if (!"".equals(fails.description())) {
                        stringBuilder.append(" Description: ").append(fails.description()).append(".");
                    }

                    final String message = stringBuilder.toString();

                    // set throwable
                    final TestFailureException testFailureException = new TestFailureException(message, throwable);
                    testResult.setThrowable(testFailureException);

                    // set readable message
                    methodContext.errorContext().setThrowable(null, throwable, true);
                    String formerReadableMessage = methodContext.errorContext().getReadableErrorMessage();
                    methodContext.addPriorityMessage(formerReadableMessage);
                    methodContext.errorContext().setThrowable(message, throwable, true);

                    // flag testresult as expected failed
                    if (fails.intoReport()) {
                        testResult.setAttribute(SharedTestResultAttributes.expectedFailed, Boolean.FALSE);
                    }
                    else {
                        testResult.setAttribute(SharedTestResultAttributes.expectedFailed, Boolean.TRUE);
                    }
                }
            }
        }
    }

}
