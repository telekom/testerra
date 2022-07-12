/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package io.testerra.report.test;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.core.server.Server;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.POConfig;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.useragents.ChromeConfig;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.lang.reflect.Method;
import java.net.BindException;

/**
 * Abstract test class for tests based on static test site resources
 */
public abstract class AbstractReportTest extends TesterraTest implements Loggable {

    private final static File serverRootDir = FileUtils.getResourceFile("reports");
    private final static Server server = new Server(serverRootDir);

    @DataProvider
    public Object[][] dataProviderForPreTestMethods(){
        return new Object[][]{
                // method, class, status, failure aspect
                //passed
                {"test_Passed", "GeneratePassedStatusInTesterraReportTest", "Passed", null},
                {"test_Optional_Assert", "GeneratePassedStatusInTesterraReportTest", "Passed", null},
                {"test_GenerateScreenshotManually", "GenerateScreenshotsInTesterraReportTest", "Passed", null},
                // recovered
                {"test_PassedAfterRetry", "GenerateExpectedFailedStatusInTesterraReportTest", "Recovered", null},
                // repaired
                {"test_expectedFailedPassed", "GenerateExpectedFailedStatusInTesterraReportTest", "Repaired", null},
                // skipped
                {"test_SkippedNoStatus", "GenerateSkippedStatusInTesterraReportTest", "Skipped", "SkipException: Test Skipped."},
                {"test_Skipped_AfterErrorInDataProvider", "GenerateSkippedStatusInTesterraReportTest", "Skipped", "RuntimeException: Error in DataProvider."},
                {"test_Skipped_DependingOnFailed", "GenerateSkippedStatusInTesterraReportTest", "Skipped", "Throwable:[...] depends on not successfully finished methods"},
                {"test_Skipped_AfterErrorInBeforeMethod", "GenerateSkippedStatusViaBeforeMethodInTesterraReportTest", "Skipped", "AssertionError: Error in @BeforeMethod"},
                // Failed
                {"testAssertCollector", "GenerateFailedStatusInTesterraReportTest", "Failed", "AssertionError: failed1\n AssertionError: failed2"},
                {"test_failedPageNotFound", "GenerateFailedStatusInTesterraReportTest", "Failed", "PageNotFoundException: Test page not reached."},
                {"test_Failed", "GenerateFailedStatusInTesterraReportTest", "Failed", "AssertionError: Creating TestStatus 'Failed'"},
                {"test_Failed", "GenerateFailedStatusInTesterraReportTest", "Failed", "AssertionError: Creating TestStatus 'Failed'"},
                {"test_Failed_WithScreenShot", "GenerateFailedStatusInTesterraReportTest", "Failed", "AssertionError: 'Failed' on reached Page."},
                // expected Failed
                {"test_expectedFailedAssertCollector", "GenerateExpectedFailedStatusInTesterraReportTest", "Expected Failed", "AssertionError: failed1\n AssertionError: failed2"},
                {"test_expectedFailedPageNotFound", "GenerateExpectedFailedStatusInTesterraReportTest", "Expected Failed", "PageNotFoundException: Test page not reached."},
                {"test_expectedFailed", "GenerateExpectedFailedStatusInTesterraReportTest", "Expected Failed", "AssertionError: No Oil."},
                // retried
                {"test_PassedAfterRetry", "GenerateExpectedFailedStatusInTesterraReportTest", "Retried", "AssertionError: test_FailedToPassedHistoryWithRetry"}
        };
    }

    @BeforeMethod(alwaysRun = true)
    public void configureChromeOptions(Method method) {
        WebDriverManager.setUserAgentConfig(Browsers.chromeHeadless, new ChromeConfig() {
            @Override
            public void configure(ChromeOptions options) {
                options.addArguments("--disable-dev-shm-usage");
            }
        });
    }

    @BeforeTest(alwaysRun = true)
    public void setUp() throws Exception {
        POConfig.setUiElementTimeoutInSeconds(3);
        try {
            server.start(80);
        } catch (BindException e) {
            log().warn("Use already running WebServer: " + e.getMessage());
        }
    }

//    @BeforeMethod(alwaysRun = true)
//    public void visitTestPage(Method method) {
////        visitTestPage(getWebDriver());
//        visitTestPage(WebDriverManager.getWebDriver());
//    }

    /**
     * Open a custom Webdriver session with the default test page.
     *
     * @param driver {@link WebDriver} Current webDriver Instance
     */
    public synchronized void visitTestPage(WebDriver driver) {
        visitTestPage(ReportDashBoardPage.class, driver, getReportDir());
    }


    public synchronized <T extends AbstractReportPage> T visitTestPage(final Class<T> reportPageClass, final WebDriver driver) {
        return visitTestPage(reportPageClass, driver, PropertyManager.getProperty("file.path.content.root"));
    }


    /**
     * Open a custom Webdriver session with the default test page.
     *
     * @param reportPageClass report page that should be reached
     * @param driver          {@link WebDriver} Current Instance
     * @param directory       {@link TestPage} page to open
     */
    public synchronized <T extends AbstractReportPage> T visitTestPage(final Class<T> reportPageClass, final WebDriver driver, final String directory) {
        Assert.assertTrue(serverRootDir.exists(), String.format("Server root directory '%s' doesn't exists", serverRootDir));

        File reportDir = new File(serverRootDir, directory);
        Assert.assertTrue(reportDir.exists(), String.format("Report directory '%s' doesn't exists", reportDir));

        if (!driver.getCurrentUrl().contains(directory)) {
            String baseUrl = String.format("http://localhost:%d/%s", server.getPort(), directory);
            driver.get(baseUrl);
        }
        return PageFactory.create(reportPageClass, driver);
    }

    protected String getReportDir() {
        return PropertyManager.getProperty(TesterraProperties.REPORTDIR, "test-report");
    }


}
