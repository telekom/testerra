/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.test.guielement;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringAssertion;
import eu.tsystems.mms.tic.testframework.test.PageFactoryTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UiElementTests extends AbstractTestSitesTest implements Loggable, PageFactoryTest {

    @BeforeClass
    public void before() {
        this.enableExclusiveSession();
    }

    @Test
    public void test_Page_title() {
        WebTestPage page = getPage();

        StringAssertion<String> title = page.expectThat().title();

        title.is("Input test");
        title.isNot("Affentest");
        title.contains("Input");
        title.containsNot("SuperTestPage");
        title.containsWords("Input", "test").is(true);

        QuantityAssertion<Integer> length = page.expectThat().title().length();
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
        Control.withTimeout(0, () -> {
            if (page.waitFor().title().contains("Katzentitel")) {
                Assert.assertFalse(true);
            }

            if (page.waitFor().title().is("Input test")) {
                Assert.assertTrue(true);
            }
        });
    }

    @Test
    public void test_Page_title_matches() {
        WebTestPage page = getPage();
        page.expectThat().title().matches("input\\s+.es.").is(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_Page_title_matches_fails() {
        WebTestPage page = getPage();
        page.expectThat().title().matches("input\\s+.es.").is(false);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_Page_title_length_fails() {
        WebTestPage page = getPage();
        page.expectThat().title().length().isGreaterThan(10);
    }

    @Test
    @Fails(description = "The test itself passes, but collected assertions will always fail")
    public void test_Page_title_length_fails_collected() {
        WebTestPage page = getPage();
        Control.collectAssertions(() -> page.expectThat().title().length().isGreaterThan(10));
    }

    @Test
    public void test_Page_title_length_fails_nonFunctional() {
        WebTestPage page = getPage();
        Control.nonFunctionalAssertions(()-> page.expectThat().title().length().isGreaterThan(10));
    }

    @Test
    public void test_Page_url() {
        WebTestPage page = getPage();
        page.expectThat().url().startsWith("http");
        page.expectThat().url().endsWith("input.html");
        page.expectThat().url().length().isGreaterEqualThan(10);
    }

    @Test()
    public void test_Page_url_fails() {
        WebTestPage page = getPage();
        try {
            page.expectThat().url().endsWith("nonexistingfile.html", "Wrong URL");
        } catch (AssertionError e) {
            Assert.assertContains(e.getMessage(), "Wrong URL");
            Assert.assertEndsWith(e.getMessage(), "ends with [nonexistingfile.html]");
        }

        try {
            page.expectThat().url().length().isGreaterEqualThan(10000, "URL is too short");
        } catch (AssertionError e) {
            Assert.assertContains(e.getMessage(), "URL is too short");
            Assert.assertEndsWith(e.getMessage(), "is greater or equal than [10000]");
        }

    }

    @Test
    public void test_GuiElement_clear() {
        WebTestPage page = getPage();
        UiElement element = page.findById(5);
        element.sendKeys("Test");
        element.clear().expectThat().text().is("");
    }

    @Test
    public void test_GuiElement_displayed_false() {
        WebTestPage page = getPage();
        page.notDisplayedElement().expectThat().value(Attribute.STYLE).contains("display: none");
        page.notDisplayedElement().expectThat().displayed().is(false);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_GuiElement_displayed_false_fails() {
        WebTestPage page = getPage();
        page.notDisplayedElement().expectThat().displayed().is(true);
    }

    @Test()
    public void test_GuiElement_displayed_false_fails_with_message() {
        WebTestPage page = getPage();
        try {
            page.notDisplayedElement().expectThat().displayed().is(true,"Important element visibility");
        } catch (AssertionError e) {
            Assert.assertContains(e.getMessage(), "Important element visibility");
        }
    }

    @Test
    public void test_GuiElement_visible_false() {
        WebTestPage page = getPage();
        page.notVisibleElement().expectThat().value(Attribute.STYLE).contains("hidden");
        page.notVisibleElement().expectThat().value("style").contains("hidden");
        page.notVisibleElement().expectThat().visible(true).is(false);
        page.notVisibleElement().expectThat().visible(false).is(false);
        page.notDisplayedElement().expectThat().css("display").is("none");
    }

    @Test
    public void test_GuiElement_waitFor() {
        WebTestPage page = getPage();
        Control.withTimeout(0, () -> {
            Assert.assertFalse(page.notVisibleElement().waitFor().value(Attribute.STYLE).is("humbug"));
            Assert.assertTrue(page.notVisibleElement().waitFor().value(Attribute.STYLE).contains("hidden"));
        });
    }

    @Test
    public void test_UiElement_waitFor_text() {
        WebTestPage page = getPage();
        Assert.assertTrue(page.getOpenAgainLink().waitFor().text("Open again"));
    }

    @Test
    public void test_UiElement_waitFor_text_fails() {
        WebTestPage page = getPage();
        Assert.assertFalse(page.getOpenAgainLink().waitFor().text("Close again"));
    }

    @Test
    public void test_UiElement_waitFor_text_mapped() {
        WebTestPage page = getPage();
        Assert.assertTrue(page.getOpenAgainLink().waitFor().text().map(String::toLowerCase).is("open again"));
    }

    @Test
    public void test_UiElement_attribute_present() {
        WebTestPage page = getPage();
        page.getRadioBtn().expectThat().value("disabled").isNot(null);
    }

    @Test
    public void test_UiElement_null_attribute() {
        WebTestPage page = getPage();
        page.getRadioBtn().expectThat().value("not-existent-attribute").is(null);
    }

    @Test
    public void test_UiElement_null_attribute_mapped() {
        WebTestPage page = getPage();
        page.getRadioBtn().expectThat().value("not-existent-attribute").map(String::trim).is(null);
    }

    @Test
    public void test_UiElement_waitFor_text_mapped_fails() {
        WebTestPage page = getPage();
        Assert.assertFalse(page.getOpenAgainLink().waitFor().text().map(String::toLowerCase).is("close again"));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_GuiElement_visible_false_fails() {
        WebTestPage page = getPage();
        page.notVisibleElement().expectThat().visible(true).is(true);
    }

    @Test
    public void test_NonExistent_GuiElement_present() {
        WebTestPage page = getPage();
        page.nonExistentElement().expectThat().present().is(false);
    }

    @Test
    public void test_NonExistent_GuiElement_present_fails() {
        WebTestPage page = getPage();
        String msg = null;
        try {
            page.nonExistentElement().expectThat().present().is(true);
        } catch (AssertionError e) {
            msg = e.getMessage();
        }
        Assert.assertEndsWith(msg, "is one of [true, 'on', '1', 'yes']", AssertionError.class.toString());
    }

    @Test
    public void test_NonExistent_GuiElement_present_fails_fast() {
        Control.withTimeout(0, () -> test_NonExistent_GuiElement_present_fails());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_NonExistent_GuiElement_displayed_fails() {
        WebTestPage page = getPage();
        page.nonExistentElement().expectThat().displayed().is(false);
    }

    @Test
    public void test_GuiElement_screenshot() {
        WebTestPage page = getPage();
        ImageAssertion screenshot = page.notVisibleElement().expectThat().screenshot();
        screenshot.file().exists().is(true);
    }

    @Test
    public void test_NonExistent_GuiElement_screenshot_fails() {
        WebTestPage page = getPage();
        String msg=null;
        try {
            page.nonExistentElement().expectThat().screenshot().file().exists().is(true);
        } catch (ElementNotFoundException e) {
            msg = e.getMessage();
        }
        Assert.assertStartsWith(msg, "Element not found", ElementNotFoundException.class.toString());
    }

    @Test
    public void test_Component() {
        final String input = "Ich gebe etwas ein";
        WebTestPage page = getPage();
        page.inputForm().button().expectThat().value().is("Button1");
        page.inputForm().input().clear().sendKeys(input).expectThat().value().is(input);
        page.inputForm().button().expectThat().numberOfElements().is(1);
    }

    @Test
    public void test_Attributes() {
        WebTestPage page = getPage();
        UiElement attributes = page.findByQa("section/attributeTest");

        //attributes.value("ariaExpanded").is("true");
        attributes.expectThat().value("aria-expanded").is("true");
        //attributes.value("dataCompletelyCustomAttribute").is("true");
        attributes.expectThat().value("data-completely-custom-attribute").is("yes");
    }

    @Test
    public void test_retry() {
        WebTestPage page = getPage();
        UiElement disableMyselfBtn = page.findById("disableMyselfBtn");
        disableMyselfBtn.expectThat().enabled().is(true);
        Control.retryFor(10).withTimeout(0,() -> {
            disableMyselfBtn.click();
            disableMyselfBtn.expectThat().enabled().is(false);
        });
        disableMyselfBtn.expectThat().enabled().is(false);
    }

    @Test(expectedExceptions = TimeoutException.class)
    public void test_UiElement_click_fails() {
        WebTestPage page = getPage();
        page.nonExistentElement().click();
    }

    @Override
    public WebTestPage getPage() {
        return pageFactory.createPage(WebTestPage.class, getWebDriver());
    }
}
