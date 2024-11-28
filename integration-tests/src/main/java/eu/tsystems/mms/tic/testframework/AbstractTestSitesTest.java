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

package eu.tsystems.mms.tic.testframework;

import eu.tsystems.mms.tic.testframework.core.server.StaticServer;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import java.net.BindException;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeTest;

/**
 * Abstract test class for tests based on static test site resources
 */
public abstract class AbstractTestSitesTest extends AbstractWebDriverTest implements Loggable, AssertProvider {

    protected static StaticServer staticServer = new StaticServer(FileUtils.getResourceFile("testsites"));

    protected TestPage getTestPage() {
        return TestPage.INPUT_TEST_PAGE;
    }

    @BeforeTest(alwaysRun = true)
    public void setUp() throws Exception {
        try {
            staticServer.start(8080);
        } catch (BindException e) {
            log().warn("Use already running WebServer: " + e.getMessage());
        }
    }

    public WebDriver getWebDriver(TestPage testPage) {
        WebDriver webDriver = super.getWebDriver();
        visitTestPage(webDriver, testPage);
        return webDriver;
    }

    @Override
    public WebDriver getWebDriver() {
        WebDriver webDriver = super.getWebDriver();
        visitTestPage(webDriver, getTestPage());
        return webDriver;
    }

    /**
     * Open a custom Webdriver session with the default test page.
     *
     * @param driver   {@link WebDriver} Current Instance
     * @param testPage {@link TestPage} page to open
     */
    private void visitTestPage(WebDriver driver, TestPage testPage) {
        if (!driver.getCurrentUrl().contains(testPage.getPath())) {
            String baseUrl = String.format("http://localhost:%d/%s", staticServer.getPort(), testPage.getPath());
            driver.get(baseUrl);
        }
    }
}
