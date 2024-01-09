/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.LayoutTestPage;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.layout.LayoutCheck;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import org.testng.annotations.Test;

public class LayoutCheckTest extends AbstractTestSitesTest implements PageFactoryProvider {

    @Override
    protected TestPage getTestPage() {
        return TestPage.LAYOUT;
    }

    @Test
    public void testT01_CheckElementLayout() {
        LayoutTestPage page = PAGE_FACTORY.createPage(LayoutTestPage.class, this.getWebDriver());

        page.layoutTestArticle.expect().screenshot().pixelDistance("TestArticleElement").isLowerThan(1.3);
        page.invisibleTestArticle.expect().screenshot().pixelDistance("InvisibleTestArticleElement").isLowerThan(1.3);
    }

    @Test
    public void testT02_CheckElementLayoutWithSubfolder() {
        LayoutTestPage page = PAGE_FACTORY.createPage(LayoutTestPage.class, this.getWebDriver());

        page.layoutTestArticle.assertThat().screenshot().pixelDistance("subfolder/TestArticleElement").isLowerThan(1.3);
        page.invisibleTestArticle.assertThat().screenshot().pixelDistance("subfolder/InvisibleTestArticleElement").isLowerThan(1.3);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT03_CheckElementLayoutDistance_fails() {
        LayoutTestPage page = PAGE_FACTORY.createPage(LayoutTestPage.class, this.getWebDriver());

        page.layoutTestArticle.assertThat().screenshot().pixelDistance("TestArticleFailed").isLowerThan(1);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT04_CheckElementLayoutSize_fails() {
        LayoutTestPage page = PAGE_FACTORY.createPage(LayoutTestPage.class, this.getWebDriver());

        page.layoutTestArticle.assertThat().screenshot().pixelDistance("TestArticle-90-percent-width").isLowerThan(1);
    }

    @Test
    public void testT05_CheckPageLayout() {
        LayoutCheck.assertScreenshot(getWebDriver(), "LayoutTestPage", 5);
    }
}
