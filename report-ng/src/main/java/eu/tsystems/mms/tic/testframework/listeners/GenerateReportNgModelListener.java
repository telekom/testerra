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
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.events.FinalizeExecutionEvent;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.model.ExecutionAggregate;
import eu.tsystems.mms.tic.testframework.report.model.History;
import eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate;
import eu.tsystems.mms.tic.testframework.report.model.LogMessageAggregate;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Video;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepAction;
import eu.tsystems.mms.tic.testframework.report.Report;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class GenerateReportNgModelListener extends AbstractReportModelListener implements FinalizeExecutionEvent.Listener {
    private final ExecutionAggregate.Builder executionAggregateBuilder = ExecutionAggregate.newBuilder();
    private final HistoryAggregate.Builder historyAggregateBuilder = HistoryAggregate.newBuilder();
    private final LogMessageAggregate.Builder logMessageAggregateBuilder = LogMessageAggregate.newBuilder();

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
            Stream.of(builders).filter(Objects::nonNull).forEach(this::writeFileBuilderToFile);
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
            eu.tsystems.mms.tic.testframework.report.model.SuiteContext builtSuiteContext = contextExporter.buildSuiteContext(suiteContext).build();
            executionAggregateBuilder.putSuiteContexts(suiteContext.getId(), builtSuiteContext);
            historyAggregateBuilder.putSuiteContexts(suiteContext.getId(), builtSuiteContext);

            suiteContext.readTestContexts().forEach(testContext -> {
                eu.tsystems.mms.tic.testframework.report.model.TestContext builtTestContext = contextExporter.buildTestContext(testContext).build();
                executionAggregateBuilder.putTestContexts(testContext.getId(), builtTestContext);
                historyAggregateBuilder.putTestContexts(testContext.getId(), builtTestContext);

                testContext.readClassContexts().forEach(classContext -> {
                    eu.tsystems.mms.tic.testframework.report.model.ClassContext builtClassContext = contextExporter.buildClassContext(classContext).build();
                    executionAggregateBuilder.putClassContexts(classContext.getId(), builtClassContext);
                    historyAggregateBuilder.putClassContexts(classContext.getId(), builtClassContext);

                    classContext.readMethodContexts().forEach(methodContext -> {
                        this.buildUniqueMethod(methodContext);
                        methodContext.readSessionContexts().forEach(this::buildUniqueSession);
                        methodContext.readRelatedMethodContexts().forEach(this::buildUniqueMethod);
                        if (!historyAggregateBuilder.containsMethodContexts(methodContext.getId())) {
                            historyAggregateBuilder.putMethodContexts(methodContext.getId(), contextExporter.buildMethodContext(methodContext, true).build());
                        }
                        // Extract LogMessages for LogMessageAggregate
                        methodContext.readTestSteps()
                                .flatMap(TestStep::readActions)
                                .flatMap(TestStepAction::readEntries)
                                .filter(LogMessage.class::isInstance)
                                .map(entry -> (eu.tsystems.mms.tic.testframework.report.model.context.LogMessage) entry)
                                .forEach(logMessage -> logMessageAggregateBuilder.putLogMessages(logMessage.getId(), contextExporter.buildLogMessage(logMessage).build()));
                    });
                });
            });
        });
        executionContext.readExclusiveSessionContexts().forEach(this::buildUniqueSession);

        // Export all test metrics from SessionContext, MethodContext, ...
        executionAggregateBuilder.setTestMetrics(contextExporter.buildTestMetrics());

        eu.tsystems.mms.tic.testframework.report.model.ExecutionContext.Builder executionContextBuilder = contextExporter.buildExecutionContext(executionContext, false);
        executionAggregateBuilder.setExecutionContext(executionContextBuilder);
        writeBuilderToFile(executionAggregateBuilder, new File(baseDir, "execution"));

        executionContext.readMethodContextLessLogs().map(contextExporter::buildLogMessage).forEach(logMessage -> logMessageAggregateBuilder.putLogMessages(logMessage.getId(), logMessage.build()));
        writeBuilderToFile(logMessageAggregateBuilder, new File(baseDir, "logMessages"));

        eu.tsystems.mms.tic.testframework.report.model.ExecutionContext.Builder historyExecutionContextBuilder = contextExporter.buildExecutionContext(executionContext, true);
        historyAggregateBuilder.setExecutionContext(historyExecutionContextBuilder);
        writeHistoryFile(historyAggregateBuilder, new File(baseDir, "history"));
    }

    private void buildUniqueSession(SessionContext sessionContext) {
        if (!executionAggregateBuilder.containsSessionContexts(sessionContext.getId())) {
            eu.tsystems.mms.tic.testframework.report.model.SessionContext.Builder sessionContextBuilder = contextExporter.buildSessionContext(sessionContext);
            executionAggregateBuilder.putSessionContexts(sessionContext.getId(), sessionContextBuilder.build());
        }
    }

    private void buildUniqueMethod(MethodContext methodContext) {
        if (!executionAggregateBuilder.containsMethodContexts(methodContext.getId())) {
            executionAggregateBuilder.putMethodContexts(methodContext.getId(), contextExporter.buildMethodContext(methodContext, false).build());
        }
    }

    protected void writeHistoryFile(HistoryAggregate.Builder newHistoryEntry, File file) {
        int maxHistoryEntries = Report.Properties.HISTORY_MAXTESTRUNS.asLong().intValue();
        int historyIndex = 1;

        History.Builder history = History.newBuilder();
        Report report = Testerra.getInjector().getInstance(Report.class);
        File currentHistoryFile = report.getFinalReportDirectory("report-ng/model/history");

        // Check if a history file already exists and read its content
        if (currentHistoryFile.exists()) {
            log().info("History file already exists. Appending new entry.");
            try (FileInputStream stream = new FileInputStream(currentHistoryFile)) {
                history.mergeFrom(stream);
                HistoryAggregate lastEntry = history.getEntries(history.getEntriesCount() - 1);
                historyIndex = lastEntry.getHistoryIndex() + 1;
            } catch (Exception e) {
                log().error("Unable to read history file", e);
                return;
            }
        } else {
            log().info("No history file found: {}. Creating new one.", currentHistoryFile.getAbsolutePath());
        }

        newHistoryEntry.setHistoryIndex(historyIndex);

        // Add the new entry from the current execution
        // TODO: Check for better implementation
        history.addEntries(newHistoryEntry);
        List<HistoryAggregate> historyEntryList = history.getEntriesList();
        int entriesCount = historyEntryList.size();
        int entriesToRemove = entriesCount - maxHistoryEntries;
        if (entriesToRemove > 0) {
            // Cut the history to the correct size
            historyEntryList = historyEntryList.subList(entriesToRemove, entriesCount);
        }

        history.clear();
        history.addAllEntries(historyEntryList);
        writeBuilderToFile(history, file);
    }
}
