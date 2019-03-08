/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.model.NodeInfo;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;

/**
 * Created by pele on 19.07.2017.
 */
public abstract class WebDriverRequest {

    /*
    Request
     */
    public String browser;
    public String sessionKey;
    public String baseUrl;
    public String browserVersion;
    public SessionContext sessionContext;

    /*
    Runtime
     */
    public String storedSessionId;
    public NodeInfo storedExecutingNode;
    public String storedVideoName;


    public void copyFrom(WebDriverRequest webDriverRequest) {
        this.browser = webDriverRequest.browser;
        this.sessionKey = webDriverRequest.sessionKey;
        this.baseUrl = webDriverRequest.baseUrl;
        this.browserVersion = webDriverRequest.browserVersion;
    }
}
