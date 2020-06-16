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
package eu.tsystems.mms.tic.testframework.connectors.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A helper class for holding all the test methods that should be executed while testing.
 *
 * @author mibu, sepr
 */
public class TestsToRun {

    /**
     * A list containing all the methods to execute.
     */
    private final Map<String, List<Method>> locatedMethods;

    /**
     * Public constructor. Creates a new <code>TestsToRun</code> object.
     */
    public TestsToRun() {
        locatedMethods = new HashMap<String, List<Method>>();
    }

    /**
     * Returns the number of tests to run.
     *
     * @return The number of tests to run.
     */
    public int size() {
        return locatedMethods.size();
    }

    /**
     * .
     *
     * @return A map containing the located methods.
     */
    public Map<String, List<Method>> getMethodMap() {
        return locatedMethods;
    }

    /**
     * Gets all located methods.
     *
     * @return A list containing all located methods.
     */
    public List<Method> getAllMethods() {
        final List<Method> rList = new ArrayList<Method>();
        for (final List<Method> l : locatedMethods.values()) {
            rList.addAll(l);
        }
        return rList;
    }

    /**
     * Adds new entry to the map.
     *
     * @param key   The testset string.
     * @param value A list containing the methods.
     */
    public void addEntryToMap(final String key, final List<Method> value) {
        locatedMethods.put(key, value);
    }
}
