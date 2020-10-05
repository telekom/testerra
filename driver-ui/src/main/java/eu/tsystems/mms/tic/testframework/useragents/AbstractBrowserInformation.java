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

package eu.tsystems.mms.tic.testframework.useragents;

/**
 * Wrapper class for browser test statistics. Reads browser informations from user agent string.
 */
public abstract class AbstractBrowserInformation implements BrowserInformation {

    /**
     * A collection of browser information with a list of contexts.
     */
//    private static Map<String, List<String>> browserInfoWithContexts = Collections.synchronizedMap(new HashMap<>(1));

    /**
     * The driver to log the commands through.
     */
    protected String userAgent;

    /**
     * Check the user agent and parse the browser name and version.
     */
    @Override
    public abstract BrowserInformation parseUserAgent(String userAgent);

//    /**
//     * Introduce a browserInfo Text with a steps string.
//     *
//     * @param browserInfo .
//     * @param context .
//     */
//    @Override
//    public synchronized void setBrowserInfoWithContext(final String browserInfo, final String context) {
//        if (browserInfoWithContexts.containsKey(browserInfo)) {
//            List<String> contexts = browserInfoWithContexts.get(browserInfo);
//            if (!contexts.contains(context)) {
//                contexts.add(context);
//            } else {
//                contexts.add(context + " " + DateUtils.getDate());
//            }
//        } else {
//            ArrayList<String> contexts = new ArrayList<String>(1);
//            contexts.add(context);
//            browserInfoWithContexts.put(browserInfo, contexts);
//        }
//    }
//
//    /**
//     * gives the steps List of a given browserinfo key
//     *
//     * @param key .
//     * @return steps List of the given browser
//     */
//    @Override
//    public synchronized List<String> getContext(String key) {
//        if (browserInfoWithContexts.containsKey(key)) {
//            return browserInfoWithContexts.get(key);
//        }
//        return null;
//
//    }
//
//    /**
//     * Checks whether there are more then one key in the Map
//     * , so there are different browser versions in the key list
//     *
//     * @return true if there are different browsers in the Map
//     */
//    @Override
//    public boolean detectDifferentBrowsers() {
//        return (browserInfoWithContexts.size() > 1);
//    }
//
//    /**
//     * checks whether there is any browser in the map
//     *
//     * @return true if there is no browser in the map
//     */
//    @Override
//    public boolean detectAnyBrowser() {
//        return (browserInfoWithContexts.size() <= 0);
//    }

    @Override
    public String getUserAgent() {
        return userAgent;
    }
}
