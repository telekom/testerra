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
package eu.tsystems.mms.tic.testframework.core.playground.webdriver;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.report.FennecListener;
import eu.tsystems.mms.tic.testframework.utils.TestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by pele on 08.10.2015.
 */
public class ExclusiveSessionsTest extends AbstractTest {

    String exlusiveSessionKey1 = null;
    String exlusiveSessionKey2 = null;

    @BeforeClass
    public void wayBefore() {
        final WebDriver driver1 = WebDriverManager.getWebDriver();
        exlusiveSessionKey1 = WebDriverManager.makeSessionExclusive(driver1);

        TestUtils.sleep(3000);

        // open another session
        final WebDriver driver2 = WebDriverManager.getWebDriver();
        exlusiveSessionKey2 = WebDriverManager.makeSessionExclusive(driver2);
    }

    @Test
    public void testOpenAndCloseSession() throws Exception {
        WebDriverManager.getWebDriver();
        TestUtils.sleep(2000);
    }

    @Test
    public void testUseExclusiveSession() throws Exception {
        final WebDriver driver = WebDriverManager.getWebDriver(exlusiveSessionKey1);
        driver.get("http://thisisexclusive.de");
        TestUtils.sleep(2000);
    }

    @AfterClass
    public void tearDown() throws Exception {
        TestUtils.sleep(5000);
        WebDriverManager.shutdownExclusiveSession(exlusiveSessionKey1);
        WebDriverManager.shutdownExclusiveSession(exlusiveSessionKey2);
    }
}
