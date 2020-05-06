/*
 * Created on 14.03.2012
 *
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.utils;

import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains utility methods for handling all kind of files.
 */
public final class LogAssertUtils {

    /**
     * Path to the default logfile.
     */
    public static final String LOGFILE = "logs/integration-test-log4j.log";

    /**
     * Hide constructor of utility class.
     */
    private LogAssertUtils() {

    }

    public static void assertEntryInLogFile(String entry) {
        Assert.assertTrue(LogAssertUtils.existsFile(LOGFILE), fileNotFound(LOGFILE));
        Assert.assertTrue(LogAssertUtils.hasFileEntry(LOGFILE, entry), entryInFile(entry, LOGFILE));
    }

    public static void assertEntryNotInLogFile(String entry) {
        Assert.assertTrue(LogAssertUtils.existsFile(LOGFILE), fileNotFound(LOGFILE));
        Assert.assertFalse(LogAssertUtils.hasFileEntry(LOGFILE, entry), entryInFile(entry, LOGFILE));
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
                    LoggerFactory.getLogger(LogAssertUtils.class).info(entryInFile(entry, path));
                    return true;
                }
            }
            LoggerFactory.getLogger(LogAssertUtils.class).info(entryNotInFile(entry, path));
            bufferReader.close();
            return false;
        } catch (final IOException e) {
            LoggerFactory.getLogger(LogAssertUtils.class).warn(e.getMessage());
            return false;
        }
    }


    private static String entryInFile(String entry, String filePath) {
        return "Found entry \""
                + entry
                + "\" in the file at "
                + filePath
                + ".";
    }

    private static String entryNotInFile(String entry, String filePath) {
        return "Couldn't find entry \""
                + entry
                + "\" in the file at "
                + filePath
                + ".";
    }

    private static String fileNotFound(String path) {
        return "No file found at \""
                + path
                + "\".";
    }
}