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

import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.internal.Counters;
import eu.tsystems.mms.tic.testframework.report.model.CodeInfoContainer;

import java.util.List;

/**
 * Created by pele on 19.01.2017.
 */
public class CQWorker extends MethodWorker {
    @Override
    public void run() {
        if (isTest()) {
            /*
             * Check and store asserts
             */
            int numberOfUndescriptedAsserts = Counters.getNumberOfUndescriptedAsserts();
            if (numberOfUndescriptedAsserts > 0) {
                // undescripted Callers
                List<String> undescriptedAssertCallers = Counters.getUndescriptedAssertCallers();
                CodeInfoContainer.storeUndescriptedAssertCallersForMethod(testResult, undescriptedAssertCallers);
            }
            int numberOfDescriptedAsserts = Counters.getNumberOfDescriptedAsserts();
            int asserts = numberOfDescriptedAsserts + numberOfUndescriptedAsserts;
            if (asserts == 0) {
                // no asserts
                CodeInfoContainer.storeNoAssertMethod(testResult);
            }
        }
    }
}
