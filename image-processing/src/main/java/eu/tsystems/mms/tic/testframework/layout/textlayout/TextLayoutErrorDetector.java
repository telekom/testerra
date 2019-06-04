package eu.tsystems.mms.tic.testframework.layout.textlayout;

import com.google.common.base.Stopwatch;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.layout.ImageUtil;
import eu.tsystems.mms.tic.testframework.layout.OpenCvInitializer;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.util.List;

import static org.opencv.core.Core.add;
import static org.opencv.highgui.Highgui.imread;

/**
 * Created by rnhb on 19.06.2015.
 */
public class TextLayoutErrorDetector {

    static {
        OpenCvInitializer.init();
    }

    private String changeColorScriptTemplate = "var list = document.getElementsByTagName(\"*\");\n" +
            "for (var i = 0; i < list.length; i++) {\n" +
            "    list[i].style.color = '#';\n" +
            "}";

    private String opacityScript = "var list = document.getElementsByTagName(\"#\");\n" +
            "for (var i = 0; i < list.length; i++) {\n" +
            "    list[i].style.opacity = 0;\n" +
            "}";

    private String[] tagsContainingText;

    public TextLayoutErrorDetector() {
        tagsContainingText = new String[]{
                "a", "p", "h1", "h2", "h3",
                "button", "img", "p", "span"};
    }

    public TextLayoutErrorDetector(String[] tagsContainingText) {
        this.tagsContainingText = tagsContainingText;
    }

    class CaptureOriginal implements Runnable {

        private TextLayoutScreenData screenData;

        public CaptureOriginal(TextLayoutScreenData screenData) {
            this.screenData = screenData;
        }

        @Override
        public void run() {
            WebDriver driver = WebDriverManager.getWebDriver();
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            screenData.setOriginalScreenshot(ImageUtil.loadImage(screenshot.getPath()));
        }
    }

    class CaptureTextColored implements Runnable {

        private TextLayoutScreenData screenData;
        private String color;

        public CaptureTextColored(TextLayoutScreenData screenData, String color) {
            this.screenData = screenData;
            this.color = color;
        }

        @Override
        public void run() {
            WebDriver driver = WebDriverManager.getWebDriver();
            JSUtils.executeScript(driver, changeColorScriptTemplate.replace("#", color));
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Mat mat = ImageUtil.loadImage(screenshot.getPath());
            if (color.contains("0")) {
                screenData.setScreenshotTextColor2(mat);
            } else {
                screenData.setScreenshotTextColor1(mat);
            }
        }
    }

    class CaptureTransparentText implements Runnable {

        private TextLayoutScreenData screenData;

        public CaptureTransparentText(TextLayoutScreenData screenData) {
            this.screenData = screenData;
        }

        @Override
        public void run() {
            WebDriver driver = WebDriverManager.getWebDriver();
            for (String opaqueTag : tagsContainingText) {
                JSUtils.executeScript(driver, opacityScript.replace("#", opaqueTag));
            }
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            screenData.setScreenshotNoText(ImageUtil.loadImage(screenshot.getPath()));
        }
    }

    public TextLayoutScreenData captureScreenData(WebDriver driver) {
        TextLayoutScreenData textLayoutScreenData = new TextLayoutScreenData();
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        textLayoutScreenData.setOriginalScreenshot(ImageUtil.loadImage(screenshot.getPath()));

        // JSUtils.executeScript(driver, changeColorScriptTemplate.replace("#", "#338238"));
        JSUtils.executeScript(driver, changeColorScriptTemplate.replace("#", "#FFFFFF"));
        screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        textLayoutScreenData.setScreenshotTextColor1(ImageUtil.loadImage(screenshot.getPath()));

        //  JSUtils.executeScript(driver, changeColorScriptTemplate.replace("#", "#0F33A8"));
        JSUtils.executeScript(driver, changeColorScriptTemplate.replace("#", "#000000"));
        screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        textLayoutScreenData.setScreenshotTextColor2(ImageUtil.loadImage(screenshot.getPath()));

        for (String opaqueTag : tagsContainingText) {
            JSUtils.executeScript(driver, opacityScript.replace("#", opaqueTag));
        }
        screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        textLayoutScreenData.setScreenshotNoText(ImageUtil.loadImage(screenshot.getPath()));
        return textLayoutScreenData;
    }

    private Stopwatch stopWatch = Stopwatch.createUnstarted();
    private Stopwatch stopWatch2 = Stopwatch.createUnstarted();

    private void reportTime(String s) {
        if (s == null) {
            stopWatch.start();
            stopWatch2.start();
            return;
        }
        stopWatch2.reset();
        stopWatch2.start();
    }

    public void doSomething() {
        reportTime(null);

        PropertyManager.loadProperties("");
        WebDriverManager.setBaseURL("https://www.t-systems-mms.com");
        WebDriver driver = WebDriverManager.getWebDriver();


        /*
        Thread[] threads = new Thread[4];
        threads[0] = new Thread(new CaptureOriginal(screenData));
        threads[1] = new Thread(new CaptureTextColored(screenData, "#000000"));
        threads[2] = new Thread(new CaptureTextColored(screenData, "#FFFFFF"));
        threads[3] = new Thread(new CaptureTransparentText(screenData));

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        reportTime("After WD start ");

        TextLayoutScreenData screenData = captureScreenData(driver);
        reportTime("After screen data capture ");

        Mat diffMat = ImageUtil.getAbsoluteDifference(screenData.getScreenshotTextColor1(), screenData.getScreenshotTextColor2());
        ImageUtil.writeImage(diffMat, "diffMat.png");
        reportTime("After diff ");

        Mat greyScaleMat = ImageUtil.convertToGrey(screenData.getScreenshotNoText());
        ImageUtil.writeImage(greyScaleMat, "grey.png");
        reportTime("After grey ");

        Mat edgesFromGrey = applySimpleEdgeFilter(greyScaleMat);
        ImageUtil.writeImage(edgesFromGrey, "1edgesFromGrey.png");
        reportTime("After edge ");

        LineDetector lineDetector = new LineDetector();
        List<Line> lines = lineDetector.detectLines(edgesFromGrey);
        reportTime("After line detect");

        TextLayoutErrorReport report = TextLayoutErrorReport.createReport(lines, diffMat, "");
        ImageUtil.writeImage(report.getReportMat(), "reportMat.png");
        reportTime("After report ");
    }


    public Mat applySimpleEdgeFilter(Mat... mats) {
        Mat dst = ImageUtil.createSameSizeMat(mats[0]);

        for (Mat mat : mats) {
            Mat horizontalEdgeMat = filter(mat, horizontalEdgeFilter());
            Mat verticalEdgeMat = filter(mat, verticalEdgeFilter());
            add(horizontalEdgeMat, verticalEdgeMat, dst);
        }

        return dst;
    }

    private Mat filter(Mat src, Mat kernel) {
        Mat dst = ImageUtil.createSameSizeMat(src);
        Imgproc.filter2D(src, dst, -1, kernel);
        return dst;
    }

    private Mat getDiffMat(TextLayoutScreenData screenData) {
        Mat mat1 = screenData.getScreenshotTextColor1();
        Mat mat2 = screenData.getScreenshotTextColor2();
        Mat freshMat = ImageUtil.createMat(mat1.rows(), mat1.cols());
        for (int col = 0; col < mat1.cols(); col++) {
            for (int row = 0; row < mat1.rows(); row++) {
                double[] mat1Color = mat1.get(row, col);
                double[] mat2Color = mat2.get(row, col);
                boolean diff = false;
                for (int i = 0; i < mat1Color.length; i++) {
                    if (mat1Color[i] != mat2Color[i]) {
                        diff = true;
                        break;
                    }
                }
                if (diff) {
                    freshMat.put(row, col, 255d, 255d, 255d);
                }
            }
        }
        return freshMat;
    }

    private Mat getDiffMat(Mat mat1, Mat mat2) {
        Mat freshMat = ImageUtil.createMat(mat1.rows(), mat1.cols());
        for (int col = 0; col < mat1.cols(); col++) {
            for (int row = 0; row < mat1.rows(); row++) {
                double[] mat1Color = mat1.get(row, col);
                double[] mat2Color = mat2.get(row, col);
                boolean diff = false;
                for (int i = 0; i < mat1Color.length; i++) {
                    if (mat1Color[i] != mat2Color[i]) {
                        diff = true;
                        break;
                    }
                }
                if (diff) {
                    freshMat.put(row, col, new double[]{255d, 255d, 255d});
                }
            }
        }
        return freshMat;
    }

    private Mat horizontalEdgeFilter() {
        Mat mat = new Mat(3, 3, CvType.CV_8SC1);
        mat.put(0, 0, -2);
        mat.put(0, 1, -2);
        mat.put(0, 2, -2);
        mat.put(1, 0, 2);
        mat.put(1, 1, 2);
        mat.put(1, 2, 2);
        mat.put(2, 0, 0);
        mat.put(2, 1, 0);
        mat.put(2, 2, 0);
        return mat;
    }

    private Mat horizontalEdgeFilter9() {
        Mat mat = Mat.zeros(9, 9, CvType.CV_8SC1);
        int i = 1;
        mat.put(4, 0, -i);
        mat.put(4, 1, -i);
        mat.put(4, 2, -i * 2);
        mat.put(4, 3, -i * 3);
        mat.put(4, 4, -i * 5);
        mat.put(4, 5, -i * 3);
        mat.put(4, 6, -i * 2);
        mat.put(4, 7, -i);
        mat.put(4, 8, -i);
        mat.put(5, 0, i);
        mat.put(5, 1, i);
        mat.put(5, 2, i * 2);
        mat.put(5, 3, i * 3);
        mat.put(5, 4, i * 5);
        mat.put(5, 5, i * 3);
        mat.put(5, 6, i * 2);
        mat.put(5, 7, i);
        mat.put(5, 8, i);
        return mat;
    }

    private Mat verticalEdgeFilter() {
        Mat mat = new Mat(3, 3, CvType.CV_8SC1);
        mat.put(0, 0, -2);
        mat.put(0, 1, 2);
        mat.put(0, 2, 0);
        mat.put(1, 0, -2);
        mat.put(1, 1, 2);
        mat.put(1, 2, 0);
        mat.put(2, 0, -2);
        mat.put(2, 1, 2);
        mat.put(2, 2, 0);
        return mat;
    }

    public Mat verticalEdgeFilter9() {
        Mat mat = Mat.zeros(9, 9, CvType.CV_8SC1);
        int i = 1;
        mat.put(0, 4, -i);
        mat.put(1, 4, -i);
        mat.put(2, 4, -i * 2);
        mat.put(3, 4, -i * 3);
        mat.put(4, 4, -i * 5);
        mat.put(5, 4, -i * 3);
        mat.put(6, 4, -i * 2);
        mat.put(7, 4, -i);
        mat.put(8, 4, -i);
        mat.put(0, 5, i);
        mat.put(1, 5, i);
        mat.put(2, 5, i * 2);
        mat.put(3, 5, i * 3);
        mat.put(4, 5, i * 5);
        mat.put(5, 5, i * 3);
        mat.put(6, 5, i * 2);
        mat.put(7, 5, i);
        mat.put(8, 5, i);
        return mat;
    }

    public void doSomethingElse() {
        Mat src = imread("C:\\Users\\rnhb\\Documents\\noTextShot.png", 0);

        Mat filter = filter(src, horizontalEdgeFilter());
        Mat filter2 = filter(src, verticalEdgeFilter());

        Mat dst = ImageUtil.createSameSizeMat(src);
        add(filter, filter2, dst);


        ImageUtil.writeImage(src, "thresh/src.png");
        ImageUtil.writeImage(dst, "thresh/dst.png");

        Imgproc.threshold(filter, filter, 50, 255, Imgproc.THRESH_TOZERO);
        Imgproc.threshold(filter2, filter2, 50, 255, Imgproc.THRESH_TOZERO);
        ImageUtil.writeImage(filter, "thresh/filter.png");
        ImageUtil.writeImage(filter2, "thresh/filter2.png");

        add(filter, filter2, dst);
        ImageUtil.writeImage(dst, "thresh/dst2.png");

    }

    private void nonOpenCvFilter(Mat src, Mat dst, Mat kernel) {
        int kernelDimension = (int) Math.floor(kernel.rows() / 2d);
        for (int col = kernelDimension; col < src.cols() - kernelDimension; col++) {
            for (int row = kernelDimension; row < src.rows() - kernelDimension; row++) {
                double kernelSize = 0;
                double dstValue = 0;
                for (int kernelCol = 0; kernelCol < kernel.cols(); kernelCol++) {
                    for (int kernelRow = 0; kernelRow < kernel.rows(); kernelRow++) {
                        double kernelValue = kernel.get(kernelRow, kernelCol)[0];
                        kernelSize += kernelValue;
                        dstValue += kernelValue * src.get(
                                row + kernelRow - kernelDimension,
                                col + kernelCol - kernelDimension)[0];
                    }
                }
                dstValue /= kernelSize;
                dst.put(row, col, dstValue);
            }
        }
    }
}
