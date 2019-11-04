package eu.tsystems.mms.tic.testframework.core.test.components;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Component;
import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
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

    public IGuiElement button() {
        return button;
    }
}
