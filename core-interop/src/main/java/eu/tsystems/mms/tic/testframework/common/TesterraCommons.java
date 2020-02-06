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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.common;

import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by pele on 05.02.2015.
 */
public class TesterraCommons {
    private static final Logger LOGGER = LoggerFactory.getLogger(TesterraCommons.class);
    private static boolean proxySettingsLoaded = false;

    public static final String DEFAULT_PACKAGE_NAME = "eu.tsystems.mms.tic";
    public static final String FRAMEWORK_PACKAGE=DEFAULT_PACKAGE_NAME+".testframework";
    private static final String SYSTEM_PROPERTIES_FILE = "system.properties";

    private TesterraCommons() {}

    /**
     * Log4j initialization WITHOUT TesterraLogger,
     * which is done in TesterraListener later
     */
    private static void initializeLogging() {
        BasicConfigurator.configure();
        /**
         * We have to remove the default {@link ConsoleAppender},
         * because the {@link TesterraLogger} already logs to System.out
         */
        org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
        Enumeration allAppenders = root.getAllAppenders();
        while (allAppenders.hasMoreElements()) {
            Object appender = allAppenders.nextElement();
            if (appender instanceof ConsoleAppender) {
                root.removeAppender((ConsoleAppender)appender);
            }
        }
    }

    /**
     * Loads proxy settings from a file.
     */
    public static void initializeProxySettings() {
        if (!proxySettingsLoaded) {
            pSetSystemProxySettingsFromConfigFile();
            proxySettingsLoaded = true;
        }
    }

    private static void pSetSystemProxySettingsFromConfigFile() {

        final boolean loadProxySettings = PropertyManager.getBooleanProperty(TesterraProperties.PROXY_SETTINGS_LOAD, true);
        if (!loadProxySettings) {
            LOGGER.info("Skipping loading of Proxy Settings.");
            return;
        }

        String filename = PropertyManager.getProperty(TesterraProperties.PROXY_SETTINGS_FILE, "proxysettings.properties");
        Properties props = new Properties();
        try {
            InputStream inputStream = FileUtils.getLocalFileOrResourceInputStream(filename);
            props.load(inputStream);
        } catch (Exception e) {
            LOGGER.warn(String.format("Not loaded proxy settings from file: %s", e.getMessage()), e);
            return;
        }

        for (String property : props.stringPropertyNames()) {
            final String systemPropertyValue = System.getProperty(property);
            if (StringUtils.isStringEmpty(systemPropertyValue)) {
                final String propertyValue = props.getProperty(property);
                System.setProperty(property, propertyValue);
                LOGGER.info("Setting system property " + property + " = " + propertyValue);
            } else {
                LOGGER.warn("System property " + property + " is NOT set because it was already set to "
                        + systemPropertyValue);
            }
        }
    }

    private static void initializeSystemProperties() {
        final Properties systemProperties = new Properties();
        try {
            InputStream is = FileUtils.getLocalResourceInputStream(SYSTEM_PROPERTIES_FILE);

            if (is == null) {
                LOGGER.warn("Not loaded: " + SYSTEM_PROPERTIES_FILE);
                return;
            }

            systemProperties.load(is);
            systemProperties.stringPropertyNames().forEach(key -> {
                final String value = System.getProperty(key);
                final String newValue = "" + systemProperties.get(key);
                if (!StringUtils.isStringEmpty(value)) {
                    LOGGER.warn("SystemProperty - Overwriting " + key + "=" + value + " << " + newValue);
                } else {
                    LOGGER.info("SystemProperty - Setting " + key + "=" + newValue);
                }
                System.setProperty(key, newValue);
            });
        } catch (Exception e) {
            LOGGER.warn("Not loaded: " + SYSTEM_PROPERTIES_FILE, e);
        }
    }

    public static void init() {
        initializeLogging();
        initializeSystemProperties();
        initializeProxySettings();
    }
}
