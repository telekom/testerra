/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.execution.testng.worker;

import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.util.LinkedList;
import java.util.List;

public class MethodWorkerExecutor implements WorkerExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodWorkerExecutor.class);

    private final List<MethodWorker> workers = new LinkedList<>();

    public void add(MethodWorker worker) {
        workers.add(worker);
    }

    public void run(final ITestResult testResult, final String methodName, final MethodContext methodContext,
                    final ITestContext context, final IInvokedMethod invokedMethod) {
        workers.forEach(w -> {
            w.init(testResult, methodName, methodContext, context, invokedMethod);
            try {
                LOGGER.debug("Executing worker " + w);
                w.run();
            } catch (Throwable t) {
                LOGGER.error("Error executing MethodWorker", t);
            }
        });
    }

    @Override
    public void add(Worker worker) {
        add((MethodWorker) worker);
    }
}
