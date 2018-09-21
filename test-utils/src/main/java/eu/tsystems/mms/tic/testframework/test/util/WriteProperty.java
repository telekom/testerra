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
 * Created on 26.03.2012
 * 
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.test.util;

import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class to save properties to a file.
 * 
 * @author sepr
 * 
 */
public final class WriteProperty {

    /**
     * Path to test.properties file in target folder.
     */
    public static final String TESTPROPS = "target/test-classes/test.properties";

    /**
     * hide constructor.
     */
    private WriteProperty() {
    }

    /**
     * Write Properties to a properties file.
     * 
     * @param properties Properties to save.
     * @param file Name of the file to save the properties to.
     */
    public static synchronized void saveProperties(final Properties properties, final String file) {
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e1) {
            LoggerFactory.getLogger(WriteProperty.class).error("Error writing Properties to file. " + e1.getMessage());
            return;
        }
        try {
            properties.store(out, null);
        } catch (IOException e) {
            LoggerFactory.getLogger(WriteProperty.class).error("Error writing Properties to file. " + e.getMessage());
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    LoggerFactory.getLogger(WriteProperty.class).error(
                            "Error closing Properties output stream. " + e.getMessage());
                }
            }
        }
    }
}
