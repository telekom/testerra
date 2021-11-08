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

import eu.tsystems.mms.tic.testframework.interop.TestEvidenceCollector;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionUtils;
import eu.tsystems.mms.tic.testframework.utils.SourceUtils;

import java.util.Optional;

public class ErrorContext {
    private transient Throwable throwable = null;
    private ScriptSource scriptSource;
    private ScriptSource executionObjectSource;
    private boolean optional;

    public ErrorContext(Throwable throwable, boolean optional) {
        this.throwable = throwable;
        this.optional = optional;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isNotOptional() {
        return !isOptional();
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
        if (this.throwable != null) {
            return ExecutionUtils.createStackTrace(this.throwable);
        } else {
            return null;
        }
    }
}
