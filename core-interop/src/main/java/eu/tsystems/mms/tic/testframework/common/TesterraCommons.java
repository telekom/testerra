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
package eu.tsystems.mms.tic.testframework.common;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.xml.DOMConfigurator;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by pele on 05.02.2015.
 */
public class TesterraCommons {

    private static final Logger LOGGER = LoggerFactory.getLogger(TesterraCommons.class);

    private static final String p = "eu.tsystems.mms.tic.testframework";

    private static boolean loggingInitialized = false;
    private static boolean proxySettingsLoaded = false;

    public static final String DEFAULT_PACKAGE_NAME = "eu.tsystems.mms.tic";

    private static final String SYSTEM_PROPERTIES_FILE = "system.properties";
    private static Injector ioc;

    private TesterraCommons() {}

    /**
     * If the System Property tt.loglevel is set, this method tries to change the appropriate Log4j Level.
     */
    public static void setTesterraLogLevel(Level level) {
        org.apache.log4j.Logger TesterraLogger = org.apache.log4j.Logger.getLogger(p);
        TesterraLogger.setLevel(level);
    }

    public static void setTesterraLogLevel() {

        // load from file
        String testerraLogLevelString = PropertyManager.getProperty(TesterraProperties.LOG_LEVEL, "INFO");

        /*
        Patch log level
         */
        if (testerraLogLevelString != null) {
            org.apache.log4j.Logger TesterraLogger = org.apache.log4j.Logger.getLogger(p);
            testerraLogLevelString = testerraLogLevelString.trim().toUpperCase();

            Level level = Level.toLevel(testerraLogLevelString); // is debug when conversion fails
            TesterraLogger.setLevel(level);
        }
    }

    /**
     * If no log4j configuration is set. We try to set it with the file test-log4j or through the BasicConfigurator.
     * Another Method is called, which reads the tt.loglevel from Systemproperties and may overrides an existing
     * value.
     *
     * @param basicConfigFallback If true, BasicConfigurator is called when no logging config is found.
     */
    private static void initializeLogging(final boolean basicConfigFallback) {
        if (!loggingInitialized) {
            final String loggerDefinitionsFilename = "test-log4j.xml";
            final URL log4jConfig = ClassLoader.getSystemResource(loggerDefinitionsFilename);
            if (log4jConfig != null) {
                System.setProperty("log4j.configuration", loggerDefinitionsFilename);
                DOMConfigurator.configure(log4jConfig);
            } else {
                if (basicConfigFallback) {
                    BasicConfigurator.configure();
                }
            }
            loggingInitialized = true;
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

        final String filename = PropertyManager.getProperty(TesterraProperties.PROXY_SETTINGS_FILE, "proxysettings.properties");
        final InputStream inputStream = FileUtils.getLocalFileOrResourceInputStream(filename);

        if (inputStream == null) {
            LOGGER.warn("File " + filename + " not found. No proxy settings loaded.");
            return;
        }

        final Properties props = new Properties();

        try {
            props.load(inputStream);
        } catch (Exception e) {
            LOGGER.warn("Not loaded: " + filename);
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

    public static Injector ioc() {
        return ioc;
    }

    public static void initIoc() {
        Reflections reflections = new Reflections(p);
        Set<Class<? extends AbstractModule>> classes = reflections.getSubTypesOf(AbstractModule.class);
        List<Module> modules = new ArrayList<>();
        LOGGER.info("Register IoC modules: " + classes);
        try {
            for (Class<? extends Module> moduleClass : classes) {
                Constructor<?> ctor = moduleClass.getConstructor();
                modules.add((Module) ctor.newInstance());
            }
            ioc = Guice.createInjector(modules);
        } catch (Exception e) {
            LOGGER.error("Unable to initialize IoC modules", e);
        }
    }

    public static void init() {

        TesterraCommons.initializeLogging(true);

        // implicit calls PropertyManager static block - init all the properties, load property file as well!
        TesterraCommons.setTesterraLogLevel();

        TesterraCommons.initIoc();

        // calls LOGGING - Ensure we have Logging initialized before calling!
        TesterraCommons.initializeSystemProperties();
        TesterraCommons.initializeProxySettings();
    }
}
