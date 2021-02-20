package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import java.awt.Color;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class EmptyUiElement extends GuiElement {
    private static final GuiElementCore emptyCore = new GuiElementCore() {
        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public boolean isDisplayed() {
            return false;
        }

        @Override
        public boolean isVisible(boolean complete) {
            return false;
        }

        @Override
        public boolean isSelected() {
            return false;
        }

        @Override
        public String getText() {
            return null;
        }

        @Override
        public String getAttribute(String attributeName) {
            return null;
        }

        @Override
        public Rectangle getRect() {
            return null;
        }

        @Override
        public boolean isSelectable() {
            return false;
        }

        @Override
        public String getTagName() {
            return null;
        }

        @Override
        public Point getLocation() {
            return null;
        }

        @Override
        public Dimension getSize() {
            return null;
        }

        @Override
        public String getCssValue(String cssIdentifier) {
            return null;
        }

        @Override
        public List<String> getTextsFromChildren() {
            return null;
        }

        @Override
        public int getLengthOfValueAfterSendKeys(String textToInput) {
            return 0;
        }

        @Override
        public int getNumberOfFoundElements() {
            return 0;
        }

        @Override
        public File takeScreenshot() {
            return null;
        }

        @Override
        public void findWebElement(Consumer<WebElement> consumer) {

        }

        @Override
        public void select() {

        }

        @Override
        public void deselect() {

        }

        @Override
        public void type(String text) {

        }

        @Override
        public void click() {

        }

        @Override
        public void submit() {

        }

        @Override
        public void sendKeys(CharSequence... charSequences) {

        }

        @Override
        public void clear() {

        }

        @Override
        public void highlight(Color color) {

        }

        @Override
        public void hover() {

        }

        @Override
        public void contextClick() {

        }

        @Override
        public void scrollToElement(int yOffset) {

        }

        @Override
        public void scrollIntoView(Point offset) {

        }

        @Override
        public void doubleClick() {

        }

        @Override
        public void swipe(int offsetX, int offSetY) {

        }
    };

    EmptyUiElement(WebDriver webDriver, Locator locator) {
        super(new GuiElementData(webDriver, locator), emptyCore);
    }

    public EmptyUiElement(Page page, Locator locator) {
        super(new GuiElementData(page.getWebDriver(), locator), emptyCore);
        this.setParent(page);
    }

    public EmptyUiElement(UiElement parent, Locator locator) {
        super(new GuiElementData(((GuiElement)parent).getData(), locator), emptyCore);
    }
}
