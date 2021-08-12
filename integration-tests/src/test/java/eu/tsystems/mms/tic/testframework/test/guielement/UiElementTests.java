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

import eu.tsystems.mms.tic.testframework.AbstractExclusiveTestSitesTest;
import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.exceptions.UiElementAssertionError;
import eu.tsystems.mms.tic.testframework.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.StringAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.UiElementAssertion;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.testing.TestController;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UiElementTests extends AbstractExclusiveTestSitesTest<WebTestPage> implements AssertProvider {

    @Override
    public Class<WebTestPage> getPageClass() {
        return WebTestPage.class;
    }

    @Test
    public void test_UiElement_clear() {
        WebTestPage page = getPage();
        UiElement element = page.getFinder().findById(5);
        element.sendKeys("Test");
        element.clear().expect().text().is("");
    }

    @Test(expectedExceptions = TimeoutException.class)
    public void test_inexistent_UiElement_click_fails() {
        WebTestPage page = getPage();
        page.inexistentElement().click();
    }

    @Test
    public void test_inexistent_UiElement_foundElements() {
        getPage().inexistentElement().expect().foundElements().is(0);
    }

    @Test(expectedExceptions = UiElementAssertionError.class, expectedExceptionsMessageRegExp = ".*text=\\[null] equals \\[]$")
    public void test_inexistent_UiElement_AssertionError() {
        getPage().inexistentElement().expect().text().is("");
    }

    @Test
    public void test_unique_UiElement_foundElements() {
        getPage().uniqueElement().expect().foundElements().is(1);
    }

    @Test
    public void test_not_unique_UiElement_foundElements() {
        getPage().notUniqueElement().expect().foundElements().isNot(1);
    }

    @Test
    public void test_UiElement_displayed_false() {
        WebTestPage page = getPage();
        UiElementAssertion expect = page.notDisplayedElement().expect();
        expect.attribute(Attribute.STYLE).contains("display: none");
        expect.displayed(false);
        expect.hasClasses("button").is(false);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_UiElement_displayed_false_fails() {
        WebTestPage page = getPage();
        page.notDisplayedElement().expect().displayed(true);
    }

    @Test()
    public void test_UiElement_displayed_false_fails_withMessage() {
        WebTestPage page = getPage();
        try {
            page.notDisplayedElement().expect().displayed().is(true,"Important element visibility");
        } catch (AssertionError e) {
            ASSERT.assertContains(e.getMessage(), "Important element visibility");
        }
    }

    @Test
    public void test_UiElement_visible_false() {
        WebTestPage page = getPage();
        page.notVisibleElement().expect().attribute(Attribute.STYLE).contains("hidden").is(true);
        page.notVisibleElement().expect().attribute("style").contains("hidden").is(true);
        page.notVisibleElement().expect().visible(true).is(false);
        page.notVisibleElement().expect().visible(false).is(false);
        page.notDisplayedElement().expect().css("display").is("none");
        page.notDisplayedElement().expect().hasClasses("mumu").is(false);
    }

    @Test
    public void test_UiElement_waitFor_followedBy_expect() {
        WebTestPage page = getPage();
        ASSERT.assertTrue(page.notDisplayedElement().waitFor().displayed(false), "Display status of not displayed element");
        try {
            page.notVisibleElement().expect().displayed(true);
        } catch (AssertionError error) {
            ASSERT.assertEndsWith(error.getMessage(), "is true");
        }
    }

    @Test
    public void test_UiElement_optional_assert() {
        CONTROL.optionalAssertions(() -> {
            CONTROL.withTimeout(1, this::performFails);
        });
    }

    private void performFails() {
        ASSERT.fail("This fails");
        this.getPage().inexistentElement().expect().present(true);
        ASSERT.fail("And this also fails");
    }

    @Test
    @Fails()
    public void test_collected_assert() {
        CONTROL.collectAssertions(() -> {
            CONTROL.withTimeout(1, this::performFails);
        });
    }

    @Test
    public void test_UiElement_expect_followedBy_waitFor() {
        WebTestPage page = getPage();
        try {
            page.notVisibleElement().expect().displayed(true);
        } catch (AssertionError error) {
            ASSERT.assertEndsWith(error.getMessage(), "is true");
        }
        ASSERT.assertTrue(page.notDisplayedElement().waitFor().displayed(false), "Display status of not displayed element");
    }

    @Test
    public void test_UiElement_waitFor_fast() {
        WebTestPage page = getPage();
        long start = System.currentTimeMillis();
        Assert.assertFalse(page.notVisibleElement().waitFor(0).attribute(Attribute.STYLE).is("humbug"));
        Assert.assertTrue(page.notVisibleElement().waitFor(0).attribute(Attribute.STYLE).contains("hidden").is(true));
        ASSERT.assertLowerEqualThan((System.currentTimeMillis()-start) / 1000, UiElement.Properties.ELEMENT_TIMEOUT_SECONDS.asLong());
    }

    @Test
    public void test_UiElement_waitFor_text() {
        WebTestPage page = getPage();
        ASSERT.assertTrue(page.getOpenAgainLink().waitFor().text("Open again"));
    }

    @Test
    public void test_UiElement_waitFor_text_fails() {
        WebTestPage page = getPage();
        ASSERT.assertFalse(page.getOpenAgainLink().waitFor().text("Close again"));
    }

    @Test
    public void test_UiElement_disabled_attribute_present() {
        WebTestPage page = getPage();
        UiElementAssertion expect = page.getRadioBtn().expect();
        StringAssertion<String> disabled = expect.attribute(Attribute.DISABLED);
        disabled.isNot(null);
        disabled.isNot(false);
        disabled.isNot(Boolean.FALSE);
        disabled.isNot("false");
        disabled.is(true);
        disabled.is(Boolean.TRUE);
        disabled.is("true");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_UiElement_attribute_present_fails() {
        WebTestPage page = getPage();
        page.getRadioBtn().expect().attribute(Attribute.DISABLED).is(null);
    }

    @Test
    public void test_UiElement_inexistent_attribute_present() {
        WebTestPage page = getPage();
        page.getRadioBtn().expect().attribute("not-existent-attribute").is(null);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_UiElement_inexistent_attribute_present_fails() {
        WebTestPage page = getPage();
        page.getRadioBtn().expect().attribute("not-existent-attribute").isNot(null);
    }

    @Test
    public void test_UiElement_inexistent_attribute_mapped() {
        WebTestPage page = getPage();
        page.getRadioBtn().expect().attribute("not-existent-attribute").map(String::trim).is(null);
    }

    @Test
    public void test_UiElement_waitFor_text_mapped() {
        WebTestPage page = getPage();
        ASSERT.assertTrue(page.getOpenAgainLink().waitFor().text().map(String::toLowerCase).is("open again"));
    }

    @Test
    public void test_UiElement_waitFor_text_mapped_fails() {
        WebTestPage page = getPage();
        ASSERT.assertFalse(page.getOpenAgainLink().waitFor().text().map(String::toLowerCase).is("close again"));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void test_UiElement_visible_false_fails() {
        WebTestPage page = getPage();
        page.notVisibleElement().expect().visible(true).is(true);
    }

    @Test
    public void test_inexistent_UiElement_present() {
        WebTestPage page = getPage();
        page.inexistentElement().expect().present().is(false);
    }

    @Test
    public void test_inexistent_UiElement_present_fails() {
        WebTestPage page = getPage();
        String msg = null;
        try {
            page.inexistentElement().expect().present().is(true);
        } catch (AssertionError e) {
            msg = e.getMessage();
        }
        ASSERT.assertEndsWith(msg, "is true", AssertionError.class.toString());
    }

    @Test
    public void test_inexistent_UiElement_present_fails_fast() {
        CONTROL.withTimeout(0, this::test_inexistent_UiElement_present_fails);
    }

    @Test()
    public void test_inexistent_UiElement_displayed_fails() {
        WebTestPage page = getPage();
        page.inexistentElement().expect().displayed().is(false);
    }

    @Test
    public void test_UiElement_screenshot() {
        WebTestPage page = getPage();
        ImageAssertion screenshot = page.notVisibleElement().expect().screenshot();
        screenshot.file().exists().is(true);
    }

    @Test
    public void test_inexistent_UiElement_screenshot_fails() {
        WebTestPage page = getPage();
        String msg=null;
        try {
            page.inexistentElement().expect().screenshot().file().exists().is(true);
        } catch (ElementNotFoundException e) {
            msg = e.getMessage();
        }
        ASSERT.assertEndsWith(msg, "not found", ElementNotFoundException.class.toString());
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
    public void test_UiElement_click_retry_timeout() {
        TestController.Overrides overrides = Testerra.getInjector().getInstance(TestController.Overrides.class);

        // Get default timeout and check if its not the test timeout
        int defaultTimeout = overrides.getTimeoutInSeconds();
        int useTimeoutForTest = 1;
        ASSERT.assertNotEquals(defaultTimeout, useTimeoutForTest);

        WebTestPage page = getPage();
        UiElement disableMyselfBtn = page.getFinder().findById("disableMyselfBtn");

        disableMyselfBtn.expect().enabled(true);
        AtomicInteger retryCount = new AtomicInteger();
        CONTROL.retryFor(10, () -> {
            CONTROL.withTimeout(useTimeoutForTest, () -> {
                retryCount.incrementAndGet();
                disableMyselfBtn.click();
                disableMyselfBtn.expect().enabled(false);
            });
        });
        ASSERT.assertEquals(retryCount.get(), 5, "Retry count");
        disableMyselfBtn.expect().enabled(false);

        // Check if the timeout is default
        ASSERT.assertEquals(overrides.getTimeoutInSeconds(), defaultTimeout);
    }

    @Test
    public void test_UiElement_click_retry_timeout_fails() {
        WebTestPage page = PAGE_FACTORY.createPage(WebTestPage.class, getWebDriver());
        UiElement disableMyselfBtn = page.getFinder().findById("disableMyselfBtn");
        disableMyselfBtn.expect().enabled(true);
        AtomicInteger retryCount = new AtomicInteger();
        try {
            CONTROL.retryFor(3, () -> {
                CONTROL.withTimeout(1, () -> {
                    retryCount.incrementAndGet();
                    disableMyselfBtn.click();
                    disableMyselfBtn.expect().enabled(false);
                });
            });
        } catch (Exception e) {
            ASSERT.assertStartsWith(e.getMessage(), "Retry sequence timed out", e.getClass().getSimpleName());
        }
        ASSERT.assertEquals(retryCount.get(), 3, "Retry count");
    }

    @Test
    public void test_UiElement_click_retry_times() {
        WebTestPage page = getPage();
        UiElement disableMyselfBtn = page.getFinder().findById("disableMyselfImmediatelyBtn");
        disableMyselfBtn.expect().enabled(true);
        AtomicInteger retryCount = new AtomicInteger();
        CONTROL.retryTimes(3, () -> {
            CONTROL.withTimeout(1, () -> {
                retryCount.incrementAndGet();
                disableMyselfBtn.click();
            });
        });
        disableMyselfBtn.expect().enabled(false);
        ASSERT.assertEquals(retryCount.get(), 1, "Retry count");
    }

    @Test(expectedExceptions = UiElementAssertionError.class)
    public void test_UiElement_empty() {
        UiElement empty = getPage().getFinder().createEmpty().setName("SubmitButton");
        empty.expect().displayed(true);
        Assert.assertEquals(empty.toString(true), "WebTestPage -> EmptyUiElement(SubmitButton)");
    }

}
