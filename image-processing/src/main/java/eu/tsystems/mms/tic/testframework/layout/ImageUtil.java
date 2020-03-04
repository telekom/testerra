
package eu.tsystems.mms.tic.testframework.layout;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Range;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.LinkedList;
import java.util.List;

import static org.opencv.core.Core.absdiff;
import static org.opencv.core.Core.split;
import static org.opencv.highgui.Highgui.imread;
import static org.opencv.highgui.Highgui.imwrite;

/**
 * User: rnhb
 * Date: 21.05.14
 */
public class ImageUtil {

    public static final int MAT_TYPE = CvType.CV_8UC3;

    private ImageUtil() {

    }

    public static Mat addImages(Mat leftImage, Mat rightImage) {
        Mat outImage = new Mat(Math.max(leftImage.rows(), rightImage.rows()), leftImage.cols() + rightImage.cols(), MAT_TYPE);
        int baseImgCols = leftImage.cols();
        for (int x = 0; x < baseImgCols; x++) {
            for (int y = 0; y < leftImage.rows(); y++) {
                double[] doubles1 = leftImage.get(y, x);
                outImage.put(y, x, doubles1);
                double[] doubles = rightImage.get(y, x);
                if (doubles != null) {
                    outImage.put(y, x + baseImgCols, doubles);
                }
            }
        }
        return outImage;
    }

    public static Mat copyImage(Mat image) {
        return new Mat(image, new Range(0, image.rows()));
    }

    public static Mat loadImage(String name) {
        Mat image = imread(name, Highgui.CV_LOAD_IMAGE_COLOR);
        return image;
    }

    public static void writeImage(Mat image, String filename) {
        imwrite(filename, image);
    }

    public static Mat createMat(int sizeX, int sizeY) {
        return new Mat(sizeX, sizeY, MAT_TYPE);
    }

    public static Mat invert(Mat mat) {
        for (int col = 0; col < mat.cols(); col++) {
            for (int row = 0; row < mat.rows(); row++) {
                double[] color = mat.get(row, col);
                for (int i = 0; i < color.length; i++) {
                    color[i] = 255 - color[i];
                }
                mat.put(row, col, color);
            }
        }
        return mat;
    }

    public static Mat createSameSizeMat(Mat mat) {
        return new Mat(mat.rows(), mat.cols(), mat.depth());
    }

    public static List<Mat> splitChannels(Mat src) {
        List<Mat> splittedMats = new LinkedList<Mat>();
        split(src, splittedMats);
        return splittedMats;
    }

    public static Mat convertToGrey(Mat src) {
        Mat greyScaleMat = new Mat();
        Imgproc.cvtColor(src, greyScaleMat, Imgproc.COLOR_BGR2GRAY);
        return greyScaleMat;
    }

    public static Mat getAbsoluteDifference(Mat mat1, Mat mat2) {
        Mat diff = createSameSizeMat(mat1);
        absdiff(mat1, mat2, diff);
        return diff;
    }
}
