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
 package eu.tsystems.mms.tic.testframework.core.test.pageobjects.guielement;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.exceptions.TimeoutException;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.utils.AssertUtils;
import eu.tsystems.mms.tic.testframework.utils.ThrowableUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGuiElementStandardFunctionsTest extends AbstractGuiElementTest {

    /**
     * Test if GuiElement.asserts().assertContainsText works for one string that is contained
     */
    @Test
    public void testT01_GuiElement_assertContainsText() {
        getElementWithText().asserts().assertContainsText(getTestPage().getElementText());
    }

    /**
     * Test if GuiElement.asserts().assertContainsText works for one string that is contained
     */
    @Test
    public void testT01_GuiElement_assertTextContains() {
        getElementWithText().asserts().assertTextContains(getTestPage().getElementText());
    }

    /**
     * Negative test if GuiElement.asserts().assertContainsText works for one string that is not contained
     */
    @Test(expectedExceptions = {AssertionError.class, TimeoutException.class})
    public void testT02N_GuiElement_assertContainsText() {
        getElementWithText().asserts().assertContainsText("i don't exist");
    }

    /**
     * Test if GuiElement.asserts().assertContainsText works for a string array, whose elements are contained
     */
    @Test
    public void testT03_GuiElement_assertContainsText() {
        String[] elementTextArray = getTestPage().getElementTextArray();
        GuiElement elementWithText = getElementWithText();
        for (String s : elementTextArray) {
            elementWithText.asserts().assertContainsText(s);
        }
    }

    /**
     * Test if GuiElement.asserts().assertContainsText works for a string array, whose elements are not contained except for 1
     */
    @Test(expectedExceptions = {AssertionError.class, TimeoutException.class})
    public void testT04N_GuiElement_assertContainsText() {
        String[] strings = {"yxyxyxya", "zzzzzzzxzzzzxzzz", getTestPage().getElementText()};
        for (String s : strings) {
            getElementWithText().asserts().assertContainsText(s);
        }
    }

    /**
     * Test if GuiElement.asserts().assertText works for a string that is its text
     */
    @Test
    public void testT05_GuiElement_assertText() {
        getElementWithText().asserts().assertText(getTestPage().getElementText());
    }

    /**
     * Test if GuiElement.asserts().assertText works for a string that is not its text
     */
    @Test(expectedExceptions = {AssertionError.class, TimeoutException.class})
    public void testT06N_GuiElement_assertText() {
        getElementWithText().asserts().assertContainsText("This is not the text i am looking for");
    }

    /**
     * Test if GuiElement.asserts().assertAttributeText works for an attribute that has the specified value
     */
    @Test
    public void testT07_GuiElement_assertAttributeText() {
        getElementWithAttribute().asserts().assertAttributeValue(getTestPage().getAttributeName(), getTestPage().getAttributeValue());
    }

    /**
     * Test if GuiElement.asserts().assertAttributeText works for an attribute that has not specified value
     */
    @Test(expectedExceptions = {AssertionError.class})
    public void testT08N_GuiElement_assertAttributeText() {
        getElementWithAttribute().asserts().assertAttributeValue(getTestPage().getAttributeName(), "blurp012zyx");
    }

    /**
     * Test if GuiElement.asserts().assertAttributeText works for an attribute that contains the specified part
     */
    @Test
    public void testT09_GuiElement_assertAttributeContains() {
        getElementWithAttribute().asserts().assertAttributeContains(getTestPage().getAttributeName(), getTestPage().getAttributeValuePart());
    }

    /**
     * Test if GuiElement.asserts().assertAttributeText works for an attribute that contains the specified part
     */
    @Test
    public void testT09_GuiElement_assertAttributeContainsNot() {
        getElementWithAttribute().asserts().assertTextContainsNot(getTestPage().getAttributeName(), "inexistent");
    }

    /**
     * Test if GuiElement.asserts().assertAttributeText works for an attribute that does not contain the specified part
     */
    @Test(expectedExceptions = {AssertionError.class})
    public void testT10N_GuiElement_assertAttributeContains() {
        getElementWithAttribute().asserts().assertAttributeContains(getTestPage().getAttributeName(), "blurp012zyx");
    }

    /**
     * Test if GuiElement.asserts().assertIsSelected works for a a selected element
     */
    @Test
    public void testT11_GuiElement_assertIsSelected() {
        GuiElement selectableElement = getSelectableElement();
        selectableElement.select();
        selectableElement.asserts().assertIsSelected();
    }

    /**
     * Test if GuiElement.asserts().assertIsSelected works for an unselected element
     */
    @Test(expectedExceptions = {AssertionError.class})
    public void testT12N_GuiElement_assertIsSelected() {
        GuiElement selectableElement = getSelectableElement();
        selectableElement.deselect();
        selectableElement.asserts().assertIsSelected();
    }

    /**
     * Test if GuiElement.asserts().assertIsNotSelectable works for an unselectable element
     */
    @Test
    @Fails(validFor = "unsupportedBrowser=true", description = "Does not work in this browser!")
    public void testT13_GuiElement_assertIsNotSelectable() {
        getNotSelectableElement().asserts().assertIsNotSelectable();
    }

    /**
     * Test if GuiElement.asserts().assertIsNotSelectable works for a selectable element
     */
    @Test(expectedExceptions = {AssertionError.class})
    public void testT14N_GuiElement_assertIsNotSelectable() {
        getSelectableElement().asserts().assertIsNotSelectable();
    }

    /**
     * Test if GuiElement.asserts().assertIsDisplayed works for a displayed element
     */
    @Test
    public void testT15_GuiElement_assertIsDisplayed() {
        getDisplayedElement().asserts().assertIsDisplayed();
    }

    @Test
    public void testT16_GuiElement_assertIsDisplayedFromWebElement() {
        getDisplayedElement().asserts().assertIsDisplayedFromWebElement();
    }

    /**
     * Test if GuiElement.asserts().assertIsDisplayed works for a not displayed element
     */
    @Test(expectedExceptions = {AssertionError.class})
    public void testT17N_GuiElement_assertIsDisplayed() {
        getNotDisplayedElement().asserts().assertIsDisplayed();
    }

    /**
     * Test if GuiElement.asserts().assertIsDisplayedFromWebElement works for a not displayed element
     */
    @Test(expectedExceptions = {AssertionError.class})
    public void testT18N_GuiElement_assertIsDisplayedFromWebElement() {
        getNotDisplayedElement().asserts().assertIsDisplayedFromWebElement();
    }

    /**
     * Test if GuiElement.asserts().assertIsPresent works for a displayed element
     */
    @Test
    public void testT19_GuiElement_assertIsPresent() {
        getAnyElement().asserts().assertIsPresent();
    }

    /**
     * Test if GuiElement.asserts().assertIsPresent works for a non existing element
     */
    @Test
    public void testT20N_GuiElement_assertIsPresent() {
        boolean isPresent = getNotExistingElement().isPresent();
        Assert.assertFalse(isPresent, "The Element is not present, assertion should fail");
    }


    /**
     * Test if GuiElement.asserts().assertAnyFollowingTextNodeContains works for an element that contains the text in any
     * following node
     */
    @Test
    public void testT21_GuiElement_assertAnyFollowingTextNodeContains() {
        getParent2().asserts().assertAnyFollowingTextNodeContains(getTestPage().getElementText());
    }

    /**
     * Test if GuiElement.asserts().assertAnyFollowingTextNodeContains works for an element that doesn't contain the text in any
     * following node
     */
    @Test(expectedExceptions = {AssertionError.class})
    public void testT22N_GuiElement_assertAnyFollowingTextNodeContains() {
        getParent2().asserts().assertAnyFollowingTextNodeContains("aqwertzuasdfghj");
    }

    /**
     * Test if GuiElement.find() works for an existing element found by its ID
     */
    @Test
    public void testT23_GuiElement_findByID() {
        WebElement webElement = getElementWithText().getWebElement();
        Assert.assertNotNull(webElement);
    }

    @Test
    public void testT23_GuiElement_findByIDUnique() {
        GuiElement guiElement = getGuiElementBy(Locate.by().unique().id("11"));
        WebElement webElement = guiElement.getWebElement();
        Assert.assertNotNull(webElement);
    }

    @Test
    public void test_GuiElement_findNonUnique() {
        GuiElement guiElement = getGuiElementBy(Locate.by().unique().xpath("//div"));
        try {
            WebElement webElement = guiElement.getWebElement();
        } catch (TimeoutException e) {
            AssertUtils.assertContains(e.getCause().getMessage(), "GuiElement not found");
            AssertUtils.assertContains(e.getCause().getMessage(), guiElement.getLocator().toString());
        }
    }


    @Test
    public void test_GuiElement_findPrepared() {
        final Locate locator = Locate.prepare("//*[@id='%s']");
        GuiElement guiElement = getGuiElementBy(locator.with("11"));
        Assert.assertNotNull(guiElement.getWebElement());
    }

    /**
     * Test if GuiElement.find() works for an existing element found by a Xpath
     */
    @Test
    public void testT24_GuiElement_findByXpath() {
        WebElement webElement = getAnyElementByXpath().getWebElement();
        Assert.assertNotNull(webElement);
    }

    /**
     * Test if GuiElement.find() works for an existing element found by its class name
     */
    @Test
    public void testT25_GuiElement_findByClassName() {
        WebElement webElement = getAnyElementByClassName().getWebElement();
        Assert.assertNotNull(webElement);
    }

    /**
     * Test if GuiElement.find() works for an existing link found by its link text
     */
    @Test
    public void testT26_GuiElement_findByLinkText() {
        WebElement webElement = getAnyElementByLinkText().getWebElement();
        Assert.assertNotNull(webElement);
    }

    @Test
    public void testT26a_GuiElement_locateByLinkText() {

        final GuiElement linkOpenAgain = getGuiElementBy(Locate.by().linkText("Open again"));
        Assert.assertNotNull(linkOpenAgain.getWebElement());
    }

    /**
     * Test if GuiElement.find() works for an existing link found by its link text
     */
    @Test
    public void testT27_GuiElement_findByPartialLinkText() {
        WebElement webElement = getAnyElementByPartialLinkText().getWebElement();
        Assert.assertNotNull(webElement);
    }

    @Test
    public void test27a_GuiElement_locateByPartialLinkText() {

        final GuiElement linkOpenAgain = getGuiElementBy(Locate.by().partialLinkText("again"));
        Assert.assertNotNull(linkOpenAgain.getWebElement());
    }


    /**
     * Test if GuiElement.find() works for a named element
     */
    @Test
    public void testT28_GuiElement_findByName() {
        WebElement webElement = getAnyElementByName().getWebElement();
        Assert.assertNotNull(webElement);
    }

    @Test
    public void testT28a_GuiElement_findByName() {
        final GuiElement linkOpenAgain = getGuiElementBy(Locate.by().name("radioBtn"));
        Assert.assertNotNull(linkOpenAgain.getWebElement());
    }

    /**
     * Test if mouseOver() works with TesterraProperties "false"
     */
    @Test
    @Fails(validFor = "unsupportedBrowser=true", description = "Does not work in this browser!")
    public void testT29N_GuiElement_mouseOverNotSupportedMouseActions() {
        getTextBoxElement().mouseOver();
        getLoggerTableElement().asserts().assertTextContains("Mouse over");
    }

    /**
     * Test if mouseOver() works with TesterraProperties "true"
     */
    @Test
    @Fails(validFor = "unsupportedBrowser=true", description = "Does not work in this browser!")
    public void testT30_GuiElement_mouseOverWithSupportedMouseActions() {
        getTextBoxElement().mouseOver();
        getLoggerTableElement().asserts().assertTextContains("Mouse over");
    }

    /**
     * Test if GuiElement.asserts().assertIsNotDisplayed works for a not displayed element
     */
    @Test
    public void testT31_GuiElement_assertIsNotDisplayed() {
        getNotDisplayedElement().asserts().assertIsNotDisplayed();
    }

    /**
     * Test if GuiElement.asserts().assertIsNotDisplayed works for a not displayed WebElement
     */
    @Test
    public void testT32_GuiElement_assertIsNotDisplayedFromWebElement() {
        getNotDisplayedElement().asserts().assertIsNotDisplayedFromWebElement();
    }

    /**
     * Test if GuiElement.asserts().assertIsNotDisplayed works for a displayed element
     */
    @Test(expectedExceptions = {AssertionError.class})
    public void testT33N_GuiElement_assertIsNotDisplayed() {
        getDisplayedElement().asserts().assertIsNotDisplayed();
    }

    /**
     * Test if GuiElement.asserts().assertIsNotDisplayed works for a displayed WebElement
     */
    @Test(expectedExceptions = {AssertionError.class})
    public void testT34N_GuiElement_assertIsNotDisplayedFromWebElement() {
        getDisplayedElement().asserts().assertIsNotDisplayedFromWebElement();
    }

    @Test
    public void testT35_GuiElement_assertIsNotPresent() {
        getNotExistingElement().asserts().assertIsNotPresent();
    }

    /**
     * Test if GuiElement.asserts().assertIsNotPresent works for a displayed element
     */
    @Test
    public void testT36N_GuiElement_assertIsNotPresent() {
        boolean found = false;
        try {
            getDisplayedElement().asserts().assertIsNotDisplayed();
        } catch (AssertionError e) {
            found = true;
        }
        Assert.assertTrue(found, "Displayed element is present");
    }

    /**
     * Test if mouseclick on GuiElement was performed
     */
    @Test
    public void testT37_GuiElement_clickWithPriorMouseOverActions() {
        GuiElement element = getClickableElement();
        GuiElement out = getLoggerTableElement();
        element.mouseOver();
        element.click();
        out.asserts().assertContainsText("Form 16 submit");
    }

    /**
     * Test if mouseclickOverSeleniumXPath on GuiElement was performed
     */
    @Test
    public void testT39_GuiElement_classNameNavigation() {
        GuiElement textBox = getTextBoxElement();
        textBox.type("TestString");

        GuiElement checkBox = getSelectableElement();
        checkBox.select();
        checkBox.deselect();
        checkBox.select();

        GuiElement list = getLoggerTableElement();
        GuiElement textfield2 = getTextBoxElement();
        textfield2.mouseOver();
    }

    /**
     * Test if submit on GuiElement was performed
     */
    @Test
    public void testT40_GuiElement_submit() {
        GuiElement submitBtn = getClickableElement();
        GuiElement out = getLoggerTableElement();
        submitBtn.submit();
        out.asserts().assertContainsText("Form 16 submit");
    }

    /**
     * Test if getTextFromChildren returns expected values
     */
    @Test
    public void testT41_GuiElement_getTextFromChildren() {
        GuiElement guiElement = getParent2();

        List<String> textsFromChildren = guiElement.getTextsFromChildren();
        Assert.assertEquals(textsFromChildren.size(), 5, "Correct amount of texts contained. Texts: " + textsFromChildren);

        List<String> stringsManual = new ArrayList<>();
        stringsManual.add("Open again");
        stringsManual.add("Show confirm");
        stringsManual.add("Open pop up");
        stringsManual.add("Show alert");
        stringsManual.add("Show prompt");

        for (String expectedString : stringsManual) {
            Assert.assertTrue(textsFromChildren.contains(expectedString), "Strings not equal to getTextFromChildren(). " +
                    "Expected: " + stringsManual + ". Actual: " + textsFromChildren);
        }
    }

    /**
     * Test if assertIsPresentFast() works with present element
     */
    @Test
    public void testT41_GuiElement_assertIsPresentFast() {
        getDisplayedElement().asserts().assertIsPresentFast();
    }

    /**
     * Test if assertIsPresentFast() works with a not present element
     */
    @Test
    public void testT42N_GuiElement_assertIsPresentFast() {
        getNotDisplayedElement().asserts().assertIsPresentFast();
    }

    /**
     * Test if assertIsEnabled works for a enabled element
     */
    @Test
    public void testT43_GuiElement_assertIsEnabled() {
        getEnabledElement().asserts().assertIsEnabled();
    }

    /**
     * Test if assertIsEnabled works for a not enabled element
     */
    @Test(expectedExceptions = {AssertionError.class})
    public void testT45N_GuiElement_assertIsEnabled() {
        getDisabledElement().asserts().assertIsEnabled();
    }

    /**
     * Test if clickJS on GuiElement was performed
     */
    @Test
    public void testT46_GuiElement_clickJS_WithPriorMouseOverActions() {
        GuiElement element = getClickableElement();
        GuiElement out = getLoggerTableElement();

        element.mouseOver();
        element.clickJS();
        out.asserts().assertContainsText("Form 16 submit");
    }

    /**
     * Test if clickAbsolute on GuiElement was performed
     */
    @Test
    @Fails(validFor = "unsupportedBrowser=true", description = "Does not work in this browser!")
    public void testT47_GuiElement_clickAbsolute_WithPriorMouseOverActions() {
        GuiElement element = getClickableElement();
        GuiElement out = getLoggerTableElement();

        element.mouseOver();
        element.clickAbsolute();
        out.asserts().assertContainsText("Form 16 submit");
    }

    /**
     * Test if mouseOverAbsolute2Axis on GuiElement was performed
     */
    @Test
    public void testT48_GuiElement_mouseOverAbsolute2Axis() {
        GuiElement element = getTextBoxElement();
        GuiElement out = getLoggerTableElement();

        element.mouseOverAbsolute2Axis();
    }

    /**
     * Test if mouseOverJS on GuiElement was performed
     */
    @Test
    public void testT49_GuiElement_mouseOverJS() {
        GuiElement element = getTextBoxElement();
        GuiElement out = getLoggerTableElement();

        element.mouseOverJS();
        out.asserts().assertContainsText("Input 5 Mouse over");
    }


    /**
     * Test if GuiElement.asserts().assertInputFieldLength returns right length of input field
     */
    //    @Test
    public void testT50_GuiElement_assertInputFieldLength() {
        GuiElement element = getTextBoxElement();
        element.asserts().assertInputFieldLength(5);
    }


    @Test
    public void testT53_GuiElement_anyFollowingTextNodeContains() {
        GuiElement element = getParent2();
        boolean ret = element.anyFollowingTextNodeContains("Show alert");
        Assert.assertTrue(ret, "Element mit Text gefunden");
    }

    @Test(expectedExceptions = {AssertionError.class})
    public void testT54N_GuiElement_anyFollowingTextNodeContains() {
        GuiElement element = getParent2();
        boolean ret = element.anyFollowingTextNodeContains("42");
        Assert.assertTrue(ret, "Element mit Text gefunden");
    }

    @Test
    public void testT55_GuiElement_assertAttributeIsPresent() {
        GuiElement element = getDisplayedElement();
        element.asserts().assertAttributeIsPresent("href");
    }

    @Test(expectedExceptions = {AssertionError.class})
    public void testT56N_GuiElement_assertAttributeIsPresent() {
        GuiElement element = getDisplayedElement();
        element.asserts().assertAttributeIsPresent("label");
    }

    @Test
    public void testT57_GuiElement_assertIsDisabled() {
        GuiElement element = getDisabledElement();
        element.asserts().assertIsDisabled();
    }

    @Test(expectedExceptions = {AssertionError.class})
    public void testT58N_GuiElement_assertIsDisabled() {
        GuiElement element = getDisplayedElement();
        element.asserts().assertIsDisabled();
    }

    @Test
    public void testT59_GuiElement_assertIsNotPresentFast() {
        getNotExistingElement().asserts().assertIsNotPresentFast();
    }

    @Test(expectedExceptions = {AssertionError.class})
    public void testT60N_GuiElement_assertIsNotPresentFast() {
        getDisplayedElement().asserts().assertIsNotPresentFast();
    }

    @Test
    public void testT61_GuiElement_assertIsNotSelected() {
        GuiElement selectableElement = getSelectableElement();
        selectableElement.deselect();
        selectableElement.asserts().assertIsNotSelected();
    }

    @Test(expectedExceptions = {AssertionError.class})
    public void testT61N_GuiElement_assertIsNotSelected() {
        GuiElement selectableElement = getSelectableElement();
        selectableElement.select();
        selectableElement.asserts().assertIsNotSelected();
    }

    @Test
    public void testT63_GuiElement_assertIsSelectable() {
        getSelectableElement().asserts().assertIsSelectable();
    }

    @Test(expectedExceptions = {AssertionError.class})
    public void testT64N_GuiElement_assertIsSelectable() {
        getNotSelectableElement().asserts().assertIsSelectable();
    }

    @Test
    public void testT65_GuiElement_clear() {
        GuiElement element = getTextBoxElement();
        element.sendKeys("Test");
        element.clear();
        Assert.assertEquals(element.getText(), "", "Textboxelement is empty");
    }

    @Test(enabled = false)
    public void testT66_GuiElement_doubleClick() {
        GuiElement element = getSelectableElement();
        element.doubleClick();
        getLoggerTableElement().asserts().assertContainsText("Input 3 Double clicked");
    }

    @Test
    public void testT67_WebElement_findElement() {
        GuiElement element = getSelectableElement();
        WebElement webElement = element.getWebElement();
        if (element.hasFrameLogic()) {
            element.getFrameLogic().switchToCorrectFrame();
        }
        webElement = webElement.findElement(By.xpath("//div[1]"));
        if (element.hasFrameLogic()) {
            element.getFrameLogic().switchToDefaultFrame();
        }
        Assert.assertNotNull(webElement, "Found Element");
    }

    @Test
    public void testT68N_WebElement_findElement() {
        GuiElement element = getSelectableElement();
        WebElement webElement = element.getWebElement();
        if (element.hasFrameLogic()) {
            element.getFrameLogic().switchToCorrectFrame();
        }
        ThrowableUtils.expectThrowable(NoSuchElementException.class, () -> webElement.findElement(By.xpath("//div[text()=\'\']")));
        if (element.hasFrameLogic()) {
            element.getFrameLogic().switchToDefaultFrame();
        }
    }

    @Test
    public void testT69_WebElement_findElements() {
        GuiElement element = getSelectableElement();
        WebElement webElement = element.getWebElement();
        if (element.hasFrameLogic()) {
            element.getFrameLogic().switchToCorrectFrame();
        }
        List<WebElement> webElements = webElement.findElements(By.xpath("//div"));
        if (element.hasFrameLogic()) {
            element.getFrameLogic().switchToDefaultFrame();
        }
        Assert.assertNotEquals(webElements.size(), 0, "List is not empty");
    }

    @Test
    public void testT70N_WebElement_findElements() {
        GuiElement element = getSelectableElement();
        WebElement webElement = element.getWebElement();
        if (element.hasFrameLogic()) {
            element.getFrameLogic().switchToCorrectFrame();
        }
        List<WebElement> webElements = webElement.findElements(By.xpath("//div[text()=\'\']"));
        if (element.hasFrameLogic()) {
            element.getFrameLogic().switchToDefaultFrame();
        }
        Assert.assertEquals(webElements.size(), 0, "List is empty");
    }

    @Test
    public void testT71_GuiElement_getAttribute() {
        GuiElement element = getElementWithAttribute();
        String attribute = element.getAttribute("value");
        Assert.assertEquals(attribute, "Button2", "Attribute is equal");
    }

    @Test
    public void testT72N_GuiElement_getAttribute() {
        GuiElement element = getElementWithAttribute();
        String attribute = element.getAttribute("href");
        Assert.assertEquals(attribute, null, "Attribute is equal");
    }

    @Test
    public void testT73_GuiElement_getBy() {
        GuiElement element = getElementWithAttribute();
        By by = element.getBy();
        Assert.assertNotNull(by, "By is not null");
    }

    @Test
    public void testT74_GuiElement_getCSSValue() {
        GuiElement element = getTableElement();
        String cssValue = element.getCssValue("visibility");
        Assert.assertEquals(cssValue, "hidden", "CSS Value is equal");
    }

    @Test
    public void testT75_GuiElement_getDriver() {
        GuiElement element = getTableElement();
        WebDriver driver = element.getDriver();
        Assert.assertNotNull(driver, "Driver is not null");
    }

    @Test
    public void testT76_GuiElement_getLengthOfInputAfterSendKeys() {
        GuiElement element = getTextBoxElement();
        int lengthOfInput = element.getLengthOfValueAfterSendKeys("Hurry you fools");
        Assert.assertEquals(lengthOfInput, 5, "Text has length of 5");
    }

    @Test
    public void testT77_GuiElement_getLocation() {
        GuiElement element = getTextBoxElement();
        Point location = element.getLocation();
        int x = location.getX();
        int y = location.getY();
        Assert.assertTrue(10 < x && x < 500 && 10 < y && y < 500, "Location is appropriate");
    }

    @Test
    public void testT78_GuiElement_getSelectElement() {
        GuiElement element = getSingleSelect();
        Select selectElement = element.getSelectElement();
        List<WebElement> selectOptions = selectElement.getOptions();
        Assert.assertEquals(selectOptions.size(), 5, "Select Item Count is right");
    }

    @Test
    public void testT79_GuiElement_getSize() {
        GuiElement element = getTextBoxElement();
        Dimension sizeGuiElement = element.getSize();
        int height = sizeGuiElement.getHeight();
        int width = sizeGuiElement.getWidth();
        Assert.assertTrue(10 < height && height < 500 && 10 < width && width < 500, "Size is appropriate");
    }


    @Test
    public void testT80_GuiElement_IsEnabled() {
        GuiElement element = getEnabledElement();

        boolean found = element.isEnabled();
        Assert.assertTrue(found, "The Element is enabled.");
    }

    @Test
    public void testT81N_GuiElement_IsEnabled() {
        GuiElement element = getDisabledElement();

        boolean found = element.isEnabled();
        Assert.assertFalse(found, "The Element is enabled.");
    }

    @Test
    public void testT82_GuiElement_IsDisplayed() {
        GuiElement element = getDisplayedElement();

        boolean found = element.isDisplayed();
        Assert.assertTrue(found, "The Element is displayed.");
    }

    @Test
    public void testT83N_GuiElement_IsDisplayed() {
        GuiElement element = getNotDisplayedElement();
        Assert.assertFalse(element.isDisplayed(), "The Element is displayed.");
    }

    @Test
    public void test_GuiElement_NotExisting_IsDisplayed() {
        GuiElement element = getNotExistingElement();
        Assert.assertFalse(element.isDisplayed(), "The Element is displayed.");
    }

    @Test
    public void test_GuiElement_NotVisible_IsDisplayed() {
        GuiElement element = getGuiElementBy(By.id("notVisibleElement"));
        Assert.assertFalse(element.isDisplayed(), "The Element is displayed.");
    }

    @Test
    public void testT85_GuiElement_getTagName() {
        GuiElement element = getDisplayedElement();
        String tagName = element.getTagName();

        Assert.assertEquals(tagName, "a", "Expected Tagname was found");
    }

    @Test
    public void testT85a_GuiElement_getTagName() {
        GuiElement element = getGuiElementBy(Locate.by().tagName("a"));
        Assert.assertTrue(element.isDisplayed(), "The Element is displayed.");
        Assert.assertEquals(element.getTagName(), "a", "Expected Tagname was found");
    }

    @Test
    public void testT86_GuiElement_isSelectable() {
        GuiElement element = getSelectableElement();
        boolean found = element.isSelectable();
        Assert.assertTrue(found, "The Element is selectable");
    }

    @Test
    public void testT87N_GuiElement_isSelectable() {
        GuiElement element = getNotSelectableElement();
        boolean found = element.isSelectable();
        Assert.assertFalse(found, "The Element is selectable");
    }


    @Test
    public void testT88_GuiElement_getBy() {
        GuiElement g = getEnabledElement();
        By by = g.getBy();
        Assert.assertEquals(by.toString(), "By.id: 16", "By Identifier is equal");
    }

    @Test
    public void testT89_GuiElement_IsDisplayedFromWebElement() {
        boolean result = getDisplayedElement().isDisplayedFromWebElement();
        Assert.assertTrue(result, "Element is displayed");
    }

    @Test
    public void testT90N_GuiElement_IsDisplayedFromWebElement() {
        boolean result = getNotDisplayedElement().isDisplayedFromWebElement();
        Assert.assertFalse(result, "Element is displayed");
    }

    @Test
    public void testT92_GuiElement_restoreDefaultTimeOut() {
        GuiElement element = getTableElement();
        int timeOutInSeconds = element.getTimeoutInSeconds();
        element.setTimeoutInSeconds(50000);
        element.restoreDefaultTimeout();
        Assert.assertEquals(element.getTimeoutInSeconds(), timeOutInSeconds, "TimeOut is identical");
    }

    @Test
    public void TestT94_GuiElement_highlight() {
        GuiElement element = getClickableElement();
        element.highlight();
        element.asserts().assertIsDisplayed();
        // no idea how to test highlighted element
    }

    @Test
    public void testT95_GuiElement_selectDeselect() {
        GuiElement element = getSelectableElement();
        element.select(true);
        element.asserts().assertIsSelected();
        element.select(false);
        element.asserts().assertIsNotSelected();
    }

    @Test
    public void testT96_GuiElement_rightClick() {
        GuiElement element = getClickableElement();
        element.rightClick();
    }

    @Test
    public void testT97_GuiElement_rightClickJS() {
        GuiElement element = getClickableElement();
        element.rightClickJS();
    }

    @Test
    public void testT98_GuiElement_assertClassIsPresent() {
        GuiElement element = getGuiElementBy(By.xpath(String.format("//*[@data-qa='%s']", "action/toggleClass")));
        element.asserts().assertCssClassIsNotPresent("active");
        element.click();
        element.asserts().assertCssClassIsPresent("active");
    }

    @Test
    public void testT99_pageChangeOnAnchorClick() {

        final GuiElement elementToClick = getGuiElementBy(Locate.by().qa("action/pageChangeAnchor"));
        elementToClick.click();

        final GuiElement guiElementShownAfterClick = getGuiElementBy(Locate.by().cssSelector("input[type='submit']"));
        Assert.assertTrue(guiElementShownAfterClick.isDisplayed());
    }
}
