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

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by matz on 30.10.2015.
 */
public abstract class GuiElementWaiterTest extends GuiElementLayoutsTest {

    private void pre_hideText(boolean hide) {
        getTimeOutInput().type("1");
        if (hide) {
            getHideWithTimeOutButton().click();
        } else {
            getShowWithTimeOutButton().click();
        }
    }

    private void pre_setDisableAttributeOnRadioButton(boolean disable) {
        getTimeOutInput().type("1");
        if (disable) {
            getDisableRDButton().click();
        } else {
            getEnableRDButton().click();
        }
    }

    @Test
    public void testT1_GuiElement_waitForIsDisplayedFromWebElement() {
        pre_hideText(true);

        GuiElement g = getDynamicTextElement();
        g.asserts().assertIsNotDisplayed();


        getTimeOutInput().type("4000");
        getShowWithTimeOutButton().click();


        g.setTimeoutInSeconds(8);

        boolean result = g.waitForIsDisplayedFromWebElement();
        Assert.assertTrue(result, "Text was found after TimeOut");
    }

    @Test
    public void testT2N_GuiElement_waitForIsDisplayedFromWebElement() {
        pre_hideText(true);
        GuiElement g = getDynamicTextElement();
        g.setTimeoutInSeconds(3);

        boolean result = g.waitForIsDisplayedFromWebElement();
        Assert.assertFalse(result, "Text was not found after TimeOut");
    }

    @Test
    public void testT3_GuiElement_waitForIsNotDisplayedFromWebElement() {
        //initialize a page with not displayed text
        pre_hideText(false);

        //test if after 3s text is displayed
        GuiElement g = getDynamicTextElement();
        g.asserts().assertIsDisplayed();

        //hide message after one second
        getTimeOutInput().type("4000");
        getHideWithTimeOutButton().click();

        g.setTimeoutInSeconds(8);
        boolean result = g.waitForIsNotDisplayedFromWebElement();
        Assert.assertTrue(result, "Text was not found after TimeOut");
    }

    @Test
    public void testT4N_GuiElement_waitForIsNotDisplayedFromWebElement() {
        //initialize a page with not displayed text
        pre_hideText(false);

        //test if after 3s text is displayed
        GuiElement g = getDynamicTextElement();
        g.setTimeoutInSeconds(3);
        g.asserts().assertIsDisplayed();
        boolean result = g.waitForIsNotDisplayedFromWebElement();
        Assert.assertFalse(result, "Text was not found after TimeOut");
    }


    @Test
    public void testT5_GuiElement_waitForAnyFollowingTextNodeContains() {
        GuiElement element = getParent2();
        boolean ret = element.waitForAnyFollowingTextNodeContains("Show alert");
        Assert.assertTrue(ret, "Found Node with given Text");

    }

    @Test
    public void testT6_GuiElement_waitForAnyFollowingTextNodeContains() {

        getTimeOutInput().type("3000");
        GuiElement parent = getTimeOutDIV();
        parent.setTimeoutInSeconds(4);

        getInsertTextByJSButton().click();

        boolean ret = parent.waitForAnyFollowingTextNodeContains("per Javascript");
        Assert.assertTrue(ret, "Found Node with given Text");
    }

    @Test(expectedExceptions = {AssertionError.class})
    public void testT7N_GuiElement_waitForAnyFollowingTextNodeContains() {
        GuiElement element = getParent2();
        boolean ret = element.waitForAnyFollowingTextNodeContains("42");
        Assert.assertTrue(ret, "Found Node with given Text");
    }

    @Test
    public void testT8_GuiElement_waitForAttribute() {
        GuiElement element = getElementWithAttribute();
        boolean isAttribute = element.waitForAttribute("value");
        Assert.assertEquals(isAttribute, true, "Attribute is present");
    }

    @Test
    public void testT9_GuiElement_waitForAttribute() {
        GuiElement rdButton = getRadio();
        getTimeOutInput().type("3000");

        rdButton.setTimeoutInSeconds(4);
        getAddAttributeWithTimeOutButton().click();

        boolean isAttribute = rdButton.waitForAttribute("someAttribute");
        Assert.assertEquals(isAttribute, true, "Attribute is present");
    }

    @Test
    public void testT10_GuiElement_waitForAttribute() {
        GuiElement element = getElementWithAttribute();
        boolean isAttribute = element.waitForAttribute("href");
        Assert.assertEquals(isAttribute, false, "Attribute is present");
    }

    @Test
    public void testT11_GuiElement_waitForAttribute() {
        GuiElement element = getElementWithAttribute();
        boolean isAttribute = element.waitForAttribute("value", "Button2");
        Assert.assertEquals(isAttribute, true, "Attribute with value is present");
    }

    @Test
    public void testT12_GuiElement_waitForAttribute() {
        GuiElement rdButton = getRadio();
        getTimeOutInput().type("3000");

        rdButton.setTimeoutInSeconds(4);
        getAddAttributeWithTimeOutButton().click();

        boolean isAttribute = rdButton.waitForAttribute("someAttribute", "someValue");
        Assert.assertEquals(isAttribute, true, "Attribute is present");
    }

    @Test
    public void testT13_GuiElement_waitForAttribute() {
        GuiElement element = getElementWithAttribute();
        boolean isAttribute = element.waitForAttribute("value", "Button3");
        Assert.assertEquals(isAttribute, false, "Attribute with value is present");
    }

    @Test
    public void testT14_GuiElement_waitForAttributeContains() {
        GuiElement element = getElementWithAttribute();
        boolean isAttribute = element.waitForAttributeContains("value", "Butt");
        Assert.assertEquals(isAttribute, true, "Attribute with value is present");
    }

    @Test
    public void testT15_GuiElement_waitForAttributeContains() {
        GuiElement rdButton = getRadio();
        getTimeOutInput().type("3000");

        rdButton.setTimeoutInSeconds(4);
        getAddAttributeWithTimeOutButton().click();

        boolean isAttribute = rdButton.waitForAttributeContains("someAttribute", "meVal");
        Assert.assertEquals(isAttribute, true, "Attribute is present");
    }

    @Test
    public void testT16_GuiElement_waitForAttributeContains() {
        GuiElement element = getElementWithAttribute();
        boolean isAttribute = element.waitForAttributeContains("value", "Gandalf");
        Assert.assertEquals(isAttribute, false, "Attribute with value is present");
    }

    @Test
    public void testT17_GuiElement_waitForIsDisabled() {
        boolean found = getDisabledElement().waitForIsDisabled();
        Assert.assertTrue(found, "The Element is found.");
    }

    @Test
    public void testT18_GuiElement_waitForIsDisabled() {
        pre_setDisableAttributeOnRadioButton(false);
        getTimeOutInput().type("3000");
        getDisableRDButton().click();

        GuiElement g = getRadio();
        g.setTimeoutInSeconds(4);

        boolean result = g.waitForIsDisabled();
        Assert.assertTrue(result, "Radio Button was disabled after TimeOut");
    }

    @Test
    public void testT19_GuiElement_waitForIsDisabled() {
        boolean found = getEnabledElement().waitForIsDisabled();
        Assert.assertFalse(found, "The Element is found.");
    }


    /**
     * Test if GuiElement.waitForIsNotPresent works for a not displayed element
     */
    @Test
    public void testT20_GuiElement_waitForIsNotPresent() {
        boolean found = getNotDisplayedElement().waitForIsNotPresent();
        Assert.assertTrue(found, "The Element is found.");
    }

    /**
     * Test if GuiElement.waitForIsNotPresent works for a displayed element
     */
    @Test
    public void testT21N_GuiElement_waitForIsNotPresent() {
        boolean found = getDisplayedElement().waitForIsNotPresent();
        Assert.assertFalse(found, "The Element is found.");
    }


    /**
     * Test if GuiElement.waitForText works for text that is there already
     */
    @Test
    public void testT22_GuiElement_waitForText() {

        getElementWithText().waitForText(testPage.getElementText());
    }

    /**
     * Test if GuiElement.waitForText works for text that is created by JavaScript after Timeout
     */
    @Test
    public void testT23_GuiElement_waitForText() {

        getTimeOutInput().type("2000");

        GuiElement g = getDynamicTextElement();
        g.setTimeoutInSeconds(3);

        getChangeTextByJSButton().click();

        boolean result = g.waitForText("Dieser Text wurde per Javascript ge\u00E4ndert");
        Assert.assertTrue(result, "Text was found after TimeOut");
    }

    /**
     * Test if GuiElement.waitForText works for text that is not there
     */
    @Test
    public void testT24N_GuiElement_waitForText() {
        boolean assertionFailed = getElementWithText().waitForText("100qwertz7");
        Assert.assertFalse(assertionFailed, "The Element doesn't have this Text, the wait should timeout");
    }

    /**
     * Test if GuiElement.waitForTextContains works for a text-fragment that is there already
     */
    @Test
    public void testT25_GuiElement_waitForTextContains() {
        boolean found = getElementWithText().waitForTextContains(testPage.getElementTextArray()[0]);
        Assert.assertTrue(found, "The Element is found");
    }

    /**
     * Test if GuiElement.waitForTextContains works for text that is created by JavaScript after Timeout
     */
    @Test
    public void testT26_GuiElement_waitForTextContains() {

        getTimeOutInput().type("2000");

        GuiElement g = getDynamicTextElement();
        g.setTimeoutInSeconds(3);

        getChangeTextByJSButton().click();

        boolean result = g.waitForTextContains("per Javascript");
        Assert.assertTrue(result, "Text was found after TimeOut");
    }

    /**
     * Test if GuiElement.waitForTextContains works for a text-fragment that is not there
     */
    @Test
    public void testT27N_GuiElement_waitForTextContains() {
        boolean found = getElementWithText().waitForTextContains("blopka321ertz");
        Assert.assertFalse(found, "The Element is found.");
    }

    /**
     * Test if GuiElement.waitForIsDisplayed works for a displayed element
     */
    @Test
    public void testT28_GuiElement_waitForIsDisplayed() {
        boolean found = getDisplayedElement().waitForIsDisplayed();
        Assert.assertTrue(found, "The Element is found.");
    }

    /**
     * Test if GuiElement.waitForIsDisplayed works for text that is created by JavaScript after Timeout
     */
    @Test
    public void testT29_GuiElement_waitForIsDisplayed() {
        pre_hideText(true);
        getTimeOutInput().type("2000");

        getShowWithTimeOutButton().click();

        GuiElement g = getDynamicTextElement();
        g.setTimeoutInSeconds(3);

        boolean result = g.waitForIsDisplayed();
        Assert.assertTrue(result, "Text was found after TimeOut");
    }

    /**
     * Test if GuiElement.waitForIsDisplayed works for a not displayed element
     */
    @Test
    public void testT30N_GuiElement_waitForIsDisplayed() {
        boolean found = getNotDisplayedElement().waitForIsDisplayed();
        Assert.assertFalse(found, "The Element is found.");
    }


    @Test
    public void testT31_GuiElement_waitForIsNotDisplayed() {
        pre_hideText(false);
        getTimeOutInput().type("2000");

        getHideWithTimeOutButton().click();

        GuiElement g = getDynamicTextElement();
        g.setTimeoutInSeconds(3);

        boolean result = g.waitForIsNotDisplayed();
        Assert.assertTrue(result, "Text was found after TimeOut");
    }

    /**
     * Test if GuiElement.waitForIsNotDisplayed works for a displayed element
     */
    @Test
    public void testT32N_GuiElement_waitForIsNotDisplayed() {
        boolean notDisplayed = getDisplayedElement().waitForIsNotDisplayed();
        Assert.assertFalse(notDisplayed, "The Element is checked as (not not) displayed");
    }

    /**
     * Test if GuiElement.waitForIsPresent works for a displayed element
     */
    @Test
    public void testT33_GuiElement_waitForIsPresent() {
        boolean found = getDisplayedElement().waitForIsPresent();
        Assert.assertTrue(found, "The Element is found.");

    }

    /**
     * Test if GuiElement.waitForIsPresent works for text that is created by JavaScript after Timeout
     */
    @Test
    public void testT34_GuiElement_waitForIsPresent() {
        getTimeOutInput().type("2000");

        getInsertTextByJSButton().click();

        GuiElement g = getInsertedTextElement();
        g.setTimeoutInSeconds(3);

        boolean result = g.waitForIsPresent();
        Assert.assertTrue(result, "Text was found after TimeOut");
    }

    /**
     * Test if GuiElement.waitForIsPresent works for a non existing element
     */
    @Test
    public void testT35N_GuiElement_waitForIsPresent() {
        boolean found = getNotExistingElement().waitForIsPresent();
        Assert.assertFalse(found, "The Element is found.");
    }

    /**
     * Test if GuiElement.waitForIsEnabled works for an enabled element
     */
    @Test
    public void testT36_GuiElement_waitForIsEnabled() {
        boolean found = getEnabledElement().waitForIsEnabled();
        Assert.assertTrue(found, "The Element is found.");
    }

    /**
     * Test if GuiElement.waitForIsEnabled works for an Element that is Enabled with Timeout by Javascript
     */
    @Test
    public void testT37_GuiElement_waitForIsEnabled() {
        pre_setDisableAttributeOnRadioButton(true);

        GuiElement g = getRadio();
        g.asserts().assertIsDisabled();

        getTimeOutInput().type("3000");
        getEnableRDButton().click();

        g.setTimeoutInSeconds(6);
        boolean result = g.waitForIsEnabled();
        Assert.assertTrue(result, "Radio Button was enabled after TimeOut");
    }

    /**
     * Test if GuiElement.waitForIsEnabled works for a disabled element
     */
    @Test
    public void testT38N_GuiElement_waitForIsEnabled() {
        boolean found = getDisabledElement().waitForIsEnabled();
        Assert.assertFalse(found, "The Element is found.");
    }

    @Test
    public void testT39_GuiElement_waitForIsSelected() {
        pre_setDisableAttributeOnRadioButton(false);

        getTimeOutInput().type("1");
        getDeselectRadioButtonMitVerzoegerungButton().click();

        GuiElement radio = getRadio();
        radio.asserts().assertIsNotSelected();

        getTimeOutInput().type("4000");
        getSelectRadioButtonMitVerzoegerungButton().click();

        radio.setTimeoutInSeconds(8);
        boolean result = radio.waitForIsSelected();
        Assert.assertTrue(result, "Radio Button was selected after TimeOut");
    }

    @Test
    public void testT40_GuiElement_waitForIsNotSelected() {
        pre_setDisableAttributeOnRadioButton(false);

        getTimeOutInput().type("1");
        getSelectRadioButtonMitVerzoegerungButton().click();

        GuiElement g = getRadio();
        g.asserts().assertIsSelected();

        getTimeOutInput().type("4000");
        getDeselectRadioButtonMitVerzoegerungButton().click();

        g.setTimeoutInSeconds(8);
        boolean result = g.waitForIsNotSelected();
        Assert.assertTrue(result, "Radio Button was deselected after TimeOut");
    }

    //assertion tests
    @Test
    @Fails(validFor = "unsupportedBrowser=true", description = "Does not work in this browser!")
    public void testT41_GuiElement_assertIsNotSelectable() {
        testT13_GuiElement_assertIsNotSelectable();
    }
}
