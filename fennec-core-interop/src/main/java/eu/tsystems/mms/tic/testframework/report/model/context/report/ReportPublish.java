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
package eu.tsystems.mms.tic.testframework.report.model.context.report;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.fennecProperties;
import eu.tsystems.mms.tic.testframework.exceptions.fennecRuntimeException;
import eu.tsystems.mms.tic.testframework.exceptions.fennecSystemException;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.context.Video;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportPublish {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportPublish.class);

    /**
     * Frames dir.
     */
    public static final String FRAMES_DIR = "/frames";

    public static final String SCREENSHOTS_FOLDER_NAME = "screenshots";
    public static final String SCREENSHOTS_PATH = getReportDir() + SCREENSHOTS_FOLDER_NAME + "/";

    public static final String VIDEO_FOLDER_NAME = "videos/";
    public static final String VIDEO_PATH = getReportDir() + VIDEO_FOLDER_NAME;

    public static File reportDirectory;

    private static final String DEFAULT_REPORTDIR = "/fennec-report/";

    private static final String USER_DIR = "user.dir";

    static {
        pInit();
    }

    /**
     * Init.
     */
    private static void pInit() {
        /*
         * Init report dir.
         */
        getReportDir();
    }

    /**
     * Gets the absolute directory for the reports with "/" ending.
     *
     * @return the default directory to save the reports.
     */
    public static String getReportDir() {
        if (reportDirectory == null) {
            initializeReportDir();
        }
        return reportDirectory.getAbsolutePath() + File.separator;
    }

    public static File getReportDirectory() {
        if (!reportDirectory.exists()) {
            reportDirectory.mkdirs();
        }
        return reportDirectory;
    }

    public static String getVideoFolderName() {
        return VIDEO_FOLDER_NAME;
    }

    public static String getScreenshotsFolderName() {
        return SCREENSHOTS_FOLDER_NAME;
    }

    public static String getReporterXMLDir() {
        return getReportDir() + "xml";
    }

    public static File getScreenshotsFolder() {
        File file = new File(getReportDirectory(), getScreenshotsFolderName());
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getVideosFolder() {
        File file = new File(getReportDirectory(), getVideoFolderName());
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    private static void initializeReportDir() {
        String userDirectory = System.getProperty(USER_DIR);
        String relativeReportDir = PropertyManager.getProperty(fennecProperties.REPORTDIR, DEFAULT_REPORTDIR);

        Pattern pattern = Pattern.compile("\\d+");
        if (!(relativeReportDir.startsWith("/") || relativeReportDir.startsWith("\\"))) {
            relativeReportDir = File.separator + relativeReportDir;
        }
        if (!relativeReportDir.endsWith("/") || relativeReportDir.endsWith("\\")) {
            relativeReportDir = relativeReportDir + File.separator;
        }
        reportDirectory = new File(userDirectory + relativeReportDir + File.separator);

        if (Flags.fennec_REUSE_REPORTDIR) {
            /*
             * ReportDir Reuse mode.
             */

            // cleanup
            try {
                FileUtils.deleteDirectory(reportDirectory);
            } catch (IOException e) {
                throw new fennecRuntimeException("Could not clean report dir.", e);
            }
            if (!reportDirectory.mkdirs()) {
                throw new fennecSystemException(
                        "Error cleaning report dir: " + reportDirectory +
                                "\nCheck consoles or other directory and file accesses for locks.");
            }
        } else {
            /*
             * new numbered report dir mode
             */
            while (reportDirectory.exists()) {
                String[] split = relativeReportDir.split("/");
                int dirPosition = split.length - 1;
                String lastDirName = split[dirPosition];
                Matcher matcher = pattern.matcher(lastDirName);
                if (matcher.find()) {
                    // number exists
                    String number = lastDirName.substring(matcher.start(), matcher.end());
                    int newNumber = Integer.valueOf(number) + 1;
                    lastDirName = lastDirName.replace(number, newNumber + "");
                } else {
                    // number does not exist
                    lastDirName = lastDirName + "1";
                }

                split[dirPosition] = lastDirName;
                // Pfad zusammensetzen
                relativeReportDir = org.apache.commons.lang.StringUtils.join(split, File.separator);

                reportDirectory = new File(userDirectory + relativeReportDir + File.separator);
            }
            if (!reportDirectory.mkdirs()) {
                throw new fennecSystemException("Error creating report dir.");
            }
        }
    }

    public enum Mode {
        COPY,
        MOVE
    }

    public static Screenshot provideScreenshot(File screenshotFile, File sourceFileOrNull, Mode mode, List<String> infosOrNull) throws IOException {
        if (!screenshotFile.exists()) {
            LOGGER.error("Cannot provide screenshot: " + screenshotFile + " does not exist");
            return null;
        }
        if (sourceFileOrNull != null && !sourceFileOrNull.exists()) {
            LOGGER.warn("Cannot provide screenshot source: " + sourceFileOrNull+ " does not exist");
            sourceFileOrNull = null;
        }

        final File screenshotsFolder = getScreenshotsFolder();
        final Screenshot screenshot = new Screenshot();

        /*
        provide screenshot
         */
        final String screenshotFileExtension = FilenameUtils.getExtension(screenshotFile.getName());
        final String screenshotFileName = UUID.randomUUID() + "." + screenshotFileExtension;
        final File targetScreenshotFile = new File(screenshotsFolder, screenshotFileName);
        switch (mode) {
            case COPY:
                FileUtils.copyFile(screenshotFile, targetScreenshotFile, true);
                break;
            case MOVE:
                FileUtils.moveFile(screenshotFile, targetScreenshotFile);
                break;
        }
        screenshot.filename = screenshotFileName;

        /*
        provide source
         */
        if (sourceFileOrNull != null) {
            final String sourceFileName = screenshotFileName + ".html";
            final File targetSourceFile = new File(screenshotsFolder, sourceFileName);
            switch (mode) {
                case COPY:
                    FileUtils.copyFile(sourceFileOrNull, targetSourceFile, true);
                    break;
                case MOVE:
                    FileUtils.moveFile(sourceFileOrNull, targetSourceFile);
                    break;
            }
            screenshot.sourceFilename = sourceFileName;
        }

        screenshot.infos = infosOrNull;

        LOGGER.info("Provided screenshot " + screenshotFile + " as " + targetScreenshotFile);

        return screenshot;
    }

    public static Video provideVideo(File file, Mode mode) throws IOException {
        if (!file.exists()) {
            LOGGER.error("Cannot provide video: " + file + " does not exist");
            return null;
        }

        final String fileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getName());
        final File videosFolder = getVideosFolder();

        File targetFile = new File(videosFolder, fileName);
        switch (mode) {
            case COPY:
                FileUtils.copyFile(file, targetFile, true);
                break;
            case MOVE:
                FileUtils.moveFile(file, targetFile);
                break;
        }

        Video video = new Video();
        video.filename = fileName;

        LOGGER.info("Provided video " + file + " as " + targetFile);

        return video;
    }

}
