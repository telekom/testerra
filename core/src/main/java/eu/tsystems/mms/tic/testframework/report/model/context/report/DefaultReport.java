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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.report.model.context.report;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.context.Video;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DefaultReport implements Report, Loggable {

    public static File REPORT_DIRECTORY;

    private File SCREENSHOTS_DIRECTORY;
    private File VIDEO_DIRECTORY;

    public DefaultReport() {
        FileUtils fileUtils = new FileUtils();
        String relativeReportDir = Properties.BASE_DIR.asString();
        REPORT_DIRECTORY = fileUtils.createTempDir(relativeReportDir);
        log().debug("Prepare report in " + REPORT_DIRECTORY.getAbsolutePath());

        SCREENSHOTS_DIRECTORY = new File(REPORT_DIRECTORY, SCREENSHOTS_FOLDER_NAME);
        VIDEO_DIRECTORY = new File(REPORT_DIRECTORY, VIDEO_FOLDER_NAME);

        SCREENSHOTS_DIRECTORY.mkdirs();
        VIDEO_DIRECTORY.mkdirs();
    }


    private void addFile(File sourceFile, File directory, Mode mode) {
        try {
            switch (mode) {
                case COPY:
                    FileUtils.copyFileToDirectory(sourceFile, directory, true);
                    break;
                case MOVE:
                    FileUtils.moveFileToDirectory(sourceFile, directory, true);
                    break;
            }
        } catch (IOException e) {
            log().error(e.getMessage());
        }
    }

    public File finalizeReport() {
        String relativeReportDirString = Properties.BASE_DIR.asString();
        File finalReportDirectory = new File(relativeReportDirString);
        try {
            if (finalReportDirectory.exists()) {
                FileUtils.deleteDirectory(finalReportDirectory);
            }

            if (REPORT_DIRECTORY.exists()) {
                FileUtils.moveDirectory(REPORT_DIRECTORY, finalReportDirectory);
                REPORT_DIRECTORY = finalReportDirectory;
                log().info("Report written to " + finalReportDirectory.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new TesterraRuntimeException("Could not move report dir: " + e.getMessage(), e);
        }
        return finalReportDirectory;
    }

    private void addScreenshot(Screenshot screenshot) {
        List<Screenshot> screenshots = new ArrayList<>();
        screenshots.add(screenshot);

        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        methodContext.addScreenshots(screenshots);
    }

    private void addScreenshotFiles(Screenshot screenshot, Mode mode) {
        if (screenshot.getScreenshotFile() != null) {
            addFile(screenshot.getScreenshotFile(), SCREENSHOTS_DIRECTORY, mode);
        }

        if (screenshot.getPageSourceFile() != null) {
            addFile(screenshot.getPageSourceFile(), SCREENSHOTS_DIRECTORY, mode);
        }
    }

    @Override
    public Report addScreenshot(Screenshot screenshot, Mode mode) {
        addScreenshotFiles(screenshot, mode);
        addScreenshot(screenshot);
        return this;
    }

    @Override
    public Screenshot provideScreenshot(File file, Mode mode) {
        Screenshot screenshot = new Screenshot(file, null);
        addScreenshotFiles(screenshot, mode);
        return screenshot;
    }

    @Override
    public Report addVideo(Video video, Mode mode) {
        addFile(video.getVideoFile(), VIDEO_DIRECTORY, mode);
        return this;
    }

    @Override
    public Video provideVideo(File file, Mode mode)  {
        Video video = new Video(file);
        addVideo(video, mode);
        return video;
    }

    /**
     * @return Final report directory defined by the user
     */
    public File getReportDirectory() {
        return REPORT_DIRECTORY;
    }
    /**
     * @return Final report directory defined by the user
     */
    public File getFinalReportDirectory() {
        return new File(Properties.BASE_DIR.asString());
    }
}
