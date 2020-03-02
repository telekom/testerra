package eu.tsystems.mms.tic.testframework.layout.extraction;

import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import org.opencv.core.Scalar;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class XetaBufferedImage implements XetaImage {

    private BufferedImage bufferedImage;
    private WritableRaster raster;

    public XetaBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        raster = bufferedImage.getRaster();

    }

    @Override
    public Scalar getColorAt(int x, int y) {
        double[] doubles = null;
        doubles = raster.getPixel(x, y, doubles);
        return new Scalar(doubles);
    }

    @Override
    public void setColorAt(int x, int y, Scalar color) {
        raster.setPixel(x, y, color.val);
    }

    @Override
    public Point2D getSize() {
        return new Point2D(bufferedImage.getWidth(), bufferedImage.getHeight());
    }
}
