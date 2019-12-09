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
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import nl.basjes.parse.useragent.UserAgentAnalyzerDirect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for browser test statistics. Reads browser informations from user agent string.
 *
 * @author mrgi
 */
public class YauaaBrowserInformation implements BrowserInformation {

    private static final Logger LOGGER = LoggerFactory.getLogger(YauaaBrowserInformation.class);

    /**
     * The browser name of the test run.
     */
    private String browsername = "unknown browser";

    /**
     * The browser version of the test run.
     */
    private String browserversion = "(n.a.)";

    private static UserAgentAnalyzerDirect userAgentAnalyzer;

    static {
        try {
            // See https://github.com/nielsbasjes/yauaa for more fields if needed
            userAgentAnalyzer = UserAgentAnalyzer
                    .newBuilder()
                    .hideMatcherLoadStats()
                    .withField(UserAgent.AGENT_NAME)
                    .withField(UserAgent.AGENT_VERSION)
                    .withField(UserAgent.AGENT_VERSION_MAJOR)
                    .build();
        } catch (Exception e) {
            LOGGER.error("Could not load UserAgentParser", e);
        }
    }

    /**
     * Public constructor. Creates a new <code>TestRunBrowserInformation</code> object.
     *
     * @param userAgent The user agent string reading from browser.
     */
    public YauaaBrowserInformation(final String userAgent) {
        this.parseUserAgent(userAgent);
    }

    @Override
    public String getBrowserName() {
        return browsername;
    }

    @Override
    public String getBrowserVersion() {
        return browserversion;
    }

    /**
     * Check the user agent and parse the browser name and version.
     */
    public YauaaBrowserInformation parseUserAgent(String userAgent) {
        UserAgent agent = userAgentAnalyzer.parse(userAgent);
        browsername = agent.getValue(UserAgent.AGENT_NAME);
        browserversion = agent.getValue(UserAgent.AGENT_VERSION_MAJOR);
        return this;
    }

    @Override
    public String getUserAgent() {
        return browsername+browserversion;
    }

}
