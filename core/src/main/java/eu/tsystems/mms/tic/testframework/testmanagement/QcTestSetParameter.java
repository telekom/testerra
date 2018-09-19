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
 * Created on 16.05.2013
 *
 * Copyright(c) 2011 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.testmanagement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds values for QC entity fields, that are read when the entity is synchronized. Only applies to TestRun at the
 * moment.
 * 
 * @author sepr
 */
public final class QcTestSetParameter {

    /** Logger instance */
    private static final Logger LOGGER = LoggerFactory.getLogger(QcTestSetParameter.class);
    /** Mapping of Field names to QCs internal user-field names. */
    private static Map<String, String> staticProperties = new HashMap<String, String>();
    /** ThreadLocal Map of field names and values, that can be set per test. */
    private static ThreadLocal<Map<String, String>> threadProperties = new ThreadLocal<Map<String, String>>();

    /** Hide constructor */
    private QcTestSetParameter() {
    }

    /**
     * Get the value for a paramter to substitute in QCTestset annotation.
     * 
     * @param parameter Name of parameter to get.
     * @param testResult TestResult to get TestNg parameters from.
     * @return Value for parameter.
     */
    public static String getParameter(final String parameter, final ITestResult testResult) {
        String out = "";
        if (threadProperties.get() != null && threadProperties.get().containsKey(parameter)) {
            out = threadProperties.get().get(parameter);
            LOGGER.debug("Found QCTestSet parameter in thread local properties.");
        } else if (System.getProperty(parameter) != null) {
            out = System.getProperty(parameter);
            LOGGER.debug("Found QCTestSet parameter in system properties.");
        } else if (staticProperties.containsKey(parameter)) {
            out = staticProperties.get(parameter);
            LOGGER.debug("Found QCTestSet parameter in global properties.");
        } else if (testResult != null && testResult.getTestContext() != null
                && testResult.getTestContext().getCurrentXmlTest() != null) {
            final Map<String, String> params = testResult.getTestContext().getCurrentXmlTest().getAllParameters();
            if (params != null && params.containsKey(parameter)) {
                out = params.get(parameter);
                LOGGER.debug("Found QCTestSet parameter as TestNg suite parameter.");
            }
        }
        if (out.isEmpty()) {
            LOGGER.info("Nothing found to replace {" + parameter + "} with.");
            return "{" + parameter + "}";
        } else {
            LOGGER.info("Replace {" + parameter + "} with: " + out);
            return out;
        }
    }

    /**
     * Reset the fields of the threadlocal variable.
     */
    public static void resetThreadLocalProperties() {
        threadProperties.remove();
    }

    /**
     * Add a parameter and the corresponding value to use for building the path of the QCTestset annotation. Values are
     * valid for this thread only.
     * 
     * @param parameter Parameter to substitute.
     * @param value Value to set.
     */
    public static void setProperty(final String parameter, final String value) {
        if (threadProperties.get() == null) {
            threadProperties.set(new HashMap<String, String>());
        }
        threadProperties.get().put(parameter, value);
    }

    /**
     * Add a parameter and the corresponding value to use for building the path of the QCTestset annotation.
     * 
     * @param parameter Parameter to substitute.
     * @param value Value to set.
     */
    public static void setStaticProperty(final String parameter, final String value) {
        staticProperties.put(parameter, value);
    }
}
