package eu.tsystems.mms.tic.testframework.execution.worker.finish;

import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;

public abstract class AbstractEvidencesWorker extends MethodWorker {

    protected abstract void collect();

    @Override
    public void run() {
        if (isFailed()) {
            Object attribute = testResult.getAttribute(SharedTestResultAttributes.failsFromCollectedAssertsOnly);

            if (attribute != Boolean.TRUE) {
                collect();
            }
        } else if (isSkipped()) {
            if (methodContext.status == TestStatusController.Status.FAILED_RETRIED) {
                collect();
            }
        }
    }

}
