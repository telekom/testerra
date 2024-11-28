/*
 * Testerra
 *
 * (C) 2021, Clemens Große, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.core.utils;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.Status;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Log4jFileReader implements Loggable {

    public final String PATH_TO_LOG_FILE;

    public Log4jFileReader(final String pathToLogFile) {
        PATH_TO_LOG_FILE = pathToLogFile;
    }

    /**
     * Check existence of log file
     *
     * @return boolean containing status of existence
     */
    public boolean existsFile() {
        final File file = new File(PATH_TO_LOG_FILE);
        return file.exists();
    }

    /**
     * Filter log file with given classNameSlug and methodNameSlug
     *
     * @param classNameSlug className in log file entry
     * @param methodNameSlug methodName in log file entry
     * @return List<String> of found entries
     */
    public List<String> filterLogForTestMethod(final String classNameSlug, final String methodNameSlug) {
        List<String> filteredLogEntries = readLines();
        log().debug("File has '{}' entries.", filteredLogEntries.size());

        if (classNameSlug != null) {
            filteredLogEntries = filteredLogEntries.parallelStream().filter(s -> s.contains(classNameSlug)).collect(Collectors.toList());
            log().debug("Filtered with '{}', entries left: '{}'", classNameSlug, filteredLogEntries.size());
        }

        if (methodNameSlug != null) {
            final String regEx = methodNameSlug
                    .replace("(", "\\(")
                    .replace(")", "\\)");
            final Pattern pattern = Pattern.compile(regEx);
            final Predicate<String> predicate = s -> pattern.matcher(s).find();

            filteredLogEntries = filteredLogEntries.parallelStream().filter(predicate).collect(Collectors.toList());
            log().debug("Filtered with '{}', entries left: '{}'", methodNameSlug, filteredLogEntries.size());
        }

        final String logInfoMessage = String.format("Found %s log entries for '%s' and  '%s'.", filteredLogEntries.size(), classNameSlug, methodNameSlug);
        log().info(logInfoMessage);

        log().info("Found entries: " + Arrays.toString(filteredLogEntries.toArray(new String[0])));
        return filteredLogEntries;
    }

    /**
     * Find all log entries contains a given string.
     *
     * @param searchString String used for filtering
     * @return List<String> of found entries
     */
    public List<String> filterLogForString(final String searchString) {
        final List<String> allLogEntries = readLines();

        List<String> filteredLogEntries = allLogEntries.stream().filter(line -> line.contains(searchString)).collect(Collectors.toList());
        log().info("Found {} log entries for '{}'.", filteredLogEntries.size(), searchString);
        log().info("Found entries: {}", Arrays.toString(filteredLogEntries.toArray(new String[0])));
        return filteredLogEntries;
    }

    /**
     * Find an entry existing in the log directly after given searchString
     *
     * @param searchString entry to find first following entry
     * @return String following entry
     */
    public String filterLogForFollowingEntry(final String searchString) {
        final List<String> allLogEntries = readLines();
        String matchedEntry = "not_found";

        if (searchString != null) {
            for (int i = 0; i < allLogEntries.size(); i++) {
                final String foundEntry = allLogEntries.get(i);

                if (foundEntry.equals(searchString)) {
                    log().info("Found ParentEntry: '{}'. ", searchString);

                    matchedEntry = allLogEntries.get(i + 1);
                    log().info("Found ChildEntry '{}'. ", matchedEntry);
                    break;
                }
            }
        }

        log().info("Returning entry '{}'.", matchedEntry);
        return matchedEntry;
    }

    /**
     * Verify expectedStatus for all entries found for classNameSlug and methodNameSlug
     *
     * @param classNameSlug className in log file entry
     * @param methodNameSlug methodName in log file entry
     * @param expectedStatus expectedStatus for found entries
     */
    public void assertTestStatusPerMethod(final String classNameSlug, final String methodNameSlug, final Status expectedStatus) {
        final List<String> foundEntries = filterLogForTestMethod(classNameSlug, methodNameSlug);

        log().debug("Asserting '{}' for '{}'", expectedStatus, methodNameSlug);
        if (foundEntries.stream().anyMatch(line -> line.contains(expectedStatus.title))) {
            log().info("Found status {}", expectedStatus.title);
        } else {
            Assert.fail(String.format("'%s' should have status '%s'", methodNameSlug, expectedStatus));
        }
    }

    /**
     * Read log line by line
     *
     * @return List<String> of found entries
     */
    private List<String> readLines() {

        final File logFile = Paths.get(PATH_TO_LOG_FILE).toFile();
        try {
            return new LinkedList<>(org.apache.commons.io.FileUtils.readLines(logFile, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Error reading file to List of Strings.", e);
        }
    }
}
