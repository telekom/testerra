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
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

public abstract class AbstractTestSitesTest extends AbstractTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractTestSitesTest.class);

    @BeforeTest(alwaysRun = true)
    public void setUp() throws Exception {
        Server.start();

        WebDriverManager.setBaseURL(getStartPage().getUrl());
    }

    protected TestPage getStartPage() {
        return TestPage.INPUT_TEST_PAGE;
    }

    @AfterTest(alwaysRun = true)
    public void tearDown() throws Exception {
        Server.stop();
    }
}
