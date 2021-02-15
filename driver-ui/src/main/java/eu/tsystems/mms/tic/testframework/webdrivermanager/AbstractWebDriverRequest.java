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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public abstract class AbstractWebDriverRequest implements WebDriverRequest {
    /*
    Request
     */
    private String sessionKey;
    private String browser;
    private String browserVersion;
    private URL baseUrl;

    @Override
    public String getSessionKey() {
        return sessionKey;
    }

    public AbstractWebDriverRequest setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
        return this;
    }

    @Override
    public String getBrowser() {
        return browser;
    }

    public AbstractWebDriverRequest setBrowser(String browser) {
        this.browser = browser;
        return this;
    }

    @Override
    public String getBrowserVersion() {
        return browserVersion;
    }

    public AbstractWebDriverRequest setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
        return this;
    }

    @Override
    public Optional<URL> getBaseUrl() {
        return Optional.ofNullable(baseUrl);
    }

    public AbstractWebDriverRequest setBaseUrl(String baseUrl) throws MalformedURLException {
        this.baseUrl = new URL(baseUrl);
        return this;
    }

    public AbstractWebDriverRequest setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }
}
