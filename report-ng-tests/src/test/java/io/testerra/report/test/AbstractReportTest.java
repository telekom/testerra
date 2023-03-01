/*
 * Testerra
 *
 * (C) 2022, Marc Dietrich, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import eu.tsystems.mms.tic.testframework.common.DefaultPropertyManager;
import eu.tsystems.mms.tic.testframework.core.server.Server;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import io.testerra.report.test.pages.AbstractReportPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;

import java.io.File;
import java.net.BindException;

import static eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider.PAGE_FACTORY;
import static eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider.WEB_DRIVER_MANAGER;

/**
 * Abstract test class for tests based on static test site resources
 */
public abstract class AbstractReportTest extends AbstractTest implements Loggable {

    private final static File serverRootDir = FileUtils.getResourceFile("reports");
    private final static Server server = new Server(serverRootDir);

    @BeforeTest(alwaysRun = true)
    public void setUp() throws Exception {
        try {
            server.start(80);
        } catch (BindException e) {
            log().warn("Use already running WebServer: " + e.getMessage());
        }
    }

    public synchronized ReportDashBoardPage gotoDashBoardOnGeneralReport() {
        return visitPageOnGeneralReport(ReportDashBoardPage.class);
    }

    public synchronized ReportDashBoardPage gotoDashBoardOnAdditionalReport() {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        return visitReportPage(ReportDashBoardPage.class, driver, new DefaultPropertyManager().getProperty("file.path.extend.pretest.root"));
    }

    public synchronized <T extends AbstractReportPage> T visitPageOnGeneralReport(final Class<T> reportPageClass) {
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();
        return visitReportPage(reportPageClass, driver, new DefaultPropertyManager().getProperty("file.path.content.root"));
    }

    /**
     * Open a custom Webdriver session with the default test page.
     *
     * @param reportPageClass report page that should be reached
     * @param driver {@link WebDriver} Current Instance
     * @param directory {@link TestPage} page to open
     */
    public synchronized <T extends AbstractReportPage> T visitReportPage(final Class<T> reportPageClass, final WebDriver driver, final String directory) {
        Assert.assertTrue(serverRootDir.exists(), String.format("Server root directory '%s' doesn't exists", serverRootDir));

        File reportDir = new File(serverRootDir, directory);
        Assert.assertTrue(reportDir.exists(), String.format("Report directory '%s' doesn't exists", reportDir));

        final String baseUrl = String.format("http://localhost:%d/%s", server.getPort(), directory);
        driver.get(baseUrl);

        return PAGE_FACTORY.createPage(reportPageClass, driver);
    }

    protected String getReportDir() {
        return new DefaultPropertyManager().getProperty(Report.Properties.BASE_DIR.toString(), "test-report");
    }

}
