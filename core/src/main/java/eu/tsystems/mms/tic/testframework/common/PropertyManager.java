/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */
package eu.tsystems.mms.tic.testframework.common;

import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains methods for reading from properties files.
 *
 * @author mibu, pele, mrgi, sepr
 * @author Mike Reiche <mike.reiche@t-systems.com>
 * @deprecated Use {@link IPropertyManager} instead
 */
public final class PropertyManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyManager.class);
    static final Properties FILEPROPERTIES = new Properties();
    private static final PropertiesParser propertiesParser;
    private static final ThreadLocalPropertyResolver threadLocalPropertyResolver = new ThreadLocalPropertyResolver();
    private static final PropertyResolver filePropertyResolver = new PropertiesPropertyResolver(FILEPROPERTIES);
    private static final PropertyResolver systemPropertyResolver = new PropertiesPropertyResolver(System.getProperties());
    private static final ThreadLocal<List<PropertyResolver>> priorityPropertyResolvers = new ThreadLocal<>();
    private static final String TEST_PROPERTIES = "test.properties";

    /*
     * Static constructor, creating static Properties object.
     *
     * NOTE: DO NOT USE LOGGER in this static block!
     */
    static {
        propertiesParser = new PropertiesParser(() -> {
            List<PropertyResolver> propertyResolvers = priorityPropertyResolvers.get();
            return Stream.concat(
                    (propertyResolvers != null?propertyResolvers.stream():Stream.empty()),
                    Stream.of(
                            threadLocalPropertyResolver,
                            systemPropertyResolver,
                            filePropertyResolver
                    )
            );
        });
        // set static properties
        pLoadPropertiesFromResource(FILEPROPERTIES, TEST_PROPERTIES);
        initializeSystemProperties();
    }

    /**
     * Runs the runnable with a list of prioritized resolvers
     * @param resolvers
     * @param runnable
     */
    public static void withResolvers(List<PropertyResolver> resolvers, Runnable runnable) {
        priorityPropertyResolvers.set(resolvers);
        try {
            runnable.run();
            priorityPropertyResolvers.remove();
        } catch (Throwable throwable) {
            priorityPropertyResolvers.remove();
            throw throwable;
        }
    }

    /**
     * Loads properties from a file and sets them as system properties when not already defined
     */
    private static void initializeSystemProperties() {
        String resourceFile = PropertyManager.getProperty(TesterraProperties.SYSTEM_SETTINGS_FILE, "system.properties");
        Properties temporarySystemProperties = new Properties();
        pLoadPropertiesFromResource(temporarySystemProperties, resourceFile);

        for (String property : temporarySystemProperties.stringPropertyNames()) {
            final String systemPropertyValue = System.getProperty(property);
            if (StringUtils.isBlank(systemPropertyValue)) {
                String propertyValue = temporarySystemProperties.getProperty(property);
                System.setProperty(property, propertyValue);
                LOGGER.debug("Setting system property " + property + " = " + propertyValue);
            } else {
                LOGGER.warn("System property " + property + " is NOT set because it was already set to "
                        + systemPropertyValue);
            }
        }
    }

    static boolean pLoadPropertiesFromResource(final Properties properties, final String resourceFile) {
        return pLoadPropertiesFromResource(properties, resourceFile, Charset.defaultCharset().name());
    }

    /**
     * Loads properties from a resource file into existing {@link Properties}
     * @param properties
     * @param resourceFile
     * @param charset If null, the default charset is used
     * @return TRUE if the properties have been loaded
     */
    static boolean pLoadPropertiesFromResource(final Properties properties, final String resourceFile, String charset) {
        FileUtils fileUtils = new FileUtils();
        try {
            File file = fileUtils.getLocalOrResourceFile(resourceFile);
            final InputStream propertiesInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(propertiesInputStream, charset);
            properties.load(inputStreamReader);
            LOGGER.info("Loaded " + file.getAbsolutePath());
            return true;
        } catch (FileNotFoundException e) {
            // ignore
        } catch (final IOException ioEx) {
            throw new IllegalStateException(String.format("An error occurred during reading from properties file %s.", resourceFile), ioEx);
        } catch (final IllegalArgumentException illArgEx) {
            throw new IllegalStateException(String.format("The properties file %s contains illegal characters!", resourceFile), illArgEx);
        }
        return false;
    }

    /**
     * Loads a local property file.
     *
     * @param resourceFile
     * @return
     */
    public static Properties loadThreadLocalProperties(final String resourceFile) {
       return loadThreadLocalProperties(resourceFile, Charset.defaultCharset().name());
    }

    /**
     * Loads a local property file.
     *
     * @param resourceFile
     * @param charset
     * @return
     */
    public static Properties loadThreadLocalProperties(final String resourceFile, final String charset) {
        Properties threadLocalProperties = getThreadLocalProperties();
        pLoadPropertiesFromResource(threadLocalProperties, resourceFile, charset);
        return threadLocalProperties;
    }

    /**
     * Loads a local property file.
     *
     * @param resourceFile
     * @return
     */
    public static Properties loadProperties(final String resourceFile) {
        return loadProperties(resourceFile, Charset.defaultCharset().name());
    }

    /**
     * Loads a local property file.
     *
     * @param resourceFile
     * @param charset
     * @return
     */
    public static Properties loadProperties(final String resourceFile, final String charset) {
        pLoadPropertiesFromResource(FILEPROPERTIES, resourceFile, charset);
        return FILEPROPERTIES;
    }

    /*
    GETTERS section
     */

    /**
     * Hidden constructor.
     */
    private PropertyManager() {
    }

    /**
     * Gets the value of the property identified by its key.
     *
     * @param key The properties key.
     * @return The properties value.
     */
    public static String getProperty(final String key) {
        return getPropertiesParser().getProperty(key);
    }

    /**
     * Gets the value of the property identified by its key.
     *
     * @param key          The properties key.
     * @param defaultValue default value
     * @return The properties value.
     */
    public static String getProperty(final String key, final String defaultValue) {
        return getPropertiesParser().getProperty(key, defaultValue);
    }

    /**
     * Gets the value of the property or the default identified by its key.
     *
     * @param key          key of the property
     * @param defaultValue default value
     * @return property value
     */
    public static int getIntProperty(final String key, final int defaultValue) {
        return getPropertiesParser().getIntProperty(key, defaultValue);
    }

    /**
     * Gets the value of the property identified by its key.
     *
     * @param key key of the property
     * @return property value or -1 if value cannot be parsed.
     */
    public static int getIntProperty(final String key) {
        return getPropertiesParser().getIntProperty(key);
    }

    /**
     * Gets the value of the property or the default identified by its key.
     *
     * @param key          key of the property
     * @param defaultValue default value
     * @return property value
     */
    public static double getDoubleProperty(String key, double defaultValue) {
        return getPropertiesParser().getDoubleProperty(key, defaultValue);
    }

    /**
     * Gets the value of the property identified by its key.
     *
     * @param key key of the property
     * @return property value or -1 if value cannot be parsed or is not set.
     */
    public static double getDoubleProperty(final String key) {
        return getPropertiesParser().getDoubleProperty(key);
    }

    /**
     * Gets the value of the property or the default identified by its key.
     *
     * @param key          key of the property
     * @param defaultValue default value
     * @return property value
     */
    public static long getLongProperty(String key, long defaultValue) {
        return getPropertiesParser().getLongProperty(key, defaultValue);
    }

    /**
     * Gets the value of the property identified by its key.
     *
     * @param key key of the property
     * @return property value or -1 if value cannot be parsed or is not set.
     */
    public static long getLongProperty(final String key) {
        return getPropertiesParser().getLongProperty(key);
    }

    /**
     * Get boolean property.
     *
     * @param key true or false.
     * @return boolean property value or default false, if property is not set
     * @see Boolean#parseBoolean(String)
     */
    public static boolean getBooleanProperty(final String key) {
        return getPropertiesParser().getBooleanProperty(key);
    }

    /**
     * Gets the value of the property or the default identified by its key.
     *
     * @param key          key of the property
     * @param defaultValue default value
     * @return property value
     */
    public static boolean getBooleanProperty(final String key, final boolean defaultValue) {
        return getPropertiesParser().getBooleanProperty(key, defaultValue);
    }

    /**
     * Removes all loaded properties from PropertyManager.
     */
    @Deprecated
    public static void clearProperties() {
        clearThreadlocalProperties();
        FILEPROPERTIES.clear();
    }

    /**
     * Returns properties of test.properties
     */
    @Deprecated
    public static Properties getFileProperties() {
        return FILEPROPERTIES;
    }

    /**
     * gets the local properties of the thread
     *
     * @return local properties of the thread
     * @deprecated Use {@link #getTestLocalProperties()} instead
     */
    @Deprecated
    public static Properties getThreadLocalProperties() {
       return threadLocalPropertyResolver.getProperties();
    }

    public static Properties getTestLocalProperties() {
        return getThreadLocalProperties();
    }

    /**
     * clear the local thread properties
     */
    @Deprecated
    public static void clearThreadlocalProperties() {
        threadLocalPropertyResolver.clearProperties();
    }

    /**
     * @deprecated Use {@link System#getProperties()} instead
     * @return
     */
    @Deprecated
    public static Properties getGlobalProperties() {
        return System.getProperties();
    }

    @Deprecated
    public static PropertiesParser getPropertiesParser() {
        return propertiesParser;
    }
}
