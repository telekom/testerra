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
    private File classesDir;
    private File filesDir;
    private File methodsDir;
    private File testsDir;
    private File suitesDir;

    protected MethodContextExporter getMethodContextExporter() {
        return methodContextExporter;
    }

    protected ClassContextExporter getClassContextExporter() {
        return classContextExporter;
    }

    protected SuiteContextExporter getSuiteContextExporter() {
        return suiteContextExporter;
    }

    protected TestContextExporter getTestContextExporter() {
        return testContextExporter;
    }

    protected ExecutionContextExporter getExecutionContextExporter() {
        return executionContextExporter;
    }

    protected File getBaseDir() {
        return baseDir;
    }

    protected File getClassesDir() {
        if (classesDir == null) {
            classesDir = new File(baseDir, "classes");
            classesDir.mkdir();
        }
        return classesDir;
    }

    protected File getFilesDir() {
        if (filesDir == null) {
            filesDir = new File(baseDir, "files");
            filesDir.mkdir();
        }
        return filesDir;
    }

    protected File getMethodsDir() {
        if (methodsDir == null) {
            methodsDir = new File(baseDir, "methods");
            methodsDir.mkdir();
        }
        return methodsDir;
    }

    protected File getTestsDir() {
        if (testsDir == null) {
            testsDir = new File(baseDir, "tests");
            testsDir.mkdir();
        }
        return testsDir;
    }

    protected File getSuitesDir() {
        if (suitesDir == null) {
            suitesDir = new File(baseDir, "suites");
            suitesDir.mkdir();
        }
        return suitesDir;
    }

    public GenerateReportModelListener(File baseDir) {
        this.baseDir = baseDir;
        baseDir.mkdirs();
    }

    protected void writeBuilderToFile(Message.Builder builder, File file) {
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
            writeBuilderToFile(fileBuilder, new File(getFilesDir(), fileBuilder.getId()));
        });
        writeBuilderToFile(methodContextBuilder, new File(getMethodsDir(), methodContext.id));
    }

    @Override
    @Subscribe
    public void onFinalizeExecution(FinalizeExecutionEvent event) {
        ExecutionContext executionContext = event.getExecutionContext();
        executionContext.suiteContexts.forEach(suiteContext -> {
            writeBuilderToFile(suiteContextExporter.prepareSuiteContext(suiteContext), new File(getSuitesDir(), suiteContext.id));
            suiteContext.testContextModels.forEach(testContextModel -> {
                writeBuilderToFile(testContextExporter.prepareTestContext(testContextModel), new File(getTestsDir(), testContextModel.id));
                testContextModel.classContexts.forEach(classContext -> {
                    writeBuilderToFile(classContextExporter.prepareClassContext(classContext), new File(getClassesDir(), classContext.id));
                });
            });
        });
        writeBuilderToFile(executionContextExporter.prepareExecutionContext(executionContext), new File(baseDir, "execution"));
    }
}
