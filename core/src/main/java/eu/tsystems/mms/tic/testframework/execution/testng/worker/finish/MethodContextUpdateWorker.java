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

import eu.tsystems.mms.tic.testframework.events.FennecEvent;
import eu.tsystems.mms.tic.testframework.events.FennecEventDataType;
import eu.tsystems.mms.tic.testframework.events.FennecEventService;
import eu.tsystems.mms.tic.testframework.events.FennecEventType;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;

/**
 * Created by pele on 19.01.2017.
 */
public class MethodContextUpdateWorker extends MethodWorker {

    @Override
    public void run() {
        // !!! do nothing when state is RETRY (already set from RetryAnalyzer)
        if (methodContext.status != TestStatusController.Status.FAILED_RETRIED) {

            /*
             * method container status and steps
             */
            if (isFailed()) {

                /*
                 * set throwable
                 */
                Throwable throwable = testResult.getThrowable();
                methodContext.setThrowable(null, throwable);

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
                TestStep currentTestStep = methodContext.steps().getCurrentTestStep();
                methodContext.failedStep = currentTestStep;

                String msg = "";
                String readableMessage = methodContext.getReadableErrorMessage();
                if (!StringUtils.isStringEmpty(readableMessage)) {
                    msg += readableMessage;
                }

                String additionalErrorMessage = methodContext.getAdditionalErrorMessage();
                if (!StringUtils.isStringEmpty(additionalErrorMessage)) {
                    msg += additionalErrorMessage;
                }
                currentTestStep.getCurrentTestStepAction().addFailingLogMessage(msg);
            } else if (isSuccess()) {
                TestStatusController.Status status = TestStatusController.Status.PASSED;
                if (methodContext.nonFunctionalInfos.size() > 0) {
                    status = TestStatusController.Status.MINOR;
                }
                TestStatusController.setMethodStatus(methodContext, status, method);
            } else if (isSkipped()) {
                TestStatusController.setMethodStatus(methodContext, TestStatusController.Status.SKIPPED, method);
            }
        }

        /*
        fire CONTEXT_UPDATE event
         */
        FennecEventService.getInstance().fireEvent(new FennecEvent(FennecEventType.CONTEXT_UPDATE)
                .addData(FennecEventDataType.CONTEXT, methodContext));
    }
}
