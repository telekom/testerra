/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.events;

import eu.tsystems.mms.tic.testframework.report.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.context.ExecutionContext;
import java.util.List;
import java.util.Optional;

/**
 * Gets fired when the report model has been finalized
 */
public class FinalizeExecutionEvent {
    public interface Listener {
        void onFinalizeExecution(FinalizeExecutionEvent event);
    }

    private Optional<List<ClassContext>> methodStatsPerClass;
    private ExecutionContext executionContext;

    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    public FinalizeExecutionEvent setExecutionContext(ExecutionContext executionContext) {
        this.executionContext = executionContext;
        return this;
    }

    public Optional<List<ClassContext>> getMethodStatsPerClass() {
        return methodStatsPerClass;
    }

    public FinalizeExecutionEvent setMethodStatsPerClass(List<ClassContext> methodStatsPerClass) {
        this.methodStatsPerClass = Optional.of(methodStatsPerClass);
        return this;
    }

}
