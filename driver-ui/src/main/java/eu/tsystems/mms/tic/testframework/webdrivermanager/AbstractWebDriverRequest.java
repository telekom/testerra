/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import org.openqa.selenium.remote.DesiredCapabilities;

public class AbstractWebDriverRequest implements Serializable, WebDriverRequest {

    private String sessionKey = DEFAULT_SESSION_KEY;
    private URL serverUrl;
    private DesiredCapabilities desiredCapabilities;
    private boolean shutdownAfterTest = false;
    private boolean shutdownAfterTestFailed = false;
    private boolean shutdownAfterExecution = true;
    private String browserName;

    public AbstractWebDriverRequest() {
        setShutdownAfterTest(PropertyManager.getBooleanProperty(TesterraProperties.CLOSE_WINDOWS_AFTER_TEST_METHODS, true));
        setShutdownAfterTestFailed(PropertyManager.getBooleanProperty(TesterraProperties.CLOSE_WINDOWS_ON_FAILURE, true));

        // leave all windows open when this condition is true (except you call forceShutdown)
        if (PropertyManager.getBooleanProperty(TesterraProperties.ON_STATE_TESTFAILED_SKIP_SHUTDOWN, false)) {
            setShutdownAfterTestFailed(false);
        }
    }

    public String getBrowser() {
        return this.browserName;
    }

    public void setBrowser(String browser) {
        this.browserName = browser;
    }

    public String getBrowserVersion() {
        return getDesiredCapabilities().getVersion();
    }

    public void setBrowserVersion(String browserVersion) {
        this.getDesiredCapabilities().setVersion(browserVersion);
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setShutdownAfterTest(boolean shutdownAfterTest) {
        this.shutdownAfterTest = shutdownAfterTest;
    }

    public void setShutdownAfterTestFailed(boolean shutdownAfterTestFailed) {
        this.shutdownAfterTestFailed = shutdownAfterTestFailed;
    }

    public void setShutdownAfterExecution(boolean shutdownAfterExecution) {
        this.shutdownAfterExecution = shutdownAfterExecution;
    }

    @Override
    public Optional<URL> getServerUrl() {
        return Optional.ofNullable(this.serverUrl);
    }

    public void setServerUrl(String url) throws MalformedURLException {
        setServerUrl(new URL(url));
    }

    public void setServerUrl(URL url) {
        this.serverUrl = url;
    }

    @Override
    public boolean getShutdownAfterTest() {
        return shutdownAfterTest;
    }

    @Override
    public boolean getShutdownAfterTestFailed() {
        return shutdownAfterTestFailed;
    }

    @Override
    public boolean getShutdownAfterExecution() {
        return shutdownAfterExecution;
    }

    @Override
    public Map<String, Object> getCapabilities() {
        return getDesiredCapabilities().asMap();
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public DesiredCapabilities getDesiredCapabilities() {
        if (this.desiredCapabilities == null) {
            this.desiredCapabilities = new DesiredCapabilities();
        }
        return desiredCapabilities;
    }
}
