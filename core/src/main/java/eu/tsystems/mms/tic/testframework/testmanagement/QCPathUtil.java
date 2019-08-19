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

import eu.tsystems.mms.tic.testframework.common.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Class QCPathUtil.
 * 
 * @author pele
 */
public final class QCPathUtil {

    /**
     * Hidden constructor.
     */
    private QCPathUtil() {

    }

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(QCPathUtil.class);

    /**
     * Liefert den QCTestsetPath zum Testng Result.
     * 
     * @param result Testng result.
     * @return TestsetPath.
     */
    public static String getQCTestsetForTestNGResult(final ITestResult result) {
        final Class<?> clazz = result.getTestClass().getRealClass();
        final Method method = result.getMethod().getConstructorOrMethod().getMethod();
        return getQCTestsetPath(clazz, method, result);
    }

    /**
     * Looks for a qc testset path info. If information is not available, null is returned. If information is available,
     * it is stored into the info container and returned. If information is already stored in the container, this value
     * is returned (also null).
     * 
     * @param clazz Class to check.
     * @param method Method to check.
     * @param result result containing testParameters
     * @return any value, also null
     */
    public static String getQCTestsetPath(final Class<?> clazz, final Method method, final ITestResult result) {
        final String packageAndClassName = clazz.getName();
        final String methodName = method.getName();
        String testSetPath = null;

        if (TMInfoContainer.hasInfo(packageAndClassName, methodName)) {
            testSetPath = TMInfoContainer.readPath(packageAndClassName, methodName);
        }
        String newTestSetPath = checkForQCTestsetInfo(clazz, method, result);
        if (testSetPath == null || newTestSetPath == null || !testSetPath.equals(newTestSetPath)) {
            testSetPath = newTestSetPath;
            TMInfoContainer.savePath(packageAndClassName, methodName, testSetPath);
        }
        if (testSetPath != null) {
            LOGGER.info("QC TestSet Path: " + testSetPath);
        }
        return testSetPath;
    }

    /**
     * Checks a method for a QCTestset annotation. Returns the test path. Stores the test path into the container.
     * 
     * @param clazz .
     * @param method .
     * @param result .
     * @return .
     */
    private static String checkForQCTestsetInfo(Class<?> clazz, Method method, ITestResult result) {
        String packageAndClassName = clazz.getName();
        String methodName = method.getName();
        String testSetPath = null;

        // Get the annotation.
        Annotation annotation = null;
        if (clazz.isAnnotationPresent(QCTestset.class)) {
            annotation = clazz.getAnnotation(QCTestset.class);
        }
        if (method.isAnnotationPresent(QCTestset.class)) {
            annotation = method.getAnnotation(QCTestset.class);
        }

        // Get the test path info.
        if (annotation != null) {
            QCTestset qcTestset;
            qcTestset = (QCTestset) annotation;

            switch (qcTestset.sourceType()) {
                case VALUE:
                    testSetPath = qcTestset.value();
                    testSetPath = PropertiesParser.parseLine(testSetPath);

                    break;
                case FILE:
                    /*
                     * Find the test path from the mapping file.
                     */
                    String sourceFile = qcTestset.dynamicPathSourceFile();
                    InputStream inputStream = Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream(sourceFile);
                    Properties mappedPaths = new Properties();
                    try {
                        mappedPaths.load(inputStream);
                        inputStream.close();
                    } catch (IOException e) {
                        LOGGER.error("Error loading " + sourceFile + e.getMessage() +
                                "\n" + e.fillInStackTrace());
                    }

                    /*
                     * Switch through mapping types.
                     */
                    switch (qcTestset.dynamicPathMappingType()) {
                    case BY_CLASS_AND_METHODNAME: {
                        String key = packageAndClassName + "." + methodName;
                        testSetPath = mappedPaths.getProperty(key);
                        LOGGER.trace("Loaded test set path for " + key + " from " + sourceFile + ": " + testSetPath);
                    }
                        break;
                    case BY_KEY: {
                        String key = qcTestset.key();
                        testSetPath = mappedPaths.getProperty(key);
                        LOGGER.trace("Loaded test set path by key *" + key + "* from " + sourceFile + ": "
                                + testSetPath);
                    }
                        break;
                    default:
                        break;
                    }

                    break;
                default:
                    break;
            }

            if (result == null) {
                LOGGER.info("QC Path: " + testSetPath);
            }
            else {
                LOGGER.info("QC Path for " + result.getMethod().getMethodName() + ": " + testSetPath);
            }
        }

        return testSetPath;
    }
}
