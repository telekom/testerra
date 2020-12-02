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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated
public class Cause {
    private final String className;
    private final String message;
    private final List<String> stackTraceElements;
    private Cause cause;

    public Cause(Throwable throwable) {
        this.className = throwable.getClass().getName();
        this.message = throwable.getMessage();
        this.stackTraceElements = Arrays.stream(throwable.getStackTrace()).map(ste -> "at " + ste).collect(Collectors.toList());
        if ((throwable.getCause() != null) && (throwable.getCause() != throwable)) {
            this.cause = new Cause(throwable.getCause());
        }
    }

    public String getClassName() {
        return className;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getStackTraceElements() {
        return stackTraceElements;
    }

    public Cause getCause() {
        return this.cause;
    }

    @Override
    public String toString() {
        String s = className + ": " + message + "\n" + stackTraceElements.stream().collect(Collectors.joining("\n"));
        if (cause != null) {
            s += "\ncaused by: " + cause;
        }
        return s;
    }
}
