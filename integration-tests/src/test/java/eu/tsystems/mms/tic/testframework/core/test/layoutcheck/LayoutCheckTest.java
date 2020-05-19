/*
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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.core.test.layoutcheck;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.layout.LayoutCheck;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LayoutCheckTest extends AbstractTestSitesTest {

    @Override
    protected TestPage getTestPage() {
        return TestPage.LAYOUT;
    }

    private GuiElement getGuiElementQa(final String qaTag) {
        return new GuiElement(WebDriverManager.getWebDriver(), Locate.by().qa(qaTag));
    }

    @Test
    public void testCheckElementLayout() {
        GuiElement guiElement = getGuiElementQa("section/layoutTestArticle");
        guiElement.asserts().assertScreenshot("TestArticle", 1.3);

        guiElement = getGuiElementQa("section/invisibleTestArticle");
        guiElement.asserts().assertScreenshot("InvisibleTestArticle", 1.3);
    }

    @Test
    public void testCheckElementVisibility() {
        GuiElement guiElement = getGuiElementQa("section/layoutTestArticle");
        guiElement.asserts().assertVisible(true);
        Assert.assertTrue(guiElement.isVisible(true));

        guiElement = getGuiElementQa("section/invisibleTestArticle");
        guiElement.asserts().assertNotVisible();
        Assert.assertFalse(guiElement.isVisible(true));

        // Scroll to offset doesn't work
        //guiElement.scrollToElement(300);
        //Assert.assertFalse(guiElement.isVisible(true));

        guiElement.scrollToElement();
        Assert.assertTrue(guiElement.isVisible(true));
        guiElement.asserts().assertVisible(true);
    }

    @Test(expectedExceptions = TimeoutException.class)
    public void testCheckElementLayoutDistance_fails() {
        GuiElement guiElement = getGuiElementQa("section/layoutTestArticle");
        guiElement.asserts().assertScreenshot("TestArticleFailed", 1);
    }

    @Test()
    public void testCheckElementLayoutSize_fails() {
        GuiElement guiElement = getGuiElementQa("section/layoutTestArticle");
        guiElement.asserts().assertScreenshot("TestArticle-90-percent-width", 1);
    }

    @Test
    public void testCheckPageLayout() {
        LayoutCheck.assertScreenshot(WebDriverManager.getWebDriver(), "LayoutTestPage", 1);
    }

}
