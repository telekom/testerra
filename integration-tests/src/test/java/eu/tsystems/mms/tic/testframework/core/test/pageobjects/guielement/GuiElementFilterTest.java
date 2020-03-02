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
package eu.tsystems.mms.tic.testframework.core.test.pageobjects.guielement;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.filter.WebElementFilter;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by rnhb on 29.07.2015.
 */
public class GuiElementFilterTest extends AbstractTestSitesTest {

    private void assertLogFieldContains(String textToBeContained) {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement logField = new GuiElement(driver, By.xpath(".//*[@id='99']"));
        String actualLogFieldText = logField.getText();
        Assert.assertTrue(actualLogFieldText.contains("Open again clicked"), "LogField actualLogFieldText is correct.\n" +
                "Expected: \"" + textToBeContained + "\"\nActual: " + actualLogFieldText);
    }

    @Test
    public void testT01_Telxt_Is() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement openAgainLink = new GuiElement(driver, By.xpath(".//*[@id]"))
                .withWebElementFilter(WebElementFilter.TEXT.is("Open again"));
        openAgainLink.asserts().assertIsPresent();
        openAgainLink.click();
        assertLogFieldContains("Open again clicked");
    }

    @Test
    public void testT02_Text_IsNot() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement openAgainLink = new GuiElement(driver, By.xpath(".//*[@id='11']"))
                .withWebElementFilter(WebElementFilter.TEXT.isNot("Open again"));
        openAgainLink.setTimeoutInSeconds(1);
        openAgainLink.asserts().assertIsNotPresent();
    }

    @Test
    public void testT03_Text_Contains() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement openAgainLink = new GuiElement(driver, By.xpath(".//*[@id]"))
                .withWebElementFilter(WebElementFilter.TEXT.contains("Open a"));
        openAgainLink.asserts().assertIsPresent();
        openAgainLink.click();
        assertLogFieldContains("Open again clicked");
    }

    @Test
    public void testT04_Text_ContainsNot() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement openAgainLink = new GuiElement(driver, By.xpath(".//*[@id='11']"))
                .withWebElementFilter(WebElementFilter.TEXT.containsNot("Open a"));
        openAgainLink.setTimeoutInSeconds(1);
        openAgainLink.asserts().assertIsNotPresent();
    }

    @Test
    public void testT05_Displayed_Is() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement nonVisibleTable = new GuiElement(driver, By.xpath(".//*[@id]"))
                .withWebElementFilter(WebElementFilter.DISPLAYED.is(false));
        nonVisibleTable.asserts().assertIsNotDisplayed();
    }

    @Test
    public void testT05_Displayed_Is_As_Selector() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement nonVisibleTable = new GuiElement(driver, Locate.by().notDisplayed().xpath(".//*[@id]"));
        nonVisibleTable.asserts().assertIsNotDisplayed();
    }

    @Test
    public void testT06_Size_IsBetween() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement submitButton = new GuiElement(driver, By.xpath("//input[@type='submit']"));
        Dimension dimSubmitButton = submitButton.getSize();
        GuiElement submitButtonCheck = new GuiElement(driver, By.xpath(".//*"))
                .withWebElementFilter(WebElementFilter.SIZE.isBetween(
                        dimSubmitButton.getWidth() - 2,
                        dimSubmitButton.getHeight() - 2,
                        dimSubmitButton.getWidth() + 2,
                        dimSubmitButton.getHeight() + 2));
        submitButtonCheck.asserts().assertAttributeContains("value", "Submit");
    }

    @Test
    public void testT07_Attribute_Contains() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement button2 = new GuiElement(driver, By.xpath("//input[@value='Button2']"));
        GuiElement elementsWithAttributes = new GuiElement(driver, By.xpath(".//*[@id]"))
                .withWebElementFilter(WebElementFilter.ATTRIBUTE.contains("value", "Button2"));
        Point Pointbutton2Check = elementsWithAttributes.getLocation();
        Point Pointbutton2 = button2.getLocation();

        Assert.assertTrue(Pointbutton2.equals(Pointbutton2Check), "Button 2 on same position like GuiElement with Webfilter");
    }

    @Test
    public void testT08_Attribute_ContainsNot() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement elementsWithAttributes = new GuiElement(driver, By.xpath(".//*[@id='6']"))
                .withWebElementFilter(WebElementFilter.ATTRIBUTE.containsNot("value", "Button2"));
        elementsWithAttributes.setTimeoutInSeconds(1);
        elementsWithAttributes.asserts().assertIsNotPresent();
    }

    @Test
    public void testT09_Attribute_Exists() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement elementsWithAttributes = new GuiElement(driver, By.xpath("//div[@class='box'][1]/input"))
                .withWebElementFilter(WebElementFilter.ATTRIBUTE.exists("disabled"));
        elementsWithAttributes.asserts().assertAttributeContains("id", "7");

    }

    @Test
    public void testT10_Attribute_ExistsNot() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement elementsWithAttributes = new GuiElement(driver, By.xpath("//input[@type='radio']"))
                .withWebElementFilter(WebElementFilter.ATTRIBUTE.existsNot("disabled"));
        elementsWithAttributes.setTimeoutInSeconds(1);
        elementsWithAttributes.asserts().assertAttributeContains("id", "10");
    }

    @Test
    public void testT11_Attribute_Is() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement elementsWithAttributes = new GuiElement(driver, By.xpath(".//*"))
                .withWebElementFilter(WebElementFilter.ATTRIBUTE.is("type", "submit"));
        elementsWithAttributes.asserts().assertAttributeContains("value", "Submit");
    }

    @Test
    public void testT12_Attribute_IsNot() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement elementsWithAttributes = new GuiElement(driver, By.xpath(".//input[@value]"))
                .withWebElementFilter(WebElementFilter.ATTRIBUTE.isNot("type", "button"));
        elementsWithAttributes.asserts().assertAttributeContains("value", "Submit");
    }

    @Test
    public void testT13_CSS_Contains() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement elementsWithCSS = new GuiElement(driver, By.xpath(".//*[@id]"))
                .withWebElementFilter(WebElementFilter.CSS.contains("visibility", "hid"));
        elementsWithCSS.asserts().assertAttributeContains("id", "100");
    }

    @Test
    public void testT14_CSS_ContainsNot() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement elementsWithCSS = new GuiElement(driver, By.xpath(".//*[@id='100']"))
                .withWebElementFilter(WebElementFilter.CSS.containsNot("visibility", "den"));
        elementsWithCSS.setTimeoutInSeconds(1);
        elementsWithCSS.asserts().assertIsNotPresent();
    }

    @Test
    public void testT15_CSS_Exists() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement elementsWithCSS = new GuiElement(driver, By.xpath(".//*[@id]"))
                .withWebElementFilter(WebElementFilter.CSS.exists("visibility"));
        elementsWithCSS.asserts().assertAttributeContains("id", "1");
    }

    @Test
    public void testT16N_CSS_Exists() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement elementsWithCSS = new GuiElement(driver, By.xpath("//*"))
                .withWebElementFilter(WebElementFilter.CSS.exists("wabilibuu"));
        elementsWithCSS.asserts().assertIsNotPresent();
    }

    @Test
    public void testT17_CSS_ExistsNot() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement elementsWithCSS = new GuiElement(driver, By.xpath(".//*"))
                .withWebElementFilter(WebElementFilter.CSS.existsNot("visibility"));
        elementsWithCSS.setTimeoutInSeconds(1);
        elementsWithCSS.asserts().assertIsNotPresent();
    }

    @Test
    public void testT18_CSS_Is() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement elementsWithCSS = new GuiElement(driver, By.xpath(".//*[@id]"))
                .withWebElementFilter(WebElementFilter.CSS.is("visibility", "hidden"));
        elementsWithCSS.setTimeoutInSeconds(1);
        elementsWithCSS.asserts().assertAttributeContains("id", "100");
    }

    @Test
    public void testT19N_CSS_Is() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement elementWithCSS = new GuiElement(driver, By.xpath(".//*[@id]"))
                .withWebElementFilter(WebElementFilter.CSS.is("background-color", "black"));
        elementWithCSS.setTimeoutInSeconds(1);
        elementWithCSS.asserts().assertIsNotPresent();
    }

    @Test
    public void testT20_CSS_IsNot() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement elementWithCSS = new GuiElement(driver, By.xpath(".//*[@id='100']"))
                .withWebElementFilter(WebElementFilter.CSS.isNot("visibility", "hidden"));
        elementWithCSS.setTimeoutInSeconds(1);
        elementWithCSS.asserts().assertIsNotPresent();
    }

    @Test
    public void testT21_Tag_Contains() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement elementsWithTag = new GuiElement(driver, By.xpath("//*[@onclick]"))
                .withWebElementFilter(WebElementFilter.TAG.contains("inp"));
        elementsWithTag.asserts().assertAttributeContains("value", "Button1");
    }

    @Test
    public void testT22_Tag_ContainsNot() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement elementsWithTag = new GuiElement(driver, By.xpath("//input"))
                .withWebElementFilter(WebElementFilter.TAG.containsNot("inp"));
        elementsWithTag.setTimeoutInSeconds(1);
        elementsWithTag.asserts().assertIsNotPresent();
    }

    @Test
    public void testT23_Tag_Is() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement elementsWithTag = new GuiElement(driver, By.xpath(".//*"))
                .withWebElementFilter(WebElementFilter.TAG.is("select"));
        elementsWithTag.asserts().assertAttributeContains("size", "3");
    }

    @Test
    public void testT24_Tag_IsNot() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement elementWithTag = new GuiElement(driver, By.xpath(".//*[@id='6']"))
                .withWebElementFilter(WebElementFilter.TAG.isNot("input"));
        elementWithTag.setTimeoutInSeconds(1);
        elementWithTag.asserts().assertIsNotPresent();
    }

    @Test
    public void testT25_Position_Is() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement submitButton = new GuiElement(driver, By.xpath("//input[@type='submit']"));
        Point pointSubmitButton = submitButton.getLocation();
        GuiElement elementsWithTag = new GuiElement(driver, By.xpath("//input"))
                .withWebElementFilter(WebElementFilter.POSITION.is(
                        pointSubmitButton.getX(),
                        pointSubmitButton.getY()));
        elementsWithTag.asserts().assertAttributeContains("type", "submit");
    }

    @Test
    public void testT26_Position_IsBetween() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement submitButton = new GuiElement(driver, By.xpath("//input[@type='submit']"));
        Point pointSubmitButton = submitButton.getLocation();
        GuiElement elementsWithTag = new GuiElement(driver, By.xpath(".//*"))
                .withWebElementFilter(WebElementFilter.POSITION.isBetween(
                        pointSubmitButton.getX() - 5,
                        pointSubmitButton.getY() - 5,
                        pointSubmitButton.getX() + 5,
                        pointSubmitButton.getY() + 5));
        elementsWithTag.asserts().assertAttributeContains("value", "Submit");
    }


    @Test
    public void testT27_Size_Is() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        GuiElement openAgainLink = new GuiElement(driver, By.xpath(".//*[@id='11']"));
        Dimension dimensionOPenAgainLink = openAgainLink.getSize();
        GuiElement openAgainLinkCheck = new GuiElement(driver, By.xpath(".//*"))
                .withWebElementFilter(WebElementFilter.SIZE.is(
                        dimensionOPenAgainLink.getWidth(),
                        dimensionOPenAgainLink.getHeight()));

        openAgainLinkCheck.asserts().assertContainsText("Open again");
    }
}
