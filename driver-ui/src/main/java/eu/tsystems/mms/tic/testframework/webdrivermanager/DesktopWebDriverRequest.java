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
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.common.IProperties;
import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.enums.Position;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;

import java.io.Serializable;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DesktopWebDriverRequest extends SeleniumWebDriverRequest implements Loggable, Serializable, PropertyManagerProvider {

    public enum Properties implements IProperties {
        BROWSER_MAXIMIZE("tt.browser.maximize", false),
        BROWSER_MAXIMIZE_POSITION("tt.browser.maximize.position", Position.CENTER.toString()),
        /** @deprecated Use the property {@link Properties.WINDOW_SIZE} instead */
        @Deprecated
        DISPLAY_RESOLUTION("tt.display.resolution", "1920x1080"),
        WINDOW_SIZE("tt.window.size", DISPLAY_RESOLUTION.asString());

        private final String property;
        private final Object defaultValue;

        Properties(String property, Object defaultValue) {
            this.property = property;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return this.property;
        }

        @Override
        public Object getDefault() {
            return this.defaultValue;
        }
    }

    private boolean maximize;
    private Position maximizePosition;
    private Dimension dimension;

    public DesktopWebDriverRequest() {
        super();
        this.maximize = PROPERTY_MANAGER.getBooleanProperty(Properties.BROWSER_MAXIMIZE, Properties.BROWSER_MAXIMIZE.getDefault());
        this.maximizePosition = Position.valueOf(PROPERTY_MANAGER.getProperty(Properties.BROWSER_MAXIMIZE_POSITION, Properties.BROWSER_MAXIMIZE_POSITION.getDefault()).toUpperCase());
        this.dimension = this.readDimensionFromString(Properties.WINDOW_SIZE.asString());
    }

    /**
     * @deprecated Use {@link #getServerUrl()}
     */
    public WebDriverMode getWebDriverMode() {
        return null;
    }

    /**
     * @deprecated Use {@link #setServerUrl(URL)} instead
     */
    public void setWebDriverMode(WebDriverMode webDriverMode) {

    }

    public void setMaximizeBrowser(boolean maximize) {
        this.maximize = maximize;
    }

    public boolean getMaximizeBrowser() {
        return this.maximize;
    }

    public void setMaximizePosition(Position position) {
        this.maximizePosition = position;
    }

    public Position getMaximizePosition() {
        return this.maximizePosition;
    }

    public Dimension getWindowSize() {
        return this.dimension;
    }

    public void setWindowSize(Dimension dimension) {
        this.dimension = dimension;
    }

    private Dimension readDimensionFromString(final String windowSizeProperty) {
        Dimension dimension = new Dimension(1920, 1080);
        if (StringUtils.isNotBlank(windowSizeProperty)) {
            Pattern pattern = Pattern.compile("(\\d+)x(\\d+)");
            Matcher matcher = pattern.matcher(windowSizeProperty);

            if (matcher.find()) {
                int width = Integer.parseInt(matcher.group(1));
                int height = Integer.parseInt(matcher.group(2));
                dimension = new Dimension(width, height);
            } else {
                log().error(String.format("Unable to parse property %s=%s, falling back to default: %s", Properties.WINDOW_SIZE, windowSizeProperty, dimension));
            }
        }
        return dimension;
    }

}
