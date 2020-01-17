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
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.net.BindException;

/**
 * Abstract test class for tests based on static test site resources
 */
public abstract class AbstractTestSitesTest extends AbstractWebDriverTest implements Loggable {
    private Server server = new Server(FileUtils.getResourceFile("testsites"));

    @BeforeTest(alwaysRun = true)
    public void setUp() throws Exception {
        POConfig.setUiElementTimeoutInSeconds(1);
        int port = 80;
        try {
            server.start(port);
        } catch (BindException e) {
            log().warn(e.getMessage());
        }
        String baseUrl = String.format("http://localhost:%d/%s", port, getStartPage().getUrl());
        WebDriverManager.setBaseURL(baseUrl);
    }

    protected TestPage getStartPage() {
        return TestPage.INPUT_TEST_PAGE;
    }

    @AfterTest(alwaysRun = true)
    public void tearDown() throws Exception {
        server.stop();
    }
}
