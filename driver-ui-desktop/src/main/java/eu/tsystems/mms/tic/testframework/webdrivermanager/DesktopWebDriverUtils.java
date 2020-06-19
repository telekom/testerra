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
import eu.tsystems.mms.tic.testframework.utils.RESTUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DesktopWebDriverUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DesktopWebDriverUtils.class);

    private DesktopWebDriverUtils() {

    }

    public static NodeInfo getNodeInfo(DesktopWebDriverRequest desktopWebDriverRequest) {
        if (desktopWebDriverRequest.webDriverMode == WebDriverMode.local) {
            return new NodeInfo("local", 0);
        }

        final String host = desktopWebDriverRequest.seleniumServerHost;
        final String port = desktopWebDriverRequest.seleniumServerPort;
        String url = desktopWebDriverRequest.seleniumServerURL;

        final String sessionId = desktopWebDriverRequest.storedSessionId;

        url = url.replace("/wd/hub", "");

        /*
        grid3 mode
         */
        try {
            String nodeResponse = RESTUtils.requestGET(url + "/host/" + sessionId, 30 * 1000, String.class);
            JSONObject out = new JSONObject(nodeResponse);
            NodeInfo nodeInfo = new NodeInfo(out.getString("Name"), out.getInt("Port"));
            return nodeInfo;
        } catch (Exception e) {
            LOGGER.info("Could not get node info", e);
            return new NodeInfo(host, Integer.valueOf(port));
        }
    }

}
