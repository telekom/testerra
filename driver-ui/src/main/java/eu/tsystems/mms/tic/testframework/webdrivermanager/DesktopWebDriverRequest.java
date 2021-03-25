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

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.enums.Position;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class DesktopWebDriverRequest extends SeleniumWebDriverRequest implements Loggable, Serializable {

    private WebDriverMode webDriverMode;
    private boolean maximize;
    private Position maximizePosition;

    public DesktopWebDriverRequest() {
        super();
        this.webDriverMode = WebDriverMode.valueOf(Testerra.Properties.WEBDRIVER_MODE.asString());
        this.maximize = PropertyManager.getBooleanProperty(TesterraProperties.BROWSER_MAXIMIZE, false);
        this.maximizePosition = Position.valueOf(PropertyManager.getProperty(TesterraProperties.BROWSER_MAXIMIZE_POSITION, Position.CENTER.toString()).toUpperCase());
        try {
            this.setSeleniumServerUrl(new URL(StringUtils.getFirstValidString(
                    Testerra.Properties.SELENIUM_SERVER_URL.asString(),
                    "http://" + Testerra.Properties.SELENIUM_SERVER_HOST.asString() + ":" + Testerra.Properties.SELENIUM_SERVER_PORT.asString() + "/wd/hub"
            )));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to retrieve default Selenium URL from properties", e);
        }
    }

    public WebDriverMode getWebDriverMode() {
        return webDriverMode;
    }

    public DesktopWebDriverRequest setWebDriverMode(WebDriverMode webDriverMode) {
        this.webDriverMode = webDriverMode;
        return this;
    }

    public DesktopWebDriverRequest setMaximizeBrowser(boolean maximize) {
        this.maximize = maximize;
        return this;
    }

    public boolean getMaximizeBrowser() {
        return this.maximize;
    }

    public DesktopWebDriverRequest setMaximizePosition(Position position) {
        this.maximizePosition = position;
        return this;
    }

    public Position getMaximizePosition() {
        return this.maximizePosition;
    }

}
