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
package eu.tsystems.mms.tic.testframework.execution.testng.worker.finish;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.exceptions.fennecTestFailureException;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.info.ReportInfo;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionUtils;
import eu.tsystems.mms.tic.testframework.report.utils.FailsAnnotationFilter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by pele on 19.01.2017.
 */
public class MethodAnnotationCheckerWorker extends MethodWorker {

    @Override
    public void run() {
        /*
        write method annotations info
         */
        Annotation[] annotations = method.getAnnotations();
        methodContext.methodTags = Arrays.stream(annotations).collect(Collectors.toList());

        if (isTest()) {
            // get fails annotation
            Fails fails = null;
            if (method.isAnnotationPresent(Fails.class)) {
                fails = method.getAnnotation(Fails.class);
            }

            if (isSuccess()) {
                if (fails != null && FailsAnnotationFilter.isFailsAnnotationValid(fails)) {
                    // fails annotation is present but test is passed -> warn
                    ReportInfo.getDashboardInfo().addInfo(0, "Repaired >" + method.getName() + "< marked @Fails", "methods/" + methodContext.methodRunIndex + ".html");
                    methodContext.addPriorityMessage("@Fails annotation can be removed: " + fails);
                }
            }
            else if (isFailed()) {
                Throwable throwable = testResult.getThrowable();

                // may be there is a deeper @Fails annotataion present
                if (fails == null && testResult != null && throwable != null) {
                    fails = ExecutionUtils.getFailsAnnotationInStackTrace(throwable.getStackTrace());
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
                    final fennecTestFailureException fennecTestFailureException = new fennecTestFailureException(message, throwable);
                    testResult.setThrowable(fennecTestFailureException);

                    // set readable message
                    methodContext.setThrowable(null, throwable, true);
                    String formerReadableMessage = methodContext.getReadableErrorMessage();
                    methodContext.addPriorityMessage(formerReadableMessage);
                    methodContext.setThrowable(message, throwable, true);

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
