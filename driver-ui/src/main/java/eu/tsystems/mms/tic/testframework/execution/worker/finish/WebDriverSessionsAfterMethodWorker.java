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
 package eu.tsystems.mms.tic.testframework.execution.worker.finish;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.WebDriver;

public class WebDriverSessionsAfterMethodWorker implements MethodEndEvent.Listener, Loggable {

    private static final List<WebDriverSessionHandler> WEB_DRIVER_SESSION_HANDLERS = new ArrayList<>();

    public static void register(WebDriverSessionHandler webDriverSessionHandler) {
        WEB_DRIVER_SESSION_HANDLERS.add(webDriverSessionHandler);
    }

    @Override
    @Subscribe
    public void onMethodEnd(MethodEndEvent event) {
        if (WebDriverManager.hasAnySessionActive()) {
            if (WEB_DRIVER_SESSION_HANDLERS.size() > 0) {
                long threadID = Thread.currentThread().getId();
                List<WebDriver> drivers = WebDriverManager.getWebDriversFromThread(threadID);

                for (WebDriverSessionHandler webDriverSessionHandler : WEB_DRIVER_SESSION_HANDLERS) {
                    for (WebDriver driver : drivers) {
                        String webDriverSessionId = WebDriverManager.getSessionKeyFrom(driver);
                        String msg = "Executing handler for session -" + webDriverSessionId + "- : " + webDriverSessionHandler;
                        log().info(msg);

                        try {
                            webDriverSessionHandler.run(driver);
                        } catch (Throwable t) {
                            log().error("Error " + msg, t);
                        }
                    }
                }
            }
        }
    }
}
