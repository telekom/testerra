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
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.ExecutionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.events.WebDriverEventListener;

@Deprecated
public class EventLoggingEventDriverListener implements Loggable {

    private static final ExecutionUtils executionUtils = Testerra.getInjector().getInstance(ExecutionUtils.class);

    //    //    @Override
    public void beforeAlertAccept(WebDriver webDriver) {

    }

    //    //    @Override
    public void afterAlertAccept(WebDriver webDriver) {

    }

    //    //    @Override
    public void afterAlertDismiss(WebDriver webDriver) {

    }

    //    //    @Override
    public void beforeAlertDismiss(WebDriver webDriver) {

    }

    //    //    @Override
    public void beforeNavigateTo(String s, WebDriver webDriver) {
        log().info("Open " + s + " on " + webDriver);
    }

    //    //    @Override
    public void afterNavigateTo(String s, WebDriver webDriver) {

    }

    //    @Override
    public void beforeNavigateBack(WebDriver webDriver) {
        log().info("Back");
    }

    //    @Override
    public void afterNavigateBack(WebDriver webDriver) {

    }

    //    @Override
    public void beforeNavigateForward(WebDriver webDriver) {
        log().info("Forward");
    }

    //    @Override
    public void afterNavigateForward(WebDriver webDriver) {

    }

    //    @Override
    public void beforeNavigateRefresh(WebDriver webDriver) {
        log().info("Refresh");
    }

    //    @Override
    public void afterNavigateRefresh(WebDriver webDriver) {

    }

    //    @Override
    public void beforeFindBy(By by, WebElement webElement, WebDriver webDriver) {
    }

    //    @Override
    public void afterFindBy(By by, WebElement webElement, WebDriver webDriver) {
    }

    //    @Override
    public void beforeClickOn(WebElement webElement, WebDriver webDriver) {
    }

    //    @Override
    public void afterClickOn(WebElement webElement, WebDriver webDriver) {
    }

    //    @Override
    public void beforeChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {
    }

    //    @Override
    public void afterChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {

    }

    //    @Override
    public void beforeScript(String s, WebDriver webDriver) {

    }

    //    @Override
    public void afterScript(String s, WebDriver webDriver) {

    }

    //    @Override
    public void beforeSwitchToWindow(String s, WebDriver webDriver) {
    }

    //    @Override
    public void afterSwitchToWindow(String s, WebDriver webDriver) {
        log().info(
                String.format("Switched to window \"%s\" (%s)", executionUtils.getFailsafe(webDriver::getTitle).orElse("(na)"), executionUtils.getFailsafe(webDriver::getCurrentUrl).orElse("(na)")));
    }

    //    @Override
    public void onException(Throwable throwable, WebDriver webDriver) {

    }

    //    @Override
    public <X> void beforeGetScreenshotAs(OutputType<X> target) {

    }

    //    @Override
    public <X> void afterGetScreenshotAs(OutputType<X> target, X screenshot) {

    }

    //    @Override
    public void beforeGetText(WebElement element, WebDriver driver) {

    }

    //    @Override
    public void afterGetText(WebElement element, WebDriver driver, String text) {

    }
}
