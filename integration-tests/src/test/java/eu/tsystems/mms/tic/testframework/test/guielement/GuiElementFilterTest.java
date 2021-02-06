/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.test.guielement;

import eu.tsystems.mms.tic.testframework.AbstractExclusiveTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.BasePage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.LocatorFactoryProvider;
import eu.tsystems.mms.tic.testframework.pageobjects.filter.WebElementFilter;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class GuiElementFilterTest extends AbstractExclusiveTestSitesTest<BasePage> implements LocatorFactoryProvider {

    @Override
    public Class<BasePage> getPageClass() {
        return BasePage.class;
    }

    private void assertLogFieldContains(String textToBeContained) {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement logField = new GuiElement(driver, By.xpath(".//*[@id='99']"));
        String actualLogFieldText = logField.getText();
        Assert.assertTrue(actualLogFieldText.contains("Open again clicked"), "LogField actualLogFieldText is correct.\n" +
                "Expected: \"" + textToBeContained + "\"\nActual: " + actualLogFieldText);
    }

    @Test
    public void testT01_Telxt_Is() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement openAgainLink = new GuiElement(driver, By.xpath(".//*[@id]"))
                .withWebElementFilter(webElement -> webElement.getText().equals("Open again"));
        openAgainLink.asserts().assertIsPresent();
        openAgainLink.click();
        assertLogFieldContains("Open again clicked");
    }

    @Test
    public void testT02_Text_IsNot() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement openAgainLink = new GuiElement(driver, By.xpath(".//*[@id='11']"))
                .withWebElementFilter(WebElementFilter.TEXT.isNot("Open again"));
        Control.withTimeout(1, () -> {
            openAgainLink.asserts().assertIsNotPresent();
        });
    }

    @Test
    public void testT03_Text_Contains() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement openAgainLink = new GuiElement(driver, By.xpath(".//*[@id]"))
                .withWebElementFilter(WebElementFilter.TEXT.contains("Open a"));
        openAgainLink.asserts().assertIsPresent();
        openAgainLink.click();
        assertLogFieldContains("Open again clicked");
    }

    @Test
    public void testT04_Text_ContainsNot() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement openAgainLink = new GuiElement(driver, By.xpath(".//*[@id='11']"))
                .withWebElementFilter(WebElementFilter.TEXT.containsNot("Open a"));
        Control.withTimeout(1, () -> {
            openAgainLink.asserts().assertIsNotPresent();
        });
    }

    @Test
    public void testT05_Displayed_Is() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement nonVisibleTable = new GuiElement(driver, By.xpath(".//*[@id]"))
                .withWebElementFilter(WebElementFilter.DISPLAYED.is(false));
        nonVisibleTable.asserts().assertIsNotDisplayed();
    }

    @Test
    public void testT05a_Displayed_Is() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement nonVisibleTable = new GuiElement(driver, Locate.by(By.xpath(".//*[@id]")).filter(webElement -> !webElement.isDisplayed()));
        nonVisibleTable.asserts().assertIsNotDisplayed();
    }

    @Test
    public void testT05b_Displayed_Is() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement nonVisibleTable = new GuiElement(driver, Locate.by(By.xpath(".//*[@id]")).displayed(false));
        nonVisibleTable.asserts().assertIsNotDisplayed();
    }

    @Test
    public void testT05c_Displayed_Is() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement visibleTable = new GuiElement(driver, Locate.by(By.xpath(".//*[@id]")).displayed());
        visibleTable.asserts().assertIsDisplayed();
    }


    @Test
    public void testT07_Attribute_Contains() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement button2 = new GuiElement(driver, By.xpath("//input[@value='Button2']"));
        GuiElement elementsWithAttributes = new GuiElement(driver, By.xpath(".//*[@id]"))
                .withWebElementFilter(WebElementFilter.ATTRIBUTE.contains("value", "Button2"));
        Point Pointbutton2Check = elementsWithAttributes.getLocation();
        Point Pointbutton2 = button2.getLocation();

        Assert.assertTrue(Pointbutton2.equals(Pointbutton2Check), "Button 2 on same position like GuiElement with Webfilter");
    }

    @Test
    public void testT08_Attribute_ContainsNot() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement elementsWithAttributes = new GuiElement(driver, By.xpath(".//*[@id='6']"))
                .withWebElementFilter(WebElementFilter.ATTRIBUTE.containsNot("value", "Button2"));
        Control.withTimeout(1, () -> {
            elementsWithAttributes.asserts().assertIsNotPresent();
        });
    }

    @Test
    public void testT09_Attribute_Exists() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement elementsWithAttributes = new GuiElement(driver, By.xpath("//div[@class='box'][1]/input"))
                .withWebElementFilter(WebElementFilter.ATTRIBUTE.exists("disabled"));
        elementsWithAttributes.asserts().assertAttributeContains("id", "7");

    }

    @Test
    public void testT10_Attribute_ExistsNot() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement elementsWithAttributes = new GuiElement(driver, By.xpath("//input[@type='radio']"))
                .withWebElementFilter(WebElementFilter.ATTRIBUTE.existsNot("disabled"));
        Control.withTimeout(1, () -> {
            elementsWithAttributes.asserts().assertAttributeContains("id", "10");
        });
    }

    @Test
    public void testT11_Attribute_Is() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement elementsWithAttributes = new GuiElement(driver, By.xpath(".//*"))
                .withWebElementFilter(WebElementFilter.ATTRIBUTE.is("type", "submit"));
        elementsWithAttributes.asserts().assertAttributeContains("value", "Submit");
    }

    @Test
    public void testT12_Attribute_IsNot() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement elementsWithAttributes = new GuiElement(driver, By.xpath(".//input[@value]"))
                .withWebElementFilter(WebElementFilter.ATTRIBUTE.isNot("type", "button"));
        elementsWithAttributes.asserts().assertAttributeContains("value", "Submit");
    }

    @Test
    public void testT13_CSS_Contains() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement elementsWithCSS = new GuiElement(driver, By.xpath(".//*[@id]"))
                .withWebElementFilter(WebElementFilter.CSS.contains("visibility", "hid"));
        elementsWithCSS.asserts().assertAttributeContains("id", "100");
    }

    @Test
    public void testT14_CSS_ContainsNot() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement elementsWithCSS = new GuiElement(driver, By.xpath(".//*[@id='100']"))
                .withWebElementFilter(WebElementFilter.CSS.containsNot("visibility", "den"));
        Control.withTimeout(1, () -> {
            elementsWithCSS.asserts().assertIsNotPresent();
        });
    }

    @Test
    public void testT15_CSS_Exists() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement elementsWithCSS = new GuiElement(driver, By.xpath(".//*[@id]"))
                .withWebElementFilter(WebElementFilter.CSS.exists("visibility"));
        elementsWithCSS.asserts().assertAttributeContains("id", "1");
    }

    @Test
    public void testT16N_CSS_Exists() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement elementsWithCSS = new GuiElement(driver, By.xpath("//*"))
                .withWebElementFilter(WebElementFilter.CSS.exists("wabilibuu"));
        elementsWithCSS.asserts().assertIsNotPresent();
    }

    @Test
    public void testT17_CSS_ExistsNot() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement elementsWithCSS = new GuiElement(driver, By.xpath(".//*"))
                .withWebElementFilter(WebElementFilter.CSS.existsNot("visibility"));
        Control.withTimeout(1, () -> {
            elementsWithCSS.asserts().assertIsNotPresent();
        });
    }

    @Test
    public void testT18_CSS_Is() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement elementsWithCSS = new GuiElement(driver, By.xpath(".//*[@id]"))
                .withWebElementFilter(WebElementFilter.CSS.is("visibility", "hidden"));
        Control.withTimeout(1, () -> {
            elementsWithCSS.asserts().assertAttributeContains("id", "100");
        });
    }

    @Test
    public void testT19N_CSS_Is() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement elementWithCSS = new GuiElement(driver, By.xpath(".//*[@id]"))
                .withWebElementFilter(WebElementFilter.CSS.is("background-color", "black"));
        Control.withTimeout(1, () -> {
            elementWithCSS.asserts().assertIsNotPresent();
        });
    }

    @Test
    public void testT20_CSS_IsNot() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement elementWithCSS = new GuiElement(driver, By.xpath(".//*[@id='100']"))
                .withWebElementFilter(WebElementFilter.CSS.isNot("visibility", "hidden"));
        Control.withTimeout(1, () -> {
            elementWithCSS.asserts().assertIsNotPresent();
        });
    }

    @Test
    public void testT21_Tag_Contains() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement elementsWithTag = new GuiElement(driver, By.xpath("//*[@onclick]"))
                .withWebElementFilter(WebElementFilter.TAG.contains("inp"));
        elementsWithTag.asserts().assertAttributeContains("value", "Button1");
    }

    @Test
    public void testT22_Tag_ContainsNot() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement elementsWithTag = new GuiElement(driver, By.xpath("//input"))
                .withWebElementFilter(WebElementFilter.TAG.containsNot("inp"));
        Control.withTimeout(1, () -> {
            elementsWithTag.asserts().assertIsNotPresent();
        });
    }

    @Test
    public void testT23_Tag_Is() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement elementsWithTag = new GuiElement(driver, By.xpath(".//*"))
                .withWebElementFilter(WebElementFilter.TAG.is("select"));
        elementsWithTag.asserts().assertAttributeContains("size", "3");
    }

    @Test
    public void testT24_Tag_IsNot() {
        final WebDriver driver = getPage().getWebDriver();
        GuiElement elementWithTag = new GuiElement(driver, By.xpath(".//*[@id='6']"))
                .withWebElementFilter(WebElementFilter.TAG.isNot("input"));
        Control.withTimeout(1, () -> {
            elementWithTag.asserts().assertIsNotPresent();
        });
    }
}
