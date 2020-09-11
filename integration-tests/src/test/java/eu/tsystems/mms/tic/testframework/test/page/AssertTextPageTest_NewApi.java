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
package eu.tsystems.mms.tic.testframework.test.page;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestFramedPage;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.test.PageFactoryTest;
import org.testng.annotations.Test;

/**
 * Created by rnhb on 19.06.2015.
 *
 * Tests for correct execution of checkpage().
 * To test that checkpage() is executed, a not existing, check-annotated element is used.
 *
 */
public class AssertTextPageTest_NewApi extends AbstractTestSitesTest implements PageFactoryTest {

    @Override
    protected TestPage getTestPage() {
        return TestPage.FRAME_TEST_PAGE;
    }

    @Override
    public WebTestFramedPage getPage() {
        return pageFactory.createPage(WebTestFramedPage.class);
    }

    @Test
    public void testT11_assertIsTextPresent() {
        Control.withTimeout(0, () -> {
            WebTestFramedPage page = getPage();
            page.anyElementContainsText("Frame1234").present().is(true);
        });
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_assertIsTextPresent_fails() {
        Control.withTimeout(0, () -> {
            WebTestFramedPage page = getPage();
            page.anyElementContainsText("Bifi").present().is(true);
        });
    }

    @Test
    public void testT13_assertIsNotTextPresent() {
        Control.withTimeout(0, () -> {
            WebTestFramedPage page = getPage();
            page.anyElementContainsText("Bifi").present().is(false);
        });
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_assertIsNotTextPresent_fails() {
        Control.withTimeout(0, () -> {
            WebTestFramedPage page = getPage();
            page.anyElementContainsText("Frame1234").present().is(false);
        });
    }

    @Test
    public void testT21_assertIsTextDisplayed() {
        Control.withTimeout(0, () -> {
            WebTestFramedPage page = getPage();
            page.anyElementContainsText("Frame1234").displayed().is(true);
        });
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_assertIsTextDisplayed_fails() {
        Control.withTimeout(0, () -> {
            WebTestFramedPage page = getPage();
            page.anyElementContainsText("Bifi").displayed().is(true);
        });
    }

    @Test
    public void testT23_assertIsNotTextDisplayed() {
        Control.withTimeout(0, () -> {
            WebTestFramedPage page = getPage();
            page.anyElementContainsText("Bifi").displayed().is(false);
        });
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_assertIsNotTextDisplayed_fails() {
        Control.withTimeout(0, () -> {
            WebTestFramedPage page = getPage();
            page.anyElementContainsText("Frame1234").displayed().is(false);
        });
    }
}
