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
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.model.context.AbstractContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.IExecutionContextController;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import java.util.Collection;
import java.util.stream.Collectors;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.sikuli.api.ScreenLocation;
import org.sikuli.api.ScreenRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.imageio.ImageIO;
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

/**
 * Helper class containing some util methods for tt.
 *
 * @author pele
 */
public class UITestUtils implements WebDriverManagerProvider {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UITestUtils.class);
    private static final ExecutionUtils executionUtils = Testerra.getInjector().getInstance(ExecutionUtils.class);

    /**
     * A date format for files like screenshots.
     */
    private static final DateFormat FILES_DATE_FORMAT = new SimpleDateFormat("dd_MM_yyyy__HH_mm_ss");
    private static final Report report = Testerra.getInjector().getInstance(Report.class);
    private static final IExecutionContextController executionContextController = Testerra.getInjector().getInstance(IExecutionContextController.class);
    private static final int IE_SCREENSHOT_LIMIT = 1200;

    @Deprecated
    private UITestUtils() {

    }

    public static Screenshot takeScreenshot(final WebDriver driver, boolean intoReport) {
        Screenshot screenshot = takeScreenshot(driver, driver.getWindowHandle());

        if (intoReport) {
            IExecutionContextController executionContextController = Testerra.getInjector().getInstance(IExecutionContextController.class);
            executionContextController.getCurrentMethodContext().ifPresent(methodContext -> {
                methodContext.addScreenshot(screenshot);
                report.addScreenshot(screenshot, Report.FileMode.MOVE);
            });
        }

        return screenshot;
    }

    public static void takeScreenshot(WebDriver webDriver, Screenshot screenshot) {
        Optional<SessionContext> sessionContext = WEB_DRIVER_MANAGER.getSessionContext(webDriver);
        File screenshotFile = screenshot.getScreenshotFile();
        takeWebDriverScreenshotToFile(webDriver, screenshotFile);

        // get page source (webdriver)
        executionUtils.getFailsafe(webDriver::getPageSource).ifPresent(pageSource -> {
            savePageSource(pageSource, screenshot.createPageSourceFile());
        });

        Map<String, String> metaData = screenshot.getMetaData();
        sessionContext.map(SessionContext::getSessionKey).ifPresent(s -> metaData.put(Screenshot.MetaData.SESSION_KEY, s));
        sessionContext.map(SessionContext::getId).ifPresent(s -> metaData.put(Screenshot.MetaData.SESSION_CONTEXT_ID, s));
        executionUtils.getFailsafe(webDriver::getTitle).ifPresent(s -> metaData.put(Screenshot.MetaData.TITLE, s));
        executionUtils.getFailsafe(webDriver::getCurrentUrl).ifPresent(s -> metaData.put(Screenshot.MetaData.URL, s));

        /*
        window and focus infos
         */
        String window = "";
        String windowHandle = executionUtils.getFailsafe(webDriver::getWindowHandle).orElse("");
        Set<String> windowHandles = webDriver.getWindowHandles();
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
        metaData.put(Screenshot.MetaData.WINDOW, window);
    }

    private static Screenshot takeScreenshot(WebDriver eventFiringWebDriver, String originalWindowHandle) {
        Screenshot screenshot = new Screenshot();
        takeScreenshot(eventFiringWebDriver, screenshot);

        String windowHandle = eventFiringWebDriver.getWindowHandle();
        if (originalWindowHandle != null) {
            if (windowHandle.equals(originalWindowHandle)) {
                screenshot.getMetaData().put(Screenshot.MetaData.DRIVER_FOCUS, "true");
            } else {
                screenshot.getMetaData().put(Screenshot.MetaData.DRIVER_FOCUS, "false");
            }
        }
        return screenshot;
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
        if (Browsers.ie.equalsIgnoreCase(WEB_DRIVER_MANAGER.getRequestedBrowser(eventFiringWebDriver).orElse(null))) {
            Rectangle viewport = new JSUtils().getViewport(driver);

            if (viewport.height > IE_SCREENSHOT_LIMIT) {
                LOGGER.warn("IE: Not taking screenshot because screen size is larger than height limit of " + IE_SCREENSHOT_LIMIT);
                return;
            }
        }

        // take screenshot
        makeSimpleScreenshot(driver, screenShotTargetFile);
    }

    private static void makeSimpleScreenshot(WebDriver driver, File screenShotTargetFile) {
        try {
            File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.moveFile(file, screenShotTargetFile);
        } catch (Exception e) {
            LOGGER.error("Unable to take screenshot to file", e);
        }
    }

    /**
     * Utility to store a Screenshot at the specified location.
     *
     * @param image BufferedImage
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
     * @param pageSource page source.
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

            final String filename = "Desktop_" + FILES_DATE_FORMAT.format(new Date()) + ".png";
            Screenshot screenshot = new Screenshot(filename);
            Report report = Testerra.getInjector().getInstance(Report.class);
            saveBufferedImage(screenshotImage, screenshot.getScreenshotFile());
            report.addScreenshot(screenshot, Report.FileMode.MOVE);
        } else {
            LOGGER.error("Could not take native screenshot, screen region is missing");
        }
    }

    private static boolean switchToWindow(WebDriver webDriver, String windowHandle) {
        try {
            webDriver.switchTo().window(windowHandle);
            return true;
        } catch (Exception e) {
            LOGGER.error("Unable to switch to window " + windowHandle, e);
            return false;
        }
    }

    private static List<Screenshot> pTakeAllScreenshotsForSession(WebDriver webDriver) {
        final List<Screenshot> screenshots = new LinkedList<>();
        executionUtils.getFailsafe(webDriver::getWindowHandles).ifPresent(windowHandles -> {
            String originalWindowHandle = executionUtils.getFailsafe(webDriver::getWindowHandle).orElseGet(() -> windowHandles.stream().findFirst().orElse(""));

            if (windowHandles.size() > 1) {
                for (String windowHandle : windowHandles) {
                    takeScreenshotToList(screenshots, webDriver, windowHandle, originalWindowHandle);
                }
                // Switch back to original window handle
                switchToWindow(webDriver, originalWindowHandle);
            } else {
                takeScreenshotToList(screenshots, webDriver, originalWindowHandle, originalWindowHandle);
            }
        });
        return screenshots;
    }

    private static void takeScreenshotToList(List<Screenshot> screenshots, WebDriver webDriver, String windowHandle, String originalWindowHandle) {
        if (switchToWindow(webDriver, windowHandle)) {
            try {
                Screenshot screenshot = takeScreenshot(webDriver, originalWindowHandle);
                screenshots.add(screenshot);
            } catch (Throwable t) {
                LOGGER.error("Unable to take screenshot", t);
            }
        }
    }

    /**
     * initialize properties for a Perf test with default values if PERF_TEST is true.
     * all properties can be overwritten with the values in test.properties
     */
    public static void initializePerfTest() {

        if (Testerra.Properties.PERF_TEST.asBool()) {
            Properties fileProperties = PropertyManager.getFileProperties();

            String value = fileProperties.getProperty(TesterraProperties.CLOSE_WINDOWS_AFTER_TEST_METHODS, "false");
            fileProperties.setProperty(TesterraProperties.CLOSE_WINDOWS_AFTER_TEST_METHODS, value);
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
        Stream<Screenshot> screenshotStream = Stream.concat(WEB_DRIVER_MANAGER.readWebDriversFromCurrentThread(), WEB_DRIVER_MANAGER.readExclusiveWebDrivers())
                .map(UITestUtils::pTakeAllScreenshotsForSession)
                .flatMap(Collection::stream);

        if (publishToReport && executionContextController.getCurrentMethodContext().isPresent()) {
            MethodContext methodContext = executionContextController.getCurrentMethodContext().get();
            screenshotStream = screenshotStream.peek(screenshot -> {
                methodContext.addScreenshot(screenshot);
                report.addScreenshot(screenshot, Report.FileMode.MOVE);
            });
        }
        return screenshotStream.collect(Collectors.toList());
    }
}
