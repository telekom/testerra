/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann
 *     pele
 */

package eu.tsystems.mms.tic.testframework.report.threadvisualizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: peter Date: 16.12.12 Time: 13:49 To change this template use File | Settings | File
 * Templates.
 */
public final class DataStorage {

    /** Logger instance */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataStorage.class);
    /** Data Sets */
    private static final List<DataSet> LIST = new LinkedList<DataSet>();

    /** Hide constructor of utility method. */
    private DataStorage() {
    }

    public static List<DataSet> getList() {
        return LIST;
    }

    /**
     * Add new DataSet to storage.
     *
     * @param dataSet new DataSet.
     */
    public static void addDataSet(final DataSet dataSet) {
        LOGGER.trace("Adding ThreadVisualizer DataSet (" + LIST.size() + ") for: "
                + dataSet.getStartTime() + " - "
                + dataSet.getStopTime() +
                " - " + dataSet.getThreadName());
        synchronized (LIST) {
            LIST.add(dataSet);
        }
    }

}
