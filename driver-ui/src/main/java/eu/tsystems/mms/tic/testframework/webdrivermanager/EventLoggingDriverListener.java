/*
 * Testerra
 *
 * (C) 2024, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.ExecutionUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverListener;

/**
 * Created on 2023-12-15
 *
 * @author mgn
 */
public class EventLoggingDriverListener implements WebDriverListener, Loggable {

    private static final ExecutionUtils executionUtils = Testerra.getInjector().getInstance(ExecutionUtils.class);

    @Override
    public void beforeGet(WebDriver driver, String url) {
        log().info("Open {} on {}", url, driver);
    }

    @Override
    public void beforeTo(WebDriver.Navigation navigation, String url) {
        log().info("Navigate to {}", url);
    }

    @Override
    public void beforeBack(WebDriver.Navigation navigation) {
        log().info("Back");
    }

    @Override
    public void beforeForward(WebDriver.Navigation navigation) {
        log().info("Forward");
    }

    @Override
    public void beforeRefresh(WebDriver.Navigation navigation) {
        log().info("Refresh");
    }

    @Override
    public void afterWindow(WebDriver.TargetLocator targetLocator, String nameOrHandle, WebDriver driver) {
        log().info(
                "Switched to window \"{}\" ({})",
                executionUtils.getFailsafe(driver::getTitle).orElse("(na)"),
                executionUtils.getFailsafe(driver::getCurrentUrl).orElse("(na)"));
    }

}
