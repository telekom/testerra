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

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.events.TesterraEventUserDataManager;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.internal.CollectedAssertions;
import eu.tsystems.mms.tic.testframework.internal.Counters;

/**
 * Created by pele on 19.01.2017.
 */
public class TestMethodFinishedWorker extends MethodWorker {
    @Override
    public void run() {
        String msg = "";

        if (isTest()) {
            msg += "Test ";
        }
        else {
            msg += "Config method ";
        }

        final String testClassName = testMethod.getTestClass().getName();
        msg += testClassName + "." + methodName + " ";

        Throwable throwable = null;
        if (isFailed()) {
            msg += "failed";
            throwable = testResult.getThrowable();
            if (throwable != null) {
                msg += " with: ";
            }
        }
        else if (isSuccess()) {
            msg += "passed";
        }
        else if (isSkipped()) {
            msg += "skipped";
        }

        /*
         * Log and introduce result
         */

        if (isSuccess()) {
            LOGGER.info(msg);
        } else if (isFailed()) {
            if (throwable != null) {
                LOGGER.error(msg, throwable);
            }
            else {
                LOGGER.error(msg);
            }
        }

        if (isTest()) {
            // clean thread local event user data
            TesterraEventUserDataManager.cleanupThreadLocalData();

            // cleanup counters
            Counters.cleanupThreadLocals();

            // cleanup thread locals from PropertyManager
            PropertyManager.clearThreadlocalProperties();

            // cleanup collected assertions
            CollectedAssertions.clear();
        }
    }
}
