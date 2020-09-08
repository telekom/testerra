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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.core.test.pageobjects.guielement;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.PageFactoryTest;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.QuantityAssertion;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

/**
 * Created by pele on 31.08.2015.
 */
public class UiElementLayoutTests extends AbstractTestSitesTest implements PageFactoryTest {

    @Test
    public void testT01_LeftOf() throws Exception {
        WebTestPage page = getPage();
        UiElement left = page.findById(1);
        UiElement right = page.find(By.linkText("Open again"));
        left.bounds().leftOf(right).is(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT02_LeftOfFails() throws Exception {
        WebTestPage page = getPage();
        UiElement left = page.findById(1);
        UiElement right =  page.find(By.linkText("Open again"));
        Control.withTimeout(0, () -> right.bounds().leftOf(left).is(true));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT03_LeftOfFailsSameCoords() throws Exception {
        WebTestPage page = getPage();
        UiElement left = page.findById(1);
        UiElement right = page.findById(5);
        Control.withTimeout(0, () -> right.bounds().leftOf(left).is(true));
    }

    /*
    Right
     */

    @Test
    public void testT11_RightOf() throws Exception {
        WebTestPage page = getPage();
        UiElement left = page.findById(1);
        UiElement right = page.find(By.linkText("Open again"));
        right.bounds().rightOf(left).is(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT12_RightOfFails() throws Exception {
        WebTestPage page = getPage();
        UiElement left = page.findById(1);
        UiElement right = page.find(By.linkText("Open again"));
        Control.withTimeout(0, () -> left.bounds().rightOf(right).is(true));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT13_RightOfFailsSameCoords() throws Exception {
        WebTestPage page = getPage();
        UiElement left = page.findById(1);
        UiElement right = page.findById(5);
        Control.withTimeout(0, () -> left.bounds().rightOf(right).is(true));
    }

    /*
    Top
     */

    @Test
    public void testT21_Above() throws Exception {
        WebTestPage page = getPage();
        UiElement oben = page.findById(1);
        UiElement unten = page.findById(5);
        oben.bounds().above(unten).is(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT22_AboveFails() throws Exception {
        WebTestPage page = getPage();
        UiElement oben = page.findById(1);
        UiElement unten = page.findById(5);
        Control.withTimeout(0, () -> unten.bounds().above(oben).is(true));
    }

    /*
    Bottom
     */

    @Test
    public void testT31_Below() throws Exception {
        WebTestPage page = getPage();
        UiElement oben = page.findById(1);
        UiElement unten = page.findById(5);
        unten.bounds().below(oben).is(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT32_BelowFails() throws Exception {
        WebTestPage page = getPage();
        UiElement oben = page.findById(1);
        UiElement unten = page.findById(5);
        Control.withTimeout(0, () -> oben.bounds().below(unten).is(true));
    }

    /*
    Same top coords
     */

    @Test
    public void testT41_SameTop() throws Exception {
        WebTestPage page = getPage();
        UiElement e1 = page.findById(1).find(By.xpath("./.."));
        UiElement e2 = page.findById(11).find(By.xpath("./.."));
        e1.bounds().fromTop().toTopOf(e2).is(0);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT42_SameTopFails() throws Exception {
        WebTestPage page = getPage();
        UiElement e1 = page.findById(1).find(By.xpath("./.."));
        UiElement e2 = page.findById(11);
        Control.withTimeout(0, () -> e1.bounds().fromTop().toTopOf(e2).is(0));
    }

    @Test
    public void testT43_SameTopWithDelta() throws Exception {
        WebTestPage page = getPage();
        UiElement e1 = page.findById(1).find(By.xpath("./.."));
        UiElement e2 = page.findById(11);
        e1.bounds().fromTop().toTopOf(e2).absolute().isLowerEqualThan(20);
    }

    /*
    Same bottom coords
     */

    @Test
    public void testT51_SameBottom() throws Exception {
        WebTestPage page = getPage();
        UiElement e1 = page.findById(1).find(By.xpath("./.."));
        UiElement e2 = page.findById(11).find(By.xpath("./.."));
        e1.bounds().fromBottom().toBottomOf(e2).is(0);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT52_SameBottomFails() throws Exception {
        WebTestPage page = getPage();
        UiElement e1 = page.findById(1).find(By.xpath("./.."));
        UiElement e2 = page.findById(11);
        Control.withTimeout(0, () -> e1.bounds().fromBottom().toBottomOf(e2).is(0));
    }

    @Test
    public void testT53_SameBottomWithDelta() throws Exception {
        WebTestPage page = getPage();
        UiElement e1 = page.findById(1).find(By.xpath("./.."));
        UiElement e2 = page.findById(11);
        e1.bounds().fromBottom().toBottomOf(e2).absolute().isLowerEqualThan(311);
    }

    /*
    Same left coords
     */

    @Test
    public void testT61_SameLeft() throws Exception {
        WebTestPage page = getPage();
        UiElement e1 = page.findById(1);
        UiElement e2 = page.findById(5);
        e1.bounds().fromLeft().toLeftOf(e2).is(0);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT62_SameLeftFails() throws Exception {
        WebTestPage page = getPage();
        UiElement e1 = page.findById(1);
        UiElement e2 = page.findById(3);
        Control.withTimeout(0, () -> e1.bounds().fromLeft().toLeftOf(e2).is(0));
    }

    @Test
    public void testT63_SameLeftWithDelta() throws Exception {
        WebTestPage page = getPage();
        UiElement e1 = page.findById(1);
        UiElement e2 = page.findById(3);
        QuantityAssertion<Integer> integerQuantityAssertion = e1.bounds().fromLeft().toLeftOf(e2);
        integerQuantityAssertion.map(integer -> Math.abs(integer)).isLowerEqualThan(20);
        integerQuantityAssertion.absolute().isLowerEqualThan(20);
    }

    /*
    Same right coords
     */

    @Test
    public void testT71_SameRight() throws Exception {
        WebTestPage page = getPage();
        UiElement e1 = page.findById(1);
        UiElement e2 = page.findById(5);
        e1.bounds().fromRight().toRightOf(e2).is(0);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT72_SameRightFails() throws Exception {
        WebTestPage page = getPage();
        UiElement e1 = page.findById(1);
        UiElement e2 = page.findById(3);
        Control.withTimeout(0, () -> e1.bounds().fromRight().toRightOf(e2).is(0));
    }

    @Test
    public void testT73_SameRightWithDelta() throws Exception {
        WebTestPage page = getPage();
        UiElement e1 = page.findById(11);
        UiElement e2 = page.findById(12);
        e1.bounds().fromRight().toRightOf(e2).absolute().isLowerEqualThan(150);
    }

    /*
    Inner
     */

    @Test(enabled = false)
    /**
     * There is no implementation for inner borders
     */
    public void testT81_InnerBorders() throws Exception {
    }

    @Test(enabled = false)
    /**
     * There is no implementation for inner borders
     */
    public void testT82_Checkon_Assert() throws Exception {

    }

    @Override
    public WebTestPage getPage() {
        return pageFactory.createPage(WebTestPage.class);
    }
}
