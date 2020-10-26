/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.connectors.util;

import eu.tsystems.mms.tic.testframework.interop.TestEvidenceCollector;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.context.Video;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.apache.maven.surefire.testset.TestRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class containing methods for Synchronizer.
 *
 * @author mrgi, sepr
 */
public final class SyncUtils {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncUtils.class);

    /**
     * String literal 'test', cause it appears very often in this class.
     */
    private static final String TEST = "test";

    /**
     * String literal 'test_', cause it appears very often in this class.
     */
    private static final String TEST2 = "test_";

    /**
     * Protected constructor to hide the public one since this is a static only class.
     */
    private SyncUtils() {

    }

    /**
     * Cuts "test" or "test_" from string if necessary for mapping.
     *
     * @param stringToCut     The string to cut the "test" string.
     * @param stringToCompare The string needed to compare.
     *
     * @return the stringToCut
     */
    public static String cutTestFromString(final String stringToCut, final String stringToCompare) {

        return cutTestFromStringIntern(stringToCut, stringToCompare);
    }

    /**
     * Cuts "test" or "test_" from string if necessary for mapping.
     *
     * @param stringToCut     The string to cut the "test" string.
     * @param stringToCompare The string needed to compare.
     *
     * @return the stringToCut
     */
    private static String cutTestFromStringIntern(final String stringToCut, final String stringToCompare) {

        if (!stringToCut.equalsIgnoreCase(stringToCompare)) {

            if (stringToCut.length() > TEST2.length() && stringToCut.matches("[Tt]est_.*")
                    && stringToCut.substring(TEST2.length()).equalsIgnoreCase(stringToCompare)) {

                return stringToCut.substring(TEST2.length());
            }
            if (stringToCut.length() > TEST.length() && stringToCut.matches("[Tt]est.*")
                    && stringToCut.substring(TEST.length()).equalsIgnoreCase(stringToCompare)) {

                return stringToCut.substring(TEST.length());
            }
        }
        return stringToCut;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory The base directory
     * @param pName     The package name for classes found inside the base directory
     *
     * @return The classes
     */
    public static List<Class<?>> findClasses(final File directory, final String pName) {

        return findClassesIntern(directory, pName);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory The base directory
     * @param pName     The package name for classes found inside the base directory
     *
     * @return The classes
     */
    private static List<Class<?>> findClassesIntern(final File directory, final String pName) {

        final List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        final File[] files = directory.listFiles();
        for (final File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, pName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                try {
                    classes.add(Class.forName(pName + '.' + file.getName().substring(0,
                            file.getName().length() - ".class".length())));
                } catch (final ClassNotFoundException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return classes;
    }

    /**
     * Scans all classes accessible from the steps class loader which belong to the given package and subpackages.
     *
     * @param pName           The base package
     * @param testClassLoader ClassLoader to load classes from.
     *
     * @return The classes
     */
    public static List<Class<?>> getClasses(final String pName, final ClassLoader testClassLoader) {

        final String path = pName.replace('.', '/');
        Enumeration<URL> resources;
        try {
            resources = testClassLoader.getResources(path);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        final List<File> dirs = new ArrayList<File>();
        URL resource;
        while (resources.hasMoreElements()) {
            resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        for (final File directory : dirs) {
            classes.addAll(SyncUtils.findClasses(directory, pName));
        }
        return classes;
    }

    /**
     * Checks if XmlSuiteFile is available.
     *
     * @param testSuiteDefinition The TestRequest given by surefire.
     *
     * @return true if XmlSuiteFile is available.
     */
    public static boolean isTestNGXmlSuite(final TestRequest testSuiteDefinition) {

        return testSuiteDefinition.getSuiteXmlFiles() != null && testSuiteDefinition.getSuiteXmlFiles().size() > 0;
    }

    public static List<File> getVideoFiles() {

        final List<Video> videoList = TestEvidenceCollector.collectVideos();
        final List<File> result = new ArrayList<>();

        for (final Video video : videoList) {
            result.add(new File(video.filename));
        }

        return result;
    }

    /**
     * Gets the screenshot files for the testmethod. Looks in Logs of LoggingContainer first and if nothing is found
     * we'll look for the default pattern in the screenshots folder
     * (Screenshot_<i>{methodName}</i>_<i>{timestamp}</i>.png.
     *
     * @return File object of screenshot.
     */
    public static List<File> getScreenshotFiles() {

        final List<Screenshot> screenshotList = TestEvidenceCollector.collectScreenshots();
        final List<File> fileList = new ArrayList<>();

        for (Screenshot screenshot : screenshotList) {
            fileList.add(new File(screenshot.filename));
        }

        return fileList;
    }

}
