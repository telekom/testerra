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
package eu.tsystems.mms.tic.testframework.core.test.pageobjects.guielement;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.PageFactoryTest;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringAssertion;
import org.testng.annotations.Test;

public class UiElementTests extends AbstractTestSitesTest implements Loggable, PageFactoryTest {

    @Test
    public void test_Page_title() {
        WebTestPage page = getPage();

        StringAssertion<String> title = page.title();

        title.is("Input test");
        title.isNot("Affentest");
        title.contains("Input");
        title.containsNot("SuperTestPage");
        title.containsWords("Input", "test").isTrue();

        QuantityAssertion<Integer> length = page.title().length();
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
        Control.elementTimeout(0, () -> {
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
        page.title().matches("input\\s+.es.").isTrue();
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_Page_title_matches_fails() {
        WebTestPage page = getPage();
        page.title().matches("input\\s+.es.").isFalse();
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_Page_title_length_fails() {
        WebTestPage page = getPage();
        page.title().length().isGreaterThan(10);
    }

    @Test
    @Fails(description = "The test itself passes, but collected assertions will always fail")
    public void test_Page_title_length_fails_collected() {
        WebTestPage page = getPage();
        Control.collectAssertions(() -> page.title().length().isGreaterThan(10));
    }

    @Test
    public void test_Page_title_length_fails_nonFunctional() {
        WebTestPage page = getPage();
        Control.nonFunctionalAssertions(()-> page.title().length().isGreaterThan(10));
    }

    @Test
    public void test_Page_url() {
        WebTestPage page = getPage();
        page.url().startsWith("http");
        page.url().endsWith("input.html");
        page.url().length().isGreaterEqualThan(10);
    }

    @Test()
    public void test_Page_url_fails() {
        WebTestPage page = getPage();
        try {
            page.url().endsWith("nonexistingfile.html", "Wrong URL");
        } catch (AssertionError e) {
            Assert.assertBeginsWith(e.getMessage(), "Wrong URL");
            Assert.assertEndsWith(e.getMessage(), "ends with [nonexistingfile.html]");
        }

        try {
            page.url().length().isGreaterEqualThan(10000, "URL is too short");
        } catch (AssertionError e) {
            Assert.assertBeginsWith(e.getMessage(), "URL is too short");
            Assert.assertEndsWith(e.getMessage(), "is greater or equal than [10000]");
        }

    }

    @Test
    public void test_GuiElement_clear() {
        WebTestPage page = getPage();
        UiElement element = page.findById(5);
        element.sendKeys("Test");
        element.clear().text().is("");
    }

    @Test
    public void test_GuiElement_displayed_false() {
        WebTestPage page = getPage();
        page.notDisplayedElement().value(Attribute.STYLE).contains("display: none");
        page.notDisplayedElement().displayed().isFalse();
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_GuiElement_displayed_false_fails() {
        WebTestPage page = getPage();
        page.notDisplayedElement().displayed().isTrue();
    }

    @Test
    public void test_GuiElement_displayed_false_fails_with_message() {
        WebTestPage page = getPage();
        try {
            page.notDisplayedElement().displayed().isTrue("Missing visible element here");
        } catch (AssertionError e) {
            Assert.assertBeginsWith(e.getMessage(), "Missing visible element here");
            Assert.assertContains(e.getMessage(), page.notDisplayedElement().toString());
        }
    }

    @Test
    public void test_GuiElement_visible_false() {
        WebTestPage page = getPage();
        page.notVisibleElement().value(Attribute.STYLE).contains("hidden");
        page.notVisibleElement().value("style").contains("hidden");
        page.notVisibleElement().visible(true).isFalse();
        page.notVisibleElement().visible(false).isFalse();
        page.notDisplayedElement().css("display").is("none");
    }

    @Test
    public void test_GuiElement_waitFor() {
        WebTestPage page = getPage();
        Control.elementTimeout(0, () -> {
            if (page.notVisibleElement().waitFor().value(Attribute.STYLE).is("humbug")) {
                Assert.assertFalse(true);
            }
            if (page.notVisibleElement().waitFor().value(Attribute.STYLE).contains("hidden")) {
                Assert.assertTrue(true);
            }
        });
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_GuiElement_visible_false_fails() {
        WebTestPage page = getPage();
        page.notVisibleElement().visible(true).isTrue();
    }

    @Test
    public void test_NonExistent_GuiElement_present() {
        WebTestPage page = getPage();
        page.nonExistentElement().present().isFalse();
    }

    @Test
    public void test_NonExistent_GuiElement_present_fails() {
        WebTestPage page = getPage();
        String msg = null;
        try {
            page.nonExistentElement().present().isTrue();
        } catch (AssertionError e) {
            msg = e.getMessage();
        }
        Assert.assertEndsWith(msg, "is one of [true, 'on', '1', 'yes']", AssertionError.class.toString());
    }

    @Test
    public void test_NonExistent_GuiElement_present_fails_fast() {
        Control.elementTimeout(0, () -> test_NonExistent_GuiElement_present_fails());
    }

    @Test
    public void test_NonExistent_GuiElement_displayed_fails() {
        WebTestPage page = getPage();
        String msg=null;
        try {
            page.nonExistentElement().displayed().isFalse();
        } catch (AssertionError e) {
            msg = e.getMessage();
        }
        Assert.assertEndsWith(msg, "not found", AssertionError.class.toString());
    }

    @Test
    public void test_GuiElement_screenshot() {
        WebTestPage page = getPage();
        ImageAssertion screenshot = page.notVisibleElement().screenshot();
        screenshot.file().exists().isTrue();
    }

    @Test
    public void test_NonExistent_GuiElement_screenshot_fails() {
        WebTestPage page = getPage();
        String msg=null;
        try {
            page.nonExistentElement().screenshot().file().exists().isTrue();
        } catch (ElementNotFoundException e) {
            msg = e.getMessage();
        }
        Assert.assertEndsWith(msg, "not found", ElementNotFoundException.class.toString());
    }

    @Test
    public void test_Component() {
        final String input = "Ich gebe etwas ein";
        WebTestPage page = getPage();
        page.inputForm().button().value().is("Button1");
        page.inputForm().input().clear().sendKeys(input).value().is(input);
        page.inputForm().button().numberOfElements().is(1);
    }

    @Test
    public void test_User_sendKeys() {
        WebTestPage page = getPage();
        final String input = "Ich bin langsam im Tippen";
        page.inputForm().input().asUser().clear().sendKeys(input).value().is(input);
    }

    @Test
    public void test_Attributes() {
        WebTestPage page = getPage();
        UiElement attributes = page.findByQa("section/attributeTest");

        //attributes.value("ariaExpanded").is("true");
        attributes.value("aria-expanded").is("true");
        //attributes.value("dataCompletelyCustomAttribute").is("true");
        attributes.value("data-completely-custom-attribute").is("yes");
    }

    @Override
    public WebTestPage getPage() {
        return pageFactory.createPage(WebTestPage.class);
    }
}
