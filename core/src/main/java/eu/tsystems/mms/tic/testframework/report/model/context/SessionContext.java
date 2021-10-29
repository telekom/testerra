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
package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.model.NodeInfo;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;
import org.apache.commons.lang3.SerializationUtils;

public class SessionContext extends AbstractContext implements SynchronizableContext {
    private String remoteSessionId;
    private Video video;
    private NodeInfo nodeInfo;
    private String browserName;
    private String browserVersion;
    private Map<String, Object> capabilities;
    private final WebDriverRequest webDriverRequest;
    private final Queue<MethodContext> methodContexts = new ConcurrentLinkedQueue<>();

    public SessionContext(WebDriverRequest webDriverRequest) {
        try {
            this.webDriverRequest = webDriverRequest.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        this.name = webDriverRequest.getSessionKey();

//        this.provider = provider;
//
//        final MethodContext currentMethodContext = ExecutionContextController.getCurrentMethodContext();
//        if (currentMethodContext != null) {
//            this.name = currentMethodContext.getName() + "_";
//        } else {
//            this.name = "";
//        }
    }

    public WebDriverRequest getWebDriverRequest() {
        return this.webDriverRequest;
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

    public Optional<Video> getVideo() {
        return Optional.ofNullable(video);
    }

    public SessionContext setVideo(Video video) {
        this.video = video;
        return this;
    }

    public Optional<NodeInfo> getNodeInfo() {
        return Optional.ofNullable(nodeInfo);
    }

    public SessionContext setNodeInfo(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
        return this;
    }

    public String getActualBrowserName() {
        return browserName;
    }

    public SessionContext setActualBrowserName(String browserName) {
        this.browserName = browserName;
        return this;
    }

    public String getActualBrowserVersion() {
        return browserVersion;
    }

    public SessionContext setActualBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
        return this;
    }

    public Optional<Map<String, Object>> getCapabilities() {
        return Optional.ofNullable(capabilities);
    }

    public SessionContext setCapabilities(Map<String, Object> capabilities) {
        this.capabilities = capabilities;
        return this;
    }

    void addMethodContext(MethodContext methodContext) {
        this.methodContexts.add(methodContext);
    }

    public Stream<MethodContext> readMethodContexts() {
        return this.methodContexts.stream();
    }
}
