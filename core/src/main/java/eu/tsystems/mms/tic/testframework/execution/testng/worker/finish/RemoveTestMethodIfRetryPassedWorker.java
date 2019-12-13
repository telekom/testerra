/*
 * Created on 13.12.2019
 *
 * Copyright(c) 1995 - 2007 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.execution.testng.worker.finish;

import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;

/**
 * RemoveTestMethodIfRetryPassedWorker
 * Remove retried method results if passed in second try to allow testng to execute testcases that depends on retried method
 * <p>
 * Date: 13.12.2019
 * Time: 11:13
 *
 * @author Eric Kubenka
 */
public class RemoveTestMethodIfRetryPassedWorker extends MethodWorker {

    @Override
    public void run() {

        if (isSuccess()) {

            if (methodContext.hasBeenRetried()) {
                for (final MethodContext dependsOnMethodContexts : methodContext.getDependsOnMethodContexts()) {
                    if (dependsOnMethodContexts.isSame(methodContext) && dependsOnMethodContexts.isRetry()) {
                        testResult.getTestContext().getFailedTests().removeResult(dependsOnMethodContexts.testResult);
                        testResult.getTestContext().getSkippedTests().removeResult(dependsOnMethodContexts.testResult);
                    }
                }
            }
        }

    }
}
