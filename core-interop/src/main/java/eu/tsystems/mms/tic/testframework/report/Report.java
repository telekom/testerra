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
    enum Mode {
        COPY,
        MOVE
    }
    enum Properties implements IProperties {
        BASE_DIR("dir", "testerra-report"),
        SCREENSHOTS_PREVIEW("screenshots.preview", true),
        NAME("name", null),
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
    Report addScreenshot(Screenshot screenshot, Mode mode);
    /**
     * Creates a screenshot, moves it files but doesn't add in to the current MethodContext
     */
    Screenshot provideScreenshot(File file, Mode mode);
    Report addVideo(Video video, Mode mode);
    Video provideVideo(File file, Mode mode);
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
