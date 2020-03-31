/*
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
package eu.tsystems.mms.tic.testframework.execution.worker.finish;

import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.pageobjects.POConfig;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class WebDriverSessionsAfterMethodWorker extends MethodWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverSessionHandler.class);

    private static final List<WebDriverSessionHandler> WEB_DRIVER_SESSION_HANDLERS = new ArrayList<>();

    public static void register(WebDriverSessionHandler webDriverSessionHandler) {
        WEB_DRIVER_SESSION_HANDLERS.add(webDriverSessionHandler);
    }

    @Override
    public void run() {
        if (WebDriverManager.hasAnySessionActive()) {
            if (WEB_DRIVER_SESSION_HANDLERS.size() > 0) {
                long threadID = Thread.currentThread().getId();
                List<WebDriver> drivers = WebDriverManager.getWebDriversFromThread(threadID);

                for (WebDriverSessionHandler webDriverSessionHandler : WEB_DRIVER_SESSION_HANDLERS) {
                    for (WebDriver driver : drivers) {
                        String webDriverSessionId = WebDriverManager.getSessionKeyFrom(driver);
                        String msg = "Executing handler for session -" + webDriverSessionId + "- : " + webDriverSessionHandler;
                        LOGGER.info(msg);

                        try {
                            webDriverSessionHandler.run(driver);
                        } catch (Throwable t) {
                            LOGGER.error("Error " + msg, t);
                        }
                    }
                }
            }
        }

        POConfig.removeThreadLocalUiElementTimeout();
    }
}
