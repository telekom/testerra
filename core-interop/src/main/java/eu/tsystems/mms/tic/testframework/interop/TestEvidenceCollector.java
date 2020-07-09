/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.interop;

import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.context.ScriptSource;
import eu.tsystems.mms.tic.testframework.report.model.context.Video;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TestEvidenceCollector {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestEvidenceCollector.class);

    private static final List<ScreenshotCollector> SCREENSHOT_COLLECTORS = new LinkedList<>();
    private static final List<VideoCollector> VIDEO_COLLECTORS = new LinkedList<>();
    private static final List<SourceCollector> SOURCE_COLLECTORS = new LinkedList<>();

    public static void registerScreenshotCollector(ScreenshotCollector screenshotCollector) {
        LOGGER.debug("Register " + screenshotCollector);
        SCREENSHOT_COLLECTORS.add(screenshotCollector);
    }

    public static void registerVideoCollector(VideoCollector videoCollector) {
        LOGGER.debug("Register " + videoCollector);
        VIDEO_COLLECTORS.add(videoCollector);
    }

    public static void registerSourceCollector(SourceCollector sourceCollector) {
        LOGGER.debug("Register " + sourceCollector);
        SOURCE_COLLECTORS.add(sourceCollector);
    }

    public static List<Screenshot> collectScreenshots() {
        if (!Report.Properties.SCREENSHOTTER_ACTIVE.asBool()) {
            return null;
        }

        if (SCREENSHOT_COLLECTORS.isEmpty()) {
            return null;
        }

        List<Screenshot> screenshots = new LinkedList<>();
        for (ScreenshotCollector screenshotCollector : SCREENSHOT_COLLECTORS) {
            List<Screenshot> screenshots1 = screenshotCollector.takeScreenshots();
            if (screenshots1 != null) {
                screenshots.addAll(screenshots1);
            }
        }
        return screenshots;
    }

    public static List<Video> collectVideos() {
        if (!Report.Properties.SCREENCASTER_ACTIVE.asBool()) {
            return null;
        }

        if (VIDEO_COLLECTORS.isEmpty()) {
            return null;
        }

        List<Video> videos = new LinkedList<>();
        try {
            for (VideoCollector videoCollector : VIDEO_COLLECTORS) {
                List<Video> videos1 = videoCollector.getVideos();
                if (videos1 != null) {
                    videos.addAll(videos1);
                }
            }
        } catch (Throwable t) {
            LOGGER.warn("Collecting videos failed", t);
        }
        return videos;
    }

    public static ScriptSource getSourceFor(Throwable throwable) {
        if (SOURCE_COLLECTORS.isEmpty()) {
            return null;
        }

        for (SourceCollector sourceCollector : SOURCE_COLLECTORS) {
            ScriptSource source = sourceCollector.getSourceFor(throwable);
            if (source != null) {
                return source;
            }
        }
        return null;
    }

    public static void logInfo() {
        LOGGER.trace("Collectors: Screenshots=" + SCREENSHOT_COLLECTORS.size() + ", Sources=" + SOURCE_COLLECTORS.size() + ", Videos: " + VIDEO_COLLECTORS.size());
        LOGGER.debug("ScreenCaster enabled=" + Report.Properties.SCREENCASTER_ACTIVE.asBool() + ", Screenshotter enabled=" + Report.Properties.SCREENSHOTTER_ACTIVE.asBool());
    }

}
