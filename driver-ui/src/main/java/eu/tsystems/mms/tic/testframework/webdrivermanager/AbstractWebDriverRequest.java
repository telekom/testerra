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
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.DefaultCapabilityUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

public class AbstractWebDriverRequest implements WebDriverRequest, Loggable {

    private String sessionKey = DEFAULT_SESSION_KEY;
    private URL serverUrl;
    @Deprecated
    private DesiredCapabilities desiredCapabilities;

    private MutableCapabilities mutableCapabilities;

    private Capabilities capabilities;
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
        return this.getMutableCapabilities().getBrowserVersion();
    }

    public void setBrowserVersion(String browserVersion) {
        if (StringUtils.isNotBlank(browserVersion)) {
            this.getMutableCapabilities().setCapability(CapabilityType.BROWSER_VERSION, browserVersion);
        }
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

    /**
     * @deprecated Use {@link #setServerUrl(URL)} instead
     */
    public void setSeleniumServerUrl(URL url) {
        this.setServerUrl(url);
    }

    /**
     * @deprecated Use {@link #setServerUrl(URL)} instead
     */
    public void setSeleniumServerUrl(String url) throws MalformedURLException {
        this.setServerUrl(url);
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

    public Capabilities getCapabilities() {
        if (capabilities != null) {
            return this.capabilities;
        } else {
            return new MutableCapabilities();
        }
    }

    /**
     * This method is needed to update the current capabilities with merged capabilities.
     * Should use only internally.
     */
    public void setCapabilities(Capabilities capabilities) {
        this.capabilities = capabilities;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    /**
     * @deprecated Use {@link #getMutableCapabilities()} instead
     */
    @Deprecated
    public DesiredCapabilities getDesiredCapabilities() {
        if (this.desiredCapabilities == null) {
            this.desiredCapabilities = new DesiredCapabilities();
        }
        return desiredCapabilities;
    }

    public MutableCapabilities getMutableCapabilities() {
        if (this.mutableCapabilities == null) {
            this.mutableCapabilities = new MutableCapabilities();
        }
        return this.mutableCapabilities;
    }

    public void setPlatformName(String platformName) {
        try {
            if (StringUtils.isNotBlank(platformName)) {
                final Platform platform = Platform.fromString(platformName);
                this.getMutableCapabilities().setCapability(CapabilityType.PLATFORM_NAME, platform);
            }
        } catch (WebDriverException e) {
            log().warn("Trying to set invalid platform '{}' was ignored.", platformName);
        }
    }

    public String getPlatformName() {
        if (this.getMutableCapabilities().getPlatformName() != null) {
            return this.getMutableCapabilities().getPlatformName().toString();
        }
        return null;
    }

    /**
     * Cloning of DesiredCapabilites with SerializationUtils occurs org.apache.commons.lang3.SerializationException: IOException while reading or closing cloned object data
     * -> We have to backup the current caps and clone WebDriverRequest without caps. After cloning the original caps are added again.
     * -> org.apache.commons.lang3.SerializationUtils cannot used because not all objects are serializable (e.g. Proxy)
     * -> merge()-Method does not clone capability values like Maps (e.g. goog:chromeOptions, no deep copy)
     * --> used Gson lib
     */
    public AbstractWebDriverRequest clone() throws CloneNotSupportedException {
        AbstractWebDriverRequest clone = (AbstractWebDriverRequest) super.clone();

        DefaultCapabilityUtils capabilityUtils = new DefaultCapabilityUtils();
        if (this.desiredCapabilities != null) {
            Map<String, Object> clonedCaps = capabilityUtils.clone(this.desiredCapabilities.asMap());
            clone.desiredCapabilities = new DesiredCapabilities(clonedCaps);
        }
        if (this.capabilities != null) {
            Map<String, Object> clonedCaps = capabilityUtils.clone(this.capabilities.asMap());
            clone.capabilities = new MutableCapabilities(clonedCaps);
        }
        return clone;
    }
}
