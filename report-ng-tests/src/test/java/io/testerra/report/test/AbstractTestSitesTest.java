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

import eu.tsystems.mms.tic.testframework.core.server.Server;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import io.testerra.report.test.pages.TestPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;

import java.net.BindException;

/**
 * Abstract test class for tests based on static test site resources
 */
public abstract class AbstractTestSitesTest extends AbstractTest implements WebDriverManagerProvider, PageFactoryProvider, Loggable {

    protected static Server server = new Server(FileUtils.getResourceFile("testsites"));
    private String exclusiveSessionId;

    @BeforeTest(alwaysRun = true)
    public void setUp() throws Exception {
        try {
            server.start(80);
        } catch (BindException e) {
            log().warn("Use already running WebServer: " + e.getMessage());
        }
    }

    @AfterClass(alwaysRun = true)
    public void closeExclusiveWebDriverSession() {
        if (exclusiveSessionId != null) {
            WEB_DRIVER_MANAGER.shutdownSession(this.exclusiveSessionId);
            exclusiveSessionId = null;
        }
    }

    /**
     * Open a custom Webdriver session with the default test page.
     *
     * @param driver {@link WebDriver} Current Instance
     * @param testPage {@link TestPage} page to open
     */
    protected synchronized void visitTestPage(final WebDriver driver, final TestPage testPage) {
        if (!driver.getCurrentUrl().contains(testPage.getPath())) {
            String baseUrl = String.format("http://localhost:%d/%s", server.getPort(), testPage.getPath());
            driver.get(baseUrl);
        }
    }

    protected WebDriver getExclusiveWebDriver() {
        if (exclusiveSessionId == null) {
            WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver();
            exclusiveSessionId = WEB_DRIVER_MANAGER.makeExclusive(webDriver);
        }
        return WEB_DRIVER_MANAGER.getWebDriver(exclusiveSessionId);
    }

    public static class Groups {
        public static final String BASIC = "basic";
        public static final String EXT = "extended";
        public static final String EXT2 = "extended2";
        public static final String EXT3 = "extended2";
    }
}
