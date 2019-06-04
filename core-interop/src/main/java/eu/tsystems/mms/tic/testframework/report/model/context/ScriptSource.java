package eu.tsystems.mms.tic.testframework.report.model.context;

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
