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

import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.Map;

public class DesktopWebDriverRequest extends WebDriverRequest {

    public Map<String, Object> sessionCapabilities = new HashMap<>();
    public DesiredCapabilities desiredCapabilities;
    public String seleniumServerURL;
    public String seleniumServerHost;
    public String seleniumServerPort;
    public WebDriverMode webDriverMode;


    @Override
    public String toString() {
        return "DesktopWebDriverRequest{" +
                "sessionCapabilities=" + sessionCapabilities +
                ", desiredCapabilities=" + desiredCapabilities +
                ", seleniumServerURL='" + seleniumServerURL + '\'' +
                ", seleniumServerHost='" + seleniumServerHost + '\'' +
                ", seleniumServerPort='" + seleniumServerPort + '\'' +
                ", webDriverMode=" + webDriverMode +
                ", browser=" + browser +
                ", sessionKey='" + sessionKey + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", browserVersion='" + browserVersion + '\'' +
                ", storedSessionId='" + storedSessionId + '\'' +
                ", storedExecutingNode=" + storedExecutingNode +
                '}';
    }
}
