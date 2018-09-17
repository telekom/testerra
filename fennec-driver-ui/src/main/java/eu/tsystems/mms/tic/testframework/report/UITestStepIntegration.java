/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.report;

import eu.tsystems.mms.tic.testframework.internal.Constants;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.internal.utils.ExceptionUtils;
import eu.tsystems.mms.tic.testframework.report.model.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepController;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStepEventListener;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.report.utils.ReportUtils;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.util.UUID;

public class UITestStepIntegration implements TestStepEventListener {

    private static boolean init = false;

    public static void init() {
        if (init) {
            return;
        }

        TestStepController.addEventListener(new UITestStepIntegration());
        init = true;
    }

    @Override
    public String getTestStepActionContext(LogMessage logMessage) {
        return ExceptionUtils.getPageContextFromThrowable(new Throwable());
    }

    public static String takeLoggingScreenshot(final TestStepController.OnExec onExec, final WebDriver driver) {
        if (!Flags.fennec_WEB_TAKE_ACTION_SCREENSHOTS) {
            return null;
        }

        final MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        if (methodContext != null && onExec != null) {
            final String screenshotsPath = ReportUtils.getScreenshotsPath();

            new File(screenshotsPath).mkdirs();
            final String imageName = UUID.randomUUID() + "_" + onExec.name() + ".png";
            final String screenShotFileInFS = screenshotsPath + imageName;
            final String reportScreenshotPath = "../../" + Constants.SCREENSHOTS_PATH + imageName;

            UITestUtils.takeWebDriverScreenshotToFile(driver, screenShotFileInFS);
            return reportScreenshotPath;
        }
        return null;
    }
}
