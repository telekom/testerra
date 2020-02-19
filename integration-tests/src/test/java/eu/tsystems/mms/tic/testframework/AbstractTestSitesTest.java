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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework;

import eu.tsystems.mms.tic.testframework.core.test.Server;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.POConfig;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import java.lang.reflect.Method;
import java.net.BindException;

/**
 * Abstract test class for tests based on static test site resources
 */
public abstract class AbstractTestSitesTest extends AbstractWebDriverTest implements Loggable {
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

    @BeforeMethod()
    public void visitTestPage(Method method) {
        visitTestPage(getWebDriver());
    }

    /**
     * Open a custom Webdriver session with the default test page.
     *
     * @param driver
     */
    public synchronized void visitTestPage(WebDriver driver) {
        if (!driver.getCurrentUrl().contains(getTestPage().getPath())) {
            String baseUrl = String.format("http://localhost:%d/%s", server.getPort(), getTestPage().getPath());
            driver.get(baseUrl);
        }
    }

    protected TestPage getTestPage() {
        return TestPage.INPUT_TEST_PAGE;
    }
}
