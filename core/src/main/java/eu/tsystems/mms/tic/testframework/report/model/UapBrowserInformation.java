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
 *     Peter Lehmann
 *     pele
 */
/*
 * Created on 05.12.2011
 *
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.report.model;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import ua_parser.Parser;
import ua_parser.Client;

import java.io.IOException;

/**
 * Uap User Agent implementation
 * @author Mike Reiche
 */
public abstract class UapBrowserInformation extends AbstractBrowserInformation implements Loggable {

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
