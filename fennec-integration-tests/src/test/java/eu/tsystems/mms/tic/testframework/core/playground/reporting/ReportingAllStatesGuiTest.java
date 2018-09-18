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
/*
 * Created on 07.03.14
 *
 * Copyright(c) 1995 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.core.playground.reporting;

import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.interop.ScreenshotCollector;
import eu.tsystems.mms.tic.testframework.interop.VideoCollector;
import eu.tsystems.mms.tic.testframework.pageobjects.FennecWebTestPage5;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.context.Video;
import eu.tsystems.mms.tic.testframework.report.model.context.report.ReportPublish;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.ArrayUtils;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.utils.TestUtils;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerUtils;
import eu.tsystems.mms.tic.testhelper.AbstractReportingGuiTest;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
//@Listeners(FennecListener.class)
//@FennecClassContext("ReportingAllStatesTest")
public class ReportingAllStatesGuiTest extends AbstractReportingGuiTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportingAllStatesGuiTest.class);

    static {
        WebDriverManager.setBaseURL("http://www.google.de");
//        WebDriverManager.setBaseURL("https://www.google.de");

        //Flags.RETEST = true;

        WebDriverManager.config().browser = Browsers.chrome;
        WebDriverManager.config().browserVersion = "64";
//        WebDriverManager.config().webDriverMode = WebDriverMode.local;

        // some other stuff
        WebDriverManager.registerWebDriverStartUpHandler(driver ->
                driver.manage().addCookie(new Cookie("fennec", "true")));
        WebDriverManager.registerWebDriverShutDownHandler(driver ->
                LOGGER.info("Cookies: " +
                ArrayUtils.join(driver.manage().getCookies().toArray(), ",")));
    }

//    @BeforeSuite
//    public void beforeMethodFailing() {
//        failingStep();
//    }

//    @Test
//    public void testPassed() throws Exception {
//    }

//    @DataProvider(name = "dp")
//    public Object[][] dp() {
//        Object[][] objects = new Object[2][1];
//        objects[0][0] = "1";
//        objects[1][0] = "2";
//        return objects;
//    }
//
//    @Test(dataProvider = "dp")
//    public void testPassedDP(String dp) throws Exception {
//    }
//

//    @Test(dependsOnMethods = "testPassed")
//    public void testPassedDepending() throws Exception {
//    }

    @Test
    public void testFailed() throws Exception {
//        failingStep(How.MINOR);
//        TestUtils.sleep(5000);
//        failingStep(How.SOFT);
//        TestUtils.sleep(5000);
        failingStep(How.HARD);
    }

    @Test
    public void testFailedDoNoOverwriteScreenshot() throws Exception {
        try {
            failingStep(How.HARD);
        } catch (Throwable e) {
            MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
            UITestUtils.takeScreenshots(methodContext, true);
            WebDriverManager.getWebDriver().get("https://www.bing.com/");
            TestUtils.sleep(4000);
            throw e;
        }
    }

    @Test
    public void testFailedMyOwnScreenshotAndVideo() throws Exception {
        /*
        Example screenshot collector
         */
        ScreenshotCollector screenshotCollector = () -> {
            // link the screenshot and video first
            File screenshotFile = FileUtils.getResourceFile("images/noise1.png");
            File sourceFile = FileUtils.getResourceFile("testfiles/Test.txt");

            List<Screenshot> screenshots = new ArrayList<>();

            try {
                Screenshot screenshot = ReportPublish.provideScreenshot(screenshotFile, sourceFile, ReportPublish.Mode.COPY, null);
                screenshots.add(screenshot);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return screenshots;
        };

        /*
        Example video collector
         */
        VideoCollector videoCollector = () -> {
            File videoFile = FileUtils.getResourceFile("testfiles/Test.txt");

            List<Video> videos = new ArrayList<>();

            try {
                Video video = ReportPublish.provideVideo(videoFile, ReportPublish.Mode.COPY);
                videos.add(video);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return videos;
        };

        UITestUtils.takeScreenshots(screenshotCollector, true, true);
        UITestUtils.takeVideos(videoCollector, true);

        // let it fail with a wd session
        failingStep(How.HARD);
    }


    @Test(dependsOnMethods = "testFailed")
    public void testFailedDependsOnFailed() throws Exception {
        failingStep(How.HARD);
    }

//    @Test
//    @Fails
//    public void testFailedExpected() throws Exception {
//        failingStep(How.HARD);
//    }

//    @Test
//    @Fails(description = "blubb", intoReport = true)
//    public void testFailedExpectedButIntoReport() throws Exception {
//        failingStep(How.HARD);
//    }
//
//    @Test(dependsOnMethods = "testPassed")
//    public void testFailedDepending() throws Exception {
//        failingStep(How.HARD);
//    }
////
//    @NoStatusMethod
//    @Test
//    public void testSkippedNoStatus() throws Exception {
//        throw new SkipException("must skip");
//    }
//
//    @Test
//    public void testSkipped() throws Exception {
//        throw new SkipException("must skip");
//    }
//
    @Test
    public void testMinor() throws Exception {
        failingStep(How.MINOR);
    }
//
//    @Test
//    public void testFailedMinorHard() throws Exception {
//        failingStep(How.MINOR);
//        failingStep(How.HARD);
//    }
//
    @Test
    public void testFailedMinorSoft() throws Exception {
        failingStep(How.MINOR);
        failingStep(How.SOFT);
    }
//
//    @Test
//    public void testFailedSoftHard() throws Exception {
//        failingStep(How.SOFT);
//        failingStep(How.HARD);
//    }

    @Test
    public void testGuiElementPrioritizedErrorMessageFails() {
        WebDriver driver = WebDriverManager.getWebDriver();
        PageFactory.create(FennecWebTestPage5.class, driver);
    }

    @Test
    public void testPageFactoryPrioritizedErrorMessageFails() {
        WebDriver driver = WebDriverManager.getWebDriver();
        PageFactory.setErrorHandler(new PageFactory.ErrorHandler() {
            @Override
            public void run(WebDriver driver, Throwable throwableFromPageFactory) {
                throw new RuntimeException("My new Exception", throwableFromPageFactory);
            }
        });
        PageFactory.create(FennecWebTestPage5.class, driver);
    }
}
