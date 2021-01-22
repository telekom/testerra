/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.pageobjects.listeners;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.exceptions.UiElementAssertionError;
import eu.tsystems.mms.tic.testframework.layout.ImageUtil;
import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import eu.tsystems.mms.tic.testframework.pageobjects.location.ByMulti;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.context.report.Report;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.io.File;

public class HighlightUiElementListener implements MethodEndEvent.Listener {

    private final Report report = new Report();

    @Override
    @Subscribe
    public void onMethodEnd(MethodEndEvent event) {
        if (!event.isFailed() || !event.getTestMethod().isTest()) return;

        Throwable throwable = event.getTestResult().getThrowable();
        if (!(throwable instanceof UiElementAssertionError)) return;

        UiElementAssertionError uiElementAssertionError = (UiElementAssertionError)throwable;
        if (!(uiElementAssertionError.getData().by instanceof ByMulti)) return;
        ByMulti by = (ByMulti)uiElementAssertionError.getData().by;

        Screenshot screenshot = UITestUtils.takeScreenshot(uiElementAssertionError.getData().webDriver, null, null);
        File screenshotFile = new File(report.getReportDirectory(Report.SCREENSHOTS_FOLDER_NAME), screenshot.filename);

        if (screenshotFile.exists()) {
            Mat mat = ImageUtil.loadImage(screenshotFile.getAbsolutePath());
            Point2D start = by.getStartPoint();
            Point2D end = by.getEndPoint();
            Core.rectangle(mat, start.toOpenCvPoint(), end.toOpenCvPoint(), new Scalar(255, 0, 0), 5);
            ImageUtil.writeImage(mat, screenshotFile.getAbsolutePath());
        }
    }
}
