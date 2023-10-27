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
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.layout.LayoutCheck;
import eu.tsystems.mms.tic.testframework.pageobjects.DefaultUiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.LocatorFactoryProvider;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.testng.annotations.Test;

public class LayoutCheckTest extends AbstractTestSitesTest implements LocatorFactoryProvider {

    @Override
    protected TestPage getTestPage() {
        return TestPage.LAYOUT;
    }

    private UiElement getUIElementQa(final String qaTag) {
        return new DefaultUiElementFactory().createWithWebDriver(getWebDriver(), LOCATE.byQa(qaTag));
    }

    @Test
    public void testT01_CheckElementLayout() {
        UiElement uiElement = getUIElementQa("section/layoutTestArticle");
        uiElement.expect().screenshot().pixelDistance("TestArticleElement").isLowerThan(1.3);

        uiElement = getUIElementQa("section/invisibleTestArticle");
        uiElement.expect().screenshot().pixelDistance("InvisibleTestArticle").isLowerThan(1.3);
    }

    @Test
    public void testT02_CheckElementLayoutWithSubfolder() {
        UiElement uiElement = getUIElementQa("section/layoutTestArticle");
        uiElement.assertThat().screenshot().pixelDistance("subfolder/TestArticle").isLowerThan(1.3);

        uiElement = getUIElementQa("section/invisibleTestArticle");
        uiElement.assertThat().screenshot().pixelDistance("subfolder/InvisibleTestArticle").isLowerThan(1.3);
    }

    @Test
    public void testT03_CheckElementVisibility() {
        UiElement uiElement = getUIElementQa("section/layoutTestArticle");
        Page helperPage = new Page(uiElement.getWebDriver());
        int top = helperPage.expect().viewport().top().getActual();

        uiElement.assertThat().visibleFull().is(true);

        uiElement = getUIElementQa("section/invisibleTestArticle");
        uiElement.assertThat().visibleFull().is(false);

        uiElement.scrollIntoView();
        helperPage.expect().viewport().top().isGreaterThan(top);

        uiElement.assertThat().visibleFull().is(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT04_CheckElementLayoutDistance_fails() {
        UiElement uiElement = getUIElementQa("section/layoutTestArticle");
        uiElement.assertThat().screenshot().pixelDistance("TestArticleFailed").isLowerThan(1);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT05_CheckElementLayoutSize_fails() {
        UiElement uiElement = getUIElementQa("section/layoutTestArticle");
        uiElement.assertThat().screenshot().pixelDistance("TestArticle-90-percent-width").isLowerThan(1);
    }

    @Test
    public void testT06_CheckPageLayout() {
        LayoutCheck.assertScreenshot(getWebDriver(), "LayoutTestPage", 5);
    }
}
