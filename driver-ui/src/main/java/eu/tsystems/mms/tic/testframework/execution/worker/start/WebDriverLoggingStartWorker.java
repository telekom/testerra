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
 package eu.tsystems.mms.tic.testframework.execution.worker.start;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.events.MethodStartEvent;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.useragents.BrowserInformation;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerUtils;
import java.util.List;
import org.openqa.selenium.WebDriver;

public class WebDriverLoggingStartWorker implements MethodStartEvent.Listener, Loggable {

    @Override
    @Subscribe
    public void onMethodStart(MethodStartEvent event) {
        if (event.getTestMethod().isTest()) {
            /*
             * Log already opened wd sessions
             */
            if (WebDriverManager.hasAnySessionActive()) {
                String executingSeleniumHost = WebDriverManager.getExecutingSeleniumHosts();
                if (executingSeleniumHost == null) {
                    executingSeleniumHost = "unknown";
                }

                String msg = "Already opened webdriver sessions on: ";
                if (executingSeleniumHost.contains("\n")) {
                    msg += "\n";
                }
                log().info(msg + executingSeleniumHost);

                // log browser
                long threadId = Thread.currentThread().getId();
                List<WebDriver> webDriversFromThread = WebDriverManager.getWebDriversFromThread(threadId);
                if (webDriversFromThread != null && webDriversFromThread.size() > 0) {
                    WebDriver driver = webDriversFromThread.get(0);
                    BrowserInformation browserInformation = WebDriverManagerUtils.getBrowserInformation(driver);
                    log().info(String.format("Use user agent: %s %s", browserInformation.getBrowserName(), browserInformation.getBrowserVersion()));
                }
            }
        }
    }
}
