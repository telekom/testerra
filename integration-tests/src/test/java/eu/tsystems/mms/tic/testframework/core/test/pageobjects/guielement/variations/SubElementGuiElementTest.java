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
package eu.tsystems.mms.tic.testframework.core.test.pageobjects.guielement.variations;

import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

/**
 * Created by rnhb on 13.05.2015.
 */
public class SubElementGuiElementTest extends GuiElementTestCollector {

    @Override
    public GuiElement getGuiElementBy(By locator) {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement parentElement = new GuiElement(driver, By.xpath("//body"));
        return (GuiElement) parentElement.getSubElement(locator);
    }

    @Override
    protected TestPage getTestPage() {
        return TestPage.INPUT_TEST_PAGE;
    }

    /**
     * negative test some methods for getSubElement.
     */
    @Test
    public void testT03N_GuiElement_getSubElement_notExistingParentElement() {
        GuiElement parent = getNotExistingElement();
        GuiElement subElement = parent.getSubElement(By.linkText("Open again"));
        subElement.asserts().assertIsNotPresent();
    }

    /**
     * All Locator methods do internally use xPath, except for LinkText, PartialLinkText and Css. This Test ensures that
     * an existing element is not found with its correct locator, if trying to locate it as subelement of an element,
     * that is not actually its superelement.
     *
     * @param locatorForSubElement Locator of the subElement
     */
    public void checkGetSubElementWithLocatorIsNotPresent(By locatorForSubElement) {
        GuiElement innerElement = getGuiElementBy(locatorForSubElement);
        GuiElement otherElement = getParent1();

        innerElement.asserts().assertIsPresent();
        otherElement.asserts().assertIsPresent();

        GuiElement subElement = otherElement.getSubElement(locatorForSubElement);
        subElement.asserts().assertIsNotPresent();
    }

    /**
     * Test if the getSubElement method actually only returns sub-elements
     */
    @Test
    public void testT04N_GuiElement_GetSubElement_NoSubElement_xPath() {
        checkGetSubElementWithLocatorIsNotPresent(By.xpath(".//a[@id='11']"));
    }

    /**
     * Test if the getSubElement method actually only returns sub-elements
     */
    @Test
    public void testT05N_GuiElement_GetSubElement_NoSubElement_LinkText() {
        checkGetSubElementWithLocatorIsNotPresent(By.linkText("Open again"));
    }

    /**
     * Test if the getSubElement method actually only returns sub-elements
     */
    @Test
    public void testT06N_GuiElement_GetSubElement_NoSubElement_CssSelector() {
        checkGetSubElementWithLocatorIsNotPresent(By.cssSelector("a[id='14']"));
    }

    /**
     * Test if the getSubElement method actually only returns sub-elements
     */
    @Test
    public void testT07N_GuiElement_GetSubElement_NoSubElement_Id() {
        checkGetSubElementWithLocatorIsNotPresent(By.id("11"));
    }

    /**
     * Test if the getSubElement method actually only returns sub-elements
     */
    @Test
    public void testT08N_GuiElement_GetSubElement_NoSubElement_PartialLink() {
        checkGetSubElementWithLocatorIsNotPresent(By.partialLinkText("Open again"));
    }

    /**
     * Test if the getSubElement method actually only returns sub-elements
     */
    @Test
    public void testT09N_GuiElement_GetSubElement_NoSubElement_TagName() {
        checkGetSubElementWithLocatorIsNotPresent(By.tagName("div"));
    }
}
