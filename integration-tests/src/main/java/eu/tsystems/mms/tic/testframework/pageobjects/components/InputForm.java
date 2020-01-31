package eu.tsystems.mms.tic.testframework.pageobjects.components;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.AbstractComponent;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.InteractiveUiElement;
import org.openqa.selenium.By;

public class InputForm extends AbstractComponent<InputForm> {

    @Check
    private UiElement button = find(By.className("component-btn"));

    private UiElement input = findByQa("input/text");

    public InputForm(UiElement rootElement) {
        super(rootElement);
    }

    @Override
    protected InputForm self() {
        return this;
    }

    public InteractiveUiElement button() {
        return button;
    }

    public InteractiveUiElement input() {
        return input;
    }
}
