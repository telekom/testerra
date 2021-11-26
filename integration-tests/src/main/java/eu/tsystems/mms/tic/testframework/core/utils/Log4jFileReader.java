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

    public boolean existsFile() {
        final File file = new File(PATH_TO_LOG_FILE);
        return file.exists();
    }

    public List<String> filterLogForTestMethod(final String methodWorkerNameSlug, final String methodNameSlug) {
        final List<String> allLogEntries = readLines();
        List<String> filteredLogEntries = Collections.synchronizedList(allLogEntries);
        LOGGER.debug(String.format("File has %s entries.", filteredLogEntries.size()));

        if (methodWorkerNameSlug != null) {
            filteredLogEntries = filteredLogEntries.parallelStream().filter(s -> s.contains(methodWorkerNameSlug)).collect(Collectors.toList());
            LOGGER.debug(String.format("Filtered with %s, entries left: %s", methodWorkerNameSlug, filteredLogEntries.size()));
        }

        if (methodNameSlug != null) {
            filteredLogEntries = filteredLogEntries.parallelStream().filter(s -> s.contains(methodNameSlug)).collect(Collectors.toList());
            LOGGER.debug(String.format("Filtered with %s, entries left: %s", methodNameSlug, filteredLogEntries.size()));
        }

        final String logInfoMessage = String.format("Found %s log entries for '%s' and  '%s'.", filteredLogEntries.size(), methodWorkerNameSlug, methodNameSlug);
        LOGGER.info(logInfoMessage);

        LOGGER.info("Found entries: " + Arrays.toString(filteredLogEntries.toArray(new String[0])));
        return filteredLogEntries;
    }

    public void assertTestStatusPerMethod(final String methodWorkerNameSlug, final String methodNameSlug, final Status expectedStatus) {
        final List<String> foundEntries = filterLogForTestMethod(methodWorkerNameSlug, methodNameSlug);

        for (final String entry : foundEntries) {
            final String expectedStatusTitle = expectedStatus.title;

            LOGGER.debug(String.format("Asserting '%s' for '%s'",
                    expectedStatusTitle, methodNameSlug));
            Assert.assertTrue(entry.contains(expectedStatusTitle),
                    String.format("'%s' has status '%s'", methodNameSlug, expectedStatusTitle));
        }
    }

    private List<String> readLines() {

        final File logFile = Paths.get(PATH_TO_LOG_FILE).toFile();
        try {
            return new LinkedList<>(org.apache.commons.io.FileUtils.readLines(logFile, Charset.defaultCharset()));
        } catch (IOException e) {
            throw new RuntimeException("Error reading file to List of Strings.", e);
        }
    }
}