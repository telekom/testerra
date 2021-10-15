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
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.context.Video;
import java.io.File;

public class GenerateReportNgModelListener extends AbstractReportModelListener implements FinalizeExecutionEvent.Listener{
    private final ExecutionAggregate.Builder executionAggregateBuilder = ExecutionAggregate.newBuilder();

    private final ContextExporter contextExporter = new ContextExporter() {

        @Override
        public eu.tsystems.mms.tic.testframework.report.model.File.Builder buildVideo(Video video) {
            eu.tsystems.mms.tic.testframework.report.model.File.Builder builder = super.buildVideo(video);
            writeFileBuilderToFile(builder);
            return builder;
        }

        @Override
        public eu.tsystems.mms.tic.testframework.report.model.File.Builder[] buildScreenshot(Screenshot screenshot) {
            eu.tsystems.mms.tic.testframework.report.model.File.Builder[] builders = super.buildScreenshot(screenshot);
            writeFileBuilderToFile(builders[0]);
            writeFileBuilderToFile(builders[1]);
            return builders;
        }

        private void writeFileBuilderToFile(eu.tsystems.mms.tic.testframework.report.model.File.Builder fileBuilder) {
            writeBuilderToFile(fileBuilder, new File(getFilesDir(), fileBuilder.getId()));
        }
    };

    public GenerateReportNgModelListener(File baseDir) {
        super(baseDir);
    }

    @Override
    @Subscribe
    public void onFinalizeExecution(FinalizeExecutionEvent event) {
        ExecutionContext executionContext = event.getExecutionContext();

        executionContext.readSuiteContexts().forEach(suiteContext -> {
            executionAggregateBuilder.putSuiteContexts(suiteContext.getId(), contextExporter.buildSuiteContext(suiteContext).build());

            suiteContext.readTestContexts().forEach(testContext -> {
                executionAggregateBuilder.putTestContexts(testContext.getId(), contextExporter.buildTestContext(testContext).build());

                testContext.readClassContexts().forEach(classContext -> {
                    executionAggregateBuilder.putClassContexts(classContext.getId(), contextExporter.buildClassContext(classContext).build());

                    classContext.readMethodContexts().forEach(methodContext -> {
                        this.buildUniqueMethod(methodContext);
                        methodContext.readSessionContexts().forEach(this::buildUniqueSession);
                        methodContext.readRelatedMethodContexts().forEach(this::buildUniqueMethod);
                    });
                });
            });
        });
        executionContext.readExclusiveSessionContexts().forEach(this::buildUniqueSession);

        eu.tsystems.mms.tic.testframework.report.model.ExecutionContext.Builder executionContextBuilder = contextExporter.buildExecutionContext(executionContext);
        executionAggregateBuilder.setExecutionContext(executionContextBuilder);
        writeBuilderToFile(executionAggregateBuilder, new File(baseDir, "execution"));
    }

    private void buildUniqueSession(SessionContext sessionContext) {
        if (!executionAggregateBuilder.containsSessionContexts(sessionContext.getId())) {
            eu.tsystems.mms.tic.testframework.report.model.SessionContext.Builder sessionContextBuilder = contextExporter.buildSessionContext(sessionContext);
            executionAggregateBuilder.putSessionContexts(sessionContext.getId(), sessionContextBuilder.build());
        }
    }

    private void buildUniqueMethod(MethodContext methodContext) {
        if (!executionAggregateBuilder.containsMethodContexts(methodContext.getId())) {
            executionAggregateBuilder.putMethodContexts(methodContext.getId(), contextExporter.buildMethodContext(methodContext).build());
        }
    }
}
