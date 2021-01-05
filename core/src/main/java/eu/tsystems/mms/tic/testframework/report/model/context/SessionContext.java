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

import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import java.util.LinkedHashMap;
import java.util.Map;

public class SessionContext extends AbstractContext implements SynchronizableContext {
    private String sessionKey;
    private String provider;
    private final Map<String, Object> metaData = new LinkedHashMap<>();
    private String sessionId;

    public SessionContext(String sessionKey, String provider) {
        this.sessionKey = sessionKey;
        this.provider = provider;

        final MethodContext currentMethodContext = ExecutionContextController.getCurrentMethodContext();
        if (currentMethodContext != null) {
            this.name = currentMethodContext.getName() + "_";
        } else {
            this.name = "";
        }
        this.name += sessionKey;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public SessionContext setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
        return this;
    }

    public String getProvider() {
        return provider;
    }

    public SessionContext setProvider(String provider) {
        this.provider = provider;
        return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    public SessionContext setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    @Override
    public TestStatusController.Status getStatus() {
        /*
        Status is always null here. There is no context result status for sessions.
         */
        return null;
    }
}
