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
import java.util.stream.Stream;

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


    private File addFile(File sourceFile, File directory, FileMode fileMode) {
        try {
            switch (fileMode) {
                case COPY:
                    FileUtils.copyFileToDirectory(sourceFile, directory, true);
                    break;
                default:
                case MOVE:
                    FileUtils.moveFileToDirectory(sourceFile, directory, true);
                    break;
            }
        } catch (IOException e) {
            log().error(e.getMessage());
        }
        return new File(directory, sourceFile.getName());
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

    private void addScreenshotToMethodContext(Screenshot screenshot) {
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        methodContext.addScreenshots(Stream.of(screenshot));
    }

    private void addScreenshotFiles(Screenshot screenshot, FileMode fileMode) {
        if (screenshot.getScreenshotFile() != null) {
            screenshot.setFile(addFile(screenshot.getScreenshotFile(), SCREENSHOTS_DIRECTORY, fileMode));
        }

        if (screenshot.getPageSourceFile() != null) {
            screenshot.setPageSourceFile(addFile(screenshot.getPageSourceFile(), SCREENSHOTS_DIRECTORY, fileMode));
        }
    }

    @Override
    public Report addScreenshot(Screenshot screenshot, FileMode fileMode) {
        addScreenshotFiles(screenshot, fileMode);
        addScreenshotToMethodContext(screenshot);
        return this;
    }

    @Override
    public Screenshot provideScreenshot(File file, FileMode fileMode) {
        Screenshot screenshot = new Screenshot(file, null);
        addScreenshotFiles(screenshot, fileMode);
        return screenshot;
    }

    @Override
    public Report addVideo(Video video, FileMode fileMode) {
        video.setFile(addFile(video.getVideoFile(), VIDEO_DIRECTORY, fileMode));
        return this;
    }

    @Override
    public Video provideVideo(File file, FileMode fileMode)  {
        Video video = new Video(file);
        addVideo(video, fileMode);
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
