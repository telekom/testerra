package eu.tsystems.mms.tic.testframework.pageobjects.report;

import eu.tsystems.mms.tic.testframework.interop.ScreenshotCollector;
import eu.tsystems.mms.tic.testframework.layout.ImageUtil;
import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.util.List;

public class CustomScreenshotGrabber implements ScreenshotCollector {

    @Override
    public List<Screenshot> takeScreenshots() {
        if (WebDriverManager.hasAnySessionActive()) {
            List<Screenshot> screenshots = UITestUtils.takeScreenshots(false);
            for (Screenshot screenshot : screenshots){
                String filename = screenshot.filename;
                Mat mat = ImageUtil.loadImage(filename);
                Point2D start = new Point2D(0, 0); // get start and endpoint from ByMulti
                Point2D end = new Point2D(0, 0);
                Core.rectangle(mat, start.toOpenCvPoint(), end.toOpenCvPoint(), new Scalar(255, 0, 0), 5);
                ImageUtil.writeImage(mat, filename);
            }
        }
        return null;
    }
}
