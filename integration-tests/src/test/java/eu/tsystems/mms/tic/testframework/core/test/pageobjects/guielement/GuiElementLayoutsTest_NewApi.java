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
package eu.tsystems.mms.tic.testframework.core.test.pageobjects.guielement;

import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.FluentWebTestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.layout.Layout;
import org.openqa.selenium.By;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by pele on 31.08.2015.
 */
public class GuiElementLayoutsTest_NewApi extends GuiElementStandardFunctionsTest {
    private FluentWebTestPage page;

    @BeforeMethod
    private void createPage() {
        page = pageFactory.createPage(FluentWebTestPage.class);
    }
    /*
    Left
     */

    @Test
    public void testT01_LeftOf() throws Exception {
        GuiElement left = getGuiElementBy(By.id("1"));
        GuiElement right = getGuiElementBy(By.linkText("Open again"));
        left.rect().leftOf(right).isTrue();
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT02_LeftOfFails() throws Exception {
        GuiElement left = getGuiElementBy(By.id("1"));
        GuiElement right = getGuiElementBy(By.linkText("Open again"));
        right.rect().leftOf(left).isTrue();
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT03_LeftOfFailsSameCoords() throws Exception {
        GuiElement left = getGuiElementBy(By.id("1"));
        GuiElement right = getGuiElementBy(By.id("5"));
        right.rect().leftOf(left).isTrue();
    }

    /*
    Right
     */

    @Test
    public void testT11_RightOf() throws Exception {
        GuiElement left = getGuiElementBy(By.id("1"));
        GuiElement right = getGuiElementBy(By.linkText("Open again"));
        right.rect().rightOf(left).isTrue();
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT12_RightOfFails() throws Exception {
        GuiElement left = getGuiElementBy(By.id("1"));
        GuiElement right = getGuiElementBy(By.linkText("Open again"));
        left.rect().rightOf(right).isTrue();
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT13_RightOfFailsSameCoords() throws Exception {
        GuiElement left = getGuiElementBy(By.id("1"));
        GuiElement right = getGuiElementBy(By.id("5"));
        left.asserts().assertLayout(Layout.outer().rightOf(right));
    }

    /*
    Top
     */

    @Test
    public void testT21_Above() throws Exception {
        GuiElement oben = getGuiElementBy(By.id("1"));
        GuiElement unten = getGuiElementBy(By.id("5"));
        oben.asserts().assertLayout(Layout.outer().above(unten));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT22_AboveFails() throws Exception {
        GuiElement oben = getGuiElementBy(By.id("1"));
        GuiElement unten = getGuiElementBy(By.id("5"));
        unten.asserts().assertLayout(Layout.outer().above(oben));
    }

    /*
    Bottom
     */

    @Test
    public void testT31_Below() throws Exception {
        GuiElement oben = getGuiElementBy(By.id("1"));
        GuiElement unten = getGuiElementBy(By.id("5"));
        unten.asserts().assertLayout(Layout.outer().below(oben));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT32_BelowFails() throws Exception {
        GuiElement oben = getGuiElementBy(By.id("1"));
        GuiElement unten = getGuiElementBy(By.id("5"));
        oben.asserts().assertLayout(Layout.outer().below(unten));
    }

    /*
    Same top coords
     */

    @Test
    public void testT41_SameTop() throws Exception {
        GuiElement e1 = getGuiElementBy(By.id("1")).getSubElement(By.xpath("./.."));
        GuiElement e2 = getGuiElementBy(By.id("11")).getSubElement(By.xpath("./.."));
        e1.asserts().assertLayout(Layout.outer().sameTop(e2, 0));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT42_SameTopFails() throws Exception {
        GuiElement e1 = getGuiElementBy(By.id("1")).getSubElement(By.xpath("./.."));
        GuiElement e2 = getGuiElementBy(By.id("11"));
        e1.asserts().assertLayout(Layout.outer().sameTop(e2, 0));
    }

    @Test
    public void testT43_SameTopWithDelta() throws Exception {
        GuiElement e1 = getGuiElementBy(By.id("1")).getSubElement(By.xpath("./.."));
        GuiElement e2 = getGuiElementBy(By.id("11"));
        e1.asserts().assertLayout(Layout.outer().sameTop(e2, 20));
    }

    /*
    Same bottom coords
     */

    @Test
    public void testT51_SameBottom() throws Exception {
        GuiElement e1 = getGuiElementBy(By.id("1")).getSubElement(By.xpath("./.."));
        GuiElement e2 = getGuiElementBy(By.id("11")).getSubElement(By.xpath("./.."));
        e1.asserts().assertLayout(Layout.outer().sameBottom(e2, 0));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT52_SameBottomFails() throws Exception {
        GuiElement e1 = getGuiElementBy(By.id("1")).getSubElement(By.xpath("./.."));
        GuiElement e2 = getGuiElementBy(By.id("11"));
        e1.asserts().assertLayout(Layout.outer().sameBottom(e2, 0));
    }

    @Test
    public void testT53_SameBottomWithDelta() throws Exception {
        GuiElement e1 = getGuiElementBy(By.id("1")).getSubElement(By.xpath("./.."));
        GuiElement e2 = getGuiElementBy(By.id("11"));
        e1.asserts().assertLayout(Layout.outer().sameBottom(e2, 311));
    }

    /*
    Same left coords
     */

    @Test
    public void testT61_SameLeft() throws Exception {
        GuiElement e1 = getGuiElementBy(By.id("1"));
        GuiElement e2 = getGuiElementBy(By.id("5"));
        e1.asserts().assertLayout(Layout.outer().sameLeft(e2, 0));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT62_SameLeftFails() throws Exception {
        GuiElement e1 = getGuiElementBy(By.id("1"));
        GuiElement e2 = getGuiElementBy(By.id("3"));
        e1.asserts().assertLayout(Layout.outer().sameLeft(e2, 0));
    }

    @Test
    public void testT63_SameLeftWithDelta() throws Exception {
        GuiElement e1 = getGuiElementBy(By.id("1"));
        GuiElement e2 = getGuiElementBy(By.id("3"));
        e1.asserts().assertLayout(Layout.outer().sameLeft(e2, 10));
    }

    /*
    Same right coords
     */

    @Test
    public void testT71_SameRight() throws Exception {
        GuiElement e1 = getGuiElementBy(By.id("1"));
        GuiElement e2 = getGuiElementBy(By.id("5"));
        e1.asserts().assertLayout(Layout.outer().sameRight(e2, 0));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT72_SameRightFails() throws Exception {
        GuiElement e1 = getGuiElementBy(By.id("1"));
        GuiElement e2 = getGuiElementBy(By.id("3"));
        e1.asserts().assertLayout(Layout.outer().sameRight(e2, 0));
    }

    @Test
    public void testT73_SameRightWithDelta() throws Exception {
        GuiElement e1 = getGuiElementBy(By.id("11"));
        GuiElement e2 = getGuiElementBy(By.id("12"));
        e1.asserts().assertLayout(Layout.outer().sameRight(e2, 150));
    }

    /*
    Inner
     */

    @Test
    public void testT81_InnerBorders() throws Exception {
        GuiElement e1 = getGuiElementBy(By.id("1"));
        Layout.LayoutBorders innerBorders = Layout.inner().getElementLayoutBorders(e1);
        Layout.LayoutBorders outerBorders = Layout.outer().getElementLayoutBorders(e1);

        Assert.assertTrue(innerBorders.getLeft() > outerBorders.getLeft(), "inner left > outer left");
        Assert.assertTrue(innerBorders.getRight() < outerBorders.getRight(), "inner right < outer right");
        Assert.assertTrue(innerBorders.getTop() > outerBorders.getTop(), "inner top > outer top");
        Assert.assertTrue(innerBorders.getBottom() < outerBorders.getBottom(), "inner bottom < outer bottom");
    }

    @Test
    public void testT82_Checkon_Assert() throws Exception {
        GuiElement e1 = getGuiElementBy(By.id("1"));
        GuiElement e2 = getGuiElementBy(By.id("11"));
        boolean catched = false;
        try {
            e1.asserts().assertLayout(Layout.inner().sameRight(e2, 0));
        } catch (AssertionError e) {
            catched = true;
        }
        Assert.assertTrue(catched, "Assertion is thrown");
    }

    @Override
    public GuiElement getGuiElementBy(By locator) {
        return (GuiElement) page.getGuiElementBy(locator);
    }

    @Override
    protected TestPage getTestPage() {
        return TestPage.INPUT_TEST_PAGE;
    }
}
