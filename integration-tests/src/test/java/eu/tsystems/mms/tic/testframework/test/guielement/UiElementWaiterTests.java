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
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UiElementWaiterTests extends AbstractExclusiveTestSitesTest<WebTestPage> implements AssertProvider {

    @Override
    public Class<WebTestPage> getPageClass() {
        return WebTestPage.class;
    }

    @Test
    public void testT01_UiElement_expect_followedBy_waitFor() {
        WebTestPage page = getPage();
        try {
            page.notVisibleElement().expect().displayed(true);
        } catch (AssertionError error) {
            ASSERT.assertEndsWith(error.getMessage(), "is true");
        }
        ASSERT.assertTrue(page.notDisplayedElement().waitFor().displayed(false), "Display status of not displayed element");
    }

    @Test
    public void testT02_UiElement_waitFor_followedBy_expect() {
        WebTestPage page = getPage();
        ASSERT.assertTrue(page.notDisplayedElement().waitFor().displayed(false), "Display status of not displayed element");
        try {
            page.notVisibleElement().expect().displayed(true);
        } catch (AssertionError error) {
            ASSERT.assertEndsWith(error.getMessage(), "is true");
        }
    }

    @Test
    public void testT03_UiElement_waitFor_fast() {
        WebTestPage page = getPage();
        long start = System.currentTimeMillis();
        Assert.assertFalse(page.notVisibleElement().waitFor(0).attribute(Attribute.STYLE).is("humbug"));
        Assert.assertTrue(page.notVisibleElement().waitFor(0).attribute(Attribute.STYLE).contains("hidden").is(true));
        ASSERT.assertLowerEqualThan((System.currentTimeMillis() - start) / 1000, UiElement.Properties.ELEMENT_TIMEOUT_SECONDS.asLong());
    }

    @Test
    public void testT04_UiElement_waitFor_text() {
        WebTestPage page = getPage();
        ASSERT.assertTrue(page.getOpenAgainLink().waitFor().text("Open again"));
    }

    @Test
    public void testT05_UiElement_waitFor_text_fails() {
        WebTestPage page = getPage();
        ASSERT.assertFalse(page.getOpenAgainLink().waitFor().text("Close again"));
    }

    @Test
    public void testT06_UiElement_waitFor_text_mapped() {
        WebTestPage page = getPage();
        ASSERT.assertTrue(page.getOpenAgainLink().waitFor().text().map(String::toLowerCase).is("open again"));
    }

    @Test
    public void testT07_UiElement_waitFor_text_mapped_fails() {
        WebTestPage page = getPage();
        ASSERT.assertFalse(page.getOpenAgainLink().waitFor().text().map(String::toLowerCase).is("close again"));
    }

}
