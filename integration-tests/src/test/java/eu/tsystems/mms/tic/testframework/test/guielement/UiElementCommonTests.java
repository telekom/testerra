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
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.UiElementAssertionError;
import eu.tsystems.mms.tic.testframework.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.XPath;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UiElementCommonTests extends AbstractExclusiveTestSitesTest<WebTestPage> implements AssertProvider {

    @Override
    public Class<WebTestPage> getPageClass() {
        return WebTestPage.class;
    }

    @Test
    public void testT01_inexistent_UiElement_foundElements() {
        getPage().inexistentElement().expect().foundElements().is(0);
    }

    @Test(expectedExceptions = UiElementAssertionError.class, expectedExceptionsMessageRegExp = ".*text=\\[null] equals \\[]$")
    public void testT02_inexistent_UiElement_AssertionError() {
        getPage().inexistentElement().expect().text().is("");
    }

    @Test
    public void testT03_unique_UiElement_foundElements() {
        getPage().uniqueElement().expect().foundElements().is(1);
    }

    @Test
    public void testT04_not_unique_UiElement_foundElements() {
        getPage().notUniqueElement().expect().foundElements().isNot(1);
    }

    @Test
    public void testT05_UiElement_optional_assert() {
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
    public void testT06_collected_assert() {
        CONTROL.collectAssertions(() -> {
            CONTROL.withTimeout(1, this::performFails);
        });
    }

    @Test
    public void testT07_UiElement_screenshot() {
        WebTestPage page = getPage();
        ImageAssertion screenshot = page.notVisibleElement().expect().screenshot();
        screenshot.file().exists().is(true);
    }

    @Test
    public void testT08_inexistent_UiElement_screenshot_fails() {
        WebTestPage page = getPage();
        String msg = null;
        try {
            page.inexistentElement().expect().screenshot().file().exists().is(true);
        } catch (ElementNotFoundException e) {
            msg = e.getMessage();
        }
        ASSERT.assertEndsWith(msg, "not found", ElementNotFoundException.class.toString());
    }

    @Test(expectedExceptions = UiElementAssertionError.class)
    public void testT09_UiElement_empty() {
        UiElement empty = getPage().getFinder().createEmpty().setName("SubmitButton");
        empty.expect().displayed(true);
        Assert.assertEquals(empty.toString(true), "WebTestPage -> EmptyUiElement(SubmitButton)");
    }

    @Test
    public void testT10_UiElement_text_assert() {
        WebTestPage page = getPage();
        UiElement element = page.getFinder().find(XPath.from("label").text().hasWords("TimeOut in Millis (0-inf)"));
        element.expect().text().hasWords("TimeOut in Millis (0-inf)").is(true);
        element.expect().text().hasWords("TimeOut in Millis").is(true);
        element.expect().text().hasWords("0-inf)").is(true);
        element.expect().text().hasWords("TimeOut in Millis".split("\\s+")).is(true);
        element.expect().text().hasWords("TimeOut", "in", "Millis").is(true);

        element.expect().text().hasWords("Out").is(false);



    }

}
