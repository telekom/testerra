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
/*
 * Created on 11.01.2011
 *
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.common;

import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    static final ThreadLocal<Properties> THREAD_LOCAL_PROPERTIES = new ThreadLocal<>();

    /**
     * The static properties.
     */
    static final Properties FILEPROPERTIES = new Properties();

    static final Properties GLOBALPROPERTIES = new Properties();

    public static final PropertiesParser parser = new PropertiesParser();

    /*
     * Static constructor, creating static Properties object.
     *
     * NOTE: DO NOT USE LOGGER in this static block!
     */
    static {
        // set static properties
        String propertyFile = "test.properties";
        pLoadPropertiesFromResource(FILEPROPERTIES, propertyFile, null);

        parser.properties.add(FILEPROPERTIES);
        parser.properties.add(System.getProperties());
        parser.properties.add(GLOBALPROPERTIES);
    }

    /*
    LOADERS section
     */
    private static void pLoadPropertiesFromResource(final Properties properties, final String resourceFile, String charset) {
        FileUtils fileUtils = new FileUtils();
        try {
            File file = fileUtils.getLocalOrResourceFile(resourceFile);
            LOGGER.info("Load " + file.getAbsolutePath());
            final InputStream propertiesInputStream = new FileInputStream(file);
            if (charset == null) {
                charset = Charset.defaultCharset().name();
            }
            InputStreamReader inputStreamReader = new InputStreamReader(propertiesInputStream, charset);
            properties.load(inputStreamReader);
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        } catch (final IOException ioEx) {
            throw new IllegalStateException(String.format("An error occurred during reading from properties file %s.", resourceFile), ioEx);
        } catch (final IllegalArgumentException illArgEx) {
            throw new IllegalStateException(String.format("The properties file %s contains illegal characters!", resourceFile), illArgEx);
        }
    }

    private static Properties pLoadThreadLocalProperties(final String resourceFile, final String charset) {
        Properties threadLocalProperties = getThreadLocalProperties();
        pLoadPropertiesFromResource(threadLocalProperties, resourceFile, charset);

        LOGGER.info("ThreadLocalProperties: " + threadLocalProperties);
        return threadLocalProperties;
    }

    /**
     * Loads a local property file.
     *
     * @param resourceFile
     *
     * @return
     */
    public static Properties loadThreadLocalProperties(final String resourceFile) {
        return loadThreadLocalProperties(resourceFile, null);
    }

    /**
     * Loads a local property file.
     *
     * @param resourceFile
     * @param charset
     *
     * @return
     */
    public static Properties loadThreadLocalProperties(final String resourceFile, final String charset) {
        return pLoadThreadLocalProperties(resourceFile, charset);
    }

    /**
     * Load static properties from a property file.
     *
     * @param resourceFile The property file to load.
     * @param localOnly    Deprecated, Testerra only loads local files
     *
     * @return Return loaded properties.
     */
    @Deprecated
    public static Properties loadProperties(final String resourceFile, boolean localOnly) {
        return loadProperties(resourceFile);
    }

    /**
     * Loads a local property file.
     *
     * @param resourceFile
     *
     * @return
     */
    public static Properties loadProperties(final String resourceFile) {
        return loadProperties(resourceFile, null);
    }

    /**
     * Loads a local property file.
     *
     * @param resourceFile
     * @param charset
     *
     * @return
     */
    public static Properties loadProperties(final String resourceFile, final String charset) {
        pLoadPropertiesFromResource(FILEPROPERTIES, resourceFile, charset);
        LOGGER.info("Global Properties: " + FILEPROPERTIES);
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
     *
     * @return The properties value.
     */
    public static String getProperty(final String key) {
        return parser.getProperty(key);
    }

    /**
     * Gets the value of the property identified by its key.
     *
     * @param key          The properties key.
     * @param defaultValue default value
     *
     * @return The properties value.
     */
    public static String getProperty(final String key, final String defaultValue) {
        return parser.getProperty(key, defaultValue);
    }

    /**
     * Gets the value of the property or the default identified by its key.
     *
     * @param key          key of the property
     * @param defaultValue default value
     *
     * @return property value
     */
    public static int getIntProperty(final String key, final int defaultValue) {
        return parser.getIntProperty(key, defaultValue);
    }

    /**
     * Gets the value of the property identified by its key.
     *
     * @param key key of the property
     *
     * @return property value or -1 if value cannot be parsed.
     */
    public static int getIntProperty(final String key) {
        return parser.getIntProperty(key);
    }

    /**
     * Gets the value of the property or the default identified by its key.
     *
     * @param key          key of the property
     * @param defaultValue default value
     *
     * @return property value
     */
    public static double getDoubleProperty(String key, double defaultValue) {
        return parser.getDoubleProperty(key, defaultValue);
    }

    /**
     * Gets the value of the property identified by its key.
     *
     * @param key key of the property
     *
     * @return property value or -1 if value cannot be parsed or is not set.
     */
    public static double getDoubleProperty(final String key) {
        return parser.getDoubleProperty(key);
    }

    /**
     * Gets the value of the property or the default identified by its key.
     *
     * @param key          key of the property
     * @param defaultValue default value
     *
     * @return property value
     */
    public static long getLongProperty(String key, long defaultValue) {
        return parser.getLongProperty(key, defaultValue);
    }

    /**
     * Gets the value of the property identified by its key.
     *
     * @param key key of the property
     *
     * @return property value or -1 if value cannot be parsed or is not set.
     */
    public static long getLongProperty(final String key) {
        return parser.getLongProperty(key);
    }

    /**
     * Get boolean property.
     *
     * @param key true or false.
     *
     * @return boolean property value or default false, if property is not set
     *
     * @see java.lang.Boolean#parseBoolean(String)
     */
    public static boolean getBooleanProperty(final String key) {
        return parser.getBooleanProperty(key);
    }

    /**
     * Gets the value of the property or the default identified by its key.
     *
     * @param key          key of the property
     * @param defaultValue default value
     *
     * @return property value
     */
    public static boolean getBooleanProperty(final String key, final boolean defaultValue) {
        return parser.getBooleanProperty(key, defaultValue);
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
            parser.properties.add(THREAD_LOCAL_PROPERTIES.get());
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
