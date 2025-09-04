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

package eu.tsystems.mms.tic.testframework.test.pagefactory;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithCheckTimeout;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithExistingElement;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithPageOptions;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PageOptionsTest extends AbstractTestSitesTest implements PageFactoryProvider, AssertProvider {

    /**
     * This beforeClass is needed to initialize the used page factory implementation if it's not done at this point.
     *
     * (Currently ResponsivePageFactory)
     */
    @BeforeClass
    public void pageOptionsInitializer() {
        WebDriver webDriver = getClassExclusiveWebDriver();
        PAGE_FACTORY.createPage(PageWithExistingElement.class, webDriver);
    }

    @Test
    public void testT01_PageOptionsTimeout() {
        WebDriver webDriver = getClassExclusiveWebDriver();
//        CONTROL.withTimeout(8, () -> PAGE_FACTORY.createPage(PageWithExistingElement.class, webDriver));
        this.runTest(webDriver, PageWithPageOptions.class, 3000, 3500);
    }

    @Test
    public void testT02_PageCheckTimeout() {
        WebDriver webDriver = getClassExclusiveWebDriver();
        this.runTest(webDriver, PageWithCheckTimeout.class, 3000, 3500);
    }

    /**
     * CONTROL.withTimeout() overrides PageOptions timeout
     */
    @Test
    public void testT03_PageOptionsWithControlTimeout() {
        WebDriver webDriver = getClassExclusiveWebDriver();
        CONTROL.withTimeout(4, () -> this.runTest(webDriver, PageWithPageOptions.class, 4000, 4500));
    }

    /**
     * CONTROL.withTimeout() overrides Check element timeout
     */
    @Test
    public void testT04_PageCheckWithControlTimeout() {
        WebDriver webDriver = getClassExclusiveWebDriver();
        CONTROL.withTimeout(4, () -> this.runTest(webDriver, PageWithCheckTimeout.class, 4000, 4500));
    }

    /**
     * Setting the default timeout with CONTROL.withTimeout is the same running without CONTROL.withTimeout.
     * Here: PageWithPageOptions class has 3 seconds, default are 2 seconds.
     */
    @Test
    public void testT05_PageOptionsWithControlTimeoutWithDefault() {
        int defaultTimeout = UiElement.Properties.ELEMENT_TIMEOUT_SECONDS.asLong().intValue();
        WebDriver webDriver = getClassExclusiveWebDriver();
        CONTROL.withTimeout(defaultTimeout, () -> this.runTest(webDriver, PageWithPageOptions.class, 3000, 3500));
    }

    private <T extends Page> void  runTest(WebDriver webDriver, Class<T> clazz, int minMs, int maxMs) {
        long start = System.currentTimeMillis();
        long end = start;
        try {
            PAGE_FACTORY.createPage(clazz, webDriver);
        } catch (Throwable throwable) {
            end = System.currentTimeMillis();
        }

        ASSERT.assertBetween(end - start, minMs, maxMs);
        log().info("Duration: {}ms", end - start);
    }
}
