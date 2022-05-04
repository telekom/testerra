/*
 * Testerra
 *
 * (C) 2022, Martin Großmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.testing.TestController;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 04.05.2022
 *
 * @author mgn
 */
public class UiElementActionTests extends AbstractExclusiveTestSitesTest<WebTestPage> implements AssertProvider {

    @Override
    public Class<WebTestPage> getPageClass() {
        return WebTestPage.class;
    }

    @Test
    public void testT01_UiElement_click_retry_timeout() {
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
    public void testT02_UiElement_click_retry_timeout_fails() {
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
    public void testT03_UiElement_click_retry_times() {
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

    @Test(expectedExceptions = TimeoutException.class)
    public void testT04_inexistent_UiElement_click_fails() {
        WebTestPage page = getPage();
        page.inexistentElement().click();
    }

    @Test
    public void testT05_UiElement_clear() {
        WebTestPage page = getPage();

        UiElement element = page.getFinder().findById(1);
        element.sendKeys("Test");
        element.clear().expect().attribute(Attribute.VALUE).is("");
    }

    @Test
    public void testT06_UiElement_type() {
        WebTestPage page = getPage();

        UiElement element = page.getFinder().findById(5);
        element.type("text");
        element.expect().attribute(Attribute.VALUE).is("text");
        element.expect().attribute(Attribute.VALUE).isContaining("tex");
        element.type("foo");
        element.expect().attribute(Attribute.VALUE).is("foo");
    }

}
