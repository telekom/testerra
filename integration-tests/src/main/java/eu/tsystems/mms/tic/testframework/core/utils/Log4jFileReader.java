package eu.tsystems.mms.tic.testframework.core.utils;

import eu.tsystems.mms.tic.testframework.report.Status;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class Log4jFileReader {

    public final String PATH_TO_LOG_FILE;
    private static final Logger LOGGER = LoggerFactory.getLogger(Log4jFileReader.class);

    public Log4jFileReader(final String pathToLogFile) {
        PATH_TO_LOG_FILE = pathToLogFile;
    }

    /**
     * check existence of log file
     * @return boolean containing status of existence
     */
    public boolean existsFile() {
        final File file = new File(PATH_TO_LOG_FILE);
        return file.exists();
    }

    /**
     * filter log file with given classNameSlug and methodNameSlug
     * @param classNameSlug className in log file entry
     * @param methodNameSlug methodName in log file entry
     * @return List<String> of found entries
     */
    public List<String> filterLogForTestMethod(final String classNameSlug, final String methodNameSlug) {
        final List<String> allLogEntries = readLines();
        List<String> filteredLogEntries = Collections.synchronizedList(allLogEntries);
        LOGGER.debug(String.format("File has '%s' entries.", filteredLogEntries.size()));

        if (classNameSlug != null) {
            filteredLogEntries = filteredLogEntries.parallelStream().filter(s -> s.contains(classNameSlug)).collect(Collectors.toList());
            LOGGER.debug(String.format("Filtered with '%s', entries left: '%s'", classNameSlug, filteredLogEntries.size()));
        }

        if (methodNameSlug != null) {
            final String regEx = methodNameSlug.concat("\\b");
            final Pattern pattern = Pattern.compile(regEx);
            final Predicate<String> predicate = s -> pattern.matcher(s).find();

            filteredLogEntries = filteredLogEntries.parallelStream().filter(predicate).collect(Collectors.toList());
            LOGGER.debug(String.format("Filtered with '%s', entries left: '%s'", methodNameSlug, filteredLogEntries.size()));
        }

        final String logInfoMessage = String.format("Found %s log entries for '%s' and  '%s'.", filteredLogEntries.size(), classNameSlug, methodNameSlug);
        LOGGER.info(logInfoMessage);

        LOGGER.info("Found entries: " + Arrays.toString(filteredLogEntries.toArray(new String[0])));
        return filteredLogEntries;
    }

    /**
     * find a entry existing in the log directly after given parentEntry
     * @param parentEntry entry to find first following entry
     * @return String following entry
     */
    public String filterLogForFollowingEntry(final String parentEntry) {
        final List<String> allLogEntries = readLines();
        String matchedEntry = "not_found";

        if (parentEntry != null) {
            for (int i = 0; i < allLogEntries.size(); i++) {
                final String foundEntry = allLogEntries.get(i);

                if (foundEntry.equals(parentEntry)) {
                    LOGGER.info(String.format("Found ParentEntry: '%s'. ", parentEntry));

                    matchedEntry = allLogEntries.get(i + 1);
                    LOGGER.info(String.format("Found ChildEntry '%s'. ", matchedEntry));
                    break;
                }
            }
        }

        LOGGER.info(String.format("Returning entry '%s'.", matchedEntry));
        return matchedEntry;
    }

    /**
     * verify expectedStatus for all entries found for classNameSlug and methodNameSlug
     * @param classNameSlug className in log file entry
     * @param methodNameSlug methodName in log file entry
     * @param expectedStatus expectedStatus for found entries
     */
    public void assertTestStatusPerMethod(final String classNameSlug, final String methodNameSlug, final Status expectedStatus) {
        final List<String> foundEntries = filterLogForTestMethod(classNameSlug, methodNameSlug);

        for (final String entry : foundEntries) {
            LOGGER.debug(String.format("Asserting '%s' for '%s'",
                    expectedStatus, methodNameSlug));
            Assert.assertTrue(entry.contains(expectedStatus.title),
                    String.format("'%s' has status '%s'", methodNameSlug, expectedStatus));
        }
    }

    /**
     * read log line by line
     * @return List<String> of found entries
     */
    private List<String> readLines() {

        final File logFile = Paths.get(PATH_TO_LOG_FILE).toFile();
        try {
            return new LinkedList<>(org.apache.commons.io.FileUtils.readLines(logFile, Charset.defaultCharset()));
        } catch (IOException e) {
            throw new RuntimeException("Error reading file to List of Strings.", e);
        }
    }
}