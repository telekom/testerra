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
 * Created on 14.03.2012
 * 
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.test.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;
import org.testng.Assert;

import eu.tsystems.mms.tic.testframework.test.constants.FileUtilMessages;

/**
 * This class contains utility methods for handling all kind of files.
 */
public final class TestFileUtils {

    /**
     * Path to the default logfile.
     */
    public static final String LOGFILE = "target/log4j.log";

    /**
     * Path to the logfile for intengration tests.
     */
    public static final String ITLOGFILE = "target/system-log.log";

    /**
     * Path to the report base directory.
     */
    public static final String REPORTPATH = "target/surefire-reports/";

    /**
     * Hide constructor of utility class.
     */
    private TestFileUtils() {

    }

    public static void assertEntryInLogFile(String entry) {
        Assert.assertTrue(TestFileUtils.existsFile(LOGFILE),
                FileUtilMessages.fileNotFound(LOGFILE));
        Assert.assertTrue(TestFileUtils.hasFileEntry(LOGFILE, entry),
                FileUtilMessages.entryNotInFile(entry, LOGFILE));
    }

    /**
     * Checks if File under a specified path exists.
     *
     * @param path The path to file.
     * @return true if file exists, false otherwise.
     */
    public static boolean existsFile(final String path) {
        final File file = new File(path);
        return file.exists();
    }

    /**
     * Checks if file under a specified path has a specified entry.
     *
     * @param path  The path to file.
     * @param entry The entry to search for.
     * @return true if file has entry, false otherwise.
     */
    public static boolean hasFileEntry(final String path, final String entry) {
        FileReader fileReader;
        try {
            fileReader = new FileReader(path);
            final BufferedReader bufferReader = new BufferedReader(fileReader);
            String stream;
            while ((stream = bufferReader.readLine()) != null) {
                if (stream.contains(entry)) {
                    fileReader.close();
                    bufferReader.close();
                    LoggerFactory.getLogger(TestFileUtils.class).info(FileUtilMessages.entryInFile(entry, path));
                    return true;
                }
            }
            LoggerFactory.getLogger(TestFileUtils.class).info(FileUtilMessages.entryNotInFile(entry, path));
            bufferReader.close();
            return false;
        } catch (final FileNotFoundException e) {
            LoggerFactory.getLogger(TestFileUtils.class).warn(e.getMessage());
            return false;
        } catch (final IOException e) {
            LoggerFactory.getLogger(TestFileUtils.class).warn(e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the file under a specified path has an entry matching a given regular expression.
     *
     * @param path  The path to file.
     * @param regex The regular Expression to search for.
     * @return true if file has a mathing entry, false otherwise.
     */
    public static boolean hasFileEntry(final String path, final Pattern regex) {
        return getMatchingEntryForPattern(path, regex) != null;
    }

    /**
     * Checks if the file under a specified path has an entry matching a given regular expression and returns the found
     * entry.
     *
     * @param path  The path to file.
     * @param regex The regular expression to search for.
     * @return Entry (line) of the file which matches the regex or null.
     */
    public static String getMatchingEntryForPattern(final String path, final Pattern regex) {
        FileReader fileReader;
        try {
            fileReader = new FileReader(path);
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            String stream;
            while ((stream = bufferedReader.readLine()) != null) {
                final Matcher matcher = regex.matcher(stream);
                if (matcher.find()) {
                    LoggerFactory.getLogger(TestFileUtils.class).info(
                            FileUtilMessages.entryInFile(regex.pattern(), path) + " Match: " + stream.trim());
                    fileReader.close();
                    bufferedReader.close();
                    return stream.trim();
                }
            }
            LoggerFactory.getLogger(TestFileUtils.class).info(String.format(
                    FileUtilMessages.entryNotInFile(regex.pattern(), path)));
            bufferedReader.close();
            return null;
        } catch (final FileNotFoundException e) {
            LoggerFactory.getLogger(TestFileUtils.class).warn(e.getMessage());
            return null;
        } catch (final IOException e) {
            LoggerFactory.getLogger(TestFileUtils.class).warn(e.getMessage());
            return null;
        }
    }

    /**
     * This method tests if a string is in-between two other strings in a given file.
     *
     * @param path   Path to a file.
     * @param start  String that indicates now to start the search for the String "toTest".
     * @param toTest The string which should be found between start and end.
     * @param end    String that indicates to stop search for "toTest" (if start was found before).
     * @return Return true if toTest was found after start and before end. False otherwise.
     */
    public static boolean isTextInBetween(
            final String path, final String start, final String toTest, final String end) {
        FileReader fileReader;
        try {
            fileReader = new FileReader(path);
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            boolean inBetween = false;
            String stream;
            while ((stream = bufferedReader.readLine()) != null) {
                if (stream.contains(start)) {
                    inBetween = true;
                } else if (stream.contains(toTest) && inBetween) {
                    LoggerFactory.getLogger(TestFileUtils.class).info(
                            FileUtilMessages.entryInFile(toTest, path) + " In-between " + start + " and " + end);
                    fileReader.close();
                    bufferedReader.close();
                    return true;
                } else if (inBetween && stream.contains(end)) {
                    LoggerFactory.getLogger(TestFileUtils.class).info(String.format(FileUtilMessages.entryNotInFile(toTest, path)));
                    fileReader.close();
                    bufferedReader.close();
                    return false;
                }
            }
            bufferedReader.close();
            return false;
        } catch (final FileNotFoundException e) {
            LoggerFactory.getLogger(TestFileUtils.class).warn(e.getMessage());
            return false;
        } catch (final IOException e) {
            LoggerFactory.getLogger(TestFileUtils.class).warn(e.getMessage());
            return false;
        }
    }
}
