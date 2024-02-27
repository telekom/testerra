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
package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.webdrivermanager.IWebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Provides some utilities for handling the Selenium {@link WebDriver}
 *
 * @author pele
 */
public final class WebDriverUtils {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverUtils.class);
    private static final ExecutionUtils executionUtils = Testerra.getInjector().getInstance(ExecutionUtils.class);

    /**
     * Timeout / maximum duration for Window Switching
     */
    private static final long WINDOW_SWITCH_MAX_DURATION_TIME_SECONDS = PropertyManager.getLongProperty(TesterraProperties.WEBDRIVER_WINDOW_SWITCH_MAX_DURATION, 3);

    /**
     * Hidden constructor.
     */
    private WebDriverUtils() {
    }

    public static boolean switchToWindow(Predicate<WebDriver> predicate) {
        return switchToWindow(WebDriverManager.getWebDriver(), predicate);
    }

    public static boolean switchToWindow(WebDriver mainWebDriver, Predicate<WebDriver> predicate) {
        String finalMainWindowHandle = executionUtils.getFailsafe(mainWebDriver::getWindowHandle).orElse("");

        return mainWebDriver.getWindowHandles().stream()
                .map(windowHandle -> mainWebDriver.switchTo().window(windowHandle))
                .anyMatch(webDriver -> {
                    boolean valid = predicate.test(webDriver);
                    if (!valid && !finalMainWindowHandle.isEmpty()) {
                        mainWebDriver.switchTo().window(finalMainWindowHandle);
                    }
                    return valid;
                });
    }

    /**
     * Finds a window by matching title and will switch to it.
     *
     * @param windowTitle {@link String} Title of the window to switch to.
     * @return true if switching was successful.
     * @deprecated Use {@link IWebDriverManager#switchToWindow(Predicate)} instead
     */
    public static boolean findWindowAndSwitchTo(final String windowTitle) {
        return findWindowAndSwitchTo(windowTitle, null);
    }

    /**
     * Finds a window by matching title and will switch to it.
     *
     * @param windowTitle {@link String} Title of the window to switch to.
     * @param driver {@link WebDriver} object or null (then the default session will be used)
     * @param excludeWindowHandles {@link String} array of window handles that should not be switch to.
     * @return true if switching was successful.
     * @deprecated Use {@link IWebDriverManager#switchToWindow(Predicate)} instead
     */
    public static boolean findWindowAndSwitchTo(final String windowTitle, WebDriver driver,
                                                final String... excludeWindowHandles) {
        return findWindowAndSwitchTo(windowTitle, null, driver, excludeWindowHandles);
    }

    /**
     * Finds a window by matching title and will switch to it.
     *
     * @param windowTitle {@link String}Title of the window to switch to.
     * @param driver {@link WebDriver} object or null (then the default session will be used)
     * @param excludeWindowHandles {@link String} array of window handles that should not be switch to.
     * @return true if switching was successful.
     * @deprecated Use {@link IWebDriverManager#switchToWindow(Predicate)} instead
     */
    public static boolean findWindowAndSwitchTo(final String windowTitle, final String urlContains, WebDriver driver,
                                                final String... excludeWindowHandles) {
        if (driver == null) {
            driver = WebDriverManager.getWebDriver();
        }

        final WebDriver wdRef = driver;
        final Timer timer = new Timer(500, WINDOW_SWITCH_MAX_DURATION_TIME_SECONDS * 1000);

        final String expectedCriteriaMsg =
                "\ntitle: " + windowTitle +
                        "\nurl  : " + urlContains;

        final ThrowablePackedResponse<Boolean> response = timer.executeSequence(new Timer.Sequence<Boolean>() {
            @Override
            public void run() {
                final Set<String> windowHandles = wdRef.getWindowHandles();
                LOGGER.info("Found " + windowHandles.size() + " opened windows");
                setPassState(false);
                setReturningObject(false);
                for (String windowHandle : windowHandles) {
                    boolean skip = false;
                    if (excludeWindowHandles != null) {
                        for (String excludeHandle : excludeWindowHandles) {
                            if (excludeHandle.equals(windowHandle)) {
                                // skip this one
                                skip = true;
                                break;
                            }
                        }
                    }

                    if (!skip) {
                        WebDriver window = wdRef.switchTo().window(windowHandle);

                        String realTitle = window.getTitle();
                        String url = window.getCurrentUrl();

                        boolean matchesTitle = windowTitle == null;
                        boolean matchesUrl = urlContains == null;
                        // search criteria
                        if (windowTitle != null) {
                            matchesTitle = realTitle.contains(windowTitle);
                        }
                        if (urlContains != null) {
                            matchesUrl = url.contains(urlContains);
                        }

                        if (matchesTitle && matchesUrl) {
                            setPassState(true);
                            setReturningObject(true);
                        } else {
                            LOGGER.debug("Window title -" + realTitle + "- does not match" + expectedCriteriaMsg);
                        }
                    } else {
                        LOGGER.debug("Skipping window handle " + windowHandle);
                    }
                }

            }
        });
        return response.getResponse();
    }

    /**
     * Finds an element by it's location using  Selenium features and utilities
     *
     * @param driver {@link WebDriver}
     * @param x int - coordinate
     * @param y int - coordinate
     * @return org.openqa.selenium.WebElement
     */
    public static WebElement findElementByLocation(WebDriver driver, int x, int y) {
        /*
        TODO: x and y are switched sometimes, i really dont know why this is. I have changed this multiple times... :(
         */
        Object out = JSUtils.executeScript(driver, "return document.elementFromPoint(" + x + "," + y + ");");
        if (out != null) {
            LOGGER.debug("Element from coordinates " + x + "," + y + " : " + out);
            if (out instanceof WebElement) {
                WebElement webElement = (WebElement) out;
                LOGGER.debug("WebElement at " + x + "," + y + " is found at " + webElement.getLocation().getX() + ","
                        + webElement.getLocation().getY());
                return webElement;
            } else {
                throw new SystemException("Could not get WebElement under " + x + "," + y);
            }
        }
        return null;
    }

    /**
     * Grabs cookies from WebDriver session and prepare them in a string like 'key=value;...'
     * to mimic the current cookie state of the session
     *
     * @param driver WebDriver the current driver
     * @return String
     */
    static String getCookieString(final WebDriver driver) {

        String cookieString = "";

        final Set<Cookie> cookieSet = driver.manage().getCookies();
        for (Cookie cookie : cookieSet) {
            cookieString += String.format("%s=%s;", cookie.getName(), cookie.getValue());
        }

        if (cookieString.length() > 0) {
            cookieString = StringUtils.removeEnd(cookieString, ";");
        }

        return cookieString;
    }

    /**
     * deletes all cookies of the given Webdriver
     *
     * @param driver Webdriver
     */
    static void deleteAllCookies(final WebDriver driver) {
        driver.manage().deleteAllCookies();
    }

    /**
     * @deprecated Use {@link JSUtils#getViewport(WebDriver)} instead
     */
    public static Rectangle getViewport(WebDriver webDriver) {
        return new JSUtils().getViewport(webDriver);
    }

    /**
     * Initialize a {@link WebDriverKeepAliveSequence} and runs it with {@link Timer} in given interval.
     * This will keep the {@link WebDriver} alive, when acting with another driver in same test or waiting for something to happen in main thread.
     * NOTE: Please use this method with care AND clean up your Sequence by calling {@link WebDriverUtils#removeKeepAliveForWebDriver(WebDriver)}
     *
     * @param driver {@link WebDriver}
     * @param intervalSleepTimeInSeconds int
     * @param durationInSeconds int
     * @return WebDriverKeepAliveSequence
     * @deprecated Use {@link IWebDriverManager#keepAlive(WebDriver, int, int)} instead
     */
    public static WebDriverKeepAliveSequence keepWebDriverAlive(final WebDriver driver, final int intervalSleepTimeInSeconds, int durationInSeconds) {

        if (durationInSeconds > WebDriverKeepAliveSequence.GLOBAL_KEEP_ALIVE_TIMEOUT_IN_SECONDS) {
            LOGGER.warn(String.format("Your duration %s is higher than the global duration. We will set your duration to %s to avoid abuse.",
                    durationInSeconds,
                    WebDriverKeepAliveSequence.GLOBAL_KEEP_ALIVE_TIMEOUT_IN_SECONDS));

            durationInSeconds = WebDriverKeepAliveSequence.GLOBAL_KEEP_ALIVE_TIMEOUT_IN_SECONDS;
        }

        final WebDriverKeepAliveSequence webDriverKeepAliveSequence = new WebDriverKeepAliveSequence(driver);
        new Timer(intervalSleepTimeInSeconds * 1000, durationInSeconds * 1000).executeSequenceThread(webDriverKeepAliveSequence);

        return webDriverKeepAliveSequence;
    }

    /**
     * Removes an active {@link WebDriverKeepAliveSequence} if present.
     *
     * @param driver {@link WebDriver} the current driver
     * @deprecated Use {@link IWebDriverManager#stopKeepingAlive(WebDriver)} instead
     */
    public static void removeKeepAliveForWebDriver(final WebDriver driver) {

        new WebDriverKeepAliveSequence().removeKeepAliveForDriver(driver);
    }

}
