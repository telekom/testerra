package eu.tsystems.mms.tic.testframework.layout.textlayout;

import org.opencv.core.Mat;

/**
 * Created by rnhb on 24.06.2015.
 */
public class TextLayoutScreenData {
    private Mat originalScreenshot;
    private Mat screenshotTextColor1;
    private Mat screenshotTextColor2;
    private Mat screenshotNoText;

    public Mat getOriginalScreenshot() {
        return originalScreenshot;
    }

    public void setOriginalScreenshot(Mat originalScreenshot) {
        this.originalScreenshot = originalScreenshot;
    }

    public Mat getScreenshotTextColor1() {
        return screenshotTextColor1;
    }

    public void setScreenshotTextColor1(Mat screenshotTextColor1) {
        this.screenshotTextColor1 = screenshotTextColor1;
    }

    public Mat getScreenshotTextColor2() {
        return screenshotTextColor2;
    }

    public void setScreenshotTextColor2(Mat screenshotTextColor2) {
        this.screenshotTextColor2 = screenshotTextColor2;
    }

    public Mat getScreenshotNoText() {
        return screenshotNoText;
    }

    public void setScreenshotNoText(Mat screenshotNoText) {
        this.screenshotNoText = screenshotNoText;
    }
}
