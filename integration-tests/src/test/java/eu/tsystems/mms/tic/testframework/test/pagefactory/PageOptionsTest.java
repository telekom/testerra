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
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithCheckTimeout;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithPageOptions;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class PageOptionsTest extends AbstractTestSitesTest implements PageFactoryProvider, AssertProvider {

    @Test
    public void testPageOptionsTimeout() {
        WebDriver webDriver = getClassExclusiveWebDriver();
        long start = System.currentTimeMillis();
        long end = start;
        try {
            PAGE_FACTORY.createPage(PageWithPageOptions.class, webDriver);
        } catch (Throwable throwable) {
            end = System.currentTimeMillis();
        }

        ASSERT.assertBetween(end-start, 3000, 3500);
    }

    @Test
    public void testCheckTimeout() {
        WebDriver webDriver = getClassExclusiveWebDriver();
        long start = System.currentTimeMillis();
        long end = start;
        try {
            PAGE_FACTORY.createPage(PageWithCheckTimeout.class, webDriver);
        } catch (Throwable throwable) {
            end = System.currentTimeMillis();
        }

        ASSERT.assertBetween(end-start, 3000, 3500);
    }
}
