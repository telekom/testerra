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
package eu.tsystems.mms.tic.testframework.execution.worker.start;

import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerUtils;
import org.openqa.selenium.WebDriver;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Created by pele on 19.01.2017.
 */
public class WebDriverLoggingStartWorker extends MethodWorker {

    @Override
    public void run() {
        if (isTest()) {
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
                LOGGER.info(msg + executingSeleniumHost);

                // log browser
                long threadId = Thread.currentThread().getId();
                List<WebDriver> webDriversFromThread = WebDriverManager.getWebDriversFromThread(threadId);
                if (webDriversFromThread != null && webDriversFromThread.size() > 0) {
                    WebDriver driver = webDriversFromThread.get(0);
                    WebDriverManagerUtils.logUserAgent(driver);
                }
            }
        }
    }
}
