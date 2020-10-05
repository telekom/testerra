/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.common;

import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TesterraCommons {
    private static final Logger LOGGER = LoggerFactory.getLogger(TesterraCommons.class);
    public static final String DEFAULT_PACKAGE_NAME = "eu.tsystems.mms.tic";
    public static final String FRAMEWORK_PACKAGE=DEFAULT_PACKAGE_NAME+".testframework";

    private TesterraCommons() {}

    /**
     * Loads properties from a file and sets them as system properties when not already defined
     */
    private static void initializeSystemProperties() {
        FileUtils fileUtils = new FileUtils();
        String filename = PropertyManager.getProperty(TesterraProperties.SYSTEM_SETTINGS_FILE, "system.properties");
        try {
            File file = fileUtils.getLocalOrResourceFile(filename);
            if (file.exists()) {
                loadSystemProperties(file);
            }
        } catch (FileNotFoundException e) {
            //
        }
    }

    private static void loadSystemProperties(File file) {
        Properties props;
        try {
            InputStream inputStream = new FileInputStream(file);
            LOGGER.info("Load system properties: " + file.getAbsolutePath());
            props = new Properties();
            props.load(inputStream);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            return;
        }

        for (String property : props.stringPropertyNames()) {
            final String systemPropertyValue = System.getProperty(property);
            if (StringUtils.isStringEmpty(systemPropertyValue)) {
                String propertyValue = props.getProperty(property);
                System.setProperty(property, propertyValue);
                LOGGER.debug("Setting system property " + property + " = " + propertyValue);
            } else {
                LOGGER.warn("System property " + property + " is NOT set because it was already set to "
                        + systemPropertyValue);
            }
        }
    }

    public static void init() {
        initializeSystemProperties();
    }
}
