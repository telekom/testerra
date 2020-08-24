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

package eu.tsystems.mms.tic.testframework.worker;

import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.report.threadvisualizer.DataSet;
import eu.tsystems.mms.tic.testframework.report.threadvisualizer.DataStorage;

/**
 * Creates a simple dataset after each method ran.
 * <p>
 * Date: 24.08.2020
 * Time: 08:13
 *
 * @author Eric Kubenka
 */
public class CreateThreadVisualizerDataSetWorker extends MethodWorker {

    private void addThreadVisualizerDataSet() {

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
    public void run() {
        if (wasMethodInvoked()) {
            addThreadVisualizerDataSet();
        }
    }
}
