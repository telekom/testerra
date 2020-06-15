package eu.tsystems.mms.tic.testframework.layout.textlayout;

import eu.tsystems.mms.tic.testframework.layout.LayoutCheck;
import org.opencv.core.Mat;

import java.util.LinkedList;
import java.util.List;

public class LineDetector {

    private final int minLineLength = LayoutCheck.Properties.LAYOUTCHECK_TEXT_ERRORDETECTOR_MINIMAL_LINELENGTH.asLong().intValue();
    private final double minEdgeStrength = LayoutCheck.Properties.LAYOUTCHECK_TEXT_ERRORDETECTOR_MINIMAL_EDGESTRENGTH.asDouble();

    public List<Line> detectLines(Mat mat) {
        LinkedList<Line> lines = new LinkedList<Line>();
        for (int col = 0; col < mat.cols(); col++) {
            Mat aCol = mat.col(col);
            Line line = null;
            int i;
            for (i = 0; i < aCol.rows(); i++) {
                double value = aCol.get(i, 0)[0];
                if (value > minEdgeStrength) {
                    if (line == null) {
                        line = new Line(col, i);
                    }
                } else {
                    if (isLineRelevant(line, col, i)) {
                        lines.add(line);
                    }
                    line = null;
                }
            }
            if (isLineRelevant(line, col, i)) {
                // Could be a line that reaches the border
                lines.add(line);
            }
        }

        for (int row = 0; row < mat.rows(); row++) {
            Mat aRow = mat.row(row);
            Line line = null;
            int i;
            for (i = 0; i < aRow.cols(); i++) {
                double value = aRow.get(0, i)[0];
                if (value > minEdgeStrength) {
                    if (line == null) {
                        line = new Line(i, row);
                    }
                } else {
                    if (isLineRelevant(line, i, row)) {
                        lines.add(line);
                    }
                    line = null;
                }
            }
            if (isLineRelevant(line, i, row)) {
                // Could be a line that reaches the border
                lines.add(line);
            }
        }
        return lines;
    }

    private boolean isLineRelevant(Line line, int x, int y) {
        if (line == null) {
            return false;
        } else {
            if (line.distanceTo(x, y) >= minLineLength) {
                line.setEndPoint(x, y);
                return true;
            } else {
                // line too short
                return false;
            }
        }
    }
}
