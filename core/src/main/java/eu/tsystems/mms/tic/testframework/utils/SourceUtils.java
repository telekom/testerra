/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.model.context.ScriptSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

public final class SourceUtils {

    private SourceUtils() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceUtils.class);

    private static final boolean FIND_SOURCES = Report.Properties.ACTIVATE_SOURCES.asBool();

    public static ScriptSource findScriptSourceForThrowable(Throwable throwable) {
        if (!FIND_SOURCES) {
            return null;
        }

        AtomicReference<File> atomicClassFile = new AtomicReference<>();
        Optional<StackTraceElement> optionalStackTraceElement = traceStackTraceElement(throwable, atomicClassFile);

        ScriptSource source = null;
        if (optionalStackTraceElement.isPresent()) {
            StackTraceElement stackTraceElement = optionalStackTraceElement.get();
            source = getSource(atomicClassFile.get(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
        }
        return source;
    }

    /**
     * Searches for a stack trace element which source can be resolved on the local file system
     */
    private static Optional<StackTraceElement> traceStackTraceElement(Throwable throwable, AtomicReference<File> atomicClassFile) {
        String exclusionRegex = Report.Properties.SOURCE_EXCLUSION.asString();
        Stream<StackTraceElement> stream = Arrays.stream(throwable.getStackTrace());
        if (StringUtils.isNotBlank(exclusionRegex)) {
            try {
                Pattern pattern = Pattern.compile(exclusionRegex);
                stream = stream.filter(stackTraceElement -> !pattern.matcher(stackTraceElement.getClassName()).find());
            } catch (PatternSyntaxException e) {
                LOGGER.warn("Cannot filter throwable for code snippet exclusions: {}", e.getMessage());
            }
        }

        Optional<StackTraceElement> optionalStackTraceElement = stream
                // Filter for files that exists in the source path
                .filter(stackTraceElement -> {
                    Optional<File> optionalClassFile = findClassFile(stackTraceElement.getClassName());
                    optionalClassFile.ifPresent(atomicClassFile::set);
                    return optionalClassFile.isPresent();
                })
                .findFirst();

        /*
         * When no class file has been found, search recursive in the stacktrace of it's cause
         */
        if (
                !optionalStackTraceElement.isPresent()
                        && throwable.getCause() != null
                        && throwable.getCause() != throwable
        ) {
            return traceStackTraceElement(throwable.getCause(), atomicClassFile);
        } else {
            return optionalStackTraceElement;
        }
    }

    private static Optional<File> findClassFile(String className) {
        String sourceRoot = Report.Properties.SOURCE_ROOT.asString();
        String filePath = className.replace(".", "/").concat(".java");
        File file = new File(sourceRoot + "/main/java/" + filePath);
        if (file.exists()) {
            return Optional.of(file);
        }

        file = new File(sourceRoot + "/test/java/" + filePath);
        if (file.exists()) {
            return Optional.of(file);
        }

        return Optional.empty();
    }

    private static ScriptSource getSource(File file, String methodName, int lineNr) {
        ScriptSource scriptSource = new ScriptSource(file.getName(), methodName);
        int linePrefetch = Report.Properties.SOURCE_LINES_PREFETCH.asLong().intValue();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            int lineCounter = 1;
            int startAtLine = lineNr - linePrefetch;
            if (startAtLine < 1) {
                startAtLine = 1;
            }
            while (line != null) {
                if (lineCounter >= startAtLine && lineCounter < lineNr) {
                    /*
                    LINE
                     */
                    scriptSource.addLine(new ScriptSource.Line(line, lineCounter));
                } else if (lineCounter == lineNr) {
                    /*
                    LINE WITH ISSUE
                     */
                    scriptSource.addLine(new ScriptSource.Line(line, lineCounter)).markLineNumber(lineCounter);
                } else if (lineCounter > lineNr) {
                    // stop
                    br.close();

                    return scriptSource;
                }

                // read next line
                line = br.readLine();
                lineCounter++;
            }
            br.close();
        } catch (IOException e) {
            LOGGER.warn("Error reading source of " + file.getName(), e);
        }
        return null;
    }

}
