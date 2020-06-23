package eu.tsystems.mms.tic.testframework.simulation;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.TestableUiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementActions;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.BinaryAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.RectAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringAssertion;
import org.openqa.selenium.WebElement;

import java.util.function.Consumer;

public class AbstractSimulator implements UiElementActions {
    protected final GuiElement guiElement;

    public AbstractSimulator(GuiElement guiElement) {
        this.guiElement = guiElement;
    }

    @Override
    public StringAssertion<String> text() {
        return guiElement.text();
    }

    @Override
    public QuantityAssertion<Integer> numberOfElements() {
        return guiElement.numberOfElements();
    }

    @Override
    public StringAssertion<String> value(String attribute) {
        return guiElement.value();
    }

    @Override
    public StringAssertion<String> css(String property) {
        return guiElement.css(property);
    }

    @Override
    public BinaryAssertion<Boolean> enabled() {
        return guiElement.enabled();
    }

    @Override
    public BinaryAssertion<Boolean> selected() {
        return guiElement.selected();
    }

    @Override
    public BinaryAssertion<Boolean> present() {
        return guiElement.present();
    }

    @Override
    public BinaryAssertion<Boolean> displayed() {
        return guiElement.displayed();
    }

    @Override
    public BinaryAssertion<Boolean> visible(boolean complete) {
        return guiElement.visible(complete);
    }

    @Override
    public StringAssertion<String> tagName() {
        return guiElement.tagName();
    }

    @Override
    public RectAssertion bounds() {
        return guiElement.bounds();
    }

    @Override
    public TestableUiElement waitFor() {
        return guiElement.waitFor();
    }

    @Override
    public ImageAssertion screenshot() {
        return guiElement.screenshot();
    }

    @Override
    public TestableUiElement highlight() {
        return guiElement.highlight();
    }

    @Override
    public String createXPath() {
        return guiElement.createXPath();
    }

    @Override
    public Locate getLocate() {
        return guiElement.getLocate();
    }

    @Override
    public WebElement getWebElement() {
        return guiElement.getWebElement();
    }

    @Override
    public UiElementActions click() {
        guiElement.click();
        return this;
    }

    @Override
    public UiElementActions click(Consumer<UiElement> whenFail) {
        guiElement.click(whenFail);
        return this;
    }

    @Override
    public UiElementActions doubleClick() {
        guiElement.doubleClick();
        return this;
    }

    @Override
    public UiElementActions contextClick() {
        guiElement.contextClick();
        return this;
    }

    @Override
    public UiElementActions select() {
        guiElement.select();
        return this;
    }

    @Override
    public UiElementActions deselect() {
        guiElement.deselect();
        return this;
    }

    @Override
    public UiElementActions sendKeys(CharSequence... charSequences) {
        guiElement.sendKeys(charSequences);
        return this;
    }

    @Override
    public UiElementActions type(String text) {
        guiElement.type(text);
        return this;
    }

    @Override
    public UiElementActions clear() {
        guiElement.clear();
        return this;
    }

    @Override
    public UiElementActions hover() {
        guiElement.hover();
        return this;
    }

    @Override
    public UiElementActions scrollTo(int yOffset) {
        guiElement.scrollTo(yOffset);
        return this;
    }
}
