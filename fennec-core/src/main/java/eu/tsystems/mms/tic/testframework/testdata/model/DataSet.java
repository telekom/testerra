/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
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
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
/*
 * Created on 09.08.2013
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.testdata.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data set.
 *
 * @author pele
 *
 */
public class DataSet {

    /**
     * Identifier string.
     */
    private String identifier;

    /**
     * Data elements.
     */
    private List<DataElement> dataElements = new ArrayList<DataElement>(1);

    /**
     * Create a data set with an identifier and at least 1 data element.
     *
     * @param identifier .
     * @param dataElements .
     */
    public DataSet(final String identifier, final DataElement... dataElements) {
        this.identifier = identifier;
        pAddDataElements(dataElements);
    }

    /**
     * Element list constructor.
     *
     * @param identifier .
     * @param dataElements .
     */
    public DataSet(final String identifier, final List<DataElement> dataElements) {
        this.identifier = identifier;
        this.dataElements = dataElements;
    }

    /**
     * Add DataElements to list.
     *
     * @param newDataElements .
     */
    public synchronized void addDataElement(final DataElement... newDataElements) {
        pAddDataElements(newDataElements);
    }

    /**
     * Add DataElements to list.
     *
     * @param newDataElements .
     */
    private void pAddDataElements(final DataElement... newDataElements) {
        if ((dataElements == null) || (newDataElements.length == 0)) {
            throw new IllegalArgumentException("Parameter dataElements MUST NOT be empty.");
        }
        Collections.addAll(this.dataElements, newDataElements);
    }

    public synchronized List<DataElement> getDataElements() {
        return dataElements;
    }

    public synchronized String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return identifier;
    }
}
