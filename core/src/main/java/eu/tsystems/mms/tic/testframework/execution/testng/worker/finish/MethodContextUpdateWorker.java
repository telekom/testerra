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

import eu.tsystems.mms.tic.testframework.annotations.InfoMethod;
import eu.tsystems.mms.tic.testframework.events.TesterraEvent;
import eu.tsystems.mms.tic.testframework.events.TesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.TesterraEventService;
import eu.tsystems.mms.tic.testframework.events.TesterraEventType;
import eu.tsystems.mms.tic.testframework.execution.testng.RetryAnalyzer;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;

public class MethodContextUpdateWorker extends MethodWorker {

    @Override
    public void run() {
        // !!! do nothing when state is RETRY (already set from RetryAnalyzer)
        if (methodContext.status != TestStatusController.Status.FAILED_RETRIED) {

            // in case of info method
            if (method.isAnnotationPresent(InfoMethod.class) && (isSkipped() || isSuccess())) {
                TestStatusController.setMethodStatus(methodContext, TestStatusController.Status.INFO, method);
            } else {

                /*
                 * method container status and steps
                 */
                if (isFailed()) {

                    /*
                     * set throwable
                     */
                    Throwable throwable = testResult.getThrowable();
                    methodContext.errorContext().setThrowable(null, throwable);

                    /*
                     * set status
                     */
                    if (isTest()) {
                        Object expectedFailed = testResult.getAttribute(SharedTestResultAttributes.expectedFailed);
                        if (expectedFailed == Boolean.TRUE) {
                            // expected failed
                            TestStatusController.setMethodStatus(methodContext, TestStatusController.Status.FAILED_EXPECTED, method);
                        } else {
                            // regular failed
                            TestStatusController.Status status = TestStatusController.Status.FAILED;
                            if (methodContext.nonFunctionalInfos.size() > 0) {
                                status = TestStatusController.Status.FAILED_MINOR;
                            }

                            TestStatusController.setMethodStatus(methodContext, status, method);
                        }
                    } else {
                        TestStatusController.setMethodStatus(methodContext, TestStatusController.Status.FAILED, method);
                    }

                    /*
                     * Enhance step infos
                     */
                    TestStep failedStep = methodContext.steps().getCurrentTestStep();
                    methodContext.setFailedStep(failedStep);
//                    String msg = "";
//                    String readableMessage = methodContext.errorContext().getReadableErrorMessage();
//                    if (!StringUtils.isStringEmpty(readableMessage)) {
//                        msg += readableMessage;
//                    }
//
//                    String additionalErrorMessage = methodContext.errorContext().getAdditionalErrorMessage();
//                    if (!StringUtils.isStringEmpty(additionalErrorMessage)) {
//                        msg += additionalErrorMessage;
//                    }
//                    failedStep.getCurrentTestStepAction().addFailingLogMessage(msg);
                } else if (isSuccess()) {
                    TestStatusController.Status status = TestStatusController.Status.PASSED;

                    // is it a retried test?
                    if (RetryAnalyzer.hasMethodBeenRetried(methodContext)) {
                        status = TestStatusController.Status.PASSED_RETRY;
                        if (methodContext.nonFunctionalInfos.size() > 0) {
                            status = TestStatusController.Status.MINOR_RETRY;
                        }
                    } else {
                        if (methodContext.nonFunctionalInfos.size() > 0) {
                            status = TestStatusController.Status.MINOR;
                        }
                    }

                    // set status
                    TestStatusController.setMethodStatus(methodContext, status, method);
                } else if (isSkipped()) {
                    TestStatusController.setMethodStatus(methodContext, TestStatusController.Status.SKIPPED, method);
                }
            }
        }

        /*
        fire CONTEXT_UPDATE event
         */
        TesterraEventService.getInstance().fireEvent(new TesterraEvent(TesterraEventType.CONTEXT_UPDATE)
                .addUserData()
                .addData(TesterraEventDataType.CONTEXT, methodContext));
    }
}
