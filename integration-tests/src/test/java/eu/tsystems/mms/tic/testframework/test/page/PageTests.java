/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */
package eu.tsystems.mms.tic.testframework.test.page;

import eu.tsystems.mms.tic.testframework.AbstractExclusiveTestSitesTest;
import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.StringAssertion;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.test.PageFactoryTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PageTests extends AbstractExclusiveTestSitesTest<WebTestPage> implements Loggable, PageFactoryTest {

    @Override
    public Class getPageClass() {
        return WebTestPage.class;
    }

    @Test
    public void test_Page_title() {
        WebTestPage page = getPage();

        StringAssertion<String> title = page.expect().title();

        title.is("Input test");
        title.isNot("Affentest");
        title.contains("Input").is(true);
        title.contains("SuperTestPage").is(false);
        title.hasWords("Input", "test").is(true);

        QuantityAssertion<Integer> length = page.expect().title().length();
        length.is(10);
        length.isLowerThan(100);
        length.isGreaterThan(5);
        length.isBetween(1,11);
        length.isGreaterEqualThan(-10);
        length.isLowerEqualThan(10);
    }

    @Test
    public void test_Page_waitFor() {
        WebTestPage page = getPage();
        CONTROL.withTimeout(0, () -> {
            Assert.assertFalse(page.waitFor().title().contains("Katzentitel").is(true));
            Assert.assertTrue(page.waitFor().title().is("Input test"));
        });
    }

    @Test
    public void test_Page_title_matches() {
        WebTestPage page = getPage();
        page.expect().title().matches("input\\s+.es.").is(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_Page_title_matches_fails() {
        WebTestPage page = getPage();
        page.expect().title().matches("input\\s+.es.").is(false);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_Page_title_length_fails() {
        WebTestPage page = getPage();
        page.expect().title().length().isGreaterThan(10);
    }

    @Test
    @Fails(description = "The test itself passes, but collected assertions will always fail")
    public void test_Page_title_length_fails_collected() {
        WebTestPage page = getPage();
        CONTROL.collectAssertions(() -> page.expect().title().length().isGreaterThan(10));
    }

    @Test
    public void test_Page_title_length_fails_nonFunctional() {
        WebTestPage page = getPage();
        CONTROL.optionalAssertions(()-> page.expect().title().length().isGreaterThan(10));
    }

    @Test
    public void test_Page_url() {
        WebTestPage page = getPage();
        page.expect().url().startsWith("http").is(true);
        page.expect().url().endsWith("input.html").is(true);
        page.expect().url().length().isGreaterEqualThan(10);
    }

    @Test
    public void test_Component() {
        final String input = "Ich gebe etwas ein";
        WebTestPage page = getPage();
        page.inputForm().button().expect().value().is("Button1");
        page.inputForm().input().clear().sendKeys(input).expect().value().is(input);
        page.inputForm().button().expect().numberOfElements().is(1);
    }
}
