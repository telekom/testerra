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
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.core.test.layoutcheck;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LayoutCheckTest_NewApi extends AbstractTestSitesTest implements Loggable {

    @Override
    protected TestPage getStartPage() {
        return TestPage.LAYOUT;
    }

    @BeforeMethod
    private FluentLayoutCheckPage preparePage() {
        FluentLayoutCheckPage page = pageFactory.createPage(FluentLayoutCheckPage.class);
        page.call(TestPage.LAYOUT.getUrl());
        return page;
    }

    @Test
    public void testCheckElementLayout() {
        FluentLayoutCheckPage page = preparePage();
        IGuiElement guiElement = page.getGuiElementQa("section/layoutTestArticle");
        guiElement.screenshot().pixelDistance("TestArticle").lowerThan(1.3);

        guiElement = page.getGuiElementQa("section/invisibleTestArticle");
        guiElement.screenshot().pixelDistance("InvisibleTestArticle").lowerThan(1.3);
    }

    @Test
    public void testCheckElementVisibility() {
        FluentLayoutCheckPage page = preparePage();
        IGuiElement guiElement = page.getGuiElementQa("section/layoutTestArticle");
        guiElement.visible(true).isTrue();

        guiElement = page.getGuiElementQa("section/invisibleTestArticle");
        guiElement.visible(false).isFalse();

        // Scroll to offset doesn't work
        //guiElement.scrollToElement(300);
        //Assert.assertFalse(guiElement.isVisible(true));

        guiElement.scrollTo();
        guiElement.visible(true).isTrue();
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testCheckElementLayoutDistance() {
        FluentLayoutCheckPage page = preparePage();
        IGuiElement guiElement = page.getGuiElementQa("section/layoutTestArticle");
        guiElement.screenshot().pixelDistance("TestArticleChrome").lowerThan(10);
    }

    @Test
    public void testCheckPageLayout() {
        FluentLayoutCheckPage page = preparePage();
        page.screenshot().pixelDistance("LayoutTestPage").lowerThan(1);
    }
}
