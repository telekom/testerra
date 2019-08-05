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
 * Created on 11.01.2011
 *
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.common;

import eu.tsystems.mms.tic.testframework.constants.RTConstants;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Contains methods for reading from properties files.
 *
 * @author mibu, pele, mrgi, sepr
 */
public final class PropertyManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyManager.class);

    /**
     * Threadlocal Properties.
     */
    static final ThreadLocal<Properties> THREAD_LOCAL_PROPERTIES = new ThreadLocal<Properties>();

    /**
     * The static properties.
     */
    static final Properties FILEPROPERTIES = new Properties();

    static final Properties GLOBALPROPERTIES = new Properties();

    private static void pLoadProperties(final Properties properties, final String resourceFile, String charset) {
        final InputStream propertiesInputStream = FileUtils.getLocalOrResourceFileAsStream(resourceFile);

        if (propertiesInputStream == null) {
            throw new TesterraSystemException("Resource property file not found: " + resourceFile);
        }

        if (charset == null) {
            charset = Charset.defaultCharset().name();
        }

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(propertiesInputStream, charset);
            properties.load(inputStreamReader);
        } catch (final IOException ioEx) {
            throw new IllegalStateException(String.format("An error occurred during reading from properties file %s.",
                    resourceFile), ioEx);
        } catch (final IllegalArgumentException illArgEx) {
            throw new IllegalStateException(String.format("The properties file %s contains illegal characters!",
                    resourceFile), illArgEx);
        } catch (final Exception e) {
            LOGGER.error("Error loading properties file", e);
        }
    }

    /** Static constructor, creating static Properties object. */
    static {
        // set static properties
        String propertyFile = RTConstants.getTesterraTestPropertiesFile();
        pLoadProperties(FILEPROPERTIES, propertyFile, null);
    }

    /*
    LOADERS section
     */

    private static Properties pLoadThreadLocalProperties(final String resourceFile, final String charset) {
        Properties threadLocalProperties;
        if (THREAD_LOCAL_PROPERTIES.get() == null) {
            threadLocalProperties = new Properties();
            THREAD_LOCAL_PROPERTIES.set(threadLocalProperties);
        }
        else {
            threadLocalProperties = THREAD_LOCAL_PROPERTIES.get();
        }

        pLoadProperties(threadLocalProperties, resourceFile, charset);

        return threadLocalProperties;
    }

    /**
     * Load properties from a property file.
     *
     * @param resourceFile The property file to load.
     * @return Return loaded properties.
     */
    public static Properties loadThreadLocalProperties(final String resourceFile) {
        return pLoadThreadLocalProperties(resourceFile, null);
    }

    /**
     * Load properties from a property file.
     *
     * @param resourceFile The property file to load.
     * @return Return loaded properties.
     */
    public static Properties loadThreadLocalProperties(final String resourceFile, final String charset) {
        return pLoadThreadLocalProperties(resourceFile, null);
    }

    /**
     * Load static properties from a property file.
     *
     * @param resourceFile The property file to load.
     * @return Return loaded properties.
     */
    public static Properties loadProperties(final String resourceFile) {
        pLoadProperties(FILEPROPERTIES, resourceFile, null);
        return FILEPROPERTIES;
    }

    /**
     * Load static properties from a property file.
     *
     * @param resourceFile The property file to load.
     * @return Return loaded properties.
     */
    public static Properties loadProperties(final String resourceFile, final String charset) {
        pLoadProperties(FILEPROPERTIES, resourceFile, charset);
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
        return PropertiesParser.getParsedPropertyStringValue(key);
    }

    /**
     * Gets the value of the property identified by its key.
     *
     * @param key          The properties key.
     * @param defaultValue default value
     * @return The properties value.
     */
    public static String getProperty(final String key, final String defaultValue) {
        final String prop = getProperty(key);
        if (prop == null || prop.length() <= 0) {
            return defaultValue;
        } else {
            return prop;
        }
    }

    /**
     * Gets the value of the property or the default identified by its key.
     *
     * @param key          key of the property
     * @param defaultValue default value
     * @return property value
     */
    public static int getIntProperty(final String key, final int defaultValue) {
        final String prop = getProperty(key);
        try {
            return Integer.parseInt(prop);
        } catch (final NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Gets the value of the property identified by its key.
     *
     * @param key key of the property
     * @return property value or -1 if value cannot be parsed.
     */
    public static int getIntProperty(final String key) {
        final String prop = getProperty(key);
        try {
            return Integer.parseInt(prop);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Gets the value of the property or the default identified by its key.
     *
     * @param key          key of the property
     * @param defaultValue default value
     * @return property value
     */
    public static double getDoubleProperty(String key, double defaultValue) {
        final String prop = getProperty(key);
        if (prop == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(prop);
        } catch (final NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Gets the value of the property identified by its key.
     *
     * @param key key of the property
     * @return property value or -1 if value cannot be parsed or is not set.
     */
    public static double getDoubleProperty(final String key) {
        final String prop = getProperty(key);
        if (prop == null) {
            return -1;
        }
        try {
            return Double.parseDouble(prop);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Gets the value of the property or the default identified by its key.
     *
     * @param key          key of the property
     * @param defaultValue default value
     * @return property value
     */
    public static long getLongProperty(String key, long defaultValue) {
        final String prop = getProperty(key);
        if (prop == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(prop);
        } catch (final NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Gets the value of the property identified by its key.
     *
     * @param key key of the property
     * @return property value or -1 if value cannot be parsed or is not set.
     */
    public static long getLongProperty(final String key) {
        final String prop = getProperty(key);
        if (prop == null) {
            return -1;
        }
        try {
            return Long.parseLong(prop);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Get boolean property.
     *
     * @param key true or false.
     * @return boolean property value or default false, if property is not set
     * @see java.lang.Boolean#parseBoolean(String)
     */
    public static boolean getBooleanProperty(final String key) {
        final String prop = getProperty(key);
        if (prop == null) {
            return false;
        }
        return Boolean.parseBoolean(prop.trim());
    }

    /**
     * Gets the value of the property or the default identified by its key.
     *
     * @param key          key of the property
     * @param defaultValue default value
     * @return property value
     */
    public static boolean getBooleanProperty(final String key, final boolean defaultValue) {
        final String prop = getProperty(key);
        if (prop == null) {
            return defaultValue;
        }
        if (prop.equalsIgnoreCase("true")) {
            return true;
        } else if (prop.equalsIgnoreCase("false")) {
            return false;
        } else {
            return defaultValue;
        }
    }

    /**
     * Removes all loaded properties from PropertyManager.
     */
    public static void clearProperties() {
        THREAD_LOCAL_PROPERTIES.remove();
        FILEPROPERTIES.clear();
        GLOBALPROPERTIES.clear();
    }

    public static Properties getFileProperties() {
        return FILEPROPERTIES;
    }

    /**
     * gets the local properties of the thread
     *
     * @return local properties of the thread
     */
    public static Properties getThreadLocalProperties() {
        if (THREAD_LOCAL_PROPERTIES.get() == null) {
            THREAD_LOCAL_PROPERTIES.set(new Properties());
        }
        return THREAD_LOCAL_PROPERTIES.get();
    }

    /**
     * clear the local thread properties
     */
    public static void clearThreadlocalProperties() {
        THREAD_LOCAL_PROPERTIES.remove();
    }

    public static void clearGlobalProperties() {
        GLOBALPROPERTIES.clear();
    }

    public static Properties getGlobalProperties() {
        return GLOBALPROPERTIES;
    }

}
