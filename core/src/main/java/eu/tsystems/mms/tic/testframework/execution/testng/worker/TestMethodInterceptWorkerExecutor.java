/*
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
 *      Eric Kubenka
 */
package eu.tsystems.mms.tic.testframework.execution.testng.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IMethodInstance;
import org.testng.ITestContext;

import java.util.LinkedList;
import java.util.List;

/**
 * Executor for {@link TestMethodInterceptWorker}
 * <p>
 * Date: 01.04.2020
 * Time: 13:18
 *
 * @author Eric Kubenka
 */
public class TestMethodInterceptWorkerExecutor implements WorkerExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestMethodInterceptWorkerExecutor.class);

    private final List<TestMethodInterceptWorker> workers = new LinkedList<>();

    public void add(TestMethodInterceptWorker worker) {
        workers.add(worker);
    }

    public List<IMethodInstance> run(List<IMethodInstance> list, final ITestContext context) {

        for (final TestMethodInterceptWorker worker : workers) {

            worker.init(list, context);

            try {
                LOGGER.debug("Executing worker " + worker);
                list = worker.run();
            } catch (Throwable t) {
                LOGGER.error("Error executing TestMethodFilterWorker", t);
            }
        }

        return list;
    }

    @Override
    public void add(Worker worker) {
        add((TestMethodInterceptWorker) worker);
    }
}
