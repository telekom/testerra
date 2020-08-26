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
import eu.tsystems.mms.tic.testframework.adapters.MethodContextExporter;
import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import eu.tsystems.mms.tic.testframework.events.ITesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.TesterraEvent;
import eu.tsystems.mms.tic.testframework.events.TesterraEventDataType;
import eu.tsystems.mms.tic.testframework.events.TesterraEventListener;
import eu.tsystems.mms.tic.testframework.events.TesterraEventType;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.ContextLogFormatter;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.report.Report;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

public class GenerateReportModelListener implements TesterraEventListener, Loggable {

    private MethodContextExporter methodContextExporter = new MethodContextExporter();
    private Report report = new Report();

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
            final Map<ITesterraEventDataType, Object> eventData = testerraEvent.getData();
            final MethodContext methodContext = (MethodContext) eventData.get(TesterraEventDataType.CONTEXT);
            File modelDir = report.getReportDirectory("model");
            eu.tsystems.mms.tic.testframework.report.model.MethodContext.Builder methodContextBuilder = methodContextExporter.prepareMethodContext(methodContext, fileBuilder -> {
                writeBuilderToFile(fileBuilder, new File(modelDir, "file-"+fileBuilder.getId()));

            });
            writeBuilderToFile(methodContextBuilder, new File(modelDir, "method-"+methodContext.id));
        }

        if (testerraEvent.getTesterraEventType() == TesterraEventType.GENERATE_REPORT) {
            // Enable report formatter here
            TesterraCommons.getTesterraLogger().setFormatter(new ContextLogFormatter());

        }
    }
}
