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
import eu.tsystems.mms.tic.testframework.report.model.ClassContextAggregate;
import eu.tsystems.mms.tic.testframework.report.model.ExecutionAggregate;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import java.io.File;

public class GenerateReportNgModelListener extends GenerateReportModelListener {
    public GenerateReportNgModelListener(File baseDir) {
        super(baseDir);
    }

    @Override
    public void onMethodEnd(MethodEndEvent event) {
        // ignore
    }
    @Override
    public void onFinalizeExecution(FinalizeExecutionEvent event) {

        ExecutionAggregate.Builder executionAggregateBuilder = ExecutionAggregate.newBuilder();

        ExecutionContext executionContext = event.getExecutionContext();
        executionContext.suiteContexts.forEach(suiteContext -> {
            executionAggregateBuilder.addSuiteContexts(getSuiteContextExporter().prepareSuiteContext(suiteContext));

            suiteContext.testContextModels.forEach(testContextModel -> {
                executionAggregateBuilder.addTestContexts(getTestContextExporter().prepareTestContext(testContextModel));

                testContextModel.classContexts.forEach(classContext -> {
                    ClassContextAggregate.Builder classContextAggregateBuilder = ClassContextAggregate.newBuilder();
                    classContextAggregateBuilder.setClassContext(getClassContextExporter().prepareClassContext(classContext));

                    classContext.methodContexts.forEach(methodContext -> {
                        classContextAggregateBuilder.addMethodContexts(getMethodContextExporter().prepareMethodContext(methodContext, fileBuilder -> {
                            writeBuilderToFile(fileBuilder, new File(getFilesDir(), fileBuilder.getId()));
                        }));
                    });
                    writeBuilderToFile(classContextAggregateBuilder, new File(getClassesDir(), classContext.id));
                });
            });
        });
        executionAggregateBuilder.setExecutionContext(getExecutionContextExporter().prepareExecutionContext(executionContext));
        writeBuilderToFile(executionAggregateBuilder, new File(baseDir, "execution"));
    }
}
