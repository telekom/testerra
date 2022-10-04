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

import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.report.DefaultReport;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.model.context.ScriptSource;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public final class SourceUtils {

    private SourceUtils() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceUtils.class);

    private static String sourceRoot = System.getProperty(TesterraProperties.MODULE_SOURCE_ROOT, "src");
    private static int linePrefetch = DefaultReport.Properties.SOURCE_LINES_PREFETCH.asLong().intValue();
    private static String classExceptions = Report.Properties.SOURCE_LINES_CLASS_EXCEPTION.asString();
    private static final boolean FIND_SOURCES = DefaultReport.Properties.ACTIVATE_SOURCES.asBool();
    private static HashMap<Class, List<String>> cachedClassNames = new HashMap<>();

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
        Stream<StackTraceElement> stream = Arrays.stream(throwable.getStackTrace());
        if (StringUtils.isNotBlank(classExceptions)) {
            stream = stream.filter(stackTraceElement -> !stackTraceElement.getClassName().contains(classExceptions));
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

    /**
     * Print part of source of *callerSubClass* if stacktrace contains a failure of *classWithFailure* in
     * *callerSubClass*.
     * <p>
     * Only for internal use!
     *
     * @param throwable .
     * @param classWithFailure .
     * @param callerSubClass .
     * @return .
     */
    public static ScriptSource findSourceForThrowable(Throwable throwable, Class classWithFailure, Class callerSubClass) {
        if (!FIND_SOURCES) {
            return null;
        }

        // is class in throwable??
        int causeNumberForClass = getCauseNumberForClass(throwable, classWithFailure, 0);
        if (causeNumberForClass == -1) {
            return null;
        }

        // search for caller
        List<String> classNames;
        if (!cachedClassNames.containsKey(callerSubClass)) {
            classNames = findClassNamesForSubTypesOf(callerSubClass);
            cachedClassNames.put(callerSubClass, classNames);
        } else {
            classNames = cachedClassNames.get(callerSubClass);
            if (classNames.size() == 0) {
                classNames = findClassNamesForSubTypesOf(callerSubClass);
                cachedClassNames.remove(callerSubClass);
                cachedClassNames.put(callerSubClass, classNames);
            }
        }

        if (classNames.size() == 0) {
            return null;
        }

        return findCallerSubclassInThrowable(throwable, classNames, 0, causeNumberForClass);
    }

    private static ScriptSource findCallerSubclassInThrowable(Throwable throwable, List classNames,
                                                              int causeCounter, int fromCauseNumber) {
        if (causeCounter >= fromCauseNumber) {
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                String className = stackTraceElement.getClassName();
                if (classNames.contains(className)) {
                    return getSourceFrom(className, stackTraceElement.getFileName(),
                            stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
                }
            }
        }
        Throwable cause = throwable.getCause();
        causeCounter++;
        if (cause != null) {
            return findCallerSubclassInThrowable(cause, classNames, causeCounter, fromCauseNumber);
        }

        return null;
    }

    private static int getCauseNumberForClass(Throwable throwable, Class clazz, int counter) {
        String classname = clazz.getName();
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (stackTraceElement.getClassName().equals(classname)) {
                return counter;
            }
        }
        Throwable cause = throwable.getCause();
        if (cause != null) {
            counter++;
            return getCauseNumberForClass(cause, clazz, counter);
        }
        return -1;
    }

    private static List<String> findClassNamesForSubTypesOf(Class clazz) {
        final List<String> classnames = new ArrayList<String>();
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(TesterraListener.DEFAULT_PACKAGES));
        Set<Class> subTypesOf = reflections.getSubTypesOf(clazz);
        for (Class aClass : subTypesOf) {
            classnames.add(aClass.getName());
        }
        return classnames;
    }

    private static Optional<File> findClassFile(String className) {
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

    private static ScriptSource getSourceFrom(String className, String filename, String methodName, int lineNr) {
        ScriptSource source = null;

        Optional<File> optionalClassFile = findClassFile(className);
        if (optionalClassFile.isPresent()) {
            source = getSource(optionalClassFile.get(), methodName, lineNr);
        }

        if (source != null) {
            LOGGER.debug("Found source:\n" + source);
            return source;
        } else {
            LOGGER.debug("Did not find source for " + filename + " in " + sourceRoot);
            return null;
        }
    }

    private static ScriptSource getSource(File file, String methodName, int lineNr) {
        ScriptSource scriptSource = new ScriptSource(file.getName(), methodName);

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
