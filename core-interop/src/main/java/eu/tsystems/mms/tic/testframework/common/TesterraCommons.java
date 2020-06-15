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
package eu.tsystems.mms.tic.testframework.common;

import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.report.DefaultLogAppender;
import eu.tsystems.mms.tic.testframework.report.TesterraLogger;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class TesterraCommons {
    private static final Logger LOGGER = LoggerFactory.getLogger(TesterraCommons.class);
    private static boolean proxySettingsLoaded = false;
    public static final String DEFAULT_PACKAGE_NAME = "eu.tsystems.mms.tic";
    public static final String FRAMEWORK_PACKAGE=DEFAULT_PACKAGE_NAME+".testframework";

    private TesterraCommons() {}

    /**
     * Log4j initialization with TesterraLogger,
     * and remove all duplicate ConsoleLoggers
     */
    private static void initializeLogging() {
        DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
        LoggerContext loggerContext = Configurator.initialize(defaultConfiguration);
        Configurator.setRootLevel(Level.INFO);
        org.apache.logging.log4j.core.Logger rootLogger = loggerContext.getRootLogger();

        if (getTesterraLogger() == null) {
            DefaultLogAppender.Builder builder = new DefaultLogAppender.Builder();
            builder.setName(TesterraLogger.class.getSimpleName());
            TesterraLogger testerraLogger = builder.build();
            testerraLogger.start();
            rootLogger.addAppender(testerraLogger);
        }

        /**
         * We have to remove the default {@link ConsoleAppender},
         * because the {@link TesterraLogger} already logs to System.out
         */
        Map<String, Appender> allAppenders = rootLogger.getAppenders();

        for (Map.Entry<String, Appender> appender : allAppenders.entrySet()) {
            if (appender.getValue() instanceof ConsoleAppender) {
                rootLogger.removeAppender(appender.getValue());
            }
        }
    }

    public static TesterraLogger getTesterraLogger() {
        org.apache.logging.log4j.core.Logger root = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        Appender testerraLogger = root.getAppenders().get(TesterraLogger.class.getSimpleName());
        if (testerraLogger != null) {
            return (TesterraLogger) testerraLogger;
        } else {
            return null;
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
            LOGGER.debug("Skipping loading of Proxy Settings.");
            return;
        }

        FileUtils fileUtils = new FileUtils();
        String filename = PropertyManager.getProperty(TesterraProperties.PROXY_SETTINGS_FILE, "proxysettings.properties");
        Properties props = new Properties();
        try {
            File proxySettings = fileUtils.getLocalOrResourceFile(filename);
            if (proxySettings.exists()) {
                LOGGER.info("Load proxy settings: " + proxySettings.getAbsolutePath());
            }
            InputStream inputStream = new FileInputStream(proxySettings);
            props.load(inputStream);
        } catch (Exception e) {
            //LOGGER.warn(String.format("Not loaded proxy settings from file: %s", e.getMessage()), e);
            return;
        }

        for (String property : props.stringPropertyNames()) {
            final String systemPropertyValue = System.getProperty(property);
            if (StringUtils.isStringEmpty(systemPropertyValue)) {
                final String propertyValue = props.getProperty(property);
                System.setProperty(property, propertyValue);
                LOGGER.debug("Setting system property " + property + " = " + propertyValue);
            } else {
                LOGGER.warn("System property " + property + " is NOT set because it was already set to "
                        + systemPropertyValue);
            }
        }
    }

    public static void init() {
        initializeLogging();
        initializeProxySettings();
    }
}
