/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.report;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.context.Video;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import org.apache.commons.io.file.PathUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultReport implements Report, Loggable {

    private Path currentReportDirectory;
    private final String baseDir = Properties.BASE_DIR.asString();
    private final Path finalReportPath = Path.of(baseDir);
    private final Path tempReportDirectory;
    private final ConcurrentHashMap<Class<? extends Annotation>, AnnotationConverter> annotationConverters = new ConcurrentHashMap<>();

    public DefaultReport() {
        FileUtils fileUtils = new FileUtils();
        tempReportDirectory = fileUtils.createTempDir(baseDir);
        log().debug("Prepare report in " + tempReportDirectory.toAbsolutePath());
        currentReportDirectory = tempReportDirectory;
    }

    // TODO: Migrate to Path
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
            log().error("Could not add file", e);
        }
        return new File(directory, sourceFile.getName());
    }

    public Path finalizeReport() {
        try {
            if (Files.exists(finalReportPath)) {
                PathUtils.deleteDirectory(finalReportPath);
            }

            if (Files.exists(tempReportDirectory)) {
                log().debug("Temporary directory is {}", tempReportDirectory);
                PathUtils.copyDirectory(tempReportDirectory, finalReportPath);
                currentReportDirectory = finalReportPath;
                log().info("Report written to " + finalReportPath.toAbsolutePath());
            }

            try {
                PathUtils.deleteDirectory(tempReportDirectory);
            } catch (IOException e) {
                log().warn("Could not delete temporary directory {}", tempReportDirectory);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not copy report dir: " + e.getMessage(), e);
        }
        return finalReportPath;
    }

    private void addScreenshotFiles(Screenshot screenshot, FileMode fileMode) {
        Path screenshotsDirectory = getReportDirectory(SCREENSHOTS_FOLDER_NAME);
        if (screenshot.getScreenshotFile() != null) {
            screenshot.setFile(addFile(screenshot.getScreenshotFile(), screenshotsDirectory.toFile(), fileMode));
        }

        screenshot.getPageSourceFile().ifPresent(file -> {
            screenshot.setPageSourceFile(addFile(file, screenshotsDirectory.toFile(), fileMode));
        });
    }

    @Override
    public Report addScreenshot(Screenshot screenshot, FileMode fileMode) {
        addScreenshotFiles(screenshot, fileMode);
        return this;
    }

    @Override
    public Screenshot provideScreenshot(File file, FileMode fileMode) {
        Screenshot screenshot = new Screenshot(file);
        addScreenshotFiles(screenshot, fileMode);
        return screenshot;
    }

    @Override
    public Report addVideo(Video video, FileMode fileMode) {
        Path videoDirectory = getReportDirectory(VIDEO_FOLDER_NAME);
        video.setFile(addFile(video.getVideoFile(), videoDirectory.toFile(), fileMode));
        return this;
    }

    @Override
    public Video provideVideo(File file, FileMode fileMode) {
        Video video = new Video(file);
        addVideo(video, fileMode);
        return video;
    }

    /**
     * @return Final report directory defined by the user
     */
    public Path getReportDirectory() {
        return currentReportDirectory;
    }

    /**
     * @return Final report directory defined by the user
     */
    public Path getFinalReportDirectory() {
        return finalReportPath;
    }

    @Override
    public String getRelativePath(File file) {
        String absFilePath = file.getAbsolutePath();

        for (Path path : Arrays.asList(tempReportDirectory, finalReportPath)) {
            String absDirPath = path.toAbsolutePath().toString();
            if (absFilePath.startsWith(absDirPath)) {
                return absFilePath.replace(absDirPath, "");
            }
        }
        return absFilePath;
    }

    public void registerAnnotationConverter(Class<? extends Annotation> annotationClass, AnnotationConverter annotationExporter) {
        annotationConverters.put(annotationClass, annotationExporter);
    }

    public void unregisterAnnotationConverter(Class<? extends Annotation> annotationClass) {
        annotationConverters.remove(annotationClass);
    }

    public Optional<AnnotationConverter> getAnnotationConverter(Annotation annotation) {
        return Optional.ofNullable(annotationConverters.get(annotation.annotationType()));
    }
}
