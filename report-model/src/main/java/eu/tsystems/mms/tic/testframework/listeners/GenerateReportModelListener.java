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

package eu.tsystems.mms.tic.testframework.listeners;

import com.google.common.eventbus.Subscribe;
import com.google.protobuf.Message;
import eu.tsystems.mms.tic.testframework.adapters.ClassContextExporter;
import eu.tsystems.mms.tic.testframework.adapters.ExecutionContextExporter;
import eu.tsystems.mms.tic.testframework.adapters.MethodContextExporter;
import eu.tsystems.mms.tic.testframework.adapters.SuiteContextExporter;
import eu.tsystems.mms.tic.testframework.adapters.TestContextExporter;
import eu.tsystems.mms.tic.testframework.events.FinalizeExecutionEvent;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Generates the protobuf report model to the local report directory
 */
public class GenerateReportModelListener implements
        MethodEndEvent.Listener,
        Loggable,
        FinalizeExecutionEvent.Listener
{

    private MethodContextExporter methodContextExporter = new MethodContextExporter();
    private ClassContextExporter classContextExporter = new ClassContextExporter();
    private SuiteContextExporter suiteContextExporter = new SuiteContextExporter();
    private TestContextExporter testContextExporter = new TestContextExporter();
    private ExecutionContextExporter executionContextExporter = new ExecutionContextExporter();
    private final File baseDir;
    private final File classesDir;
    private final File filesDir;
    private final File methodsDir;
    private final File testsDir;
    private final File suitesDir;

    public GenerateReportModelListener(File baseDir) {
        this.baseDir = baseDir;
        baseDir.mkdir();
        classesDir = new File(baseDir, "classes");
        classesDir.mkdir();
        filesDir = new File(baseDir, "files");
        filesDir.mkdir();
        methodsDir = new File(baseDir, "methods");
        methodsDir.mkdir();
        testsDir = new File(baseDir, "tests");
        testsDir.mkdir();
        suitesDir = new File(baseDir, "suites");
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
    @Subscribe
    public void onMethodEnd(MethodEndEvent event) {
        MethodContext methodContext = event.getMethodContext();
        eu.tsystems.mms.tic.testframework.report.model.MethodContext.Builder methodContextBuilder = methodContextExporter.prepareMethodContext(methodContext, fileBuilder -> {
            writeBuilderToFile(fileBuilder, new File(filesDir, fileBuilder.getId()));
        });
        writeBuilderToFile(methodContextBuilder, new File(methodsDir, methodContext.id));
    }

    @Override
    @Subscribe
    public void onFinalizeExecution(FinalizeExecutionEvent event) {
        ExecutionContext executionContext = event.getExecutionContext();
        executionContext.suiteContexts.forEach(suiteContext -> {
            writeBuilderToFile(suiteContextExporter.prepareSuiteContext(suiteContext), new File(suitesDir, suiteContext.id));
            suiteContext.testContextModels.forEach(testContextModel -> {
                writeBuilderToFile(testContextExporter.prepareTestContext(testContextModel), new File(testsDir, testContextModel.id));
                testContextModel.classContexts.forEach(classContext -> {
                    writeBuilderToFile(classContextExporter.prepareClassContext(classContext), new File(classesDir, classContext.id));
                });
            });
        });
        writeBuilderToFile(executionContextExporter.prepareExecutionContext(executionContext), new File(baseDir, "execution"));
    }
}
