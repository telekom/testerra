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

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.adapters.ContextExporter;
import eu.tsystems.mms.tic.testframework.events.FinalizeExecutionEvent;
import eu.tsystems.mms.tic.testframework.report.model.ExecutionAggregate;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import java.io.File;

public class GenerateReportNgModelListener extends AbstractReportModelListener implements FinalizeExecutionEvent.Listener{
    private final ExecutionAggregate.Builder executionAggregateBuilder = ExecutionAggregate.newBuilder();
    private final ContextExporter contextExporter = new ContextExporter();

    public GenerateReportNgModelListener(File baseDir) {
        super(baseDir);
        this.contextExporter.setFileBuilderConsumer(fileBuilder -> {
            writeBuilderToFile(fileBuilder, new File(getFilesDir(), fileBuilder.getId()));
        });
    }

    @Override
    @Subscribe
    public void onFinalizeExecution(FinalizeExecutionEvent event) {
        ExecutionContext executionContext = event.getExecutionContext();

        executionContext.readSuiteContexts().forEach(suiteContext -> {
            executionAggregateBuilder.addSuiteContexts(contextExporter.prepareSuiteContext(suiteContext));

            suiteContext.readTestContexts().forEach(testContext -> {
                executionAggregateBuilder.addTestContexts(contextExporter.prepareTestContext(testContext));

                testContext.readClassContexts().forEach(classContext -> {
                    executionAggregateBuilder.addClassContexts(contextExporter.prepareClassContext(classContext));

                    classContext.readMethodContexts().forEach(methodContext -> {
                        executionAggregateBuilder.addMethodContexts(contextExporter.prepareMethodContext(methodContext));
                        methodContext.readSessionContexts().forEach(sessionContext -> executionAggregateBuilder.addSessionContexts(contextExporter.prepareSessionContext(sessionContext)));
                    });
                });

            });
        });
        executionContext.readExclusiveSessionContexts().forEach(sessionContext -> executionAggregateBuilder.addSessionContexts(contextExporter.prepareSessionContext(sessionContext)));

        eu.tsystems.mms.tic.testframework.report.model.ExecutionContext.Builder executionContextBuilder = contextExporter.prepareExecutionContext(executionContext);
        executionAggregateBuilder.setExecutionContext(executionContextBuilder);
        writeBuilderToFile(executionAggregateBuilder, new File(baseDir, "execution"));
    }
}
