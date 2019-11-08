package eu.tsystems.mms.tic.testframework.pageobjects.components;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Component;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.TestableGuiElement;
import org.openqa.selenium.By;

public class InputForm extends Component<InputForm> {

    @Check
    private IGuiElement button = findOne(By.className("component-btn"));

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
}
