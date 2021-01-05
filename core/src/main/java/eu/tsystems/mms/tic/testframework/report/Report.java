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

import eu.tsystems.mms.tic.testframework.common.IProperties;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.context.Video;
import java.io.File;

public interface Report {
    String SCREENSHOTS_FOLDER_NAME = "screenshots";
    String VIDEO_FOLDER_NAME = "videos";
    String XML_FOLDER_NAME = "xml";
    enum FileMode {
        COPY,
        MOVE
    }
    enum Mode {
        ALWAYS,
        WHEN_FAILED
    }
    enum Properties implements IProperties {
        BASE_DIR("dir", "test-report"),
        SCREENSHOTS_PREVIEW("screenshots.preview", true),
        NAME("name", "Test report"),
        ACTIVATE_SOURCES("activate.sources", true),
        SCREENSHOTTER_ACTIVE("screenshotter.active", true),
        SCREENSHOT_ON_PAGELOAD("screenshot.on.pageload", false),
        SCREENCASTER_ACTIVE("screencaster.active", false),
        LIST_TESTS("tt.list.tests", false)
        ;
        private final String property;
        private Object defaultValue;

        Properties(String property, Object defaultValue) {
            this.property = property;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return String.format("tt.report.%s",property);
        }
        @Override
        public IProperties newDefault(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }
        @Override
        public Double asDouble() {
            return PropertyManager.getPropertiesParser().getDoubleProperty(toString(), defaultValue);
        }
        @Override
        public Long asLong() {
            return PropertyManager.getPropertiesParser().getLongProperty(toString(), defaultValue);
        }
        @Override
        public Boolean asBool() {
            return PropertyManager.getPropertiesParser().getBooleanProperty(toString(), defaultValue);
        }
        @Override
        public String asString() {
            return PropertyManager.getPropertiesParser().getProperty(toString(), defaultValue);
        }
    }
    /**
     * Adds a screenshot to the current MethodContext
     */
    Report addScreenshot(Screenshot screenshot, FileMode fileMode);
    /**
     * Creates a screenshot, moves it files but doesn't add in to the current MethodContext
     */
    Screenshot provideScreenshot(File file, FileMode fileMode);
    Report addVideo(Video video, FileMode fileMode);
    Video provideVideo(File file, FileMode fileMode);
    File finalizeReport();

    File getReportDirectory();

    /**
     * @param childName Child directory or file name
     * @return Final report sub directory defined by the user
     */
    default File getReportDirectory(String childName) {
        return new File(getReportDirectory(), childName);
    }
    File getFinalReportDirectory();
    default File getFinalReportDirectory(String childName) {
        return new File(getFinalReportDirectory(), childName);
    }
}
