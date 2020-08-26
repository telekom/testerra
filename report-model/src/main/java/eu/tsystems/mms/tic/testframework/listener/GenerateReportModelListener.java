/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.listener;

import com.google.protobuf.Message;
import eu.tsystems.mms.tic.testframework.adapters.ClassContextExporter;
import eu.tsystems.mms.tic.testframework.adapters.ExecutionContextExporter;
import eu.tsystems.mms.tic.testframework.adapters.MethodContextExporter;
import eu.tsystems.mms.tic.testframework.adapters.SuiteContextExporter;
import eu.tsystems.mms.tic.testframework.adapters.TestContextExporter;
import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import eu.tsystems.mms.tic.testframework.events.ITesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.TesterraEvent;
import eu.tsystems.mms.tic.testframework.events.TesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.TesterraEventListener;
import eu.tsystems.mms.tic.testframework.events.TesterraEventType;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.ContextLogFormatter;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SuiteContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SynchronizableContext;
import eu.tsystems.mms.tic.testframework.report.model.context.TestContextModel;
import eu.tsystems.mms.tic.testframework.report.model.context.report.Report;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

public class GenerateReportModelListener implements TesterraEventListener, Loggable {

    private MethodContextExporter methodContextExporter = new MethodContextExporter();
    private ClassContextExporter classContextExporter = new ClassContextExporter();
    private SuiteContextExporter suiteContextExporter = new SuiteContextExporter();
    private TestContextExporter testContextExporter = new TestContextExporter();
    private ExecutionContextExporter executionContextExporter = new ExecutionContextExporter();
    private Report report = new Report();
    private File modelDir;
    private File classesDir;
    private File filesDir;
    private File methodsDir;
    private File testsDir;
    private File suitesDir;

    public GenerateReportModelListener() {
        modelDir = report.getReportDirectory("model");
        modelDir.mkdir();
        classesDir = new File(modelDir, "classes");
        classesDir.mkdir();
        filesDir = new File(modelDir, "files");
        filesDir.mkdir();
        methodsDir = new File(modelDir, "methods");
        methodsDir.mkdir();
        testsDir = new File(modelDir, "tests");
        testsDir.mkdir();
        suitesDir = new File(modelDir, "suites");
        suitesDir.mkdir();
    }

    private void writeBuilderToFile(Message.Builder builder, File file) {
        try {
            FileOutputStream stream = new FileOutputStream(file);
            builder.build().writeTo(stream);
            stream.close();
        } catch (Exception e) {
            log().error("", e);
        }
    }

    @Override
    public void fireEvent(final TesterraEvent testerraEvent) {
        if (testerraEvent.getTesterraEventType() == TesterraEventType.GENERATE_METHOD_REPORT) {
            Map<ITesterraEventDataType, Object> eventData = testerraEvent.getData();
            MethodContext methodContext = (MethodContext) eventData.get(TesterraEventDataType.CONTEXT);
            eu.tsystems.mms.tic.testframework.report.model.MethodContext.Builder methodContextBuilder = methodContextExporter.prepareMethodContext(methodContext, fileBuilder -> {
                writeBuilderToFile(fileBuilder, new File(filesDir, fileBuilder.getId()));

            });
            writeBuilderToFile(methodContextBuilder, new File(methodsDir, methodContext.id));
        } else if (testerraEvent.getTesterraEventType() == TesterraEventType.GENERATE_REPORT) {
            // Reset to default logger
            TesterraCommons.getTesterraLogger().setFormatter(new ContextLogFormatter());

            ExecutionContext currentExecutionContext = ExecutionContextController.getCurrentExecutionContext();
            currentExecutionContext.getMethodStatsPerClass(true, false).keySet().forEach(classContext -> {
                writeBuilderToFile(classContextExporter.prepareClassContext(classContext), new File(classesDir,(classContext.id)));
            });
            writeBuilderToFile(executionContextExporter.prepareExecutionContext(currentExecutionContext), new File(modelDir, "execution"));
        } else if (testerraEvent.getTesterraEventType() == TesterraEventType.CONTEXT_UPDATE) {
            File modelDir = report.getReportDirectory("model");
            modelDir.mkdirs();

            Map<ITesterraEventDataType, Object> data = testerraEvent.getData();
            SynchronizableContext context = (SynchronizableContext) data.get(TesterraEventDataType.CONTEXT);
            if (context instanceof SuiteContext) {
                writeBuilderToFile(suiteContextExporter.prepareSuiteContext((SuiteContext)context), new File(suitesDir,((SuiteContext) context).id));
            } else if (context instanceof TestContextModel) {
                writeBuilderToFile(testContextExporter.prepareTestContext((TestContextModel)context), new File(testsDir,((TestContextModel) context).id));
            }
        }
    }
}
