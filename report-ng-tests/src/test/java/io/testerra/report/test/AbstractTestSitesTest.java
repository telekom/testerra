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
import eu.tsystems.mms.tic.testframework.pageobjects.POConfig;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import io.testerra.report.test.pages.TestPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeTest;

import java.net.BindException;

/**
 * Abstract test class for tests based on static test site resources
 */
public abstract class AbstractTestSitesTest extends AbstractTest implements Loggable {

    protected static Server server = new Server(FileUtils.getResourceFile("testsites"));

    @BeforeTest(alwaysRun = true)
    public void setUp() throws Exception {
        POConfig.setUiElementTimeoutInSeconds(3);
        try {
            server.start(80);
        } catch (BindException e) {
            log().warn("Use already running WebServer: " + e.getMessage());
        }
    }

    /**
     * Open a custom Webdriver session with the default test page.
     *
     * @param driver   {@link WebDriver} Current Instance
     * @param testPage {@link TestPage} page to open
     */
    public synchronized void visitTestPage(final WebDriver driver, final TestPage testPage) {
        if (!driver.getCurrentUrl().contains(testPage.getPath())) {
            String baseUrl = String.format("http://localhost:%d/%s", server.getPort(), testPage.getPath());
            driver.get(baseUrl);
        }
    }
}
