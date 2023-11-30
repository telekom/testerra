/*
 * Testerra
 *
 * (C) 2023, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
 *
 */
package eu.tsystems.mms.tic.testframework.test.layoutcheck;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.LayoutTestFramePage;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.LocatorFactoryProvider;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import eu.tsystems.mms.tic.testframework.testing.UiElementFinderFactoryProvider;
import org.testng.annotations.Test;

public class LayoutCheckFramesTest extends AbstractTestSitesTest implements LocatorFactoryProvider, PageFactoryProvider {

    @Override
    protected TestPage getTestPage() {
        return TestPage.LAYOUT_FRAME;
    }

    @Test
    public void testT01_CheckElementLayoutFrame() {
        LayoutTestFramePage page = PAGE_FACTORY.createPage(LayoutTestFramePage.class, getWebDriver());
        page.layoutTestArticle.assertThat().screenshot().pixelDistance("TestArticleElementFrame").isLowerThan(1.3);
    }

    @Test
    public void testT02_CheckElementLayoutWithSubfolderFrame() {
        LayoutTestFramePage page = PAGE_FACTORY.createPage(LayoutTestFramePage.class, getWebDriver());
        page.layoutTestArticle.assertThat().screenshot().pixelDistance("subfolder/TestArticleElementFrame").isLowerThan(1.3);
    }

    @Test
    public void testT03_CheckElementLayoutFrame_Invisible() {
        LayoutTestFramePage page = PAGE_FACTORY.createPage(LayoutTestFramePage.class, getWebDriver());
        page.invisibleTestArticle.expect().screenshot().pixelDistance("InvisibleTestArticleElementFrame").isLowerThan(1.3);
    }

}
