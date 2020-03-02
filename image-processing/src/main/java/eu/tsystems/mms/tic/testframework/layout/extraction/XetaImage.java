package eu.tsystems.mms.tic.testframework.layout.extraction;

import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import org.opencv.core.Scalar;

public interface XetaImage {
    Scalar getColorAt(int x, int y);

    void setColorAt(int x, int y, Scalar color);

    Point2D getSize();
}
