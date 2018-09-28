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
package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.common.FennecCommons;
import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sagu on 21.09.2016.
 */
public abstract class ErrorContext extends Context {

    public String readableErrorMessage;
    public String additionalErrorMessage;
    private transient Throwable throwable = null;
    private transient StackTrace stacktraceForReadableMessage = null;
    public StackTrace stackTrace;
    public final List<Video> videos = new LinkedList<>();
    public final List<Screenshot> screenshots = new LinkedList<>();
    public String errorFingerprint = "";
    public String scriptSource;
    public String executionObjectSource;

    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Gets the stack trace.
     *
     * @return the stacktrace of the method. Can be null, if method has no stacktrace.
     */
    public StackTrace getStackTrace() {
        return stackTrace;
    }

    public String getAdditionalErrorMessage() {
        return additionalErrorMessage;
    }

    public String getAdditionalErrorMessageFormatted() {
        return "<i>" + StringUtils.prepareStringForHTML(additionalErrorMessage) + "</i>";
    }

    public String getReadableErrorMessage() {
        return readableErrorMessage;
    }

    public String getReadableMessageFormatted() {
        return StringUtils.prepareStringForHTML(this.getReadableErrorMessage());
    }

    public void setThrowable(final String readableMessage, final Throwable throwable) {
        setThrowable(readableMessage, throwable, false);
    }

    public void setThrowable(final String readableMessage, final Throwable throwable, boolean forceUpdateReadableMessage) {
        this.throwable = throwable;

        /*
        set stacktrace string
         */

        if (throwable != null) {
            /*
            quickly set pre-defined message, no other checks needed
             */
            if (readableMessage != null) {
                setReadableMessageForThrowable(readableMessage, throwable, false, forceUpdateReadableMessage);
                return;
            }

            /*
            stacktrace first line, set error message
            set stacktrace
             */
            stackTrace = throwableToStacktrace(throwable, true);

            // set message
            String message;
            if (throwable instanceof TimeoutException && throwable.getCause() != null) {
                message = throwableToMessage(throwable.getCause());
            }
            else {
                message = throwableToMessage(throwable);
            }

            /*
            set readable message
             */
            if (message != null){
                setReadableMessageForThrowable(message, throwable, false, forceUpdateReadableMessage);
            }
            else {
                setReadableMessageForThrowable(stackTrace.throwableFirstLine, throwable, true, forceUpdateReadableMessage);
            }
        }
    }

    public abstract String getName();

    private String throwableToMessage(Throwable throwable) {
        String line = throwable.toString();

        /*
        modify the message if needed
         */
        String msgDependsOn = "depends on";
        if (line.contains(msgDependsOn)) {
            String[] split = line.split(msgDependsOn);
            return getName() + " " + msgDependsOn + split[1];
        }
        else if (line.startsWith(AssertionError.class.getName())) {
            return "Assert" + line.split(AssertionError.class.getName())[1];
        }
        else if (line.startsWith("java.lang.")) {
            return line.replace("java.lang.", "");
        }
        else if (line.startsWith("org.openqa.selenium.")) {
            String out = line.replace("org.openqa.selenium.", "");
            if (out.length() > 50) {
                out = out.split(" ")[0];
                out = out.replace(':', ' ').trim();
            }
            return "Selenium: " + out;
        }
        else if (line.startsWith(FennecRuntimeException.class.getPackage().getName())) {
            return line.replace(TimeoutException.class.getPackage().getName() + ".", "");
        }
        return line;
    }

    private void setReadableMessageForThrowable(String readableErrorMessage, final Throwable forThrowable, boolean withFilter, boolean force) {
        StackTrace stacktrace = throwableToStacktrace(forThrowable, false);
        boolean storedStacktraceIsContainedInNewOne = false;
        if (stacktraceForReadableMessage != null) {
            storedStacktraceIsContainedInNewOne = stacktrace.stackTrace.contains(stacktraceForReadableMessage.stackTrace);
        }
        if (!force && storedStacktraceIsContainedInNewOne) {
            // dont set new readable message
            return;
        }

        /*
        really setReadable message
         */
        if (withFilter) {
            readableErrorMessage = filterReadableMessage(readableErrorMessage);
        }
        this.readableErrorMessage = readableErrorMessage;
        this.stacktraceForReadableMessage = stacktrace;
    }

    protected StackTrace throwableToStacktrace(Throwable throwable, boolean html) {
        List<String> stackTrace = new LinkedList<>();
        final String throwableFirstLine = throwable.toString();
        String additionalErrorMessage = "";

        Throwable iteratingThrowable = throwable;
            /*
            build stacktrace
             */
        while (iteratingThrowable != null) {
            final StackTraceElement[] stackTraceElements = iteratingThrowable.getStackTrace();
            for (final StackTraceElement ste : stackTraceElements) {
                // append
                String line = ste.toString();
                stackTrace.add(line);
            }
            iteratingThrowable = iteratingThrowable.getCause();
            if (iteratingThrowable != null) {
                String iteratingThrowableFirstLine = iteratingThrowable.toString();
                if (html) {
                    iteratingThrowableFirstLine = StringUtils.prepareStringForHTML(iteratingThrowableFirstLine);
                }
                additionalErrorMessage += "\ncaused by " + iteratingThrowableFirstLine;

                stackTrace.add("caused by: ");
                stackTrace.add(iteratingThrowableFirstLine);
            }
        }

        return new StackTrace(throwableFirstLine, stackTrace, additionalErrorMessage);
    }

    private static final String splitmark = "####fennec#";

    private String filterReadableMessage(String message) {
        String splitter = ": ";
        if (message.contains(splitter)) {
            message = message.replace(splitter, splitmark);
            String[] split = message.split(splitmark);

            if (split.length > 1) {
                message = split[1];
            } else {
                message = "";
            }
        }

        if (message.startsWith("expected [")) {
            message = "(Please add Assert description!) Boolean Assertion: " + message;
        }

        return message;
    }

    public void buildExitFingerprint() {
        errorFingerprint = "";

        /*
         * from scriptSource
         */
        if (!StringUtils.isStringEmpty(scriptSource)) {
            errorFingerprint = scriptSource;
            return;
        }

        // build testMethodName
        String testMethodName = getName();
        // check for retry naming
        if (testMethodName.contains("(")) {
            testMethodName = testMethodName.split("\\(")[0].trim();
        }

        /*
         * stacktrace filter
         */
        if (stackTrace != null) {
            for (String line : stackTrace.stackTrace) {
                final String testframeworkPackage = FennecCommons.DEFAULT_PACKAGE_NAME + ".testframework";
                if (line != null && line.contains(FennecCommons.DEFAULT_PACKAGE_NAME) &&
                        ((!line.contains(testframeworkPackage))
                                        ||
                        (line.contains(testframeworkPackage) && line.contains("playground")))
                        ){
                    errorFingerprint = line;
                    break;
                }
            }
        }

    }

    /**
     * Checks if at least 1 screenshot exists.
     *
     * @return true if screenshot exists
     */
    public boolean hasScreenshot() {
        return screenshots.size() > 0;
    }

    /**
     * Checks if at least 1 video exists.
     *
     * @return true if video exists
     */
    public boolean hasVideo() {
        return videos.size() > 0;
    }

}
