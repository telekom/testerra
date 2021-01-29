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
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.test.PageFactoryTest;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.annotations.Test;

public class UiElementTests extends AbstractTestSitesTest implements Loggable, PageFactoryTest {

    @Override
    public WebTestPage getPage() {
        return pageFactory.createPage(WebTestPage.class, getClassExclusiveWebDriver());
    }

    @Test
    public void test_UiElement_clear() {
        WebTestPage page = getPage();
        UiElement element = page.getFinder().findById(5);
        element.sendKeys("Test");
        element.clear().expectThat().text().is("");
    }

    @Test(expectedExceptions = TimeoutException.class)
    public void test_UiElement_click_fails() {
        WebTestPage page = getPage();
        page.nonExistentElement().click();
    }

    @Test
    public void test_UiElement_displayed_false() {
        WebTestPage page = getPage();
        page.notDisplayedElement().expectThat().value(Attribute.STYLE).contains("display: none");
        page.notDisplayedElement().expectThat().displayed().is(false);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_UiElement_displayed_false_fails() {
        WebTestPage page = getPage();
        page.notDisplayedElement().expectThat().displayed().is(true);
    }

    @Test()
    public void test_UiElement_displayed_false_fails_withMessage() {
        WebTestPage page = getPage();
        try {
            page.notDisplayedElement().expectThat().displayed().is(true,"Important element visibility");
        } catch (AssertionError e) {
            Assert.assertContains(e.getMessage(), "Important element visibility");
        }
    }

    @Test
    public void test_UiElement_visible_false() {
        WebTestPage page = getPage();
        page.notVisibleElement().expectThat().value(Attribute.STYLE).contains("hidden");
        page.notVisibleElement().expectThat().value("style").contains("hidden");
        page.notVisibleElement().expectThat().visible(true).is(false);
        page.notVisibleElement().expectThat().visible(false).is(false);
        page.notDisplayedElement().expectThat().css("display").is("none");
    }

    @Test
    public void test_UiElement_waitFor_followedBy_expectThat() {
        WebTestPage page = getPage();
        Assert.assertTrue(page.notDisplayedElement().waitFor().displayed(false), "Display status of not displayed element");
        try {
            page.notVisibleElement().expectThat().displayed(true);
        } catch (AssertionError error) {
            Assert.assertContains(error.getMessage(), "actual [false] is one of [true");
        }
    }

    @Test
    public void test_UiElement_nonfunctional_assert() {
        try {
            Control.optionalAssertions(() -> {
                Control.withTimeout(1, () -> {
                    getPage().notDisplayedElement().expectThat().displayed(true);
                });
            });
        } catch (Exception e) {
            log().error("Catched", e);
        }
    }

    @Test
    public void test_UiElement_expectThat_followedBy_waitFor() {
        WebTestPage page = getPage();
        try {
            page.notVisibleElement().expectThat().displayed(true);
        } catch (AssertionError error) {
            Assert.assertContains(error.getMessage(), "actual [false] is one of [true");
        }
        Assert.assertTrue(page.notDisplayedElement().waitFor().displayed(false), "Display status of not displayed element");
    }

    @Test
    public void test_UiElement_waitFor_fast() {
        WebTestPage page = getPage();
        Control.withTimeout(0, () -> {
            Assert.assertFalse(page.notVisibleElement().waitFor().value(Attribute.STYLE).is("humbug"));
            Assert.assertTrue(page.notVisibleElement().waitFor().value(Attribute.STYLE).contains("hidden").is(true));
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
    public void test_UiElement_attribute_present() {
        WebTestPage page = getPage();
        page.getRadioBtn().expectThat().value("disabled").isNot(null);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_UiElement_attribute_present_fails() {
        WebTestPage page = getPage();
        page.getRadioBtn().expectThat().value("disabled").is(null);
    }

    @Test
    public void test_UiElement_inexistent_attribute_present() {
        WebTestPage page = getPage();
        page.getRadioBtn().expectThat().value("not-existent-attribute").is(null);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_UiElement_inexistent_attribute_present_fails() {
        WebTestPage page = getPage();
        page.getRadioBtn().expectThat().value("not-existent-attribute").isNot(null);
    }

    @Test
    public void test_UiElement_inexistent_attribute_mapped() {
        WebTestPage page = getPage();
        page.getRadioBtn().expectThat().value("not-existent-attribute").map(String::trim).is(null);
    }

    @Test
    public void test_UiElement_waitFor_text_mapped() {
        WebTestPage page = getPage();
        Assert.assertTrue(page.getOpenAgainLink().waitFor().text().map(String::toLowerCase).is("open again"));
    }

    @Test
    public void test_UiElement_waitFor_text_mapped_fails() {
        WebTestPage page = getPage();
        Assert.assertFalse(page.getOpenAgainLink().waitFor().text().map(String::toLowerCase).is("close again"));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_UiElement_visible_false_fails() {
        WebTestPage page = getPage();
        page.notVisibleElement().expectThat().visible(true).is(true);
    }

    @Test
    public void test_inexistent_UiElement_present() {
        WebTestPage page = getPage();
        page.nonExistentElement().expectThat().present().is(false);
    }

    @Test
    public void test_inexistent_UiElement_present_fails() {
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
    public void test_inexistent_UiElement_present_fails_fast() {
        Control.withTimeout(0, () -> test_inexistent_UiElement_present_fails());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_inexistent_UiElement_displayed_fails() {
        WebTestPage page = getPage();
        page.nonExistentElement().expectThat().displayed().is(false);
    }

    @Test
    public void test_UiElement_screenshot() {
        WebTestPage page = getPage();
        ImageAssertion screenshot = page.notVisibleElement().expectThat().screenshot();
        screenshot.file().exists().is(true);
    }

    @Test
    public void test_inexistent_UiElement_screenshot_fails() {
        WebTestPage page = getPage();
        String msg=null;
        try {
            page.nonExistentElement().expectThat().screenshot().file().exists().is(true);
        } catch (ElementNotFoundException e) {
            msg = e.getMessage();
        }
        Assert.assertStartsWith(msg, "Element not found", ElementNotFoundException.class.toString());
    }

//    @Test
//    public void test_Attributes() {
//        WebTestPage page = getPage();
//        UiElement attributes = page.getFinder().findByQa("section/attributeTest");
//
//        //attributes.value("ariaExpanded").is("true");
//        attributes.expectThat().value("aria-expanded").is("true");
//        //attributes.value("dataCompletelyCustomAttribute").is("true");
//        attributes.expectThat().value("data-completely-custom-attribute").is("yes");
//    }

    @Test
    public void test_UiElement_click_retry() {
        WebTestPage page = getPage();
        UiElement disableMyselfBtn = page.getFinder().findById("disableMyselfBtn");
        disableMyselfBtn.expectThat().enabled(true);
        AtomicInteger retryCount = new AtomicInteger();
        Control.retryFor(10, () -> {
            Control.withTimeout(1, () -> {
                retryCount.incrementAndGet();
                disableMyselfBtn.click();
                disableMyselfBtn.expectThat().enabled(false);
            });
        });
        Assert.assertEquals(retryCount.get(), 5, "Retry count");
        disableMyselfBtn.expectThat().enabled(false);
    }

    @Test
    public void test_UiElement_click_retry_fails() {
        WebTestPage page = pageFactory.createPage(WebTestPage.class, getWebDriver());
        UiElement disableMyselfBtn = page.getFinder().findById("disableMyselfBtn");
        disableMyselfBtn.expectThat().enabled(true);
        AtomicInteger retryCount = new AtomicInteger();
        try {
            Control.retryFor(3, () -> {
                Control.withTimeout(1, () -> {
                    retryCount.incrementAndGet();
                    disableMyselfBtn.click();
                    disableMyselfBtn.expectThat().enabled(false);
                });
            });
        } catch (Exception e) {
            Assert.assertStartsWith(e.getMessage(), "Retry sequence timed out", e.getClass().getSimpleName());
            Assert.assertEndsWith(e.getCause().getMessage(), "@enabled< actual [true] is one of [false, 'off', '0', 'no']", e.getCause().getClass().getSimpleName());
        }
        Assert.assertEquals(retryCount.get(), 3, "Retry count");
    }

}
