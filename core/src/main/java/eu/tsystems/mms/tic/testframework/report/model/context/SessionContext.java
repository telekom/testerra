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
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

public class SessionContext extends AbstractContext implements SynchronizableContext {
    private String remoteSessionId;
    private Video video;
    private String actualBrowserName;
    private String actualBrowserVersion;
    private WebDriverRequest webDriverRequest;
    private URL nodeUrl;

    public SessionContext(WebDriverRequest webDriverRequest) {
        setWebDriverRequest(webDriverRequest);
//        this.provider = provider;
//
//        final MethodContext currentMethodContext = ExecutionContextController.getCurrentMethodContext();
//        if (currentMethodContext != null) {
//            this.name = currentMethodContext.getName() + "_";
//        } else {
//            this.name = "";
//        }
    }

    public void setWebDriverRequest(WebDriverRequest webDriverRequest) {
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
        return this.name;
    }

    public SessionContext setSessionKey(String sessionKey) {
        this.name = sessionKey;
        return this;
    }

    public Optional<String> getRemoteSessionId() {
        return Optional.ofNullable(remoteSessionId);
    }

    public SessionContext setRemoteSessionId(String sessionId) {
        this.remoteSessionId = sessionId;
        return this;
    }

    @Override
    public TestStatusController.Status getStatus() {
        /*
        Status is always null here. There is no context result status for sessions.
         */
        return null;
    }

    public Optional<Video> getVideo() {
        return Optional.ofNullable(video);
    }

    public SessionContext setVideo(Video video) {
        this.video = video;
        return this;
    }

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
}
