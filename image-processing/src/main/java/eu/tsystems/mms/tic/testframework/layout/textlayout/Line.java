package eu.tsystems.mms.tic.testframework.layout.textlayout;

import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class Line {
    private Point2D begin;
    private Point2D end;

    public Line(int x, int y) {
        this.begin = new Point2D(x, y);
    }

    public void setEndPoint(int x, int y) {
        end = new Point2D(x, y);
    }

    public double length() {
        return end.getEuclideanDistance(begin);
    }

    public int distanceTo(int x, int y) {
        return Math.abs(x - begin.x) + Math.abs(y - begin.y);
    }

    @Override
    public String toString() {
        return begin.toString() + (end == null ? "" : " to " + end.toString());
    }

    public void markIn(Mat mat) {
        Core.line(mat, begin.toOpenCvPoint(), end.toOpenCvPoint(), new Scalar(255, 100, 100), 1, Core.LINE_4, 0);
    }
}
