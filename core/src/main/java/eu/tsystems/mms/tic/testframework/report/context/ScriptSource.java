/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.report.context;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ScriptSource {

    public static class Line {

        public String line;
        public int lineNumber;
        public boolean mark = false;

        public Line(String line, int lineNumber, boolean mark) {
            this.line = line;
            this.lineNumber = lineNumber;
            this.mark = mark;
        }

        @Override
        public String toString() {
            String msg = "";
            if (mark) {
                msg += "M ";
            }
            else {
                msg += "  ";
            }
            msg += lineNumber + ": ";
            msg += line;
            return msg;
        }
    }

    public List<Line> lines = new LinkedList<>();

    public String fileName;
    public String methodName;

    public ScriptSource(String fileName, String methodName) {
        this.fileName = fileName;
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return fileName + "#" + methodName + "\n" + lines.stream().map(Line::toString).collect(Collectors.joining("\n"));
    }
}
