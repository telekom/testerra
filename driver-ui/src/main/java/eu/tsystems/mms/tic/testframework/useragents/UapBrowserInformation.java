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
package eu.tsystems.mms.tic.testframework.useragents;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import java.io.IOException;
import ua_parser.Client;
import ua_parser.Parser;

/**
 * Uap User Agent implementation
 * @author Mike Reiche
 */
public class UapBrowserInformation extends AbstractBrowserInformation implements Loggable {

    private static Parser userAgentAnalyzer;
    private Client client;

    public UapBrowserInformation() {
        if (userAgentAnalyzer == null) {
            try {
                userAgentAnalyzer = new Parser();
            } catch (IOException e) {
                log().error(e.getMessage(), e);
            }
        }
    }

    /**
     * Check the user agent and parse the browser name and version.
     */
    @Override
    public UapBrowserInformation parseUserAgent(String userAgent) {
        this.userAgent = userAgent;
        client = userAgentAnalyzer.parse(userAgent);
        return this;
    }

    @Override
    public String getBrowserName() {
        return client.userAgent.family;
    }

    @Override
    public String getBrowserVersion() {
        return client.userAgent.major;
    }
}
