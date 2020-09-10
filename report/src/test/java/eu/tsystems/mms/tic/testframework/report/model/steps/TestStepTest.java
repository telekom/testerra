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
package eu.tsystems.mms.tic.testframework.report.model.steps;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestStepTest extends AbstractTestSitesTest {

    public WebTestPage getPage() {
        return PageFactory.create(WebTestPage.class, WebDriverManager.getWebDriver(), new WebTestPage.MyVariables(1));
    }

    @BeforeTest
    private void beforeTest() {

    }

    @BeforeClass
    private void beforeClass() {

    }

    @AfterTest
    private void afterTest() {

    }

    @AfterClass
    private void afterClass() {

    }

    @Test
    public void test_Steps() {
        WebTestPage page = getPage();
        TestStep.begin("Explicit test step");
        page.assertFunctionalityOfButton1();
    }
}
