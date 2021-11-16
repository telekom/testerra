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
package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.model.NodeInfo;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;

import java.net.URL;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

public class SessionContext extends AbstractContext {
    private Video video;
    private String actualBrowserName;
    private String actualBrowserVersion;
    private WebDriverRequest webDriverRequest;
    private URL nodeUrl;
    private final Queue<MethodContext> methodContexts = new ConcurrentLinkedQueue<>();
    private String remoteSessionId;

    public SessionContext(WebDriverRequest webDriverRequest) {
        try {
            setWebDriverRequest(webDriverRequest.clone());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        this.setName(webDriverRequest.getSessionKey());
    }

    private void setWebDriverRequest(WebDriverRequest webDriverRequest) {
        this.webDriverRequest = webDriverRequest;
        this.setSessionKey(webDriverRequest.getSessionKey());
    }

    public WebDriverRequest getWebDriverRequest() {
        return this.webDriverRequest;
    }

    @Override
    public String getName() {
        return this.getSessionKey();
    }

    public String getSessionKey() {
        return this.getName();
    }

    public SessionContext setSessionKey(String sessionKey) {
        this.setName(sessionKey);
        return this;
    }

    public Optional<String> getRemoteSessionId() {
        return Optional.ofNullable(this.remoteSessionId);
    }

    public SessionContext setRemoteSessionId(String sessionId) {
        this.remoteSessionId = sessionId;
        return this;
    }

    public Optional<Video> getVideo() {
        return Optional.ofNullable(video);
    }

    public SessionContext setVideo(Video video) {
        this.video = video;
        return this;
    }

    /**
     * @deprecated Use {@link #getNodeUrl()} instead
     */
    @Deprecated
    public Optional<NodeInfo> getNodeInfo() {
        return getNodeUrl().map(NodeInfo::new);
    }

    public void setNodeUrl(URL url) {
        this.nodeUrl = url;
    }

    public Optional<URL> getNodeUrl() {
        return Optional.ofNullable(nodeUrl);
    }

    public Optional<String> getActualBrowserName() {
        return Optional.ofNullable(actualBrowserName);
    }

    public void setActualBrowserName(String browserName) {
        this.actualBrowserName = browserName;
    }

    public Optional<String> getActualBrowserVersion() {
        return Optional.ofNullable(actualBrowserVersion);
    }

    public  void setActualBrowserVersion(String browserVersion) {
        this.actualBrowserVersion = browserVersion;
    }

    void addMethodContext(MethodContext methodContext) {
        this.methodContexts.add(methodContext);
    }

    public Stream<MethodContext> readMethodContexts() {
        return this.methodContexts.stream();
    }
}
