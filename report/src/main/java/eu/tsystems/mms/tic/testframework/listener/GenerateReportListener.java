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

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.events.GenerateReportEvent;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.threadvisualizer.DataSet;
import eu.tsystems.mms.tic.testframework.report.threadvisualizer.DataStorage;
import eu.tsystems.mms.tic.testframework.utils.DateUtils;
import eu.tsystems.mms.tic.testframework.utils.ReportUtils;

public class GenerateReportListener implements
        Loggable,
        MethodEndEvent.Listener,
        GenerateReportEvent.Listener
{

    @Subscribe
    @Override
    public void onMethodEnd(MethodEndEvent event) {
        try {
            ReportUtils.createMethodDetailsStepsView(event.getMethodContext());
            addThreadVisualizerDataSet(event.getMethodContext());
        } catch (Throwable e) {
            log().error("FATAL: Could not create html", e);
        }
    }

    private void addThreadVisualizerDataSet(MethodContext methodContext) {

        long startTimeTime = methodContext.startTime.getTime();
        long endTimeTime = methodContext.endTime.getTime();

        if (endTimeTime - startTimeTime <= 10) {
            endTimeTime = startTimeTime + 10;
        }

        final DataSet dataSet = new DataSet(
                methodContext,
                startTimeTime,
                endTimeTime);
        DataStorage.addDataSet(dataSet);
    }

    @Override
    @Subscribe
    public void onGenerateReport(GenerateReportEvent event) {
        long start = System.currentTimeMillis();
        ReportUtils.createReport(event.getReportingData());
        long stop = System.currentTimeMillis();
        String formattedDuration = DateUtils.getFormattedDuration(stop - start, false);
        log().debug("Took " + formattedDuration + " to create the report.");
    }
}
