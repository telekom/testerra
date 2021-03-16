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
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.internal.Constants;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.internal.Viewport;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.webdrivermanager.AbstractWebDriverConfiguration;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverSessionsManager;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.sikuli.api.ScreenLocation;
import org.sikuli.api.ScreenRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class containing some util methods for tt.
 *
 * @author pele
 */
public class UITestUtils {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UITestUtils.class);

    /**
     * Error string for screenshots.
     */
    private static final String ERROR_TAKING_SCREENSHOT = "Error taking screenshot.";

    /**
     * A date format for files like screenshots.
     */
    private static final DateFormat FILES_DATE_FORMAT = new SimpleDateFormat("dd_MM_yyyy__HH_mm_ss");

    private UITestUtils() {

    }

    public static Screenshot takeScreenshot(final WebDriver driver, boolean intoReport) {
        Screenshot screenshot = takeScreenshot(driver, driver.getWindowHandle());

        if (intoReport) {
            if (screenshot != null) {
                ExecutionContextController.getCurrentMethodContext().addScreenshots(Stream.of(screenshot));
            }
        }

        return screenshot;
    }

    private static Screenshot takeScreenshot(WebDriver eventFiringWebDriver, String originalWindowHandle) {

        Optional<SessionContext> sessionContext = WebDriverSessionsManager.getSessionContext(eventFiringWebDriver);

        if (Browsers.htmlunit.equalsIgnoreCase(sessionContext.map(SessionContext::getWebDriverRequest).map(WebDriverRequest::getBrowser).orElse(null))) {
            LOGGER.warn("Not taking screenshot for " + Browsers.htmlunit);
            return null;
        }

        /*
         * Take the screenshot
         */
        if (eventFiringWebDriver != null) {
            try {
                FileUtils fileUtis = new FileUtils();
                final File screenShotTargetFile = fileUtis.createTempFileName("screenshot.png");
                final File sourceTargetFile = fileUtis.createTempFileName("pagesource.html");
                takeWebDriverScreenshotToFile(eventFiringWebDriver, screenShotTargetFile);

                // get page source (webdriver)
                String pageSource = eventFiringWebDriver.getPageSource();

                if (pageSource == null) {
                    LOGGER.error("getPageSource() returned nothing, skipping to add page source");
                } else {

                    // save page source to file
                    savePageSource(pageSource, sourceTargetFile);
                }

                Report report = TesterraListener.getReport();
                Screenshot screenshot = new Screenshot(screenShotTargetFile, sourceTargetFile);
                report.addScreenshot(screenshot, Report.FileMode.MOVE);

                Map<String, String> metaData = screenshot.getMetaData();

                /*
                get infos
                 */
                metaData.put(Screenshot.MetaData.SESSION_KEY, sessionContext.map(SessionContext::getSessionKey).orElse(null));
                metaData.put(Screenshot.MetaData.SESSION_CONTEXT_ID, sessionContext.map(SessionContext::getId).orElse(null));
                metaData.put(Screenshot.MetaData.TITLE, eventFiringWebDriver.getTitle());

                /*
                window and focus infos
                 */
                String window = "";
                String windowHandle = eventFiringWebDriver.getWindowHandle();
                if (originalWindowHandle != null) {
                    if (windowHandle.equals(originalWindowHandle)) {
                        metaData.put(Screenshot.MetaData.DRIVER_FOCUS, "true");
                    } else {
                        metaData.put(Screenshot.MetaData.DRIVER_FOCUS, "false");
                    }
                }
                Set<String> windowHandles = eventFiringWebDriver.getWindowHandles();
                if (windowHandles.size() < 2) {
                    window = "#1/1";
                } else {
                    String[] handleStrings = windowHandles.toArray(new String[0]);
                    for (int i = 0; i < handleStrings.length; i++) {
                        if (handleStrings[i].equals(windowHandle)) {
                            window = "#" + (i + 1) + "/" + handleStrings.length;
                        }
                    }
                }

                String currentUrl = eventFiringWebDriver.getCurrentUrl();
                metaData.put(Screenshot.MetaData.WINDOW, window);
                metaData.put(Screenshot.MetaData.URL, currentUrl);

                return screenshot;

            } catch (final Exception e) {
                LOGGER.error(ERROR_TAKING_SCREENSHOT, e);
            }
        } else {
            LOGGER.info("No screenshot was taken. WebDriver is not active.");
        }

        return null;
    }

    public static void takeWebDriverScreenshotToFile(WebDriver eventFiringWebDriver, File screenShotTargetFile) {
        WebDriver driver;
        if (eventFiringWebDriver instanceof EventFiringWebDriver) {
            driver = ((EventFiringWebDriver) eventFiringWebDriver).getWrappedDriver();
        } else {
            // just to be able to execute
            driver = eventFiringWebDriver;
        }

        /*
         * The ChromeDriver doesn't support Screenshots of a large Site currently (Selenium 2.44), only the viewport ist
         * captured. To allow full-page screenshots, we stitch several viewport-screenshots together.
         * If this is eventually supported by WebDriver, this special branch can be removed.
         */
        if (Browsers.ie.equalsIgnoreCase(WebDriverSessionsManager.getRequestedBrowser(eventFiringWebDriver).orElse(null))) {
            Viewport viewport = JSUtils.getViewport(driver);

            if (viewport.height > Constants.IE_SCREENSHOT_LIMIT) {
                LOGGER.warn("IE: Not taking screenshot because screen size is larger than height limit of " + Constants.IE_SCREENSHOT_LIMIT);
                return;
            }
        }

        // take screenshot
        makeSimpleScreenshot(driver, screenShotTargetFile);
    }

    private static void makeSimpleScreenshot(WebDriver driver, File screenShotTargetFile) {
        File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.moveFile(file, screenShotTargetFile);
        } catch (IOException e) {
            LOGGER.error("Error moving screenshot: " + e.getLocalizedMessage());
        }
    }

    /**
     * Utility to store a Screenshot at the specified location.
     *
     * @param image      BufferedImage
     * @param targetFile filePath with fileName
     */
    private static void saveBufferedImage(BufferedImage image, File targetFile) {
        try {
            ImageIO.write(image, "png", targetFile);
        } catch (final FileNotFoundException ex) {
            LoggerFactory.getLogger(UITestUtils.class).warn(
                    ("Screenshot file could not be written to file system: " + ex.toString()));
        } catch (final IOException ioe) {
            LoggerFactory.getLogger(UITestUtils.class).warn(ioe.toString());
        }
    }

    /**
     * Save page source to file.
     *
     * @param pageSource       page source.
     * @param sourceTargetFile target file.
     */
    private static void savePageSource(final String pageSource, final File sourceTargetFile) {
        try {
            final FileOutputStream fos = new FileOutputStream(sourceTargetFile);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos, "UTF-8");
            outputStreamWriter.write(pageSource);
            outputStreamWriter.close();
        } catch (final FileNotFoundException ex) {
            LoggerFactory.getLogger(UITestUtils.class).warn(
                    ("PageSource file could not be written to file system: " + ex.toString()));
        } catch (final IOException ioe) {
            LoggerFactory.getLogger(UITestUtils.class).warn(ioe.toString());
        }
    }

    public static void takeScreenshot(ScreenRegion screenRegion) {
        if (screenRegion != null) {
            LOGGER.info("Taking screenshot from desktop");
            final ScreenLocation upperLeftCorner = screenRegion.getUpperLeftCorner();
            final ScreenLocation lowerRightCorner = screenRegion.getLowerRightCorner();
            final BufferedImage screenshotImage = screenRegion.getScreen()
                    .getScreenshot(upperLeftCorner.getX(), upperLeftCorner.getY(), lowerRightCorner.getX(), lowerRightCorner.getY());

            try {
                File targetFile = File.createTempFile("Desktop_" + FILES_DATE_FORMAT.format(new Date()), "png");
                saveBufferedImage(screenshotImage, targetFile);
                Report report = TesterraListener.getReport();
                Screenshot screenshot = report.provideScreenshot(targetFile, Report.FileMode.MOVE);
                final MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
                if (methodContext != null) {
                    methodContext.addScreenshots(Stream.of(screenshot));
                }
            } catch (IOException e) {
                LOGGER.error("Could not take screenshot", e);
            }
        } else {
            LOGGER.error("Could not take native screenshot, screen region is missing");
        }
    }

    private static List<Screenshot> pTakeAllScreenshotsForSession(WebDriver driver) {

        final List<Screenshot> screenshots = new LinkedList<>();

        String originalWindowHandle = null;
        Set<String> windowHandles = null;
        if (driver != null) {
            // get actual window to switch back later
            try {
                originalWindowHandle = driver.getWindowHandle();
            } catch (Exception e) {
                LOGGER.error("Error getting actual window handle from driver", e);
            }
            // get all windows
            if (driver.getWindowHandles().size() > 0) {
                windowHandles = driver.getWindowHandles();
            }
        }

        if (windowHandles != null) {
            for (String windowHandle : windowHandles) {
                // switch to
                try {
                    driver.switchTo().window(windowHandle);
                    Screenshot screenshot = takeScreenshot(driver, originalWindowHandle);
                    if (screenshot != null) {
                        screenshots.add(screenshot);
                    }
                } catch (Exception e) {
                    LOGGER.error("Unable to switch to window " + windowHandle + " and take a screenshot", e);
                }
            }

            // switch back to original window handle
            try {
                driver.switchTo().window(originalWindowHandle);
            } catch (Exception e) {
                LOGGER.error("Unable to switch back to original window handle after taking all screenshots", e);
            }
        }

        return screenshots;
    }

    /**
     * initialize properties for a Perf test with default values if PERF_TEST is true.
     * all properties can be overwritten with the values in test.properties
     */
    public static void initializePerfTest() {
        boolean perfTest = PropertyManager.getBooleanProperty(TesterraProperties.PERF_TEST, false);
        if (perfTest) {
            Properties fileProperties = PropertyManager.getFileProperties();
            String value = fileProperties.getProperty(TesterraProperties.PERF_GENERATE_STATISTICS, "true");
            fileProperties.setProperty(TesterraProperties.PERF_GENERATE_STATISTICS, value);

            /* Override initial value of flag */
            boolean isPropertyActive = PropertyManager.getBooleanProperty(TesterraProperties.PERF_GENERATE_STATISTICS);
            Flags.GENERATE_PERF_STATISTICS = isPropertyActive;

            value = fileProperties.getProperty(TesterraProperties.CLOSE_WINDOWS_AFTER_TEST_METHODS, "false");
            fileProperties.setProperty(TesterraProperties.CLOSE_WINDOWS_AFTER_TEST_METHODS, value);

            // in case of perfTest reuse driver instances for dataprovider threads
            value = fileProperties.getProperty(TesterraProperties.REUSE_DATAPROVIDER_DRIVER_BY_THREAD, "true");
            fileProperties.setProperty(TesterraProperties.REUSE_DATAPROVIDER_DRIVER_BY_THREAD, value);

            /* Override initial value of flag */
            isPropertyActive = PropertyManager.getBooleanProperty(TesterraProperties.REUSE_DATAPROVIDER_DRIVER_BY_THREAD);
            Flags.REUSE_DATAPROVIDER_DRIVER_BY_THREAD = isPropertyActive;
        }
    }

    /**
     * Make screenshots from all open browser windows/selenium, webdriver instances.
     *
     * @return ScreenshotPaths.
     */
    public static List<Screenshot> takeScreenshots() {
        return takeScreenshots(true);
    }

    /**
     * Make screenshots from all open browser windows/selenium, webdriver instances.
     *
     * @param publishToReport True for publish directly into report.
     * @return ScreenshotPaths.
     */
    public static List<Screenshot> takeScreenshots(final boolean publishToReport) {
        List<Screenshot> allScreenshots = new LinkedList<>();
        takeScreenshotsFromThreadSessions().forEach(webDriverScreenshots -> {
            if (publishToReport) {
                ExecutionContextController.getCurrentMethodContext().addScreenshots(webDriverScreenshots.stream());
            }
            allScreenshots.addAll(webDriverScreenshots);
        });

        return allScreenshots;
    }

    /**
     * Take screenshots from all windows and store them into the info container.
     *
     * @return
     */
    private static Stream<List<Screenshot>> takeScreenshotsFromThreadSessions() {
        return WebDriverSessionsManager.getWebDriversFromCurrentThread().map(UITestUtils::pTakeAllScreenshotsForSession);
    }

}
