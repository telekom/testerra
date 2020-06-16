/*
 * Testerra
 *
 * (C) 2020, René Habermann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package eu.tsystems.mms.tic.testframework.layout.textlayout;

import org.opencv.core.Mat;

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
