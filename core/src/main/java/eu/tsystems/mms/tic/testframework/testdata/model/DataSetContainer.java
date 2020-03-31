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
package eu.tsystems.mms.tic.testframework.testdata.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Data set container.
 *
 * @author pele
 *
 */
public abstract class DataSetContainer {

    /**
     * List of data sets.
     */
    private List<DataSet> dataSets = new ArrayList<DataSet>(1);

    /**
     * Creates a data set container with at least 1 data set.
     * @param dataSets Optional data set.
     */
    public DataSetContainer(DataSet... dataSets) {
        for (DataSet dataSet : dataSets) {
            this.dataSets.add(dataSet);
        }
    }

    /**
     * Create an empty container.
     */
    public DataSetContainer() {
    }

    /**
     * Add datasets.
     *
     * @param dataSetsToAdd One or more data sets.
     */
    public synchronized void addDataSets(final DataSet... dataSetsToAdd) {
        for (DataSet dataSet : dataSetsToAdd) {
            dataSets.add(dataSet);
        }
    }

    /**
     * Get a dataset with specififed identifier. Returns null if not found.
     *
     * @param identifier .
     * @return dataset or null.
     */
    public synchronized DataSet getDataSet(final String identifier) {
        for (DataSet dataSet : dataSets) {
            if (dataSet.getIdentifier().equals(identifier)) {
                return dataSet;
            }
        }
        return null;
    }

    /**
     * Getter.
     *
     * @return List of data sets.
     */
    public synchronized List<DataSet> getDataSets() {
        return dataSets;
    }
}
