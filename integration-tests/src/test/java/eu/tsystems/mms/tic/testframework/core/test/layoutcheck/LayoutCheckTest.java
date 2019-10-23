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
import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.layout.LayoutCheck;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;
import eu.tsystems.mms.tic.testframework.utils.AssertUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.TakesScreenshot;
import org.testng.annotations.Test;

import java.math.BigDecimal;

public class LayoutCheckTest extends AbstractTestSitesTest implements Loggable {

    @Override
    protected TestPage getStartPage() {
        return TestPage.LAYOUT;
    }

    private GuiElement getGuiElementQa(final String qaTag) {
        return new GuiElement(WebDriverManager.getWebDriver(), Locate.by().qa(qaTag));
    }

    @Test
    public void testCheckElementLayout() {
        GuiElement GuiElement = getGuiElementQa("section/layoutTestArticle");
        GuiElement.asserts().assertScreenshot("TestArticle", 1.3);

        GuiElement = getGuiElementQa("section/invisibleTestArticle");
        GuiElement.asserts().assertScreenshot("InvisibleTestArticle", 1.3);
    }

    @Test
    public void testCheckElementVisibility() {
        GuiElement GuiElement = getGuiElementQa("section/layoutTestArticle");
        GuiElement.asserts().assertVisible(true);

        GuiElement = getGuiElementQa("section/invisibleTestArticle");
        GuiElement.asserts().assertNotVisible();

        // Scroll to offset doesn't work
        //GuiElement.scrollToElement(300);
        //Assert.assertFalse(GuiElement.isVisible(true));

        GuiElement.scrollToElement();
        GuiElement.asserts().assertVisible(true);
    }

    @Test(expectedExceptions = TimeoutException.class)
    public void testCheckElementLayoutDistance() {
        GuiElement GuiElement = getGuiElementQa("section/layoutTestArticle");
        GuiElement.asserts().assertScreenshot("TestArticleChrome", 10);
    }

    @Test
    public void testCheckPageLayout() {
        LayoutCheck.MatchStep matchStep = LayoutCheck.matchPixels((TakesScreenshot) WebDriverManager.getWebDriver(), "LayoutTestPage");
        AssertUtils.assertLowerEqualThan(new BigDecimal(matchStep.distance), new BigDecimal(1), "LayoutTestPage pixel distance");
    }

}
