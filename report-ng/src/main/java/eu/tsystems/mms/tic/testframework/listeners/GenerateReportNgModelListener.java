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

package eu.tsystems.mms.tic.testframework.listeners;

import eu.tsystems.mms.tic.testframework.events.FinalizeExecutionEvent;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.report.model.ExecutionAggregate;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import java.io.File;

public class GenerateReportNgModelListener extends GenerateReportModelListener {

    private ExecutionAggregate.Builder executionAggregateBuilder = ExecutionAggregate.newBuilder();

    public GenerateReportNgModelListener(File baseDir) {
        super(baseDir);
    }

    @Override
    public void onMethodEnd(MethodEndEvent event) {
        // ignore
    }
    @Override
    public void onFinalizeExecution(FinalizeExecutionEvent event) {
        ExecutionContext executionContext = event.getExecutionContext();
        executionContext.readSuiteContexts().forEach(suiteContext -> {
            executionAggregateBuilder.addSuiteContexts(getSuiteContextExporter().prepareSuiteContext(suiteContext));

            suiteContext.readTestContexts().forEach(testContext -> {
                executionAggregateBuilder.addTestContexts(getTestContextExporter().prepareTestContext(testContext));

                testContext.readClassContexts().forEach(classContext -> {
                    executionAggregateBuilder.addClassContexts(getClassContextExporter().prepareClassContext(classContext));

                    classContext.readMethodContexts().forEach(methodContext -> {
                        executionAggregateBuilder.addMethodContexts(getMethodContextExporter().prepareMethodContext(methodContext));
                        methodContext.readSessionContexts().forEach(sessionContext -> executionAggregateBuilder.addSessionContexts(getSessionContextExporter().prepareSessionContext(sessionContext)));
                    });
                });

            });
        });
        executionContext.readExclusiveSessionContexts().forEach(sessionContext -> executionAggregateBuilder.addSessionContexts(getSessionContextExporter().prepareSessionContext(sessionContext)));
        executionAggregateBuilder.setExecutionContext(getExecutionContextExporter().prepareExecutionContext(executionContext));
        writeBuilderToFile(executionAggregateBuilder, new File(baseDir, "execution"));
    }
}
