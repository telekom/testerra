/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.report.threadvisualizer;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.context.MethodContext;
import eu.tsystems.mms.tic.testframework.utils.ReportUtils;
import java.io.InputStream;
import java.util.List;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadVisualizer {

    /**
     * Logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadVisualizer.class);

    /**
     * Check if ThreadVisualizer DataStorage has Data.
     *
     * @return true if DataStorage is not empty, false otherwise.
     */
    public static boolean hasData() {
        return DataStorage.getList().size() > 0;
    }

    /**
     * generate the html file.
     */
    public static void generateReport() {

        LOGGER.trace("Generate Report with " + DataStorage.getList().size() + " datasets");

        final String tvFolderName = "threadvisualizer";

        final String threadVisualizerInputFile = "threads.vm";
        final String threadVisualizerOutputFile = "threads.html";

        final String css = tvFolderName + "/timeline.css";
        final InputStream cssIS = Thread.currentThread().getContextClassLoader().getResourceAsStream(css);
        if (cssIS == null) {
            throw new TesterraRuntimeException(css + " not found");
        }

        final String js = tvFolderName + "/timeline.js";
        final InputStream jsIS = Thread.currentThread().getContextClassLoader().getResourceAsStream(js);
        if (jsIS == null) {
            throw new TesterraRuntimeException(js + " not found");
        }

        // copy
        Report report = Testerra.injector.getInstance(Report.class);
        ReportUtils.copyFile(css, report.getReportDirectory());
        ReportUtils.copyFile(js, report.getReportDirectory());

        /*
         #### Velocity merge
        */

        VelocityContext context = new VelocityContext();

        context.put("reportName", ReportUtils.getReportName());

        // build data
        StringBuilder sb = new StringBuilder();
        String line;

        final List<DataSet> list = DataStorage.getList();
        for (final DataSet dataSet : list) {

            final MethodContext methodContext = dataSet.getContext();
            // generate html formatted output for report
            final String updatedMethodContent = ThreadVisualizerUtils.getFormattedContent(methodContext);

            line = "data.addRow(" +
                    "[" +
                    "new Date(" + dataSet.getStartTime() + ")," +
                    "new Date(" + dataSet.getStopTime() + ")," +
                    "'" + updatedMethodContent + "'," +
                    "'" + methodContext.threadName + "'" +
                    "]" +
                    ");";
            sb.append(line).append("\n");
        }

        context.put("data", sb.toString());

        ReportUtils.addExtraTopLevelTab(threadVisualizerInputFile, threadVisualizerOutputFile, "Threads", "threads", context, false);
    }

}
