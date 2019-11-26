package eu.tsystems.mms.tic.testframework.pageobjects.components;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.AbstractComponent;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.InteractiveGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.TestableGuiElement;
import org.openqa.selenium.By;

public class InputForm extends AbstractComponent<InputForm> {

    @Check
    private IGuiElement button = find(By.className("component-btn"));

    private IGuiElement input = findByQa("input/text");

    public InputForm(IGuiElement rootElement) {
        super(rootElement);
    }

    @Override
    protected InputForm self() {
        return this;
    }

    public TestableGuiElement button() {
        return button;
    }

    public InteractiveGuiElement input() {
        return input;
    }
}
