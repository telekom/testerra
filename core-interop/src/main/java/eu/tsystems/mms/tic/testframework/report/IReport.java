package eu.tsystems.mms.tic.testframework.report;

import eu.tsystems.mms.tic.testframework.common.IProperties;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;

import java.io.File;

public interface IReport {
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
            return PropertyManager.parser.getDoubleProperty(toString(), defaultValue);
        }
        @Override
        public Long asLong() {
            return PropertyManager.parser.getLongProperty(toString(), defaultValue);
        }
        @Override
        public Boolean asBool() {
            return PropertyManager.parser.getBooleanProperty(toString(), defaultValue);
        }
        @Override
        public String asString() {
            return PropertyManager.parser.getProperty(toString(), defaultValue);
        }
    }
    /**
     * Adds a screenshot to the current MethodContext
     */
    IReport addScreenshot(Screenshot screenshot, Mode mode);
    /**
     * Creates a screenshot, moves it files but doesn't add in to the current MethodContext
     */
    Screenshot provideScreenshot(File file, Mode mode);
}
