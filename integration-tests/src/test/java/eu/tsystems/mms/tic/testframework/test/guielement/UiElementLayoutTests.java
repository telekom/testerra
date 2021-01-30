/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.test.guielement;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.WebTestPage;
import eu.tsystems.mms.tic.testframework.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.test.PageFactoryTest;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class UiElementLayoutTests extends AbstractTestSitesTest implements PageFactoryTest {

    @Override
    public WebTestPage getPage() {
        return pageFactory.createPage(WebTestPage.class, getClassExclusiveWebDriver());
    }

    @Test
    public void testT01_LeftOf() {
        WebTestPage page = getPage();
        UiElement left = page.getFinder().findById(1);
        UiElement right = page.getFinder().find(By.linkText("Open again"));
        left.expect().bounds().leftOf(right).is(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT02_LeftOfFails() {
        WebTestPage page = getPage();
        UiElement left = page.getFinder().findById(1);
        UiElement right =  page.getFinder().find(By.linkText("Open again"));
        Control.withTimeout(0, () -> right.expect().bounds().leftOf(left).is(true));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT03_LeftOfFailsSameCoords() {
        WebTestPage page = getPage();
        UiElement left = page.getFinder().findById(1);
        UiElement right = page.getFinder().findById(5);
        Control.withTimeout(0, () -> right.expect().bounds().leftOf(left).is(true));
    }

    /*
    Right
     */

    @Test
    public void testT11_RightOf() {
        WebTestPage page = getPage();
        UiElement left = page.getFinder().findById(1);
        UiElement right = page.getFinder().find(By.linkText("Open again"));
        right.expect().bounds().rightOf(left).is(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT12_RightOfFails() {
        WebTestPage page = getPage();
        UiElement left = page.getFinder().findById(1);
        UiElement right = page.getFinder().find(By.linkText("Open again"));
        Control.withTimeout(0, () -> left.expect().bounds().rightOf(right).is(true));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT13_RightOfFailsSameCoords() {
        WebTestPage page = getPage();
        UiElement left = page.getFinder().findById(1);
        UiElement right = page.getFinder().findById(5);
        Control.withTimeout(0, () -> left.expect().bounds().rightOf(right).is(true));
    }

    /*
    Top
     */

    @Test
    public void testT21_Above()  {
        WebTestPage page = getPage();
        UiElement oben = page.getFinder().findById(1);
        UiElement unten = page.getFinder().findById(5);
        oben.expect().bounds().above(unten).is(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT22_AboveFails() {
        WebTestPage page = getPage();
        UiElement oben = page.getFinder().findById(1);
        UiElement unten = page.getFinder().findById(5);
        Control.withTimeout(0, () -> unten.expect().bounds().above(oben).is(true));
    }

    /*
    Bottom
     */

    @Test
    public void testT31_Below() {
        WebTestPage page = getPage();
        UiElement oben = page.getFinder().findById(1);
        UiElement unten = page.getFinder().findById(5);
        unten.expect().bounds().below(oben).is(true);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT32_BelowFails() {
        WebTestPage page = getPage();
        UiElement oben = page.getFinder().findById(1);
        UiElement unten = page.getFinder().findById(5);
        Control.withTimeout(0, () -> oben.expect().bounds().below(unten).is(true));
    }

    /*
    Same top coords
     */

    @Test
    public void testT41_SameTop() {
        WebTestPage page = getPage();
        UiElement e1 = page.getFinder().findById(1).find(By.xpath("./.."));
        UiElement e2 = page.getFinder().findById(11).find(By.xpath("./.."));
        e1.expect().bounds().fromTop().toTopOf(e2).is(0);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT42_SameTopFails() {
        WebTestPage page = getPage();
        UiElement e1 = page.getFinder().findById(1).find(By.xpath("./.."));
        UiElement e2 = page.getFinder().findById(11);
        Control.withTimeout(0, () -> e1.expect().bounds().fromTop().toTopOf(e2).is(0));
    }

    @Test
    public void testT43_SameTopWithDelta() {
        WebTestPage page = getPage();
        UiElement e1 = page.getFinder().findById(1).find(By.xpath("./.."));
        UiElement e2 = page.getFinder().findById(11);
        e1.expect().bounds().fromTop().toTopOf(e2).absolute().isLowerEqualThan(20);
    }

    /*
    Same bottom coords
     */

    @Test
    public void testT51_SameBottom() {
        WebTestPage page = getPage();
        UiElement e1 = page.getFinder().findById(1).find(By.xpath("./.."));
        UiElement e2 = page.getFinder().findById(11).find(By.xpath("./.."));
        e1.expect().bounds().fromBottom().toBottomOf(e2).is(0);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT52_SameBottomFails() {
        WebTestPage page = getPage();
        UiElement e1 = page.getFinder().findById(1).find(By.xpath("./.."));
        UiElement e2 = page.getFinder().findById(11);
        Control.withTimeout(0, () -> e1.expect().bounds().fromBottom().toBottomOf(e2).is(0));
    }

    @Test
    public void testT53_SameBottomWithDelta() {
        WebTestPage page = getPage();
        UiElement e1 = page.getFinder().findById(1).find(By.xpath("./.."));
        UiElement e2 = page.getFinder().findById(11);
        e1.expect().bounds().fromBottom().toBottomOf(e2).absolute().isLowerEqualThan(311);
    }

    /*
    Same left coords
     */

    @Test
    public void testT61_SameLeft() {
        WebTestPage page = getPage();
        UiElement e1 = page.getFinder().findById(1);
        UiElement e2 = page.getFinder().findById(5);
        e1.expect().bounds().fromLeft().toLeftOf(e2).is(0);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT62_SameLeftFails() {
        WebTestPage page = getPage();
        UiElement e1 = page.getFinder().findById(1);
        UiElement e2 = page.getFinder().findById(3);
        Control.withTimeout(0, () -> e1.expect().bounds().fromLeft().toLeftOf(e2).is(0));
    }

    @Test
    public void testT63_SameLeftWithDelta() {
        WebTestPage page = getPage();
        UiElement e1 = page.getFinder().findById(1);
        UiElement e2 = page.getFinder().findById(3);
        QuantityAssertion<Integer> integerQuantityAssertion = e1.expect().bounds().fromLeft().toLeftOf(e2);
        integerQuantityAssertion.map(integer -> Math.abs(integer)).isLowerEqualThan(20);
        integerQuantityAssertion.absolute().isLowerEqualThan(20);
    }

    /*
    Same right coords
     */

    @Test
    public void testT71_SameRight() {
        WebTestPage page = getPage();
        UiElement e1 = page.getFinder().findById(1);
        UiElement e2 = page.getFinder().findById(5);
        e1.expect().bounds().fromRight().toRightOf(e2).is(0);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testT72_SameRightFails() {
        WebTestPage page = getPage();
        UiElement e1 = page.getFinder().findById(1);
        UiElement e2 = page.getFinder().findById(3);
        Control.withTimeout(0, () -> e1.expect().bounds().fromRight().toRightOf(e2).is(0));
    }

    @Test
    public void testT73_SameRightWithDelta() {
        WebTestPage page = getPage();
        UiElement e1 = page.getFinder().findById(11);
        UiElement e2 = page.getFinder().findById(12);
        e1.expect().bounds().fromRight().toRightOf(e2).absolute().isLowerEqualThan(150);
    }

    /*
    Inner
     */

    @Test(enabled = false)
    /**
     * There is no implementation for inner borders
     */
    public void testT81_InnerBorders()  {
    }

    @Test(enabled = false)
    /**
     * There is no implementation for inner borders
     */
    public void testT82_Checkon_Assert() {

    }
}
