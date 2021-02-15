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

import eu.tsystems.mms.tic.testframework.model.NodeInfo;

public abstract class WebDriverRequest {
    /*
    Request
     */
    private String sessionKey;
    private String browser;
    private String browserVersion;
    private String baseUrl;

    /*
    Runtime
     */
    @Deprecated
    private String remoteSessionId;
    @Deprecated
    private NodeInfo storedExecutingNode;

    public boolean hasSessionKey() {
        return sessionKey != null && !sessionKey.trim().isEmpty();
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public WebDriverRequest setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
        return this;
    }

    public boolean hasBrowser() {
        return browser != null && !browser.trim().isEmpty();
    }

    public String getBrowser() {
        return browser;
    }

    public WebDriverRequest setBrowser(String browser) {
        this.browser = browser;
        return this;
    }

    public boolean hasBrowserVersion() {
        return browserVersion != null && !browserVersion.trim().isEmpty();
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public WebDriverRequest setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
        return this;
    }

    public boolean hasBaseUrl() {
        return baseUrl != null && !baseUrl.trim().isEmpty();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public WebDriverRequest setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    @Deprecated
    public NodeInfo getExecutingNode() {
        return storedExecutingNode;
    }

    @Deprecated
    public WebDriverRequest setExecutingNode(NodeInfo executingNode) {
        this.storedExecutingNode = executingNode;
        return this;
    }

    @Deprecated
    public String getRemoteSessionId() {
        return remoteSessionId;
    }

    @Deprecated
    public WebDriverRequest setRemoteSessionId(String sessionId) {
        this.remoteSessionId = sessionId;
        return this;
    }

    public void copyFrom(WebDriverRequest webDriverRequest) {
        this.browser = webDriverRequest.browser;
        this.sessionKey = webDriverRequest.sessionKey;
        this.baseUrl = webDriverRequest.baseUrl;
        this.browserVersion = webDriverRequest.browserVersion;
        this.remoteSessionId = webDriverRequest.remoteSessionId;
    }
}
