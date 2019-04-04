package eu.tsystems.mms.tic.testframework.layout.extraction;

import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

/**
 * Created by rnhb on 15.01.2015.
 */
public class XetaMat implements XetaImage {

    private Mat mat;

    public XetaMat(Mat mat) {
        this.mat = mat;
    }

    @Override
    public Scalar getColorAt(int x, int y) {
        double[] doubles = mat.get(y, x);
        Scalar color = new Scalar(doubles);
        return color;
    }

    @Override
    public void setColorAt(int x, int y, Scalar color) {
        mat.put(x, y, color.val);
    }

    @Override
    public Point2D getSize() {
        return new Point2D(mat.width(), mat.height());
    }
}
