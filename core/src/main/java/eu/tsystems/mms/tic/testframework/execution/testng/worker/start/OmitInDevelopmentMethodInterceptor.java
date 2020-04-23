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
package eu.tsystems.mms.tic.testframework.execution.testng.worker.start;

import eu.tsystems.mms.tic.testframework.annotations.InDevelopment;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.TestMethodInterceptWorker;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionUtils;
import org.testng.IMethodInstance;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Omit all methods annotated with {@link eu.tsystems.mms.tic.testframework.annotations.InDevelopment} annotation
 * <p>
 * Date: 01.04.2020
 * Time: 13:46
 *
 * @author Eric Kubenka
 */
public class OmitInDevelopmentMethodInterceptor extends TestMethodInterceptWorker implements Loggable {

    @Override
    public List<IMethodInstance> run() {

        if (Flags.EXECUTION_OMIT_IN_DEVELOPMENT) {
            final List<IMethodInstance> toRemove = new LinkedList<>();

            // collect methods to remove
            iMethodInstanceList.forEach(methodInstance -> {
                final Method method = methodInstance.getMethod().getConstructorOrMethod().getMethod();
                if (method.isAnnotationPresent(InDevelopment.class)) {
                    if (ExecutionUtils.isMethodInExecutionScope(method, iTestContext, null, false)) {
                        log().trace("Removing @" + InDevelopment.class.getSimpleName() + " " + method + " from execution");
                        toRemove.add(methodInstance);
                    }
                }
            });

            // remove them
            toRemove.forEach(methodInstance -> iMethodInstanceList.remove(methodInstance));
        }

        log().info("Execution plan for test context \"" + iTestContext.getName() + "\": " + iMethodInstanceList.stream().map(iMethodInstance -> iMethodInstance.getMethod().getConstructorOrMethod().getName()).collect(Collectors.joining(", ")));

        return iMethodInstanceList;
    }
}
