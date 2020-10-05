/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
//package eu.tsystems.mms.tic.testframework.report.model;
//
//import nl.basjes.parse.useragent.UserAgent;
//import nl.basjes.parse.useragent.UserAgentAnalyzer;
//import nl.basjes.parse.useragent.UserAgentAnalyzerDirect;
//
///**
// * Wrapper class for browser test statistics. Reads browser informations from user agent string.
// *
// * @author mrgi
// */
//public class YauaaBrowserInformation extends AbstractBrowserInformation {
//
//    private static UserAgentAnalyzerDirect userAgentAnalyzer;
//    private UserAgent agent;
//
//    public YauaaBrowserInformation() {
//        // See https://github.com/nielsbasjes/yauaa for more fields if needed
//        userAgentAnalyzer = UserAgentAnalyzer
//            .newBuilder()
//            .hideMatcherLoadStats()
//            .withField(UserAgent.AGENT_NAME)
//            .withField(UserAgent.AGENT_VERSION)
//            .withField(UserAgent.AGENT_VERSION_MAJOR)
//            .build();
//    }
//
//    @Override
//    public String getBrowserName() {
//        return agent.getValue(UserAgent.AGENT_NAME);
//    }
//
//    @Override
//    public String getBrowserVersion() {
//        return agent.getValue(UserAgent.AGENT_VERSION_MAJOR);
//    }
//
//    /**
//     * Check the user agent and parse the browser name and version.
//     */
//    @Override
//    public YauaaBrowserInformation parseUserAgent(String userAgent) {
//        this.userAgent = userAgent;
//        agent = userAgentAnalyzer.parse(userAgent);
//        return this;
//    }
//}
