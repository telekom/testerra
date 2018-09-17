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
 * Created on 12.02.2013
 *
 * Copyright(c) 2013 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.testmanagement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Class ITMInfoContainer. Defines an interface for storing and reading tm path info for each method.
 * 
 * @author pele
 */
public final class TMInfoContainer {

    /**
     * The mapping table.
     */
    private static final Properties METHOD_TO_PATH_MAPPING = new Properties();

    /**
     * Representation for null.
     * 
     * Must be a string representation because HashTable throws NullPointerException if value is null.
     */
    private static final String NOTSET = "#NOT_SET (null)#";

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TMInfoContainer.class);

    /**
     * Hidden constructor.
     */
    private TMInfoContainer() {
    }

    /**
     * Method to save the test path into the container.
     * 
     * @param packageAndClassName .
     * @param methodName .
     * @param testManagementToolPath .
     */
    public static void savePath(final String packageAndClassName, final String methodName,
            String testManagementToolPath) {
        pSavePath(packageAndClassName, methodName, testManagementToolPath);
    }

    /**
     * Method to save the test path into the container.
     *
     * @param packageAndClassName .
     * @param methodName .
     * @param testManagementToolPath .
     */
    private static void pSavePath(final String packageAndClassName, final String methodName,
                                  String testManagementToolPath) {
        if (testManagementToolPath == null) {
            testManagementToolPath = NOTSET;
        }
        LOGGER.trace("Storing into TMInfoContainer: " + packageAndClassName + "." + methodName + " = "
                + testManagementToolPath);
        METHOD_TO_PATH_MAPPING.setProperty(packageAndClassName + "." + methodName, testManagementToolPath);
    }

    /**
     * Method to read the test path.
     * 
     * @param packageAndClassName .
     * @param methodName .
     * @return path
     */
    public static String readPath(final String packageAndClassName, final String methodName) {
        return pReadPath(packageAndClassName, methodName);
    }

    /**
     * Method to read the test path.
     *
     * @param packageAndClassName .
     * @param methodName .
     * @return path
     */
    private static String pReadPath(final String packageAndClassName, final String methodName) {
        String testPath = "No test path info stored for now.";
        if (METHOD_TO_PATH_MAPPING.containsKey(packageAndClassName + "." + methodName)) {
            testPath = METHOD_TO_PATH_MAPPING.getProperty(packageAndClassName + "." + methodName);
        }
        LOGGER.trace("Reading from TMInfoContainer: " + packageAndClassName + "." + methodName + " = " + testPath);

        if (testPath.equals(NOTSET)) {
            testPath = null;
        }

        return testPath;
    }

    /**
     * Is info available.
     * 
     * @param packageAndClassName .
     * @param methodName .
     * @return boolean
     */
    public static boolean hasInfo(final String packageAndClassName, final String methodName) {
        return METHOD_TO_PATH_MAPPING.containsKey(packageAndClassName + "." + methodName);
    }

}
