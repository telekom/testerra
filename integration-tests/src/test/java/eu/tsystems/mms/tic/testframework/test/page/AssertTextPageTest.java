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

package eu.tsystems.mms.tic.testframework.test.page;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestFramedPage;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import eu.tsystems.mms.tic.testframework.test.PageFactoryTest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.Test;

/**
 * Created by rnhb on 19.06.2015.
 * <p>
 * Tests for correct execution of checkpage().
 * To test that checkpage() is executed, a not existing, check-annotated element is used.
 */
public class AssertTextPageTest extends AbstractTestSitesTest implements PageFactoryTest {

    @Override
    protected TestPage getTestPage() {
        return TestPage.FRAME_TEST_PAGE;
    }

    @Override
    public WebTestFramedPage getPage() {
        return PageFactory.create(WebTestFramedPage.class, WebDriverManager.getWebDriver());
    }

    @Test
    public void testT11_assertIsTextPresent() {
        WebTestFramedPage page = getPage();
        page.assertIsTextPresent("Frame1234");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_assertIsTextPresent_fails() {
        WebTestFramedPage page = getPage();
        page.assertIsTextPresent("Bifi");
    }

    @Test
    public void testT13_assertIsNotTextPresent() {
        WebTestFramedPage page = getPage();
        page.assertIsNotTextPresent("Bifi");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_assertIsNotTextPresent_fails() {
        WebTestFramedPage page = getPage();
        page.assertIsNotTextPresent("Frame1234");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT15F_waitIsNotTextPresentWithDelay() {
        WebTestFramedPage page = getPage();
        Assert.assertTrue(page.waitForIsNotTextPresentWithDelay("Frame1234", 1));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT16F_waitIsNotTextDisplayedWithDelay() {
        WebTestFramedPage page = getPage();
        Assert.assertTrue(page.waitForIsNotTextDisplayedWithDelay("Frame1234", 1));
    }

    @Test
    public void testT21_assertIsTextDisplayed() {
        WebTestFramedPage page = getPage();
        page.assertIsTextDisplayed("Frame1234");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_assertIsTextDisplayed_fails() {
        WebTestFramedPage page = getPage();
        page.assertIsTextDisplayed("Bifi");
    }

    @Test
    public void testT23_assertIsNotTextDisplayed() {
        WebTestFramedPage page = getPage();
        page.assertIsNotTextDisplayed("Bifi");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_assertIsNotTextDisplayed_fails() {
        WebTestFramedPage page = getPage();
        page.assertIsNotTextDisplayed("Frame1234");
    }

}
