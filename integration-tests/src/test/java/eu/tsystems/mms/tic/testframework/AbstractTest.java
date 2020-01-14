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
package eu.tsystems.mms.tic.testframework;

import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.useragents.ChromeConfig;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.BeforeMethod;

public abstract class AbstractTest extends TesterraTest {

    /**
     * Fixing up testing issues when /dev/shm becomes to small for test execution
     * this will fix "session deleted because of page crash"
     * https://stackoverflow.com/questions/53902507/unknown-error-session-deleted-because-of-page-crash-from-unknown-error-cannot
     */
    @BeforeMethod(alwaysRun = true)
    public void configureChromeOptions() {
        WebDriverManager.setUserAgentConfig(Browsers.chromeHeadless, new ChromeConfig() {
            @Override
            public void configure(ChromeOptions options) {
                options.addArguments("--disable-dev-shm-usage");
            }
        });
    }

    /**
     * Sets the unsupportedBrowser=true flag for the @Fails annotation
     */
    protected static void setUnsupportedBrowserFlag() {
        if (Browsers.phantomjs.equals(WebDriverManager.config().browser())) {
            System.setProperty("unsupportedBrowser", "true");
        }
    }

    static {
        setUnsupportedBrowserFlag();
    }
}
