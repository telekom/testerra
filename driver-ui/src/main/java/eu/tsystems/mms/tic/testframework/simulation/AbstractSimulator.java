package eu.tsystems.mms.tic.testframework.simulation;

import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicUiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementActions;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementAssertions;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.BinaryAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.RectAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringAssertion;
import java.awt.Color;
import org.openqa.selenium.Point;

public class AbstractSimulator implements UiElementActions {
    protected final UiElement uiElement;

    public AbstractSimulator(UiElement uiElement) {
        this.uiElement = uiElement;
    }

    @Override
    public StringAssertion<String> text() {
        return uiElement.text();
    }

    @Override
    public QuantityAssertion<Integer> numberOfElements() {
        return uiElement.numberOfElements();
    }

    @Override
    public StringAssertion<String> value(String attribute) {
        return uiElement.value();
    }

    @Override
    public StringAssertion<String> css(String property) {
        return uiElement.css(property);
    }

    @Override
    public BinaryAssertion<Boolean> enabled() {
        return uiElement.enabled();
    }

    @Override
    public BinaryAssertion<Boolean> selected() {
        return uiElement.selected();
    }

    @Override
    public BinaryAssertion<Boolean> present() {
        return uiElement.present();
    }

    @Override
    public BinaryAssertion<Boolean> displayed() {
        return uiElement.displayed();
    }

    @Override
    public BinaryAssertion<Boolean> visible(boolean complete) {
        return uiElement.visible(complete);
    }

    @Override
    public StringAssertion<String> tagName() {
        return uiElement.tagName();
    }

    @Override
    public RectAssertion bounds() {
        return uiElement.bounds();
    }

    @Override
    public UiElementAssertions waitFor() {
        return uiElement.waitFor();
    }

    @Override
    public ImageAssertion screenshot() {
        return uiElement.screenshot();
    }

    @Override
    public BasicUiElement highlight(Color color) {
        return uiElement.highlight(color);
    }

    @Override
    /**
     * @todo The simulator doesn't need that
     */
    public String createXPath() {
        return uiElement.createXPath();
    }

    @Override
    /**
     * @todo The simulator doesn't need that
     */
    public Locate getLocate() {
        return uiElement.getLocate();
    }

    @Override
    public BasicUiElement scrollIntoView(Point offset) {
        return uiElement.scrollIntoView(offset);
    }

    @Override
    public UiElementActions click() {
        uiElement.click();
        return this;
    }

    @Override
    public UiElementActions doubleClick() {
        uiElement.doubleClick();
        return this;
    }

    @Override
    public UiElementActions contextClick() {
        uiElement.contextClick();
        return this;
    }

    @Override
    public UiElementActions select() {
        uiElement.select();
        return this;
    }

    @Override
    public UiElementActions deselect() {
        uiElement.deselect();
        return this;
    }

    @Override
    public UiElementActions sendKeys(CharSequence... charSequences) {
        uiElement.sendKeys(charSequences);
        return this;
    }

    @Override
    public UiElementActions type(String text) {
        uiElement.type(text);
        return this;
    }

    @Override
    public UiElementActions clear() {
        uiElement.clear();
        return this;
    }

    @Override
    public UiElementActions hover() {
        uiElement.hover();
        return this;
    }
}
