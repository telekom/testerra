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

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScriptSource {

    public static class Line {

        private final String line;
        private final int lineNumber;

        public Line(String line, int lineNumber) {
            this.line = line;
            this.lineNumber = lineNumber;
        }

        public String getLine() {
            return line;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        @Override
        @Deprecated
        public String toString() {
            String msg = "";
//            if (mark) {
//                msg += "M ";
//            }
//            else {
                msg += "  ";
//            }
            msg += lineNumber + ": ";
            msg += line;
            return msg;
        }
    }

    /**
     * @deprecated Use {@link #readLines()} instead
     */
    public List<Line> lines = new LinkedList<>();
    private int markedLineNumber;

    private final String fileName;
    private final String methodName;

    public ScriptSource(String fileName, String methodName) {
        this.fileName = fileName;
        this.methodName = methodName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    @Deprecated
    public String toString() {
        return fileName + "#" + methodName + "\n" + lines.stream().map(Line::toString).collect(Collectors.joining("\n"));
    }

    public Stream<Line> readLines() {
        return this.lines.stream();
    }

    public ScriptSource addLine(Line line) {
        this.lines.add(line);
        return this;
    }

    public ScriptSource markLineNumber(int lineNumber) {
        this.markedLineNumber = lineNumber;
        return this;
    }

    public int getMarkedLineNumber() {
        return this.markedLineNumber;
    }
}
