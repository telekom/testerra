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
package eu.tsystems.mms.tic.testframework.interop;

import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.context.ScriptSource;
import eu.tsystems.mms.tic.testframework.report.model.context.Video;

import java.util.LinkedList;
import java.util.List;

public class CollectAssertionInfoArtefacts {

    private static final List<ScreenshotCollector> SCREENSHOT_COLLECTORS = new LinkedList<>();
    private static final List<VideoCollector> VIDEO_COLLECTORS = new LinkedList<>();
    private static final List<SourceCollector> SOURCE_COLLECTORS = new LinkedList<>();

    public static void registerScreenshotCollector(ScreenshotCollector screenshotCollector) {
        SCREENSHOT_COLLECTORS.add(screenshotCollector);
    }

    public static void registerVideoCollector(VideoCollector videoCollector) {
        VIDEO_COLLECTORS.add(videoCollector);
    }

    public static void registerSourceCollector(SourceCollector sourceCollector) {
        SOURCE_COLLECTORS.add(sourceCollector);
    }

    public static List<Screenshot> collectScreenshots() {
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
        if (VIDEO_COLLECTORS.isEmpty()) {
            return null;
        }

        List<Video> videos = new LinkedList<>();
        for (VideoCollector videoCollector : VIDEO_COLLECTORS) {
            List<Video> videos1 = videoCollector.getVideos();
            if (videos1 != null) {
                videos.addAll(videos1);
            }
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
}
