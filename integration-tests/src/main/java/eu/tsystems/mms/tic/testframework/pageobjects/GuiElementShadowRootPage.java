package eu.tsystems.mms.tic.testframework.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class GuiElementShadowRootPage extends Page {

    @Check
    public final GuiElement container = new GuiElement(getWebDriver(), By.cssSelector("#container"));
    @Check
    public final GuiElement normalContent = container.getSubElement(By.cssSelector("#normal-content p"));

    // shadow root element
    public final GuiElement shadowRootElement = new GuiElement(getWebDriver(), By.id("shadow-host")).shadowRoot();
    public final GuiElement shadowContent = shadowRootElement.getSubElement(By.cssSelector("#shadow-content"));

    public final GuiElement shadowContentParagraph = shadowContent.getSubElement(By.cssSelector("p"));
    public final GuiElement shadowContentInput = shadowContent.getSubElement(By.tagName("input"));

    public GuiElementShadowRootPage(WebDriver driver) {
        super(driver);
    }


    public void typeText(final String text) {
        shadowContentInput.type(text);
    }

    public void assertInputText(final String expectedText) {
        final String currentInputText = shadowContentInput.getAttribute("value");
        Assert.assertEquals(currentInputText, expectedText, "ShadowInput contains expected text");
    }

    public void assertShadowRootVisibility() {
        // shadow root is never displayed but present
        shadowRootElement.asserts().assertIsNotDisplayed();
        shadowRootElement.asserts().assertIsPresent();

        // sub elements from shadow root are displayed
        shadowContent.asserts().assertIsDisplayed();
        shadowContentInput.asserts().assertIsDisplayed();
        shadowContentParagraph.asserts().assertIsDisplayed();
    }
}