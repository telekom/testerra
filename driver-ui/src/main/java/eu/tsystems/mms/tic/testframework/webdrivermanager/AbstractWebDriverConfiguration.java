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

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public abstract class AbstractWebDriverConfiguration implements Serializable {
    private String browser;
    private String browserVersion;
    private URL baseUrl;
    private String platformName;

    public String getBrowser() {
        return browser;
    }

    public AbstractWebDriverConfiguration setBrowser(String browser) {
        this.browser = browser;
        return this;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public AbstractWebDriverConfiguration setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
        return this;
    }

    public Optional<URL> getBaseUrl() {
        return Optional.ofNullable(baseUrl);
    }

    public AbstractWebDriverConfiguration setBaseUrl(String baseUrl) throws MalformedURLException {
        this.baseUrl = new URL(baseUrl);
        return this;
    }

    public AbstractWebDriverConfiguration setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public void setPlatformName(String platformName) {
        if (StringUtils.isNotEmpty(platformName)) {
            this.platformName = platformName;
        }
    }

    public Optional<String> getPlatformName() {
        return Optional.ofNullable(this.platformName);
    }
}
