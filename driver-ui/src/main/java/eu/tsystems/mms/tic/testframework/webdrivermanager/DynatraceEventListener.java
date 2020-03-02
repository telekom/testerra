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

package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Listener for EventFirringWebDriver.
 *
 * @author pele
 */
public class DynatraceEventListener implements WebDriverEventListener {

    private static final Method addDynaTraceHeaderMethod;

    /**
     * The Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DynatraceEventListener.class);

    static {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        final Class<?> dtLoggerClass;
        try {
            dtLoggerClass = contextClassLoader.loadClass(
                    eu.tsystems.mms.tic.testframework.internal.Constants.DYNATRACE_LOGGER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new TesterraSystemException(
                    "Dynatrace Logging is activated but the tt.-browsermob-proxy module is not loaded.");
        }

        final String errorMessage = "Error initializing the DynaTrace Event Logger";
        try {
            addDynaTraceHeaderMethod = dtLoggerClass.getMethod("addDynaTraceHeader", String.class);
        } catch (NoSuchMethodException e) {
            throw new TesterraSystemException(errorMessage, e);
        }
    }

    private void callAddHeader(String alternateActionText) {
        if (addDynaTraceHeaderMethod != null) {
            try {
                addDynaTraceHeaderMethod.invoke(null, alternateActionText);
            } catch (IllegalAccessException e) {
                LOGGER.error("Could not invoke dt logger method", e);
            } catch (InvocationTargetException e) {
                LOGGER.error("Could not invoke dt logger method", e);
            }
        }
    }

    public DynatraceEventListener() {
        LOGGER.info(DynatraceEventListener.class.getSimpleName() + " active for this webdriver session");
    }

    @Override
    public void beforeAlertAccept(WebDriver webDriver) {

    }

    @Override
    public void afterAlertAccept(WebDriver webDriver) {

    }

    @Override
    public void afterAlertDismiss(WebDriver webDriver) {

    }

    @Override
    public void beforeAlertDismiss(WebDriver webDriver) {

    }

    @Override
    public void beforeNavigateTo(String url, WebDriver driver) {
        callAddHeader("NavigateTo_" + url);
    }

    @Override
    public void afterNavigateTo(String url, WebDriver driver) {

    }

    @Override
    public void beforeNavigateBack(WebDriver driver) {
        callAddHeader("NavigateBack");
    }

    @Override
    public void afterNavigateBack(WebDriver driver) {

    }

    @Override
    public void beforeNavigateForward(WebDriver driver) {
        callAddHeader("NavigateForward");
    }

    @Override
    public void afterNavigateForward(WebDriver driver) {

    }

    @Override
    public void beforeNavigateRefresh(WebDriver webDriver) {

    }

    @Override
    public void afterNavigateRefresh(WebDriver webDriver) {

    }

    @Override
    public void beforeFindBy(By by, WebElement element, WebDriver driver) {

    }

    @Override
    public void afterFindBy(By by, WebElement element, WebDriver driver) {

    }

    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        callAddHeader("ClickOn:" + element.toString());
    }

    @Override
    public void afterClickOn(WebElement element, WebDriver driver) {

    }

    @Override
    public void beforeChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {
        callAddHeader("ChangeValueOf:" + webElement.toString());
    }

    @Override
    public void afterChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {

    }

    @Override
    public void beforeScript(String script, WebDriver driver) {
        callAddHeader("ExecuteJS");
    }

    @Override
    public void afterScript(String script, WebDriver driver) {

    }

    @Override
    public void beforeSwitchToWindow(String s, WebDriver webDriver) {

    }

    @Override
    public void afterSwitchToWindow(String s, WebDriver webDriver) {

    }

    @Override
    public void onException(Throwable throwable, WebDriver driver) {

    }

    @Override
    public <X> void beforeGetScreenshotAs(OutputType<X> target) {

    }

    @Override
    public <X> void afterGetScreenshotAs(OutputType<X> target, X screenshot) {

    }

    @Override
    public void beforeGetText(WebElement element, WebDriver driver) {

    }

    @Override
    public void afterGetText(WebElement element, WebDriver driver, String text) {

    }
}
