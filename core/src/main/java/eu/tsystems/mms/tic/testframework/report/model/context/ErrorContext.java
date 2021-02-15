/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */
package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.interop.TestEvidenceCollector;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionUtils;
import eu.tsystems.mms.tic.testframework.utils.SourceUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import java.util.Optional;

public class ErrorContext {
    @Deprecated
    private String readableErrorMessage;
    @Deprecated
    public String additionalErrorMessage;
    private transient Throwable throwable = null;
    @Deprecated
    private transient StackTrace stacktraceForReadableMessage = null;
    private StackTrace stackTrace;
    @Deprecated
    private String errorFingerprint;

    private ScriptSource scriptSource;
    private ScriptSource executionObjectSource;
    private Object ticketId;
    private boolean optional;

    public ErrorContext() {

    }

    public ErrorContext(Throwable throwable, boolean optional) {
        this.setThrowable(null, throwable);
        this.optional = optional;
    }

    public boolean isOptional() {
        return optional;
    }

    /**
     * The test script source
     */
    public Optional<ScriptSource> getScriptSource() {
        if (this.scriptSource == null) {
            this.scriptSource = SourceUtils.findScriptSourceForThrowable(getThrowable());
        }
        return Optional.ofNullable(this.scriptSource);
    }

    /**
     * The page object source triggering this assertion
     */
    @Deprecated
    public Optional<ScriptSource> getExecutionObjectSource() {
        if (this.executionObjectSource == null) {
            this.executionObjectSource = TestEvidenceCollector.getSourceFor(getThrowable());
        }
        return Optional.ofNullable(this.executionObjectSource);
    }

    public Object getTicketId() {
        return ticketId;
    }

    public ErrorContext setTicketId(Object ticketId) {
        this.ticketId = ticketId;
        return this;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public ErrorContext setDescription(String description) {
        this.description = description;
        return this;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Gets the stack trace.
     *
     * @return the stacktrace of the method. Can be null, if method has no stacktrace.
     * @deprecated Use {@link #getThrowable()} instead
     */
    public StackTrace getStackTrace() {
        return stackTrace;
    }

    @Deprecated
    public String getAdditionalErrorMessage() {
        return additionalErrorMessage;
    }

    @Deprecated
    public String getAdditionalErrorMessageFormatted() {
        return "<i>" + StringUtils.prepareStringForHTML(additionalErrorMessage) + "</i>";
    }

    @Deprecated
    public String getReadableErrorMessage() {
        return readableErrorMessage;
    }

    @Deprecated
    public String getReadableMessageFormatted() {
        return StringUtils.prepareStringForHTML(this.getReadableErrorMessage());
    }

    public void setThrowable(final Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * Use {@link #setThrowable(Throwable)} instead
     */
    @Deprecated
    public void setThrowable(final String readableMessage, final Throwable throwable) {
        setThrowable(readableMessage, throwable, false);
    }

    /**
     * Use {@link #setThrowable(Throwable)} instead
     */
    @Deprecated
    public void setThrowable(final String readableMessage, Throwable throwable, boolean forceUpdateReadableMessage) {
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
            stackTrace = ExecutionUtils.createStackTrace(throwable);


            if (throwable instanceof TimeoutException && throwable.getCause() != null) {
                // update throwable for reading correct "cause by" later
                throwable = throwable.getCause();
            }

            // set message
            final String message = throwableToMessage(throwable);


            /*
            set readable message
             */
            if (message != null){
                setReadableMessageForThrowable(message, throwable, false, forceUpdateReadableMessage);
            }
            else {
                setReadableMessageForThrowable(stackTrace.getFirstLine(), throwable, true, forceUpdateReadableMessage);
            }
        }
    }

    private String throwableToMessage(Throwable throwable) {
        String line = throwable.toString();

        /*
        modify the message if needed
         */
        String msgDependsOn = "depends on";
        if (line.contains(msgDependsOn)) {
            String[] split = line.split(msgDependsOn);
            return getClass().getName() + " " + msgDependsOn + split[1];
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
        else if (line.startsWith(RuntimeException.class.getPackage().getName())) {
            return line.replace(TimeoutException.class.getPackage().getName() + ".", "");
        }
        return line;
    }

    private void setReadableMessageForThrowable(String readableErrorMessage, final Throwable forThrowable, boolean withFilter, boolean force) {
        StackTrace stacktrace = ExecutionUtils.createStackTrace(forThrowable);
        boolean storedStacktraceIsContainedInNewOne = false;
        if (stacktraceForReadableMessage != null) {
            storedStacktraceIsContainedInNewOne = stacktrace.getFirstLine().equals(stacktraceForReadableMessage.getFirstLine());
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

        // use first cause in strackTrace as additionalErrorMessage for report
        if(stacktrace.stackTrace.getCause() != null) {
            this.additionalErrorMessage = "caused by: " + stacktrace.stackTrace.getCause().getClassName() + " " + stacktrace.stackTrace.getCause().getMessage();
        }

        this.readableErrorMessage = readableErrorMessage;
        this.stacktraceForReadableMessage = stacktrace;
    }

    private static final String splitmark = "####tt.#";

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

    @Deprecated
    public String getExitFingerprint() {
        if (errorFingerprint != null) {
            return errorFingerprint;
        }

        Optional<ScriptSource> scriptSource = getScriptSource();
        /*
         * from scriptSource
         */
        if (scriptSource.isPresent()) {
            return scriptSource.get().toString();
        }

        /*
         * stacktrace filter
         */
        if (stackTrace != null) {
            final String testframeworkPackage = TesterraListener.DEFAULT_PACKAGE;
            final String completeStackTrace = stackTrace.toString();
            if (completeStackTrace.contains(TesterraListener.PROJECT_PACKAGE) &&
                    ((!completeStackTrace.contains(testframeworkPackage))
                                    ||
                    (completeStackTrace.contains(testframeworkPackage) && completeStackTrace.contains("playground")))
                    ){
                errorFingerprint = completeStackTrace;
            }
        }
        return errorFingerprint;
    }

//    @Override
//    public TestStatusController.Status getStatus() {
//        return TestStatusController.Status.FAILED;
//    }
}
